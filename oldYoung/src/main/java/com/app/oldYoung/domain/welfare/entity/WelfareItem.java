package com.app.oldYoung.domain.welfare.entity;

import com.app.oldYoung.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Entity
@Table(
        name = "welfare_item",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_welfare_item_title_city_age", columnNames = {"title", "city", "age"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String subscript;

    @Column(columnDefinition = "text")
    private String period;

    @Column(columnDefinition = "text")
    private String agency;

    @Column(columnDefinition = "text")
    private String contact;

    @Column(columnDefinition = "text")
    private String applicant;

    @Column(columnDefinition = "text")
    private String link;

    @Column(columnDefinition = "text")
    private String city;

    @Column(name = "age")
    private Integer age;
    
    @Builder
    public WelfareItem(String title, String subscript, String period, String agency, 
                      String contact, String applicant, String link, String city, Integer age) {
        this.title = title;
        this.subscript = subscript;
        this.period = period;
        this.agency = agency;
        this.contact = contact;
        this.applicant = applicant;
        this.link = link;
        this.city = city;
        this.age = age;
    }
}