package com.app.oldYoung.global.security.service;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.converter.AuthConverter;
import com.app.oldYoung.global.security.dto.KakaoDTO;
import com.app.oldYoung.global.security.util.CookieUtil;
import com.app.oldYoung.global.security.util.JwtUtil;
import com.app.oldYoung.global.security.util.KakaoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public User oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);

        User user = processUser(kakaoProfile);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");

        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken);

        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.addAccessTokenCookie(httpServletResponse, accessToken);
        cookieUtil.addRefreshTokenCookie(httpServletResponse, refreshToken);

        return user;
    }

    private User processUser(KakaoDTO.KakaoProfile kakaoProfile) {
        String providerId = String.valueOf(kakaoProfile.id());
        return userRepository.findByProviderAndProviderId("kakao", providerId)
            .orElseGet(() -> createNewUser(
                kakaoProfile.kakao_account().email(),
                kakaoProfile.kakao_account().profile().nickname(),
                providerId
            ));
    }

    private User createNewUser(String email, String nickname, String providerId) {
        User newUser = AuthConverter.toUser(
            email,
            nickname,
            null,
            providerId,
            passwordEncoder
        );
        return userRepository.save(newUser);
    }

    @Transactional
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        log.info("쿠키에서 추출한 RefreshToken: {}", refreshToken != null ? "존재" : "없음");

        if (refreshToken == null) {
            log.error("RefreshToken이 쿠키에 존재하지 않음");
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        log.info("RefreshToken에서 추출한 이메일: {}", email);

        if (!refreshTokenService.validateRefreshToken(email, refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        log.info("사용자 조회 성공 - 이메일: {}, ID: {}", user.getEmail(), user.getId());

        String newAccessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String newRefreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");
        
        log.info("새로운 AccessToken 생성: {}", newAccessToken.substring(0, Math.min(20, newAccessToken.length())) + "...");
        log.info("새로운 RefreshToken 생성: {}", newRefreshToken.substring(0, Math.min(20, newRefreshToken.length())) + "...");

        refreshTokenService.saveRefreshToken(user.getEmail(), newRefreshToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        cookieUtil.addAccessTokenCookie(response, newAccessToken);
        cookieUtil.addRefreshTokenCookie(response, newRefreshToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            String email = jwtUtil.getEmailFromToken(refreshToken);
            refreshTokenService.deleteRefreshToken(email);
        }

        cookieUtil.removeAccessTokenCookie(response);
        cookieUtil.removeRefreshTokenCookie(response);
    }

    public void logoutAll(String email, HttpServletResponse response) {
        refreshTokenService.deleteAllRefreshTokens(email);
        cookieUtil.removeAccessTokenCookie(response);
        cookieUtil.removeRefreshTokenCookie(response);
    }
}
