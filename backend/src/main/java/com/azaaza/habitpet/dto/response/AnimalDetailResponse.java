package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.animal.AnimalType;

import java.time.LocalDateTime;

public record AnimalDetailResponse(
        Long id, String name, AnimalType type, int level, int experience,
        long habitCount, LocalDateTime createdAt
) {

    public static AnimalDetailResponse of(Animal animal, long habitCount) {
        return new AnimalDetailResponse(animal.getId(), animal.getName(), animal.getType(),
                animal.getLevel(), animal.getExperience(), habitCount, animal.getCreatedAt());
    }
}
