package com.azaaza.habitpet.global.scheduler;

import com.azaaza.habitpet.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 매일 새벽 4시, 방금 끝난 하루(어제)에 대한 일기를 전체 동물 대상으로 생성한다.
 * DiaryService.generateToday(userId)는 사용자 하나만 대상으로 하고 "오늘" 날짜를 쓰는
 * 수동/테스트 트리거용이라 배치에는 맞지 않는다 — 그래서 전체 동물 + 지정 날짜를 받는
 * generateForDate를 배치 전용 경로로 따로 둔다. (API.md "Diaries" 섹션 참고)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiaryScheduler {

    private final DiaryService diaryService;

    @Scheduled(cron = "0 0 4 * * *")
    public void generateYesterdayDiaries() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        int animalCount = diaryService.generateForDate(targetDate);
        log.info("[DiaryScheduler] {}자 일기 생성 배치 완료 — 대상 동물 {}마리", targetDate, animalCount);
    }
}
