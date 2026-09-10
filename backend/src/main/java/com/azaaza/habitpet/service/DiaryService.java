package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.animal.Animal;
import com.azaaza.habitpet.domain.diary.Diary;
import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitRecord;
import com.azaaza.habitpet.dto.response.DiaryResponse;
import com.azaaza.habitpet.global.exception.BusinessException;
import com.azaaza.habitpet.global.exception.ErrorCode;
import com.azaaza.habitpet.repository.AnimalRepository;
import com.azaaza.habitpet.repository.DiaryRepository;
import com.azaaza.habitpet.repository.HabitRecordRepository;
import com.azaaza.habitpet.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 일기 저장/조회(멱등 생성, 검색, 상세 조회)를 다룬다. 문장 조립 로직은
 * DiaryContentGenerator로 분리돼 있다 — 여기서는 "언제, 누구 것을" 만들지만 결정한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final AnimalRepository animalRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryContentGenerator diaryContentGenerator;

    @Transactional
    public List<DiaryResponse> generateToday(Long userId) {
        // 수동/테스트 트리거는 "오늘" 기준이 직관적이라 그대로 둔다. 새벽 배치는 "방금 끝난
        // 하루(어제)"를 정리해야 하므로 별도 경로(generateForDate)를 스케줄러 전용으로 둔다.
        List<Animal> animals = animalRepository.findAllByUser_Id(userId);
        return animals.stream()
                .map(animal -> getOrCreateDiary(animal, LocalDate.now()))
                .map(DiaryResponse::from)
                .toList();
    }

    /** 매일 새벽 4시 배치(DiaryScheduler)에서 호출 — 전체 사용자의 동물을 대상으로 지정한 날짜의 일기를 만든다. */
    @Transactional
    public int generateForDate(LocalDate targetDate) {
        List<Animal> animals = animalRepository.findAll();
        animals.forEach(animal -> getOrCreateDiary(animal, targetDate));
        return animals.size();
    }

    private Diary getOrCreateDiary(Animal animal, LocalDate date) {
        // 멱등성: 이미 그날 일기가 있으면 그걸 그대로 반환하고 새로 만들지 않는다.
        Optional<Diary> existing = diaryRepository.findByAnimal_IdAndDate(animal.getId(), date);
        if (existing.isPresent()) {
            return existing.get();
        }

        List<Habit> habits = habitRepository.findAllByAnimal_Id(animal.getId());
        Map<Long, HabitRecord> recordsByHabitId = habits.isEmpty()
                ? Collections.emptyMap()
                : habitRecordRepository
                        .findAllByHabit_IdInAndDateBetween(habits.stream().map(Habit::getId).toList(), date, date)
                        .stream()
                        .collect(Collectors.toMap(r -> r.getHabit().getId(), Function.identity()));
        String content = diaryContentGenerator.generate(habits, recordsByHabitId);

        Diary diary = Diary.builder()
                .animal(animal)
                .date(date)
                .content(content)
                .build();
        return diaryRepository.save(diary);
    }

    public List<DiaryResponse> search(Long userId, Long animalId, LocalDate from, LocalDate to) {
        return diaryRepository.search(userId, animalId, from, to).stream()
                .map(DiaryResponse::from)
                .toList();
    }

    public DiaryResponse getDetail(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndAnimal_User_Id(diaryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));
        return DiaryResponse.from(diary);
    }
}
