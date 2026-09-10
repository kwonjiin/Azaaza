import React, { useCallback } from "react";
import { Link } from "react-router-dom";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getMyAnimals } from "../../services/animalService";

/** 목록 스켈레톤. 동물 생성 폼과 카드형 레이아웃은 5단계에서. */
export default function AnimalsPage() {
  const { data: animals, loading, error } = useFetch(useCallback(() => getMyAnimals(), []));

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <h2>동물 프로필</h2>
      <Card>
        <ul>
          {animals.map((animal) => (
            <li key={animal.id}>
              <Link to={`/animals/${animal.id}`}>
                {animal.name} ({animal.type}) — Lv.{animal.level}
              </Link>
            </li>
          ))}
        </ul>
      </Card>
    </div>
  );
}
