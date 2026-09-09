package com.azaaza.habitpet.repository;

import com.azaaza.habitpet.domain.animal.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findAllByUser_Id(Long userId);

    // 소유권 검증까지 쿼리 조건에 포함 — "존재하지만 남의 것"과 "존재하지 않음"을 구분하지 않고
    // 둘 다 404로 응답하기 위해 서비스에서 항상 이 메서드를 사용한다 (IDOR 방지).
    Optional<Animal> findByIdAndUser_Id(Long id, Long userId);
}
