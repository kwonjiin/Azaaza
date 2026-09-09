package com.azaaza.habitpet.domain.user;

import com.azaaza.habitpet.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 서비스 이용자.
 *
 * point: 습관 수행으로 누적된 총 포인트. 동물의 experience와는 별개로,
 * "사람 쪽"의 자기계발 성과 지표로 대시보드에 노출한다.
 */
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt 등으로 해싱된 값만 저장

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false)
    private int point;

    @Builder
    private User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.point = 0;
    }

    public void addPoint(int amount) {
        this.point += amount;
    }
}
