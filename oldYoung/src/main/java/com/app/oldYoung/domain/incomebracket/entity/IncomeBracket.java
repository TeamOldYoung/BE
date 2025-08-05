package com.app.oldYoung.domain.incomebracket.entity;

import com.app.oldYoung.common.entity.BaseEntity;
import com.app.oldYoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "income_brackets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeBracket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_num")
    private Integer familyNum;

    @Column(name = "salary")
    private Long salary;

    @Column(name = "pension")
    private Long pension;

    @Column(name = "housing_type")
    private String housingType;

    @Column(name = "asset")
    private Long asset;

    @Column(name = "debt")
    private Long debt;

    @Column(name = "car_info")
    private String carInfo;

    @Column(name = "disability")
    private Boolean disability;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Column(name = "past_supported")
    private Boolean pastSupported;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public IncomeBracket(Integer familyNum, Long salary, Long pension, String housingType,
                        Long asset, Long debt, String carInfo, Boolean disability,
                        EmploymentStatus employmentStatus, Boolean pastSupported, User user) {
        this.familyNum = familyNum;
        this.salary = salary;
        this.pension = pension;
        this.housingType = housingType;
        this.asset = asset;
        this.debt = debt;
        this.carInfo = carInfo;
        this.disability = disability;
        this.employmentStatus = employmentStatus;
        this.pastSupported = pastSupported;
        this.user = user;
    }
}
