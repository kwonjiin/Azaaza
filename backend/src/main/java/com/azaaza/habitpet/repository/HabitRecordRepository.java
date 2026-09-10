package com.azaaza.habitpet.repository;

import com.azaaza.habitpet.domain.habit.HabitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitRecordRepository extends JpaRepository<HabitRecord, Long> {

    boolean existsByHabit_IdAndDate(Long habitId, LocalDate date);

    Optional<HabitRecord> findByHabit_IdAndDate(Long habitId, LocalDate date);

    List<HabitRecord> findAllByHabit_IdAndDateBetweenOrderByDateDesc(Long habitId, LocalDate from, LocalDate to);

    // 대시보드 주간 수행률 계산용: 여러 habit을 한 번에 조회 (habitId in (...))
    List<HabitRecord> findAllByHabit_IdInAndDateBetween(List<Long> habitIds, LocalDate from, LocalDate to);

    // 습관 삭제 시 먼저 호출 — Habit에 cascade를 안 걸어뒀기 때문에(양방향 연관관계를 늘리지
    // 않으려는 선택) 서비스가 명시적으로 자식 레코드부터 지운다. deleteBy 파생 쿼리라 @Modifying 불필요.
    void deleteAllByHabit_Id(Long habitId);
}
