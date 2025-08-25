package com.app.oldYoung.domain.incomebracket.entity;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incomebreaket")
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

    @Column(name = "employment_status")
    private String employmentStatus;

    @Column(name = "past_supported")
    private Boolean pastSupported;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public static IncomeBracket create(Integer familyNum, Long salary, Long pension, String housingType,
                                     Long asset, Long debt, String carInfo, Boolean disability,
                                     String employmentStatus, Boolean pastSupported, User user) {
        IncomeBracket incomeBracket = new IncomeBracket();
        incomeBracket.familyNum = familyNum;
        incomeBracket.salary = salary;
        incomeBracket.pension = pension;
        incomeBracket.housingType = housingType;
        incomeBracket.asset = asset;
        incomeBracket.debt = debt;
        incomeBracket.carInfo = carInfo;
        incomeBracket.disability = disability;
        incomeBracket.employmentStatus = employmentStatus;
        incomeBracket.pastSupported = pastSupported;
        incomeBracket.user = user;
        return incomeBracket;
    }
}
