package com.azaaza.habitpet.controller;

import com.azaaza.habitpet.domain.habit.HabitCategory;
import com.azaaza.habitpet.dto.request.HabitCreateRequest;
import com.azaaza.habitpet.dto.request.HabitUpdateRequest;
import com.azaaza.habitpet.dto.response.HabitResponse;
import com.azaaza.habitpet.global.resolver.LoginUser;
import com.azaaza.habitpet.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitResponse> create(@LoginUser Long userId,
                                                 @Valid @RequestBody HabitCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> search(@LoginUser Long userId,
                                                        @RequestParam(required = false) HabitCategory category,
                                                        @RequestParam(required = false) Long animalId) {
        return ResponseEntity.ok(habitService.search(userId, category, animalId));
    }

    @GetMapping("/{habitId}")
    public ResponseEntity<HabitResponse> getDetail(@LoginUser Long userId, @PathVariable Long habitId) {
        return ResponseEntity.ok(habitService.getDetail(userId, habitId));
    }

    @PutMapping("/{habitId}")
    public ResponseEntity<HabitResponse> update(@LoginUser Long userId, @PathVariable Long habitId,
                                                 @Valid @RequestBody HabitUpdateRequest request) {
        return ResponseEntity.ok(habitService.update(userId, habitId, request));
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> delete(@LoginUser Long userId, @PathVariable Long habitId) {
        habitService.delete(userId, habitId);
        return ResponseEntity.noContent().build();
    }
}
