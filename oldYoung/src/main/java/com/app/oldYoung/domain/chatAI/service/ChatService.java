package com.app.oldYoung.domain.chatAI.service;

import com.app.oldYoung.domain.incomesnapshot.port.IncomeSnapshotPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

  private final IncomeSnapshotPort incomeSnapPort;
  private final ContextBuilder contextBuilder;
  private final ChatClient chatClient;

  public ChatService(IncomeSnapshotPort incomeSnapshotPort, ContextBuilder contextBuilder, ChatClient chatClient) {
    this.incomeSnapPort = incomeSnapshotPort;
    this.contextBuilder = contextBuilder;
    this.chatClient = chatClient;
  }

  @Transactional(readOnly = true)
  public String reply(Long userId, String userText){
    var snapshot = incomeSnapPort.findLatestByUserId(userId);
    var context = contextBuilder.build(snapshot.orElse(null));
    return chatClient.prompt()
        .system("너는 노인들의 복지 도우미 챗봇으로, 대답은 존댓말을 사용하고 진실되게 대답해야한다.")
        .user(context)
        .user(userText)
        .call().content();
  }

}
