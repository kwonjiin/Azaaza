import React, { useCallback } from "react";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getHabits } from "../../services/habitService";

/** 목록만 보여주는 스켈레톤. 생성/수정 폼과 오늘 기록 체크는 5단계에서 붙인다. */
export default function HabitsPage() {
  const { data: habits, loading, error } = useFetch(useCallback(() => getHabits(), []));

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <h2>습관 관리</h2>
      <Card>
        {habits.length === 0 ? (
          <p>아직 등록한 습관이 없어요.</p>
        ) : (
          <ul>
            {habits.map((habit) => (
              <li key={habit.id}>
                [{habit.category}] {habit.title} — {habit.animalName}과 함께
              </li>
            ))}
          </ul>
        )}
      </Card>
    </div>
  );
}
