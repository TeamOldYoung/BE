package com.app.oldYoung.domain.user.controller;

import com.app.oldYoung.domain.user.dto.UserRequestDTO;
import com.app.oldYoung.domain.user.dto.UserResponseDTO;
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

    @GetMapping("/auth/login/kakao")
    public ResponseEntity<ApiResponse<UserResponseDTO.JoinResultDTO>> kakaoLogin(
            @RequestParam("code") String accessCode,
            HttpServletResponse httpServletResponse) {
        UserResponseDTO.JoinResultDTO result = authService.oAuthLogin(accessCode, httpServletResponse);
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