package com.app.oldYoung.domain.user.controller;

import com.app.oldYoung.domain.user.dto.UserResponseDTO.UserMyPageResponseDTO;
import com.app.oldYoung.domain.user.service.UserService;
import com.app.oldYoung.global.common.apiResponse.response.ApiResponse;
import com.app.oldYoung.global.security.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> currentUser = new HashMap<>();
        currentUser.put("id", userPrincipal.getId());
        currentUser.put("email", userPrincipal.getEmail());
        currentUser.put("membername", userPrincipal.getMembername());
        
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }

    /**
     * 마이페이지 조회 API
     */
    @GetMapping("/my-page")
    public ResponseEntity<ApiResponse<UserMyPageResponseDTO>> getMyPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserMyPageResponseDTO response = userService.getMyPageInfo(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}