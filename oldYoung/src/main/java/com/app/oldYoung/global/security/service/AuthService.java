package com.app.oldYoung.global.security.service;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.dto.GoogleDTO;
import com.app.oldYoung.global.security.dto.KakaoDTO;
import com.app.oldYoung.global.security.util.CookieUtil;
import com.app.oldYoung.global.security.util.GoogleUtil;
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
    private final GoogleUtil googleUtil;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    /**
     * 소셜 로그인 처리를 위한 메인 메소드입니다. provider 값에 따라 카카오 또는 구글 로그인 로직을 수행합니다.
     *
     * @param provider            "kakao" 또는 "google"
     * @param accessCode          각 소셜 로그인 제공자로부터 받은 인가 코드
     * @param httpServletResponse JWT 토큰을 담아 클라이언트에게 응답하기 위한 객체
     * @return 로그인 또는 신규 가입한 사용자 정보(User 엔티티)
     */
    @Transactional
    public User oAuthLogin(String provider, String accessCode,
        HttpServletResponse httpServletResponse) {
        User user;
        Claims claims;

        // 1. provider에 따라 분기 처리
        if ("kakao".equalsIgnoreCase(provider)) {
            // 1-1. 카카오 서버에 토큰 요청
            KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
            // 1-2. ID Token 검증 및 사용자 정보(Claims) 추출
            claims = jwtUtil.validateAndGetClaimsFromKakaoToken(oAuthToken.getId_token());

        } else if ("google".equalsIgnoreCase(provider)) {
            // 1-3. 구글 서버에 토큰 요청
            GoogleDTO oAuthToken = googleUtil.requestToken(accessCode);
            // 1-4. ID Token 검증 및 사용자 정보(Claims) 추출
            claims = jwtUtil.validateAndGetClaimsFromGoogleToken(oAuthToken.getId_token());

        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }

        // 2. 추출한 Claims에서 사용자 정보 파싱
        String providerId = claims.getSubject(); // OIDC 표준에서 사용자를 식별하는 고유 ID
        String email = claims.get("email", String.class);
        // 1. 소셜 제공자별로 닉네임 필드명이 다르므로 조건부 처리 (Google: "name", Kakao: "nickname")
        String nickname = "google".equalsIgnoreCase(provider) ? claims.get("name", String.class)
            : claims.get("nickname", String.class);

        // 3. 사용자 정보로 DB 조회 또는 신규 회원가입
        user = processUser(provider, providerId, email, nickname);

        // 4. 우리 서비스의 JWT(Access/Refresh Token)를 생성하여 응답에 추가
        String accessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");

        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken);
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.addRefreshTokenCookie(httpServletResponse, refreshToken);

        return user;
    }

    /**
     * Provider로부터 받은 사용자 정보로 DB를 조회하고, 없으면 신규 가입시킵니다.
     */
    private User processUser(String provider, String providerId, String email, String nickname) {
        // 2. 기존 사용자 조회 후, 없으면 신규 생성 (orElseGet으로 Lazy Evaluation 적용)
        return userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(() -> createNewUser(provider, providerId, email, nickname));
    }

    private User createNewUser(String provider, String providerId, String email, String nickname) {
        User newUser = User.builder()
            .email(email)
            .membername(nickname)
            .password(null)
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
        // 3. Redis에 저장된 Refresh Token과 요청으로 받은 토큰 일치 여부 검증
        if (!refreshTokenService.validateRefreshToken(email, refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String newAccessToken = jwtUtil.createAccessToken(user.getEmail(), "USER");
        String newRefreshToken = jwtUtil.createRefreshToken(user.getEmail(), "USER");
        // 4. 새로운 Refresh Token을 Redis에 저장하여 기존 토큰 무효화
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