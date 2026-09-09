package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.animal.AnimalType;

public record AnimalResponse(Long id, String name, AnimalType type, int level, int experience) {

    public static AnimalResponse from(Animal animal) {
        return new AnimalResponse(animal.getId(), animal.getName(), animal.getType(),
                animal.getLevel(), animal.getExperience());
    }
}
