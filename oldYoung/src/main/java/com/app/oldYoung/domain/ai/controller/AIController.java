package com.app.oldYoung.domain.ai.controller;

import com.app.oldYoung.domain.ai.dto.*;
import com.app.oldYoung.domain.ai.service.FlaskApiService;
import com.app.oldYoung.domain.ai.service.AIAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI API", description = "Flask AI 서비스 호출 API")
public class AIController {

    private final FlaskApiService flaskApiService;
    private final AIAnalysisService aiAnalysisService;

    @PostMapping("/income/analysis")
    @Operation(summary = "소득분위 분석", description = "사용자 정보를 기반으로 소득분위를 분석합니다")
    public Mono<ResponseEntity<IncomeResponseDTO>> analyzeIncome(
            @RequestBody IncomeRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return aiAnalysisService.analyzeIncomeWithUser(requestDTO, userDetails.getUsername())
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @PostMapping("/welfare/search")
    @Operation(summary = "복지정보 검색", description = "지역명을 기반으로 복지정보를 검색합니다")
    public Mono<ResponseEntity<WelfareResponseDTO>> searchWelfare(@RequestBody WelfareRequestDTO requestDTO) {
        return flaskApiService.getWelfareInfo(requestDTO)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
}