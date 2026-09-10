import React, { useCallback } from "react";
import { useParams } from "react-router-dom";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getDiaryDetail } from "../../services/diaryService";

export default function DiaryDetailPage() {
  const { diaryId } = useParams();
  const { data: diary, loading, error } = useFetch(
    useCallback(() => getDiaryDetail(diaryId), [diaryId])
  );

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <h2>{diary.animalName}의 일기 — {diary.date}</h2>
      <Card>
        <p>{diary.content}</p>
      </Card>
    </div>
  );
}
