import React, { useCallback } from "react";
import { Link } from "react-router-dom";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getDashboard } from "../../services/dashboardService";

export default function DashboardPage() {
  const { data, loading, error } = useFetch(useCallback(() => getDashboard(), []));

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div className="stack">
      <h2>대시보드</h2>

      <div className="card-grid">
        <Card title={`${data.user.nickname}님`}>
          <p className="stat-number">{data.stats.totalPoint}P</p>
          <p className="text-muted" style={{ margin: 0 }}>누적 포인트</p>
        </Card>
        <Card title="이번 주 평균 수행률">
          <p className="stat-number">{Math.round(data.stats.weeklyAverageRate * 100)}%</p>
          <div className="progress">
            <div className="progress-bar" style={{ width: `${Math.round(data.stats.weeklyAverageRate * 100)}%` }} />
          </div>
        </Card>
      </div>

      <Card title="내 동물">
        {data.animals.length === 0 ? (
          <p className="text-muted">아직 키우는 동물이 없어요.</p>
        ) : (
          <div className="card-grid">
            {data.animals.map((animal) => (
              <Link key={animal.id} to={`/animals/${animal.id}`} className="mini-card">
                <strong>{animal.name}</strong>
                <p className="text-muted" style={{ margin: "4px 0 0" }}>
                  {animal.type} · Lv.{animal.level}
                </p>
              </Link>
            ))}
          </div>
        )}
      </Card>

      <Card title="오늘의 습관">
        {data.habits.length === 0 ? (
          <p className="text-muted">등록된 습관이 없어요. 습관 관리 페이지에서 만들어보세요.</p>
        ) : (
          <table className="habit-table">
            <thead>
              <tr>
                <th>습관</th>
                <th>동물</th>
                <th>오늘</th>
                <th>주간 수행률</th>
              </tr>
            </thead>
            <tbody>
              {data.habits.map((habit) => (
                <tr key={habit.id}>
                  <td>{habit.title}</td>
                  <td className="text-muted">{habit.animalName}</td>
                  <td>
                    <span className={habit.todayCompleted ? "badge badge-success" : "badge"}>
                      {habit.todayCompleted ? "완료" : "미완료"}
                    </span>
                  </td>
                  <td>
                    <div className="table-progress">
                      <div className="progress">
                        <div className="progress-bar" style={{ width: `${Math.round(habit.weeklyRate * 100)}%` }} />
                      </div>
                      <span className="text-muted">{Math.round(habit.weeklyRate * 100)}%</span>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}
