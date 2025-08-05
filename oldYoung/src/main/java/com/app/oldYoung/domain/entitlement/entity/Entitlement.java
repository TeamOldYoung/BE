package com.app.oldYoung.domain.entitlement.entity;

import com.app.oldYoung.global.common.BaseEntity;
import com.app.oldYoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entitlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Entitlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "subscript")
    private String subscript;

    @Column(name = "period")
    private String period;

    @Column(name = "agency")
    private String agency;

    @Column(name = "contact")
    private String contact;

    @Column(name = "applicant")
    private String applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
