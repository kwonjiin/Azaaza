import React, { useCallback, useState } from "react";
import { Link } from "react-router-dom";
import Card from "../../components/common/Card";
import Button from "../../components/common/Button";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import useSubmit from "../../hooks/useSubmit";
import { getDiaries, generateTodayDiaries } from "../../services/diaryService";
import { getMyAnimals } from "../../services/animalService";

export default function DiariesPage() {
  const [animalId, setAnimalId] = useState("");

  const { data: animals } = useFetch(useCallback(() => getMyAnimals(), []));
  const { data: diaries, loading, error, refetch } = useFetch(
    useCallback(() => getDiaries({ animalId: animalId || undefined }), [animalId])
  );
  const { submit: submitGenerate, submitting: generating, error: generateError } = useSubmit(generateTodayDiaries);

  const handleGenerate = async () => {
    try {
      await submitGenerate();
      refetch();
    } catch {
      // 에러는 useSubmit이 이미 담아뒀다.
    }
  };

  return (
    <div className="stack">
      <div className="page-header">
        <h2>일기장</h2>
        <Button variant="secondary" onClick={handleGenerate} disabled={generating}>
          {generating ? "생성 중..." : "오늘 일기 생성"}
        </Button>
      </div>
      <ErrorMessage error={generateError} />

      {animals && animals.length > 0 && (
        <div className="form-row" style={{ maxWidth: 240 }}>
          <label htmlFor="diary-animal-filter">동물별로 보기</label>
          <select id="diary-animal-filter" value={animalId} onChange={(e) => setAnimalId(e.target.value)}>
            <option value="">전체</option>
            {animals.map((a) => (
              <option key={a.id} value={a.id}>
                {a.name}
              </option>
            ))}
          </select>
        </div>
      )}

      {loading && <LoadingSpinner />}
      {error && <ErrorMessage error={error} />}

      {diaries && (
        <Card>
          {diaries.length === 0 ? (
            <p className="text-muted">아직 쓰여진 일기가 없어요.</p>
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
      )}
    </div>
  );
}
