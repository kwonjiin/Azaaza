package com.azaaza.habitpet.repository;

import com.azaaza.habitpet.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByAnimal_IdAndDate(Long animalId, LocalDate date);

    Optional<Diary> findByIdAndAnimal_User_Id(Long id, Long userId);

    // 배치 생성 시 동물마다 존재 여부를 따로 묻지 않고 한 번에 확인하려고 둔 메서드
    // (DiaryService.getOrCreateDiaries 참고 — N번의 존재-체크 쿼리를 1번으로 줄인다).
    List<Diary> findAllByAnimal_IdInAndDate(List<Long> animalIds, LocalDate date);

    @Query("""
            select d from Diary d
            where d.animal.user.id = :userId
              and (:animalId is null or d.animal.id = :animalId)
              and d.date between :from and :to
            order by d.date desc
            """)
    List<Diary> search(@Param("userId") Long userId,
                        @Param("animalId") Long animalId,
                        @Param("from") LocalDate from,
                        @Param("to") LocalDate to);
}
