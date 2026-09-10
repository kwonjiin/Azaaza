import React, { useCallback } from "react";
import { useParams } from "react-router-dom";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getAnimalDetail } from "../../services/animalService";

export default function AnimalDetailPage() {
  const { animalId } = useParams();
  const { data: animal, loading, error } = useFetch(
    useCallback(() => getAnimalDetail(animalId), [animalId])
  );

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <h2>{animal.name}</h2>
      <Card>
        <p>종류: {animal.type}</p>
        <p>레벨: {animal.level}</p>
        <p>경험치: {animal.experience} / 100</p>
        <p>키우고 있는 습관 수: {animal.habitCount}개</p>
      </Card>
    </div>
  );
}
