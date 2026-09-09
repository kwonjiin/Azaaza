package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.diary.Diary;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiaryResponse(
        Long id, Long animalId, String animalName, LocalDate date, String content, LocalDateTime createdAt
) {

    public static DiaryResponse from(Diary diary) {
        return new DiaryResponse(
                diary.getId(), diary.getAnimal().getId(), diary.getAnimal().getName(),
                diary.getDate(), diary.getContent(), diary.getCreatedAt()
        );
    }
}
