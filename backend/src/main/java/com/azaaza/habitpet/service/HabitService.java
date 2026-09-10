package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitCategory;
import com.azaaza.habitpet.domain.habit.TargetType;
import com.azaaza.habitpet.dto.request.HabitCreateRequest;
import com.azaaza.habitpet.dto.request.HabitUpdateRequest;
import com.azaaza.habitpet.dto.response.HabitResponse;
import com.azaaza.habitpet.global.exception.BusinessException;
import com.azaaza.habitpet.global.exception.ErrorCode;
import com.azaaza.habitpet.repository.HabitRecordRepository;
import com.azaaza.habitpet.repository.HabitRepository;
import com.azaaza.habitpet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;
    private final UserRepository userRepository;
    private final AnimalService animalService;

    @Transactional
    public HabitResponse create(Long userId, HabitCreateRequest request) {
        validateTarget(request.targetType(), request.targetValue());
        // findOwnedAnimal이 소유권까지 검증 — 다른 사용자의 animalId를 넣으면 404.
        Animal animal = animalService.findOwnedAnimal(userId, request.animalId());
        var userRef = userRepository.getReferenceById(userId);

        Habit habit = Habit.builder()
                .user(userRef)
                .animal(animal)
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .targetType(request.targetType())
                .targetValue(request.targetValue())
                .targetUnit(request.targetUnit())
                .build();

        return HabitResponse.from(habitRepository.save(habit));
    }

    public List<HabitResponse> search(Long userId, HabitCategory category, Long animalId) {
        return habitRepository.search(userId, category, animalId).stream()
                .map(HabitResponse::from)
                .toList();
    }

    public HabitResponse getDetail(Long userId, Long habitId) {
        return HabitResponse.from(findOwnedHabit(userId, habitId));
    }

    @Transactional
    public HabitResponse update(Long userId, Long habitId, HabitUpdateRequest request) {
        validateTarget(request.targetType(), request.targetValue());
        Habit habit = findOwnedHabit(userId, habitId);
        habit.update(request.title(), request.description(), request.category(),
                request.targetType(), request.targetValue(), request.targetUnit());
        return HabitResponse.from(habit); // 영속 상태 엔티티라 트랜잭션 커밋 시 dirty checking으로 반영
    }

    @Transactional
    public void delete(Long userId, Long habitId) {
        Habit habit = findOwnedHabit(userId, habitId);
        // Habit -> HabitRecord에 cascade를 안 걸어뒀기 때문에(양방향 연관관계를 늘리지 않으려는
        // 선택) FK 제약을 그대로 두면 기록이 하나라도 있는 습관은 삭제가 실패한다 — 자식부터 지운다.
        habitRecordRepository.deleteAllByHabit_Id(habitId);
        habitRepository.delete(habit);
    }

    Habit findOwnedHabit(Long userId, Long habitId) {
        return habitRepository.findByIdAndUser_Id(habitId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HABIT_NOT_FOUND));
    }

    private void validateTarget(TargetType targetType, Integer targetValue) {
        boolean numberNeedsValue = targetType == TargetType.NUMBER && targetValue == null;
        boolean checkShouldNotHaveValue = targetType == TargetType.CHECK && targetValue != null;
        if (numberNeedsValue || checkShouldNotHaveValue) {
            throw new BusinessException(ErrorCode.INVALID_HABIT_TARGET);
        }
    }
}
