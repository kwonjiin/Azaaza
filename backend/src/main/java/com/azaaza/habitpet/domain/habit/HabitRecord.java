package com.azaaza.habitpet.domain.habit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 하루 단위 습관 수행 기록.
 *
 * (habit_id, date) 유니크 제약으로 "같은 날 같은 습관은 한 번만 기록"을
 * DB 레벨에서 보장한다 — 동시 요청(더블 클릭, 중복 제출)에도 안전하려면
 * 애플리케이션 검증만으로는 부족하기 때문에 여기서 막는다.
 *
 * updatedAt이 없는 이유: 기록은 "그날 있었던 사실"이라 수정보다는
 * 삭제 후 재생성이 자연스럽다고 봐서 BaseTimeEntity 대신 createdAt만 둔다.
 */
@Getter
@Entity
@Table(
        name = "habit_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_habit_record_habit_date",
                columnNames = {"habit_id", "date"}
        ),
        indexes = {
                @Index(name = "idx_habit_record_date", columnList = "date")
        }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HabitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean completed;

    /** targetType == NUMBER인 습관에서 실제 수행량 (예: 450ml 마심). */
    @Column
    private Integer actualValue;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    private HabitRecord(Habit habit, LocalDate date, boolean completed, Integer actualValue) {
        this.habit = habit;
        this.date = date;
        this.completed = completed;
        this.actualValue = actualValue;
    }
}
