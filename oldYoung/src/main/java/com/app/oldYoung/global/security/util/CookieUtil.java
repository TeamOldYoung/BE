package com.app.oldYoung.global.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private final boolean isProduction;
    private final int refreshTokenMaxAge;
    private final int accessTokenMaxAge;

    public CookieUtil(
            @Value("${app.environment:local}") String environment, // 환경 설정 값 (local, production 등)
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration) {
        this.isProduction = "production".equals(environment);
        this.refreshTokenMaxAge = (int) (refreshTokenExpiration / 1000);
        this.accessTokenMaxAge = (int) (accessTokenExpiration / 1000);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(isProduction);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenMaxAge);
        
        if (isProduction) {
            refreshCookie.setAttribute("SameSite", "Strict");
        }
        
        response.addCookie(refreshCookie);
    }

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(isProduction);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(accessTokenMaxAge);
        
        if (isProduction) {
            accessCookie.setAttribute("SameSite", "Strict");
        }
        
        response.addCookie(accessCookie);
    }

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(isProduction);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    public void removeAccessTokenCookie(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", "");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(isProduction);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);
    }
}