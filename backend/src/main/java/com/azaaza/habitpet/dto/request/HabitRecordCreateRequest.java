package com.azaaza.habitpet.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record HabitRecordCreateRequest(
        @NotNull LocalDate date,
        @NotNull Boolean completed,
        @PositiveOrZero Integer actualValue
) {
}
