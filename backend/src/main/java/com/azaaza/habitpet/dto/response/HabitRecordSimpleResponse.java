package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.habit.HabitRecord;

import java.time.LocalDate;

public record HabitRecordSimpleResponse(Long id, LocalDate date, boolean completed, Integer actualValue) {

    public static HabitRecordSimpleResponse from(HabitRecord record) {
        return new HabitRecordSimpleResponse(record.getId(), record.getDate(), record.isCompleted(), record.getActualValue());
    }
}
