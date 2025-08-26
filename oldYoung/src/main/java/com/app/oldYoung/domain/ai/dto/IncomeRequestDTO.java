package com.app.oldYoung.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeRequestDTO {
    @JsonProperty("familyNum")
    private Integer familyNum;

    @JsonProperty("Salary")
    private Integer Salary;

    @JsonProperty("Pension")
    private Integer Pension;

    @JsonProperty("housing_type")
    private String housing_type;

    @JsonProperty("Asset")
    private Integer Asset;

    @JsonProperty("Debt")
    private Integer Debt;

    @JsonProperty("Car_info")
    private String Car_info;

    @JsonProperty("Disability")
    private Boolean Disability;

    @JsonProperty("EmploymentStatus")
    private String EmploymentStatus;

    @JsonProperty("pastSupported")
    private Boolean pastSupported;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private String birthDate;

    private String address;
}
