package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.dto.request.AnimalCreateRequest;
import com.azaaza.habitpet.dto.response.AnimalDetailResponse;
import com.azaaza.habitpet.dto.response.AnimalResponse;
import com.azaaza.habitpet.global.exception.BusinessException;
import com.azaaza.habitpet.global.exception.ErrorCode;
import com.azaaza.habitpet.repository.AnimalRepository;
import com.azaaza.habitpet.repository.HabitRepository;
import com.azaaza.habitpet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnimalResponse create(Long userId, AnimalCreateRequest request) {
        // getReferenceById: 이 시점엔 User 엔티티의 다른 필드가 필요 없어서(FK만 필요)
        // 실제 SELECT 없이 프록시만 얻는다 — 존재 여부는 SecurityContext에 이미 인증된 userId라 보장됨.
        var userRef = userRepository.getReferenceById(userId);

        Animal animal = Animal.builder()
                .user(userRef)
                .name(request.name())
                .type(request.type())
                .build();

        return AnimalResponse.from(animalRepository.save(animal));
    }

    public List<AnimalResponse> getMyAnimals(Long userId) {
        return animalRepository.findAllByUser_Id(userId).stream()
                .map(AnimalResponse::from)
                .toList();
    }

    public AnimalDetailResponse getDetail(Long userId, Long animalId) {
        Animal animal = findOwnedAnimal(userId, animalId);
        long habitCount = habitRepository.countByAnimal_Id(animal.getId());
        return AnimalDetailResponse.of(animal, habitCount);
    }

    Animal findOwnedAnimal(Long userId, Long animalId) {
        return animalRepository.findByIdAndUser_Id(animalId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANIMAL_NOT_FOUND));
    }
}
