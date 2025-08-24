package com.app.oldYoung.domain.welfare.controller;

import com.app.oldYoung.domain.welfare.dto.WelfareItemRequestDTO;
import com.app.oldYoung.domain.welfare.dto.WelfareItemResponseDTO;
import com.app.oldYoung.domain.welfare.service.WelfareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Parameter(description = "지역명", example = "서울")
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
            @Parameter(description = "나이 구분 (0: 청년, 1: 노인)", example = "0")
            @PathVariable Integer age) {
        List<WelfareItemResponseDTO> welfareItems = welfareService.getWelfareItemsByAge(age)
                .stream()
                .map(WelfareItemResponseDTO::from)
                .toList();
        return ResponseEntity.ok(welfareItems);
    }
    
    
    @PostMapping("/young")
    @Operation(summary = "청년 복지 정보 저장", description = "청년(age=0)을 위한 복지 정보를 저장합니다")
    public ResponseEntity<WelfareItemResponseDTO> saveYoungWelfare(
            @Valid @RequestBody WelfareItemRequestDTO requestDTO) {
        // age를 0으로 고정
        WelfareItemRequestDTO youngRequestDTO = new WelfareItemRequestDTO(
                requestDTO.title(),
                requestDTO.subscript(),
                requestDTO.period(),
                requestDTO.agency(),
                requestDTO.contact(),
                requestDTO.applicant(),
                requestDTO.link(),
                requestDTO.city(),
                0
        );
        
        WelfareItemResponseDTO savedItem = WelfareItemResponseDTO.from(
                welfareService.saveWelfareItem(youngRequestDTO.toEntity())
        );
        return ResponseEntity.ok(savedItem);
    }
    
    @PostMapping("/elder")
    @Operation(summary = "노인 복지 정보 저장", description = "노인(age=1)을 위한 복지 정보를 저장합니다")
    public ResponseEntity<WelfareItemResponseDTO> saveElderWelfare(
            @Valid @RequestBody WelfareItemRequestDTO requestDTO) {
        // age를 1로 고정
        WelfareItemRequestDTO elderRequestDTO = new WelfareItemRequestDTO(
                requestDTO.title(),
                requestDTO.subscript(),
                requestDTO.period(),
                requestDTO.agency(),
                requestDTO.contact(),
                requestDTO.applicant(),
                requestDTO.link(),
                requestDTO.city(),
                1
        );
        
        WelfareItemResponseDTO savedItem = WelfareItemResponseDTO.from(
                welfareService.saveWelfareItem(elderRequestDTO.toEntity())
        );
        return ResponseEntity.ok(savedItem);
    }
    
}