package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitRecord;
import com.azaaza.habitpet.domain.user.User;
import com.azaaza.habitpet.dto.request.HabitRecordCreateRequest;
import com.azaaza.habitpet.dto.response.HabitRecordResponse;
import com.azaaza.habitpet.dto.response.HabitRecordSimpleResponse;
import com.azaaza.habitpet.global.exception.BusinessException;
import com.azaaza.habitpet.global.exception.ErrorCode;
import com.azaaza.habitpet.repository.HabitRecordRepository;
import com.azaaza.habitpet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 수행 기록 생성 + 포인트/경험치 지급을 한 트랜잭션으로 묶는다.
 * "기록 저장"과 "보상 지급"이 분리되면 기록만 남고 보상이 안 나가는 정합성 문제가 생길 수 있어서,
 * 이 서비스가 그 트랜잭션 경계를 명시적으로 책임진다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitRecordService {

    // 스펙의 "습관 1회 수행 시 +10/+10" 규칙. targetType/targetValue에 따른 가중치는
    // MVP 범위 밖이라 상수로 고정 — 나중에 규칙이 복잡해지면 정책 객체(RewardPolicy)로 분리하면 된다.
    private static final int POINT_PER_COMPLETION = 10;
    private static final int EXP_PER_COMPLETION = 10;

    private final HabitRecordRepository habitRecordRepository;
    private final UserRepository userRepository;
    private final HabitService habitService;

    @Transactional
    public HabitRecordResponse create(Long userId, Long habitId, HabitRecordCreateRequest request) {
        Habit habit = habitService.findOwnedHabit(userId, habitId);

        if (habitRecordRepository.existsByHabit_IdAndDate(habitId, request.date())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RECORD);
        }

        HabitRecord record = HabitRecord.builder()
                .habit(habit)
                .date(request.date())
                .completed(request.completed())
                .actualValue(request.actualValue())
                .build();
        habitRecordRepository.save(record);

        // 완수하지 못한 기록(사실 그대로 남기는 것도 의미가 있다)에는 보상을 주지 않는다 —
        // "동기부여 장치"라는 컨셉상 포인트가 곧 신뢰도를 담보해야 하기 때문.
        int earnedPoint = 0;
        int earnedExp = 0;
        if (request.completed()) {
            earnedPoint = POINT_PER_COMPLETION;
            earnedExp = EXP_PER_COMPLETION;

            User user = userRepository.getReferenceById(userId);
            user.addPoint(earnedPoint);

            Animal animal = habit.getAnimal();
            animal.gainExperience(earnedExp);
        }

        return HabitRecordResponse.of(record, earnedPoint, earnedExp, habit.getAnimal());
    }

    public List<HabitRecordSimpleResponse> getRecords(Long userId, Long habitId, LocalDate from, LocalDate to) {
        habitService.findOwnedHabit(userId, habitId); // 소유권 검증
        return habitRecordRepository.findAllByHabit_IdAndDateBetweenOrderByDateDesc(habitId, from, to).stream()
                .map(HabitRecordSimpleResponse::from)
                .toList();
    }
}
