package com.app.oldYoung.domain.chatAI.service;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.repository.ChatHistoryRepository;
import com.app.oldYoung.domain.incomesnapshot.port.IncomeSnapshotPort;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

  private final IncomeSnapshotPort incomeSnapPort;
  private final ContextBuilder contextBuilder;
  private final ChatClient chatClient;
  private final ChatHistoryRepository chatHistoryRepository;

  public ChatService(IncomeSnapshotPort incomeSnapshotPort, ContextBuilder contextBuilder, ChatClient chatClient,ChatHistoryRepository chatHistoryRepository) {
    this.incomeSnapPort = incomeSnapshotPort;
    this.contextBuilder = contextBuilder;
    this.chatClient = chatClient;
    this.chatHistoryRepository = chatHistoryRepository;
  }

  @Transactional(readOnly = true)
  public String reply(Long userId, String userText) {
    // 1) 컨텍스트 생성
    var snapshot = incomeSnapPort.findLatestByUserId(userId).orElse(null);
    String context = contextBuilder.build(snapshot);

    // 2) 기존 히스토리 불러오기 (필요시 N턴 제한)
    List<ChatMessage> history = chatHistoryRepository.getHistory(userId);
    if (history.size() > 20) { // 예: 최근 20개만 사용
      history = history.subList(history.size() - 20, history.size());
    }

    // 3) spring-ai 메시지로 변환 (한 번에 담아 전달)
    List<org.springframework.ai.chat.messages.Message> aiMessages = new ArrayList<>();

    // system & context는 항상 제일 앞에
    aiMessages.add(new org.springframework.ai.chat.messages.SystemMessage(
        "너는 노인 복지 도우미 챗봇이야. "
            + "모든 답변은 존댓말로, 과장/추측 없이 사실 기반으로 대답해."
    ));

    if (context != null && !context.isBlank()) {
      aiMessages.add(new SystemMessage("사용자 맥락 정보: " + context));
    }
    // 기존 히스토리 반영
    for (ChatMessage m : history) {
      if ("user".equals(m.role())) {
        aiMessages.add(new org.springframework.ai.chat.messages.UserMessage(m.content()));
      } else if ("assistant".equals(m.role())) {
        aiMessages.add(new org.springframework.ai.chat.messages.AssistantMessage(m.content()));
      }
    }

    // 이번 유저 메시지 추가 (호출 전에 목록에도 포함!)
    aiMessages.add(new org.springframework.ai.chat.messages.UserMessage(userText));

    // 4) 모델 호출 (messages를 한 번만 세팅)
    String resp = chatClient
        .prompt()
        .messages(aiMessages)
        .call()
        .content();

    // 5) Redis에 이번 턴 저장
    chatHistoryRepository.appendMessage(userId, ChatMessage.user(userText));
    chatHistoryRepository.appendMessage(userId, ChatMessage.assistant(resp));

    return resp;
  }

}
