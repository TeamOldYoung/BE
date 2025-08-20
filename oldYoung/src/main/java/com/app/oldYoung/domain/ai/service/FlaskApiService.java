package com.app.oldYoung.domain.ai.service;

import com.app.oldYoung.domain.ai.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlaskApiService {

    private final WebClient webClient;

    public Mono<IncomeResponseDTO> getIncomeAnalysis(IncomeRequestDTO requestDTO) {
        log.info("Flask API 소득분위 분석 요청: {}", requestDTO);
        
        return webClient.post()
                .uri("/income/")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(IncomeResponseDTO.class)
                .doOnNext(response -> log.info("Flask API 소득분위 분석 응답 받음"))
                .doOnError(error -> log.error("Flask API 소득분위 분석 요청 실패", error));
    }

    public Mono<WelfareResponseDTO> getWelfareInfo(WelfareRequestDTO requestDTO) {
        log.info("Flask API 복지정보 검색 요청: 지역={}", requestDTO.region());
        
        return webClient.post()
                .uri("/welfare/")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(WelfareResponseDTO.class)
                .doOnNext(response -> log.info("Flask API 복지정보 검색 응답 받음"))
                .doOnError(error -> log.error("Flask API 복지정보 검색 요청 실패", error));
    }
}