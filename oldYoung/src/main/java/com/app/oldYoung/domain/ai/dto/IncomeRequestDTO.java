package com.app.oldYoung.domain.ai.dto;

public record IncomeRequestDTO(
        Integer familyNum,
        Integer Salary,
        Integer Pension,
        String housing_type,
        Integer Asset,
        Integer Debt,
        String Car_info,
        Boolean Disability,
        String EmploymentStatus,
        Boolean pastSupported
) {
}