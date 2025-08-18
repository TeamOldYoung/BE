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

    public CookieUtil(
            @Value("${app.environment:local}") String environment, // 환경 설정 값 (local, production 등)
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration) {
        this.isProduction = "production".equals(environment);
        this.refreshTokenMaxAge = (int) (refreshTokenExpiration / 1000);
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
}