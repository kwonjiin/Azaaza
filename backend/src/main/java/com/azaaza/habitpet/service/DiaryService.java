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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 일기 저장/조회(멱등 생성, 검색, 상세 조회)를 다룬다. 문장 조립 로직은
 * DiaryContentGenerator, 실제 insert는 DiaryWriter로 분리돼 있다 — 여기서는
 * "누구 것을, 어떤 순서로" 만들지만 결정한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private static final int DEFAULT_SEARCH_RANGE_DAYS = 30;

    private final AnimalRepository animalRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryContentGenerator diaryContentGenerator;
    private final DiaryWriter diaryWriter;

    public List<DiaryResponse> generateToday(Long userId) {
        // 수동/테스트 트리거는 "오늘" 기준이 직관적이라 그대로 둔다. 새벽 배치는 "방금 끝난
        // 하루(어제)"를 정리해야 하므로 별도 경로(generateForDate)를 스케줄러 전용으로 둔다.
        List<Animal> animals = animalRepository.findAllByUser_Id(userId);
        return getOrCreateDiaries(animals, LocalDate.now()).stream()
                .map(DiaryResponse::from)
                .toList();
    }

    /** 매일 새벽 4시 배치(DiaryScheduler)에서 호출 — 전체 사용자의 동물을 대상으로 지정한 날짜의 일기를 만든다. */
    public int generateForDate(LocalDate targetDate) {
        List<Animal> animals = animalRepository.findAll();
        return getOrCreateDiaries(animals, targetDate).size();
    }

    /**
     * 동물 목록에 대해 지정한 날짜의 일기를 "이미 있으면 그대로, 없으면 생성"한다.
     *
     * 예전엔 동물 1마리당 쿼리 3개(존재 체크·습관 조회·기록 조회)를 순서대로 날려서
     * 동물 수만큼 쿼리가 선형으로 늘었다. 지금은 존재 체크·습관·기록을 각각 한 번씩
     * bulk 조회해 메모리에서 매칭한 뒤, 실제 insert(DiaryWriter)만 동물별로 개별
     * 트랜잭션(REQUIRES_NEW)으로 실행한다 — 한 동물의 실패가 다른 동물의 결과를
     * 롤백시키지 않게 하기 위해서다.
     */
    private List<Diary> getOrCreateDiaries(List<Animal> animals, LocalDate date) {
        if (animals.isEmpty()) {
            return List.of();
        }

        List<Long> animalIds = animals.stream().map(Animal::getId).toList();
        Map<Long, Diary> existingByAnimalId = diaryRepository.findAllByAnimal_IdInAndDate(animalIds, date).stream()
                .collect(Collectors.toMap(d -> d.getAnimal().getId(), Function.identity()));

        List<Animal> missing = animals.stream()
                .filter(a -> !existingByAnimalId.containsKey(a.getId()))
                .toList();

        Map<Long, Diary> createdByAnimalId = missing.isEmpty() ? Map.of() : createMissingDiaries(missing, date);

        return animals.stream()
                .map(a -> existingByAnimalId.containsKey(a.getId()) ? existingByAnimalId.get(a.getId()) : createdByAnimalId.get(a.getId()))
                .filter(Objects::nonNull) // 개별 생성이 실패한 동물은 이번 결과에서 조용히 빠진다(로그는 남김)
                .toList();
    }

    private Map<Long, Diary> createMissingDiaries(List<Animal> missing, LocalDate date) {
        List<Long> missingAnimalIds = missing.stream().map(Animal::getId).toList();

        List<Habit> habits = habitRepository.findAllByAnimal_IdIn(missingAnimalIds);
        Map<Long, List<Habit>> habitsByAnimalId = habits.stream()
                .collect(Collectors.groupingBy(h -> h.getAnimal().getId()));

        List<Long> habitIds = habits.stream().map(Habit::getId).toList();
        Map<Long, HabitRecord> recordsByHabitId = habitIds.isEmpty()
                ? Map.of()
                : habitRecordRepository.findAllByHabit_IdInAndDateBetween(habitIds, date, date).stream()
                        .collect(Collectors.toMap(r -> r.getHabit().getId(), Function.identity()));

        Map<Long, Diary> createdByAnimalId = new HashMap<>();
        for (Animal animal : missing) {
            List<Habit> animalHabits = habitsByAnimalId.getOrDefault(animal.getId(), List.of());
            String content = diaryContentGenerator.generate(animalHabits, recordsByHabitId);
            try {
                createdByAnimalId.put(animal.getId(), diaryWriter.createIndependently(animal, date, content));
            } catch (DataIntegrityViolationException e) {
                // 유니크 제약 충돌 — 동시 실행(수동 트리거와 배치가 겹치는 등)으로 그 사이 이미
                // 만들어진 경우다. 실패로 치지 않고 그 결과를 다시 읽어와 쓴다.
                diaryRepository.findByAnimal_IdAndDate(animal.getId(), date).ifPresent(
                        diary -> createdByAnimalId.put(animal.getId(), diary));
            } catch (Exception e) {
                log.error("[DiaryService] animalId={} {}자 일기 생성 실패", animal.getId(), date, e);
            }
        }
        return createdByAnimalId;
    }

    public List<DiaryResponse> search(Long userId, Long animalId, LocalDate from, LocalDate to) {
        LocalDate resolvedTo = to != null ? to : LocalDate.now();
        // from/to를 필수로 요구했더니 프론트가 필터 없이("전체 기간") 호출하는 케이스를 못 받아
        // 매번 400이 났다 — 기본값(최근 30일)을 두고 없으면 그걸로 채운다.
        LocalDate resolvedFrom = from != null ? from : resolvedTo.minusDays(DEFAULT_SEARCH_RANGE_DAYS - 1L);

        return diaryRepository.search(userId, animalId, resolvedFrom, resolvedTo).stream()
                .map(DiaryResponse::from)
                .toList();
    }

    public DiaryResponse getDetail(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndAnimal_User_Id(diaryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));
        return DiaryResponse.from(diary);
    }
}
