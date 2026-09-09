package com.azaaza.habitpet.controller;

import com.azaaza.habitpet.dto.request.HabitRecordCreateRequest;
import com.azaaza.habitpet.dto.response.HabitRecordResponse;
import com.azaaza.habitpet.dto.response.HabitRecordSimpleResponse;
import com.azaaza.habitpet.global.resolver.LoginUser;
import com.azaaza.habitpet.service.HabitRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits/{habitId}/records")
@RequiredArgsConstructor
public class HabitRecordController {

    private final HabitRecordService habitRecordService;

    @PostMapping
    public ResponseEntity<HabitRecordResponse> create(@LoginUser Long userId, @PathVariable Long habitId,
                                                        @Valid @RequestBody HabitRecordCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitRecordService.create(userId, habitId, request));
    }

    @GetMapping
    public ResponseEntity<List<HabitRecordSimpleResponse>> getRecords(
            @LoginUser Long userId, @PathVariable Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(habitRecordService.getRecords(userId, habitId, from, to));
    }
}
