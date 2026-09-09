package com.azaaza.habitpet.controller;

import com.azaaza.habitpet.dto.request.AnimalCreateRequest;
import com.azaaza.habitpet.dto.response.AnimalDetailResponse;
import com.azaaza.habitpet.dto.response.AnimalResponse;
import com.azaaza.habitpet.global.resolver.LoginUser;
import com.azaaza.habitpet.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping
    public ResponseEntity<AnimalResponse> create(@LoginUser Long userId,
                                                   @Valid @RequestBody AnimalCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponse>> getMyAnimals(@LoginUser Long userId) {
        return ResponseEntity.ok(animalService.getMyAnimals(userId));
    }

    @GetMapping("/{animalId}")
    public ResponseEntity<AnimalDetailResponse> getDetail(@LoginUser Long userId, @PathVariable Long animalId) {
        return ResponseEntity.ok(animalService.getDetail(userId, animalId));
    }
}
