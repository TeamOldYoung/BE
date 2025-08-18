package com.app.oldYoung.global.security.service;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.global.security.converter.AuthConverter;
import com.app.oldYoung.global.security.dto.KakaoDTO;
import com.app.oldYoung.global.security.util.CookieUtil;
import com.app.oldYoung.global.security.util.JwtUtil;
import com.app.oldYoung.global.security.util.KakaoUtil;
import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    public User oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String providerId = String.valueOf(kakaoProfile.getId());

        User user = userRepository.findByProviderAndProviderId("kakao", providerId)
            .orElseGet(() -> createNewUser(kakaoProfile));

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");
        
        // Redis에 리프레시 토큰 저장
        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken);
        
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.addRefreshTokenCookie(httpServletResponse, refreshToken);

        return user;
    }

    private User createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        User newUser = AuthConverter.toUser(
            kakaoProfile.getKakao_account().getEmail(),
            kakaoProfile.getKakao_account().getProfile().getNickname(),
            null,
            String.valueOf(kakaoProfile.getId()),
            passwordEncoder
        );
        return userRepository.save(newUser);
    }

    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            // JWT 토큰 파싱
            String email = jwtUtil.getEmailFromToken(refreshToken);
            String role = jwtUtil.getRoleFromToken(refreshToken);
            
            // 사용자 존재 확인
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            // Redis에서 리프레시 토큰 검증
            if (!refreshTokenService.validateRefreshToken(email, refreshToken)) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
            }

            // 새로운 토큰 발급 (RTR 방식)
            String newAccessToken = jwtUtil.createAccessToken(email, role);
            String newRefreshToken = jwtUtil.createRefreshToken(email, role);

            // 기존 리프레시 토큰 블랙리스트 추가 (보안 강화)
            long remainingTime = jwtUtil.validateToken(refreshToken).getExpiration().getTime() - System.currentTimeMillis();
            if (remainingTime > 0) {
                refreshTokenService.addToBlacklist(refreshToken, remainingTime);
            }

            // Redis에 새로운 리프레시 토큰 저장
            refreshTokenService.saveRefreshToken(email, newRefreshToken);

            response.setHeader("Authorization", "Bearer " + newAccessToken);
            cookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        
        if (refreshToken != null) {
            try {
                String email = jwtUtil.getEmailFromToken(refreshToken);
                
                // Redis에서 리프레시 토큰 삭제
                refreshTokenService.deleteRefreshToken(email);
                
                // 리프레시 토큰 블랙리스트 추가
                long remainingTime = jwtUtil.validateToken(refreshToken).getExpiration().getTime() - System.currentTimeMillis();
                if (remainingTime > 0) {
                    refreshTokenService.addToBlacklist(refreshToken, remainingTime);
                }
            } catch (Exception e) {
                // 토큰이 유효하지 않아도 쿠키는 삭제
            }
        }
        
        cookieUtil.removeRefreshTokenCookie(response);
    }

    public void logoutAll(String email, HttpServletResponse response) {
        // 해당 사용자의 모든 리프레시 토큰 삭제 (모든 기기에서 로그아웃)
        refreshTokenService.deleteAllRefreshTokens(email);
        cookieUtil.removeRefreshTokenCookie(response);
    }

}