package com.app.oldYoung.domain.harume.entity;

public enum MessageType {
    GREETING("인사"),
    QUESTION("질문"),
    ANSWER("답변"),
    NOTIFICATION("알림");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
