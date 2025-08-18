package com.app.oldYoung.domain.chatAI.service;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.repository.ChatHistoryRepository;
import com.app.oldYoung.domain.incomesnapshot.port.IncomeSnapshotPort;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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
    String context = buildContext(userId);
    List<ChatMessage> history = loadHistory(userId);

    List<Message> aiMessages = buildPromptMessages(context, history, userText);

    String resp = chatClient.prompt()
        .messages(aiMessages)
        .call()
        .content();

    saveTurn(userId, userText, resp);

    return resp;
  }

  private String buildContext(Long userId) {
    return incomeSnapPort.findLatestByUserId(userId)
        .map(contextBuilder::build)
        .orElse(null);
  }

  private List<ChatMessage> loadHistory(Long userId) {
    List<ChatMessage> history = chatHistoryRepository.getHistory(userId);
    if (history.size() > 20) {
      return history.subList(history.size() - 20, history.size());
    }
    return history;
  }

  private List<Message> buildPromptMessages(String context, List<ChatMessage> history, String userText) {
    List<Message> aiMessages = new ArrayList<>();
    aiMessages.add(new SystemMessage("너는 노인 복지 도우미 챗봇이야. 모든 답변은 존댓말로, 과장/추측 없이 사실 기반으로 대답해."));

    if (context != null && !context.isBlank()) {
      aiMessages.add(new SystemMessage("사용자 맥락 정보: " + context));
    }

    history.forEach(m -> {
      if ("user".equals(m.role()) && m.content() != null) {
        aiMessages.add(new UserMessage(m.content()));
      } else if ("assistant".equals(m.role()) && m.content() != null) {
        aiMessages.add(new AssistantMessage(m.content()));
      }
    });

    if (userText != null && !userText.isBlank()) {
      aiMessages.add(new UserMessage(userText));
    }

    return aiMessages;
  }

  private void saveTurn(Long userId, String userText, String resp) {
    if (userText != null && !userText.isBlank()) {
      chatHistoryRepository.appendMessage(userId, ChatMessage.user(userText));
    }
    if (resp != null && !resp.isBlank()) {
      chatHistoryRepository.appendMessage(userId, ChatMessage.assistant(resp));
    }
  }

  public void startNewSession(Long userId) {
    chatHistoryRepository.clearHistory(userId);
  }

}
