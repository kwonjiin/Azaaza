package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitRecord;
import com.azaaza.habitpet.domain.user.User;
import com.azaaza.habitpet.dto.response.AnimalResponse;
import com.azaaza.habitpet.dto.response.DashboardResponse;
import com.azaaza.habitpet.global.exception.BusinessException;
import com.azaaza.habitpet.global.exception.ErrorCode;
import com.azaaza.habitpet.repository.AnimalRepository;
import com.azaaza.habitpet.repository.HabitRecordRepository;
import com.azaaza.habitpet.repository.HabitRepository;
import com.azaaza.habitpet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 대시보드는 "여러 테이블을 한 화면에 묶어 보여주는" 전형적인 read-model이라
 * 다른 도메인 서비스에 로직을 얹지 않고 별도 서비스로 뒀다 — Habit/Animal 서비스가
 * 대시보드 전용 필드(todayCompleted, weeklyRate) 계산 책임까지 지지 않도록 분리.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final int WEEK_WINDOW_DAYS = 7;

    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;

    public DashboardResponse get(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        List<AnimalResponse> animals = animalRepository.findAllByUser_Id(userId).stream()
                .map(AnimalResponse::from)
                .toList();

        List<Habit> habits = habitRepository.findAllByUser_Id(userId);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(WEEK_WINDOW_DAYS - 1L);

        List<Long> habitIds = habits.stream().map(Habit::getId).toList();
        Map<Long, List<HabitRecord>> recordsByHabit = habitIds.isEmpty()
                ? Map.of()
                : habitRecordRepository.findAllByHabit_IdInAndDateBetween(habitIds, weekStart, today).stream()
                        .collect(Collectors.groupingBy(r -> r.getHabit().getId()));

        List<DashboardResponse.HabitSummary> habitSummaries = habits.stream()
                .map(habit -> toSummary(habit, recordsByHabit.getOrDefault(habit.getId(), List.of()), today))
                .toList();

        double weeklyAverageRate = habitSummaries.isEmpty() ? 0.0
                : habitSummaries.stream().mapToDouble(DashboardResponse.HabitSummary::weeklyRate).average().orElse(0.0);

        return new DashboardResponse(
                new DashboardResponse.UserSummary(user.getNickname(), user.getPoint()),
                animals,
                habitSummaries,
                new DashboardResponse.Stats(round(weeklyAverageRate), user.getPoint())
        );
    }

    private DashboardResponse.HabitSummary toSummary(Habit habit, List<HabitRecord> weekRecords, LocalDate today) {
        boolean todayCompleted = weekRecords.stream()
                .anyMatch(r -> r.getDate().equals(today) && r.isCompleted());

        long completedDays = weekRecords.stream().filter(HabitRecord::isCompleted).count();
        double weeklyRate = round((double) completedDays / WEEK_WINDOW_DAYS);

        return new DashboardResponse.HabitSummary(
                habit.getId(), habit.getTitle(), habit.getCategory(),
                habit.getAnimal().getId(), habit.getAnimal().getName(),
                todayCompleted, weeklyRate
        );
    }

    private double round(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
