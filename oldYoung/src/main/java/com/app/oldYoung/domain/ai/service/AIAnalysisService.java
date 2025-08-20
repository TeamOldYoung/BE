package com.app.oldYoung.domain.ai.service;

import com.app.oldYoung.domain.ai.dto.IncomeRequestDTO;
import com.app.oldYoung.domain.ai.dto.IncomeResponseDTO;
import com.app.oldYoung.domain.incomebracket.entity.IncomeBracket;
import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.domain.incomebracket.repository.IncomeBracketRepository;
import com.app.oldYoung.domain.incomesnapshot.repository.IncomeSnapshotRepository;
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
                .flatMap(response -> {
                    try {
                        saveAnalysisResult(requestDTO, response, userEmail);
                        return Mono.just(response);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
;
    }

    private void saveAnalysisResult(IncomeRequestDTO requestDTO, IncomeResponseDTO response, String userEmail) {
        try {
            // 1. 사용자 조회
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userEmail));

            // 2. 기존 분석이 있는지 확인
            if (incomeBracketRepository.findByUser(user).isPresent()) {
                throw new RuntimeException("이미 분석 결과가 존재합니다. 사용자는 한 번만 분석할 수 있습니다.");
            }

            // 3. IncomeBracket 저장
            IncomeBracket incomeBracket = IncomeBracket.create(
                    requestDTO.familyNum(),
                    requestDTO.Salary().longValue(),
                    requestDTO.Pension().longValue(),
                    requestDTO.housing_type(),
                    requestDTO.Asset().longValue(),
                    requestDTO.Debt().longValue(),
                    requestDTO.Car_info(),
                    requestDTO.Disability(),
                    requestDTO.EmploymentStatus(),
                    requestDTO.pastSupported(),
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

        } catch (Exception e) {
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