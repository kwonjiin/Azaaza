import React, { useCallback } from "react";
import { Link } from "react-router-dom";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getDiaries } from "../../services/diaryService";

/** 목록만 보여주는 스켈레톤. 동물별 필터와 기간 검색은 5단계에서 붙인다. */
export default function DiariesPage() {
  const { data: diaries, loading, error } = useFetch(useCallback(() => getDiaries({}), []));

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <h2>일기장</h2>
      <Card>
        {diaries.length === 0 ? (
          <p>아직 쓰여진 일기가 없어요.</p>
        ) : (
          <ul>
            {diaries.map((diary) => (
              <li key={diary.id}>
                <Link to={`/diaries/${diary.id}`}>
                  [{diary.date}] {diary.animalName}
                </Link>
              </li>
            ))}
          </ul>
        )}
      </Card>
    </div>
  );
}
