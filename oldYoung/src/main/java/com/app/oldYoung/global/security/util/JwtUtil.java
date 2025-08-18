package com.app.oldYoung.global.security.util;

import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.exception.AuthHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String kakaoClientId;

    private final Map<String, PublicKey> kakaoPublicKeys = new ConcurrentHashMap<>();

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
        @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.kakaoClientId = kakaoClientId;
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

    // [OIDC] 카카오 ID Token 검증 및 Claims 추출 메소드
    public Claims validateAndGetClaimsFromKakaoToken(String idToken) {
        try {
            // 1. 토큰 헤더에서 kid(Key ID) 추출
            String kid = getKidFromTokenHeader(idToken);

            // 2. kid에 해당하는 공개키 가져오기 (캐시 또는 API 호출)
            PublicKey publicKey = getKakaoPublicKey(kid);

            // 3. 공개키를 사용하여 토큰 검증
            return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .requireIssuer("https://kauth.kakao.com") // iss가 카카오인지 확인
                .requireAudience(kakaoClientId)           // aud가 우리 앱 ID인지 확인
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

    private String getKidFromTokenHeader(String token) {
        try {
            String headerSegment = token.substring(0, token.indexOf('.'));
            byte[] decodedHeader = Base64.getUrlDecoder().decode(headerSegment);
            Map<String, Object> header = new ObjectMapper().readValue(new String(decodedHeader), Map.class);
            return (String) header.get("kid");
        } catch (JsonProcessingException e) {
            log.error("ID Token 헤더 파싱 실패", e);
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
        }
    }

    private PublicKey getKakaoPublicKey(String kid) {
        // 캐시된 키가 있으면 바로 반환
        if (kakaoPublicKeys.containsKey(kid)) {
            return kakaoPublicKeys.get(kid);
        }

        // 캐시에 없으면 카카오 JWKS API에서 가져오기
        RestTemplate restTemplate = new RestTemplate();
        Map<String, List<Map<String, String>>> jwks = restTemplate.getForObject("https://kauth.kakao.com/.well-known/jwks.json", Map.class);

        PublicKey foundKey = null;
        for (Map<String, String> keyInfo : jwks.get("keys")) {
            String currentKid = keyInfo.get("kid");
            // 모든 키를 캐시에 저장
            PublicKey publicKey = generatePublicKey(keyInfo);
            kakaoPublicKeys.put(currentKid, publicKey);
            if (kid.equals(currentKid)) {
                foundKey = publicKey;
            }
        }

        if (foundKey == null) {
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
        }

        return foundKey;
    }

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
            throw new AuthHandler(ErrorCode.JWT_TOKEN_INVALID);
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
