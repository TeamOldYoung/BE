package com.app.oldYoung.domain.chatAI.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.repository.ChatHistoryRepository;
import com.app.oldYoung.domain.incomesnapshot.port.IncomeSnapshotPort;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

  @Mock
  private ChatClient chatClient;
  @Mock
  private ChatClient.ChatClientRequestSpec requestSpec;
  @Mock
  private ChatClient.CallResponseSpec callResponseSpec;
  @Mock
  private IncomeSnapshotPort incomeSnapPort;
  @Mock
  private ContextBuilder contextBuilder;
  @Mock
  private ChatHistoryRepository chatHistoryRepository;

  @InjectMocks
  private ChatService chatService;

  @Test
  void reply_ShouldReturnResponseAndSaveHistory() {
    // given
    Long userId = 1L;
    String userText = "안녕하세요";

    given(chatHistoryRepository.getHistory(userId)).willReturn(List.of());

    // chain stubbing
    given(chatClient.prompt()).willReturn(requestSpec);
    given(requestSpec.messages(anyList())).willReturn(requestSpec);
    given(requestSpec.call()).willReturn(callResponseSpec);
    given(callResponseSpec.content()).willReturn("반갑습니다!");

    // when
    String result = chatService.reply(userId, userText);

    // then
    assertThat(result).isEqualTo("반갑습니다!");
    then(chatHistoryRepository).should().appendMessage(userId, ChatMessage.user(userText));
    then(chatHistoryRepository).should().appendMessage(userId, ChatMessage.assistant("반갑습니다!"));
  }

}
