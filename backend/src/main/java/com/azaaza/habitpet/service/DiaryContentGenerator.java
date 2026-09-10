package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.habit.Habit;
import com.azaaza.habitpet.domain.habit.HabitRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 일기 본문 조립만 전담하는 컴포넌트. DiaryService에서 문장 템플릿 로직을 분리해뒀다 —
 * 이렇게 떼어놔야 "오늘 완수율에 따라 톤을 바꾼다" 같은 규칙이 늘어나도
 * 저장/조회를 다루는 DiaryService를 건드리지 않고 여기만 고치면 된다.
 * (나중에 LLM 기반 생성으로 바꾸더라도 이 클래스의 시그니처만 유지하면 교체가 쉽다.)
 */
@Component
public class DiaryContentGenerator {

    public String generate(List<Habit> habits, Map<Long, HabitRecord> recordsByHabitId) {
        if (habits.isEmpty()) {
            return "오늘은 아직 같이 시작한 습관이 없어서 조용한 하루였다.";
        }

        long completedCount = habits.stream()
                .filter(habit -> isCompleted(recordsByHabitId.get(habit.getId())))
                .count();

        StringBuilder sb = new StringBuilder();
        for (Habit habit : habits) {
            sb.append(habitLine(habit, isCompleted(recordsByHabitId.get(habit.getId()))));
        }
        sb.append(closingLine(habits.size(), (int) completedCount));
        return sb.toString();
    }

    private boolean isCompleted(HabitRecord record) {
        return record != null && record.isCompleted();
    }

    private String habitLine(Habit habit, boolean completed) {
        return "오늘 주인님이 " + habit.getTitle()
                + (completed ? "을 완수해서 나도 기분이 좋았다. " : "을 아직 안 해서 조금 아쉬웠다. ");
    }

    /** 완수율 3단계(전부/일부/전무)에 따라 마무리 문장의 감정 톤을 바꾼다. */
    private String closingLine(int total, int completed) {
        String tone;
        if (completed == total) {
            tone = "오늘은 완벽한 하루였다! 주인님이 정말 자랑스럽다.";
        } else if (completed == 0) {
            tone = "내일은 조금 더 힘내주면 좋겠다.";
        } else {
            tone = "그래도 오늘 해낸 만큼은 뿌듯하다.";
        }
        return String.format("오늘 습관 %d개 중 %d개를 해냈다. %s", total, completed, tone);
    }
}
