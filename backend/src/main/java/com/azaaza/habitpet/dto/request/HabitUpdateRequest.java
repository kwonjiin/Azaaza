package com.azaaza.habitpet.dto.request;

import com.azaaza.habitpet.domain.habit.HabitCategory;
import com.azaaza.habitpet.domain.habit.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** animalId는 여기 없음 — MVP에서는 습관을 다른 동물로 옮기는 기능을 지원하지 않는다 (API.md 참고). */
public record HabitUpdateRequest(
        @NotBlank @Size(max = 50) String title,
        @Size(max = 200) String description,
        @NotNull HabitCategory category,
        @NotNull TargetType targetType,
        @Positive Integer targetValue,
        @Size(max = 10) String targetUnit
) {
}
