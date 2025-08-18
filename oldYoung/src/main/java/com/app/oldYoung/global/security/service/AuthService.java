package com.app.oldYoung.global.security.service;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.dto.KakaoDTO;
import com.app.oldYoung.global.security.util.CookieUtil;
import com.app.oldYoung.global.security.util.JwtUtil;
import com.app.oldYoung.global.security.util.KakaoUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public User oAuthLogin(String provider, String accessCode, HttpServletResponse httpServletResponse) {
        User user;

        if ("kakao".equalsIgnoreCase(provider)) {
            // 1. 카카오로부터 토큰(Access Token + ID Token)을 받습니다.
            KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
            String idToken = oAuthToken.getId_token();

            // 2. JwtUtil을 통해 ID Token을 검증하고 사용자 정보를 추출합니다.
            Claims claims = jwtUtil.validateAndGetClaimsFromKakaoToken(idToken);
            String providerId = claims.getSubject(); // 'sub' 클레임 (사용자 고유 ID)
            String email = claims.get("email", String.class);
            String nickname = claims.get("nickname", String.class);

            // 3. 사용자 정보를 처리(조회 또는 생성)합니다.
            user = processUser(provider, providerId, email, nickname);
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }

        // 4. 우리 서비스의 JWT(Access/Refresh Token)를 생성하고 응답에 담습니다.
        String accessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");

        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken);

        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.addRefreshTokenCookie(httpServletResponse, refreshToken);

        return user;
    }

    private User processUser(String provider, String providerId, String email, String nickname) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(() -> createNewUser(provider, providerId, email, nickname));
    }

    private User createNewUser(String provider, String providerId, String email, String nickname) {
        User newUser = User.builder()
            .email(email)
            .membername(nickname)
            .password(null) // 소셜 로그인은 비밀번호 없음
            .provider(provider)
            .providerId(providerId)
            .build();
        return userRepository.save(newUser);
    }

    @Transactional
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);

        if (!refreshTokenService.validateRefreshToken(email, refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String newRefreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");

        refreshTokenService.saveRefreshToken(user.getEmail(), newRefreshToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        cookieUtil.addRefreshTokenCookie(response, newRefreshToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            String email = jwtUtil.getEmailFromToken(refreshToken);
            refreshTokenService.deleteRefreshToken(email);
        }

        cookieUtil.removeRefreshTokenCookie(response);
    }

    public void logoutAll(String email, HttpServletResponse response) {
        refreshTokenService.deleteAllRefreshTokens(email);
        cookieUtil.removeRefreshTokenCookie(response);
    }
}
