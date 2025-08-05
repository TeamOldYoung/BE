package com.app.oldYoung.domain.user.entity;

import com.app.oldYoung.domain.entitlement.entity.Entitlement;
import com.app.oldYoung.domain.harume.entity.Harume;
import com.app.oldYoung.domain.incomebracket.entity.IncomeBracket;
import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import com.app.oldYoung.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "membername")
    private String membername;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IncomeBracket incomeBracket;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IncomeSnapshot incomeSnapshot;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Harume harume;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Entitlement> entitlements;
}
