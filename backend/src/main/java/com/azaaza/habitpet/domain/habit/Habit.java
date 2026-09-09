package com.azaaza.habitpet.domain.habit;

import com.azaaza.habitpet.domain.BaseTimeEntity;
import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 관리하는 개별 습관.
 *
 * 설계 메모:
 * - userId를 animal을 거치지 않고 직접 들고 있다(비정규화). "내 습관 목록"이
 *   가장 빈번한 조회이기 때문에 Animal을 조인하지 않고 habit.user_id 인덱스로
 *   바로 조회하려는 의도다. 대신 habit.user와 habit.animal.user가 항상 같아야
 *   한다는 불변식이 생기는데, 이건 DB 제약이 아니라 HabitService.create()에서
 *   생성 시점에 검증한다(코드 리뷰/면접에서 "왜 정규화를 깨뜨렸는가"에 대한 답).
 * - animal 1마리 : habit N개 (1:N)로 설계했다. 동물을 처음에 하나만 만들고
 *   여러 습관을 그 동물에 몰아서 키우는 흐름이 자연스럽고, 습관마다 동물을
 *   새로 만들게 하면 홈 화면이 동물로 뒤덮이는 문제가 있었다.
 * - targetValue/targetUnit을 하나의 문자열("500ml")이 아니라 숫자+단위로
 *   분리했다. 통계(주간 합계, 평균 달성률)를 내려면 숫자가 필요해서다.
 */
@Getter
@Entity
@Table(name = "habits", indexes = {
        @Index(name = "idx_habit_user_id", columnList = "user_id"),
        @Index(name = "idx_habit_animal_id", columnList = "animal_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Habit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HabitCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TargetType targetType;

    /** targetType == NUMBER 일 때만 의미 있음 (예: 500, 60). */
    @Column
    private Integer targetValue;

    /** targetType == NUMBER 일 때만 의미 있음 (예: "ml", "분"). */
    @Column(length = 10)
    private String targetUnit;

    @Builder
    private Habit(User user, Animal animal, String title, String description,
                  HabitCategory category, TargetType targetType,
                  Integer targetValue, String targetUnit) {
        this.user = user;
        this.animal = animal;
        this.title = title;
        this.description = description;
        this.category = category;
        this.targetType = targetType;
        this.targetValue = targetValue;
        this.targetUnit = targetUnit;
    }

    public void update(String title, String description, HabitCategory category,
                        TargetType targetType, Integer targetValue, String targetUnit) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.targetType = targetType;
        this.targetValue = targetValue;
        this.targetUnit = targetUnit;
    }
}
