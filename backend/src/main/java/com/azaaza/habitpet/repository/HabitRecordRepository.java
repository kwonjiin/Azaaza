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
}
