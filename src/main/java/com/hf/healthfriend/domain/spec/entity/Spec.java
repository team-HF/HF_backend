package com.hf.healthfriend.domain.spec.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Spec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spec_id")
    private Long specId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private LocalDate endDate = null;

    @Column(name = "is_current")
    private boolean isCurrent = false;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public Spec(Long specId) {
        this.specId = specId;
    }

    public Spec(Member member,
                LocalDate startDate,
                LocalDate endDate,
                boolean isCurrent,
                String title,
                String description) {
        this.member = member;
        member.getSpecs().add(this);
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
        this.title = title;
        this.description = description;
    }

    public void update(SpecDto specDto) {
        this.startDate = specDto.getStartDate();
        this.endDate = specDto.getEndDate();
        this.isCurrent = specDto.isCurrent();
        this.title = specDto.getTitle();
        this.description = specDto.getDescription();
    }

    public void update(LocalDate startDate, LocalDate endDate, boolean isCurrent, String title, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
        this.title = title;
        this.description = description;
    }

    public void invalidateEndDate() {
        this.endDate = null;
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public String toString() {
        return "Spec{" +
                "specId=" + specId +
                ", memberId=" + member.getId() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isCurrent=" + isCurrent +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
