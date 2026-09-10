package com.azaaza.habitpet.controller;

import com.azaaza.habitpet.dto.response.DiaryResponse;
import com.azaaza.habitpet.global.resolver.LoginUser;
import com.azaaza.habitpet.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /** 수동/테스트 트리거 — 호출한 사용자의 동물만 대상으로 "오늘" 일기를 멱등 생성한다.
     *  전체 사용자 대상 자동 생성은 DiaryScheduler(매일 새벽 4시)가 담당한다. */
    @PostMapping("/generate-today")
    public ResponseEntity<List<DiaryResponse>> generateToday(@LoginUser Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diaryService.generateToday(userId));
    }

    @GetMapping
    public ResponseEntity<List<DiaryResponse>> search(
            @LoginUser Long userId,
            @RequestParam(required = false) Long animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(diaryService.search(userId, animalId, from, to));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryResponse> getDetail(@LoginUser Long userId, @PathVariable Long diaryId) {
        return ResponseEntity.ok(diaryService.getDetail(userId, diaryId));
    }
}
