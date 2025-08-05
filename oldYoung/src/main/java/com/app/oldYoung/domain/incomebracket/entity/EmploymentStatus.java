package com.app.oldYoung.domain.incomebracket.entity;

public enum EmploymentStatus {
    EMPLOYED("취업"),
    UNEMPLOYED("미취업"),
    SELF_EMPLOYED("자영업"),
    RETIRED("퇴직");

    private final String description;

    EmploymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
