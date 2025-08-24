package com.app.oldYoung.domain.welfare.dto;

import com.app.oldYoung.domain.welfare.entity.WelfareItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WelfareItemRequestDTO(
        @NotBlank(message = "제목은 필수입니다")
        String title,
        
        String subscript,
        
        String period,
        
        String agency,
        
        String contact,
        
        String applicant,
        
        String link,
        
        @NotBlank(message = "지역명은 필수입니다")
        String city,
        
        @NotNull(message = "나이 구분은 필수입니다 (0: 청년, 1: 노인)")
        Integer age
) {
    public WelfareItem toEntity() {
        return WelfareItem.builder()
                .title(title)
                .subscript(subscript)
                .period(period)
                .agency(agency)
                .contact(contact)
                .applicant(applicant)
                .link(link)
                .city(city)
                .age(age)
                .build();
    }
}