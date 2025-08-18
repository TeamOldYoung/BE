package com.app.oldYoung.domain.user.controller;

import com.app.oldYoung.domain.user.converter.UserConverter;
import com.app.oldYoung.domain.user.dto.UserRequestDTO;
import com.app.oldYoung.domain.user.dto.UserResponseDTO;
import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.global.common.apiResponse.response.ApiResponse;
import com.app.oldYoung.global.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     * 소셜 로그인 통합 엔드포인트
     * @param provider 'kakao', 'google' 등 소셜 로그인 제공자
     * @param accessCode 각 소셜 로그인 제공자로부터 받은 인가 코드
     * @return 로그인 또는 회원가입 결과
     */
    @GetMapping("/auth/login/{provider}")
    public ResponseEntity<ApiResponse<UserResponseDTO.JoinResultDTO>> socialLogin(
        @PathVariable("provider") String provider,
        @RequestParam("code") String accessCode,
        HttpServletResponse httpServletResponse) {
        User user = authService.oAuthLogin(provider, accessCode, httpServletResponse);
        UserResponseDTO.JoinResultDTO result = UserConverter.toJoinResultDTO(user);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        authService.reissueToken(request, response);
        return ResponseEntity.ok(ApiResponse.success("토큰이 성공적으로 재발급되었습니다."));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 성공적으로 처리되었습니다."));
    }

    @PostMapping("/auth/logout/all")
    public ResponseEntity<?> logoutAll(@RequestParam String email, HttpServletResponse response) {
        authService.logoutAll(email, response);
        return ResponseEntity.ok(ApiResponse.success("모든 기기에서 로그아웃이 처리되었습니다."));
    }
}