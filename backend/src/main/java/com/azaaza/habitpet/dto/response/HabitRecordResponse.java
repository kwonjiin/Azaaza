package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.habit.HabitRecord;

import java.time.LocalDate;

/**
 * 기록 생성 응답. earnedPoint/earnedExp와 갱신된 animal 스냅샷을 함께 내려서
 * 프론트가 별도 조회 없이 레벨업 연출을 그 자리에서 트리거할 수 있게 한다 (API.md 참고).
 */
public record HabitRecordResponse(
        Long id, Long habitId, LocalDate date, boolean completed, Integer actualValue,
        int earnedPoint, int earnedExp, AnimalSnapshot animal
) {

    public record AnimalSnapshot(Long id, int level, int experience) {
        public static AnimalSnapshot from(Animal animal) {
            return new AnimalSnapshot(animal.getId(), animal.getLevel(), animal.getExperience());
        }
    }

    public static HabitRecordResponse of(HabitRecord record, int earnedPoint, int earnedExp, Animal animal) {
        return new HabitRecordResponse(
                record.getId(), record.getHabit().getId(), record.getDate(), record.isCompleted(),
                record.getActualValue(), earnedPoint, earnedExp, AnimalSnapshot.from(animal)
        );
    }
}
