package com.app.oldYoung.domain.user.controller;

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

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> currentUser = new HashMap<>();
        currentUser.put("id", userPrincipal.getId());
        currentUser.put("email", userPrincipal.getEmail());
        currentUser.put("membername", userPrincipal.getMembername());
        
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }
}