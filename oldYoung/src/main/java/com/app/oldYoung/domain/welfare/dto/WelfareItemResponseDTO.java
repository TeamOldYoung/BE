package com.app.oldYoung.domain.welfare.dto;

import com.app.oldYoung.domain.welfare.entity.WelfareItem;

public record WelfareItemResponseDTO(
        Long id,
        String title,
        String subscript,
        String period,
        String agency,
        String contact,
        String applicant,
        String link,
        String city,
        Integer age
) {
    public static WelfareItemResponseDTO from(WelfareItem welfareItem) {
        return new WelfareItemResponseDTO(
                welfareItem.getId(),
                welfareItem.getTitle(),
                welfareItem.getSubscript(),
                welfareItem.getPeriod(),
                welfareItem.getAgency(),
                welfareItem.getContact(),
                welfareItem.getApplicant(),
                welfareItem.getLink(),
                welfareItem.getCity(),
                welfareItem.getAge()
        );
    }
}