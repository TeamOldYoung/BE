package com.app.oldYoung.domain.ai.service;

import com.app.oldYoung.domain.ai.dto.IncomeRequestDTO;
import com.app.oldYoung.domain.ai.dto.IncomeResponseDTO;
import com.app.oldYoung.domain.incomebracket.entity.IncomeBracket;
import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.domain.incomebracket.repository.IncomeBracketRepository;
import com.app.oldYoung.domain.incomesnapshot.repository.IncomeSnapshotRepository;
import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisService {

    private final FlaskApiService flaskApiService;
    private final UserRepository userRepository;
    private final IncomeBracketRepository incomeBracketRepository;
    private final IncomeSnapshotRepository incomeSnapshotRepository;

    @Transactional
    public Mono<IncomeResponseDTO> analyzeIncomeWithUser(IncomeRequestDTO requestDTO, String userEmail) {
        return flaskApiService.getIncomeAnalysis(requestDTO)
                .doOnNext(response -> saveAnalysisResult(requestDTO, response, userEmail));
    }

    private void saveAnalysisResult(IncomeRequestDTO requestDTO, IncomeResponseDTO response, String userEmail) {
        // 1. 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자 정보 업데이트
        user.updateUser(requestDTO.getBirthDate(), requestDTO.getAddress());
        userRepository.save(user);

        // 2. 기존 분석이 있는지 확인
        if (incomeBracketRepository.findByUser(user).isPresent()) {
            throw new CustomException(ErrorCode.ANALYSIS_ALREADY_EXISTS);
        }

        // 3. IncomeBracket 저장
        IncomeBracket incomeBracket = IncomeBracket.create(
                requestDTO.getFamilyNum(),
                requestDTO.getSalary().longValue(),
                requestDTO.getPension().longValue(),
                requestDTO.getHousing_type(),
                requestDTO.getAsset().longValue(),
                requestDTO.getDebt().longValue(),
                requestDTO.getCar_info(),
                requestDTO.getDisability(),
                requestDTO.getEmploymentStatus(),
                requestDTO.getPastSupported(),
                user
        );

        IncomeBracket savedBracket = incomeBracketRepository.save(incomeBracket);

        // 4. IncomeSnapshot 저장
        Map<String, Object> analysisResult = response.response();
        if (analysisResult.containsKey("결과 요약")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> summary = (Map<String, Object>) analysisResult.get("결과 요약");

            IncomeSnapshot snapshot = IncomeSnapshot.create(
                    getLongValue(summary, "incomeEval"),
                    getLongValue(summary, "assetEval"),
                    getLongValue(summary, "totalIncome"),
                    getLongValue(summary, "midRatio"),
                    getLongValue(summary, "expBracket"),
                    savedBracket.getId(),
                    user
            );

            incomeSnapshotRepository.save(snapshot);
        }
    }


    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}