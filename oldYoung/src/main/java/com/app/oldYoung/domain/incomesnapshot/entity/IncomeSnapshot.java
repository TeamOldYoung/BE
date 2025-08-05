package com.app.oldYoung.domain.incomesnapshot.entity;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "income_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeSnapshot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "income_eval")
    private Long incomeEval;

    @Column(name = "asset_eval")
    private Long assetEval;

    @Column(name = "total_income")
    private Long totalIncome;

    @Column(name = "mid_ratio")
    private Long midRatio;

    @Column(name = "exp_bracket")
    private Long expBracket;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
