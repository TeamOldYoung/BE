package com.app.oldYoung.global.security.util;

import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.exception.AuthHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String kakaoClientId;
    private final String googleClientId;

    private final Map<String, PublicKey> publicKeys = new ConcurrentHashMap<>();

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
        @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId,
        @Value("${spring.security.oauth2.client.registration.google.client-id}") String googleClientId
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.kakaoClientId = kakaoClientId;
        this.googleClientId = googleClientId;
    }

    /**
     * [OIDC] 구글 ID Token의 유효성을 검증하고, 토큰에 담긴 사용자 정보(Claims)를 반환합니다.
     *
     * @param idToken 구글로부터 받은 ID Token 문자열
     * @return 사용자 정보가 담긴 Claims 객체
     */
    public Claims validateAndGetClaimsFromGoogleToken(String idToken) {
        try {
            String kid = getKidFromTokenHeader(idToken);
            PublicKey publicKey = getPublicKey("google", kid);

            return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .requireIssuer("https://accounts.google.com") // iss가 구글인지 확인
                .requireAudience(googleClientId)           // aud가 우리 앱 ID인지 확인
                .build()
                .parseClaimsJws(idToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.error("만료된 구글 ID Token입니다: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 구글 ID Token입니다: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
        }
    }

    /**
     * [OIDC] 카카오 ID Token의 유효성을 검증하고, 토큰에 담긴 사용자 정보(Claims)를 반환합니다.
     *
     * @param idToken 카카오로부터 받은 ID Token 문자열
     * @return 사용자 정보가 담긴 Claims 객체
     */
    public Claims validateAndGetClaimsFromKakaoToken(String idToken) {
        try {
            String kid = getKidFromTokenHeader(idToken);
            PublicKey publicKey = getPublicKey("kakao", kid);

            return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .requireIssuer("https://kauth.kakao.com")
                .requireAudience(kakaoClientId)
                .build()
                .parseClaimsJws(idToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.error("만료된 카카오 ID Token입니다: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 카카오 ID Token입니다: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
        }
    }

    /**
     * [OIDC] Provider(카카오, 구글)와 kid(Key ID)를 받아 해당 공개키를 반환합니다. 내부에 캐싱 로직이 있어 한번 조회한 키는 다시 API 요청을
     * 하지 않습니다.
     *
     * @param provider "kakao" 또는 "google"
     * @param kid      토큰 헤더에 명시된 Key ID
     * @return 서명 검증에 사용할 PublicKey 객체
     */
    private PublicKey getPublicKey(String provider, String kid) {
        String cacheKey = provider + "_" + kid;
        // 1. 네트워크 요청 최소화를 위해 공개키 메모리 캐싱 체크
        if (publicKeys.containsKey(cacheKey)) {
            return publicKeys.get(cacheKey);
        }

        String jwksUri;
        if ("kakao".equals(provider)) {
            jwksUri = "https://kauth.kakao.com/.well-known/jwks.json";
        } else if ("google".equals(provider)) {
            jwksUri = "https://www.googleapis.com/oauth2/v3/certs";
        } else {
            throw new IllegalArgumentException("지원하지 않는 provider입니다.");
        }

        RestTemplate restTemplate = new RestTemplate();
        Map<String, List<Map<String, String>>> jwks = restTemplate.getForObject(jwksUri, Map.class);

        PublicKey foundKey = null;
        if (jwks != null && jwks.get("keys") != null) {
            // 2. JWKS에서 모든 키를 캐싱하고, 요청된 kid와 일치하는 키 찾기
            for (Map<String, String> keyInfo : jwks.get("keys")) {
                String currentKid = keyInfo.get("kid");
                PublicKey publicKey = generatePublicKey(keyInfo);
                publicKeys.put(provider + "_" + currentKid, publicKey);
                if (kid.equals(currentKid)) {
                    foundKey = publicKey;
                }
            }
        }

        if (foundKey == null) {
            throw new CustomException(ErrorCode.JWT_TOKEN_INVALID, "일치하는 공개키를 찾을 수 없습니다.");
        }
        return foundKey;
    }

    /**
     * [OIDC] JWKS(JSON Web Key Set) 정보로부터 PublicKey 객체를 생성합니다.
     */
    private PublicKey generatePublicKey(Map<String, String> keyInfo) {
        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(keyInfo.get("n"));
            byte[] eBytes = Base64.getUrlDecoder().decode(keyInfo.get("e"));

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new CustomException(ErrorCode.JWT_TOKEN_INVALID, "공개키 생성에 실패했습니다.", ex);
        }
    }

    /**
     * [OIDC] 토큰의 헤더를 디코딩하여 kid(Key ID)를 추출합니다.
     */
    private String getKidFromTokenHeader(String token) {
        try {
            // 3. JWT 구조: header.payload.signature에서 header 부분만 Base64 디코딩 후 kid 추출
            String headerSegment = token.substring(0, token.indexOf('.'));
            byte[] decodedHeader = Base64.getUrlDecoder().decode(headerSegment);
            Map<String, Object> header = new ObjectMapper().readValue(new String(decodedHeader),
                Map.class);
            return (String) header.get("kid");
        } catch (JsonProcessingException | NullPointerException e) {
            log.error("ID Token 헤더 파싱 실패", e);
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
        }
    }

    public String createAccessToken(String email, String role) {
        return createToken(email, role, accessTokenExpiration);
    }

    public String createRefreshToken(String email, String role) {
        return createToken(email, role, refreshTokenExpiration);
    }

    private String createToken(String email, String role, long expiration) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration);

            return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        } catch (Exception e) {
            log.error("JWT 토큰 생성 실패: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_CREATION_FAILED);
        }
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료되었습니다: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (AuthHandler e) {
            return true;
        }
    }
}