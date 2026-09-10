package com.azaaza.habitpet.global.scheduler;

import com.azaaza.habitpet.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

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

    // 배포 환경의 JVM 기본 타임존이 KST가 아닐 수 있어(클라우드 기본값은 대개 UTC) 명시한다.
    // cron의 zone 속성과 "어제" 계산에 쓰는 기준 시각이 어긋나면 안 되므로 둘 다 이 상수를 쓴다.
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final DiaryService diaryService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void generateYesterdayDiaries() {
        LocalDate targetDate = LocalDate.now(ZONE).minusDays(1);
        int diaryCount = diaryService.generateForDate(targetDate);
        log.info("[DiaryScheduler] {}자 일기 생성 배치 완료 — 확보된 일기 {}건", targetDate, diaryCount);
    }
}
