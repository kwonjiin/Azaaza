package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.diary.Diary;
import com.azaaza.habitpet.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 일기 저장 하나만 REQUIRES_NEW로 독립된 트랜잭션에 담당하는 컴포넌트.
 *
 * DiaryService.generateForDate가 전체 사용자의 동물을 순회하며 이 메서드를 호출하는데,
 * 예전엔 배치 전체가 트랜잭션 하나였다 — 동물 하나가 유니크 제약 충돌(동시 실행 등)을 내면
 * 그날 밤 이미 저장된 다른 동물의 일기까지 전부 롤백됐다. 별도 빈으로 분리한 이유는 같은
 * 클래스 안에서 self-invocation하면 프록시를 안 거쳐 @Transactional이 씹히기 때문이다.
 */
@Component
@RequiredArgsConstructor
class DiaryWriter {

    private final DiaryRepository diaryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Diary createIndependently(Animal animal, LocalDate date, String content) {
        Diary diary = Diary.builder()
                .animal(animal)
                .date(date)
                .content(content)
                .build();
        return diaryRepository.save(diary);
    }
}
