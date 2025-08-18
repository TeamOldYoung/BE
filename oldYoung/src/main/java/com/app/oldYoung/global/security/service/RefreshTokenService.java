package com.app.oldYoung.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // 1. RTR(Refresh Token Rotation) 방식으로 토큰 새로 저장 시 기존 토큰 자동 무효화
    public void saveRefreshToken(String email, String refreshToken) {
        String key = getRefreshTokenKey(email);
        try {
            redisTemplate.opsForValue().set(
                key, 
                refreshToken, 
                refreshTokenExpiration, 
                TimeUnit.MILLISECONDS
            );
            log.info("리프레시 토큰 저장 완료: {}", email);
        } catch (Exception e) {
            log.error("리프레시 토큰 저장 실패: {}, error: {}", email, e.getMessage());
            throw new RuntimeException("리프레시 토큰 저장에 실패했습니다.");
        }
    }

    // 2. Redis에 저장된 토큰과 요청 토큰의 일치 여부 검증
    public boolean validateRefreshToken(String email, String refreshToken) {
        String key = getRefreshTokenKey(email);
        try {
            String storedToken = redisTemplate.opsForValue().get(key);
            boolean isValid = refreshToken.equals(storedToken);
            
            if (!isValid) {
                log.warn("유효하지 않은 리프레시 토큰: {}", email);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("리프레시 토큰 검증 실패: {}, error: {}", email, e.getMessage());
            return false;
        }
    }

    // 리프레시 토큰 삭제 (로그아웃 시)
    public void deleteRefreshToken(String email) {
        String key = getRefreshTokenKey(email);
        try {
            redisTemplate.delete(key);
            log.info("리프레시 토큰 삭제 완료: {}", email);
        } catch (Exception e) {
            log.error("리프레시 토큰 삭제 실패: {}, error: {}", email, e.getMessage());
        }
    }

    // 토큰 블랙리스트 추가 (보안 강화)
    public void addToBlacklist(String token, long expiration) {
        String key = getBlacklistKey(token);
        try {
            redisTemplate.opsForValue().set(
                key, 
                "blacklisted", 
                expiration, 
                TimeUnit.MILLISECONDS
            );
            log.info("토큰 블랙리스트 추가 완료");
        } catch (Exception e) {
            log.error("토큰 블랙리스트 추가 실패: {}", e.getMessage());
        }
    }

    // 토큰 블랙리스트 확인
    public boolean isTokenBlacklisted(String token) {
        String key = getBlacklistKey(token);
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("토큰 블랙리스트 확인 실패: {}", e.getMessage());
            return false;
        }
    }

    // 3. 모든 디바이스에서 로그아웃 처리 (패턴 매칭으로 복수 토큰 삭제)
    public void deleteAllRefreshTokens(String email) {
        String pattern = "refresh_token:" + email + "*";
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("모든 리프레시 토큰 삭제 완료: {}", email);
            }
        } catch (Exception e) {
            log.error("모든 리프레시 토큰 삭제 실패: {}, error: {}", email, e.getMessage());
        }
    }

    private String getRefreshTokenKey(String email) {
        return "refresh_token:" + email;
    }

    private String getBlacklistKey(String token) {
        // 4. 토큰 전체 값 대신 hashCode 사용으로 Redis 메모리 사용량 최적화
        return "blacklist:" + token.hashCode();
    }
}