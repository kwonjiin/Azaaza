package com.azaaza.habitpet.domain.diary;

import com.azaaza.habitpet.domain.animal.Animal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 동물이 그날의 습관 수행 결과를 바탕으로 남기는 일기.
 * (animal_id, date) 유니크 제약으로 "동물당 하루 1개"를 DB에서 보장한다.
 * 생성 로직(템플릿 조합)은 DiaryService에서 다룬다 — 3/6단계 참고.
 */
@Getter
@Entity
@Table(
        name = "diaries",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_diary_animal_date",
                columnNames = {"animal_id", "date"}
        )
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private LocalDate date;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    private Diary(Animal animal, LocalDate date, String content) {
        this.animal = animal;
        this.date = date;
        this.content = content;
    }
}
