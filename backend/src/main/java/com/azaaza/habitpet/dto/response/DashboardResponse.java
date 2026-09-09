package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.habit.HabitCategory;

import java.util.List;

public record DashboardResponse(
        UserSummary user,
        List<AnimalResponse> animals,
        List<HabitSummary> habits,
        Stats stats
) {

    public record UserSummary(String nickname, int point) {
    }

    public record HabitSummary(
            Long id, String title, HabitCategory category,
            Long animalId, String animalName,
            boolean todayCompleted, double weeklyRate
    ) {
    }

    public record Stats(double weeklyAverageRate, int totalPoint) {
    }
}
