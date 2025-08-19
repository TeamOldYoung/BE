package com.app.oldYoung.domain.chatAI.entity;

import com.app.oldYoung.global.common.entity.BaseEntity;
import com.app.oldYoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

@Entity
@Table(name = "ChatAI")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Harume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
