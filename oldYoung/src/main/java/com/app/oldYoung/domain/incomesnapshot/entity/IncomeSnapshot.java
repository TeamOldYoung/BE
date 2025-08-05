package com.app.oldYoung.domain.incomesnapshot.entity;

import com.app.oldYoung.common.entity.BaseEntity;
import com.app.oldYoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    public IncomeSnapshot(Long incomeEval, Long assetEval, Long totalIncome,
                         Long midRatio, Long expBracket, User user) {
        this.incomeEval = incomeEval;
        this.assetEval = assetEval;
        this.totalIncome = totalIncome;
        this.midRatio = midRatio;
        this.expBracket = expBracket;
        this.user = user;
    }
}
