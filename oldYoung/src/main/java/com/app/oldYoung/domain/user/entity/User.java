package com.app.oldYoung.domain.user.entity;

import com.app.oldYoung.domain.entitlement.entity.Entitlement;
import com.app.oldYoung.domain.chatAI.entity.Harume;
import com.app.oldYoung.domain.incomebracket.entity.IncomeBracket;
import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import com.app.oldYoung.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
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

    @Column(name = "password")
    private String password;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IncomeBracket incomeBracket;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IncomeSnapshot incomeSnapshot;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Harume harume;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Entitlement> entitlements;

    @Builder
    public User(String membername, String email, String password, String provider, String providerId) {
        this.membername = membername;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
    }
}
