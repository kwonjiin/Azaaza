package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitCategory;
import com.azaaza.habitpet.domain.habit.TargetType;

public record HabitResponse(
        Long id, Long animalId, String animalName, String title, String description,
        HabitCategory category, TargetType targetType, Integer targetValue, String targetUnit
) {

    public static HabitResponse from(Habit habit) {
        return new HabitResponse(
                habit.getId(), habit.getAnimal().getId(), habit.getAnimal().getName(),
                habit.getTitle(), habit.getDescription(), habit.getCategory(),
                habit.getTargetType(), habit.getTargetValue(), habit.getTargetUnit()
        );
    }
}
