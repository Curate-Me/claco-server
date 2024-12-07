package com.curateme.claco.concert.domain.entity;

import com.curateme.claco.global.entity.BaseEntity;
import com.curateme.claco.review.domain.entity.TicketReview;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Concert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id")
    private String mt20id;

    @Column(name = "concert_name")
    private String prfnm;

    @Column(name = "start_date")
    private LocalDate prfpdfrom;

    @Column(name = "end_date")
    private LocalDate prfpdto;

    @Column(name = "facility_name")
    private String fcltynm;

    @Column(name = "poster")
    private String poster;

    @Column(name = "area")
    private String area;

    @Column(name = "genre")
    private String genrenm;

    @Column(name = "openrun")
    private String openrun;

    @Column(name = "status")
    private String prfstate;

    @Column(name = "cast")
    private String prfcast;

    @Column(name = "crew")
    private String prfcrew;

    @Column(name = "runtime")
    private String prfruntime;

    @Column(name = "age")
    private String prfage;

    @Column(name = "company_name")
    private String entrpsnm;

    @Column(name = "company_namep")
    private String entrpsnmP;

    @Column(name = "company_namea")
    private String entrpsnmA;

    @Column(name = "company_nameh")
    private String entrpsnmH;

    @Column(name = "company_names")
    private String entrpsnmS;

    @Column(name = "seat_guidance")
    private String pcseguidance;

    @Column(name = "visit")
    private String visit;

    @Column(name = "child")
    private String child;

    @Column(name = "daehakro")
    private String daehakro;

    @Column(name = "festival")
    private String festival;

    @Column(name = "musical_license")
    private String musicallicense;

    @Column(name = "musical_create")
    private String musicalcreate;

    @Column(name = "update_date")
    private String updatedate;

    @Column(name = "schedule_guidance", length = 1000)
    private String dtguidance;

    @Column(name = "introduction")
    private String styurl;

    @Column(name = "summary", length = 1500)
    private String summary;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TicketReview> ticketReview;

    @ElementCollection
    @CollectionTable(name = "concert_category", joinColumns = @JoinColumn(name = "concert_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "score")
    private Map<String, Double> categories;
}
