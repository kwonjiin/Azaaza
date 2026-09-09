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
import java.util.List;
import java.util.Optional;

/**
 * 일기 생성의 뼈대만 이번 단계에서 잡는다. 문장 템플릿/감정 분기 같은 콘텐츠 로직은
 * 6단계에서 별도 컴포넌트(DiaryContentGenerator)로 확장할 예정 — 지금은 최소 동작 버전.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final AnimalRepository animalRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public List<DiaryResponse> generateToday(Long userId) {
        // NOTE: 실제로 새벽 4시 배치라면 "오늘"이 아니라 방금 끝난 하루(어제)를 정리하는 게
        // 자연스럽다. 지금은 수동/테스트 트리거 편의를 위해 호출 당일 기준으로 둔다 — 6단계에서 재논의.
        LocalDate targetDate = LocalDate.now();

        List<Animal> animals = animalRepository.findAllByUser_Id(userId);
        return animals.stream()
                .map(animal -> getOrCreateDiary(animal, targetDate))
                .map(DiaryResponse::from)
                .toList();
    }

    private Diary getOrCreateDiary(Animal animal, LocalDate date) {
        // 멱등성: 이미 오늘자 일기가 있으면 그걸 그대로 반환하고 새로 만들지 않는다.
        Optional<Diary> existing = diaryRepository.findByAnimal_IdAndDate(animal.getId(), date);
        if (existing.isPresent()) {
            return existing.get();
        }

        List<Habit> habits = habitRepository.findAllByAnimal_Id(animal.getId());
        String content = buildContent(animal, habits, date);

        Diary diary = Diary.builder()
                .animal(animal)
                .date(date)
                .content(content)
                .build();
        return diaryRepository.save(diary);
    }

    private String buildContent(Animal animal, List<Habit> habits, LocalDate date) {
        if (habits.isEmpty()) {
            return "오늘은 아직 같이 시작한 습관이 없어서 조용한 하루였다.";
        }

        long completedCount = habits.stream()
                .filter(habit -> habitRecordRepository.findByHabit_IdAndDate(habit.getId(), date)
                        .map(HabitRecord::isCompleted)
                        .orElse(false))
                .count();

        StringBuilder sb = new StringBuilder();
        for (Habit habit : habits) {
            boolean completed = habitRecordRepository.findByHabit_IdAndDate(habit.getId(), date)
                    .map(HabitRecord::isCompleted)
                    .orElse(false);
            sb.append("오늘 주인님이 ").append(habit.getTitle())
                    .append(completed ? "을 완수해서 나도 기분이 좋았다. " : "을 아직 안 해서 조금 아쉬웠다. ");
        }
        sb.append(String.format("오늘 습관 %d개 중 %d개를 해냈다.", habits.size(), completedCount));
        return sb.toString();
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
