package com.app.oldYoung.global.security.filter;

import com.app.oldYoung.global.security.service.CustomUserDetailsService;
import com.app.oldYoung.global.security.service.RefreshTokenService;
import com.app.oldYoung.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // 1. 로그아웃된 토큰이 다시 사용되는 것을 방지하기 위해 블랙리스트 체크
                if (refreshTokenService.isTokenBlacklisted(jwt)) {
                    log.warn("블랙리스트된 토큰 사용 시도");
                    filterChain.doFilter(request, response);
                    return;
                }

                // JWT에서 이메일 추출
                String email = jwtUtil.getEmailFromToken(jwt);
                
                // 2. 중복 인증 방지: 이미 SecurityContext에 인증 정보가 있으면 건너뛴
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    // 토큰 유효성 검증
                    if (!jwtUtil.isTokenExpired(jwt)) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("사용자 인증 완료: {}", email);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생", e);
            // 인증 실패 시 SecurityContext를 비워둠
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 인증이 불필요한 경로들은 필터를 적용하지 않음
        String path = request.getServletPath();
        return path.startsWith("/health") || 
               path.startsWith("/auth/") || 
               path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html");
    }
}