package com.app.oldYoung.domain.chatAI.service;

import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import org.springframework.stereotype.Component;

@Component
public class ContextBuilder {

  public String build(IncomeSnapshot snapshot) {
    if (snapshot == null) {
      return "현재 사용자의 소득·자산 정보가 없습니다.";
    }

    StringBuilder context = new StringBuilder();
    context.append("사용자의 최신 소득 정보")
        .append("소득평가액: ").append(snapshot.getIncomeEval()).append("원, ")
        .append("재산평가액: ").append(snapshot.getAssetEval()).append("원, ")
        .append("총소득: ").append(snapshot.getTotalIncome()).append("원.\n")
        .append("중위소득 대비 비율: ").append(snapshot.getMidRatio()).append("%,\n")
        .append("추정 소득 분위: ").append(snapshot.getExpBracket()).append("분위.\n")
        .append("이 정보를 참고해서 적절한 복지 혜택을 추천해주세요.");

    return context.toString();
  }

}
