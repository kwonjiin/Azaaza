import React, { useCallback } from "react";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getDashboard } from "../../services/dashboardService";

/**
 * 지금은 GET /dashboard 응답을 최소한으로만 보여주는 스켈레톤이다.
 * 카드/표 레이아웃으로 다듬는 실제 UI는 5단계에서 이어간다 — 여기서는
 * "페이지가 데이터를 어떻게 받아오는가"라는 배선(data wiring)만 확정해둔다.
 */
export default function DashboardPage() {
  const { data, loading, error } = useFetch(useCallback(() => getDashboard(), []));

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <h2>대시보드</h2>
      <Card title={`${data.user.nickname}님의 요약`}>
        <p>총 포인트: {data.stats.totalPoint}</p>
        <p>이번 주 평균 수행률: {Math.round(data.stats.weeklyAverageRate * 100)}%</p>
      </Card>

      <Card title="내 동물">
        <ul>
          {data.animals.map((animal) => (
            <li key={animal.id}>
              {animal.name} ({animal.type}) — Lv.{animal.level}
            </li>
          ))}
        </ul>
      </Card>

      <Card title="습관">
        <ul>
          {data.habits.map((habit) => (
            <li key={habit.id}>
              {habit.title} — 오늘 {habit.todayCompleted ? "완료" : "미완료"} / 주간{" "}
              {Math.round(habit.weeklyRate * 100)}%
            </li>
          ))}
        </ul>
      </Card>
    </div>
  );
}
