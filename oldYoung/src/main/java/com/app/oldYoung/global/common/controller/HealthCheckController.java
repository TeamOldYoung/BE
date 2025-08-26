package com.app.oldYoung.global.common.controller;

import com.app.oldYoung.global.common.apiResponse.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check API", description = "서버 상태 확인 API")
public class HealthCheckController {

    @GetMapping
    @Operation(summary = "서버 상태 확인", description = "서버가 정상적으로 작동하는지 확인합니다")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("정상 작동");
    }
}
