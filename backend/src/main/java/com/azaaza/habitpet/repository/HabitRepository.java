package com.azaaza.habitpet.repository;

import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    Optional<Habit> findByIdAndUser_Id(Long id, Long userId);

    // animal은 FetchType.LAZY인데 대시보드가 이 목록의 모든 habit에서 habit.getAnimal()을
    // 바로 접근한다 — @EntityGraph 없이 두면 habit마다 추가 SELECT가 나가는 N+1이 된다.
    @EntityGraph(attributePaths = "animal")
    List<Habit> findAllByUser_Id(Long userId);

    List<Habit> findAllByAnimal_Id(Long animalId);

    // 일기 배치가 동물마다 habit 목록을 따로 조회하지 않고 한 번에 가져오려고 둔 메서드.
    List<Habit> findAllByAnimal_IdIn(List<Long> animalIds);

    long countByAnimal_Id(Long animalId);

    /**
     * category/animalId 필터를 선택적으로 적용하는 검색.
     * 조건이 늘어날 것 같으면 Querydsl/Specification으로 옮기겠지만,
     * MVP 단계에서 필터 2개 정도는 JPQL의 (:param is null or ...) 패턴으로 충분하다.
     */
    @Query("""
            select h from Habit h
            where h.user.id = :userId
              and (:category is null or h.category = :category)
              and (:animalId is null or h.animal.id = :animalId)
            order by h.createdAt desc
            """)
    List<Habit> search(@Param("userId") Long userId,
                        @Param("category") HabitCategory category,
                        @Param("animalId") Long animalId);
}
