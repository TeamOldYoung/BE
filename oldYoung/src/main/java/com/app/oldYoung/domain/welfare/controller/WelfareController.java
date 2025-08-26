package com.app.oldYoung.domain.welfare.controller;

import com.app.oldYoung.domain.welfare.dto.WelfareItemResponseDTO;
import com.app.oldYoung.domain.welfare.service.WelfareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/welfare")
@RequiredArgsConstructor
@Tag(name = "Welfare API", description = "복지 서비스 관리 API")
public class WelfareController {
    
    private final WelfareService welfareService;
    
    @GetMapping("/city/{city}")
    @Operation(summary = "지역별 복지 서비스 조회", description = "특정 지역의 복지 서비스를 조회합니다")
    public ResponseEntity<List<WelfareItemResponseDTO>> getWelfareByCity(
            @Schema(description = "지역명", example = "서울")
            @PathVariable String city) {
        List<WelfareItemResponseDTO> welfareItems = welfareService.getWelfareItemsByCity(city)
                .stream()
                .map(WelfareItemResponseDTO::from)
                .toList();
        return ResponseEntity.ok(welfareItems);
    }
    
    @GetMapping("/age/{age}")
    @Operation(summary = "나이별 복지 서비스 조회", description = "특정 연령대의 복지 서비스를 조회합니다 (0: 청년, 1: 노인)")
    public ResponseEntity<List<WelfareItemResponseDTO>> getWelfareByAge(
            @Schema(description = "나이 구분 (0: 청년, 1: 노인)", example = "0")
            @PathVariable Integer age) {
        List<WelfareItemResponseDTO> welfareItems = welfareService.getWelfareItemsByAge(age)
                .stream()
                .map(WelfareItemResponseDTO::from)
                .toList();
        return ResponseEntity.ok(welfareItems);
    }
    
    
    
    
}