package com.azaaza.habitpet.dto.request;

import com.azaaza.habitpet.domain.habit.HabitCategory;
import com.azaaza.habitpet.domain.habit.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * targetValue/targetUnit은 targetType == NUMBER일 때만 채워야 한다는 규칙은
 * Bean Validation(필드 단위)만으로 표현하기 애매해서(교차 필드 검증) 여기서는 형식만 검증하고,
 * "NUMBER인데 targetValue가 없다" 같은 의미적 검증은 HabitService에서 처리한다.
 */
public record HabitCreateRequest(
        @NotNull Long animalId,
        @NotBlank @Size(max = 50) String title,
        @Size(max = 200) String description,
        @NotNull HabitCategory category,
        @NotNull TargetType targetType,
        @Positive Integer targetValue,
        @Size(max = 10) String targetUnit
) {
}
