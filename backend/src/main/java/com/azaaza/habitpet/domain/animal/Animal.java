package com.azaaza.habitpet.domain.animal;

import com.azaaza.habitpet.domain.BaseTimeEntity;
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
 * 사용자의 습관 동반자.
 *
 * 레벨업 규칙은 엔티티 안에 캡슐화했다(gainExperience). 서비스 레이어가
 * "경험치를 얼마나 줄지"만 결정하고, "그래서 레벨이 오르는지"는 도메인 스스로
 * 판단하게 해서 규칙이 여러 서비스 메서드에 흩어지지 않도록 했다.
 */
@Getter
@Entity
@Table(name = "animals", indexes = {
        @Index(name = "idx_animal_user_id", columnList = "user_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Animal extends BaseTimeEntity {

    public static final int EXP_PER_LEVEL = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnimalType type;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int experience;

    @Builder
    private Animal(User user, String name, AnimalType type) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.level = 1;
        this.experience = 0;
    }

    /** 경험치를 더하고, 임계치를 넘으면 레벨을 올린다(여러 레벨 동시 상승도 처리). */
    public void gainExperience(int amount) {
        this.experience += amount;
        while (this.experience >= EXP_PER_LEVEL) {
            this.experience -= EXP_PER_LEVEL;
            this.level += 1;
        }
    }
}
