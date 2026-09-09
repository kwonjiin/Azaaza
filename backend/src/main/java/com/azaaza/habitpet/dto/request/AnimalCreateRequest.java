package com.azaaza.habitpet.dto.request;

import com.azaaza.habitpet.domain.animal.AnimalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnimalCreateRequest(
        @NotBlank @Size(max = 20) String name,
        @NotNull AnimalType type
) {
}
