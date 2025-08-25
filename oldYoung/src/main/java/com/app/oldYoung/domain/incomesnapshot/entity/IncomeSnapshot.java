package com.app.oldYoung.domain.incomesnapshot.entity;

import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incomesnapshot")
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

    @Column(name = "incomebreaket_id")
    private Long incomeBreaketsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public static IncomeSnapshot create(Long incomeEval, Long assetEval, Long totalIncome,
                                      Long midRatio, Long expBracket, Long incomeBreaketsId, User user) {
        IncomeSnapshot snapshot = new IncomeSnapshot();
        snapshot.incomeEval = incomeEval;
        snapshot.assetEval = assetEval;
        snapshot.totalIncome = totalIncome;
        snapshot.midRatio = midRatio;
        snapshot.expBracket = expBracket;
        snapshot.incomeBreaketsId = incomeBreaketsId;
        snapshot.user = user;
        return snapshot;
    }
}
