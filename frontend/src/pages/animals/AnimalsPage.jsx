import React, { useCallback } from "react";
import { Link } from "react-router-dom";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getMyAnimals } from "../../services/animalService";
import AnimalForm, { ANIMAL_TYPES } from "./AnimalForm";

const TYPE_LABEL = Object.fromEntries(ANIMAL_TYPES.map((t) => [t.value, t.label]));

export default function AnimalsPage() {
  const { data: animals, loading, error, refetch } = useFetch(useCallback(() => getMyAnimals(), []));

  return (
    <div className="stack">
      <h2>동물 프로필</h2>

      <Card title="새 동물 만들기">
        <AnimalForm onCreated={refetch} />
      </Card>

      {loading && <LoadingSpinner />}
      {error && <ErrorMessage error={error} />}

      {animals && (
        <div className="card-grid">
          {animals.length === 0 && <p className="text-muted">아직 키우는 동물이 없어요. 위에서 먼저 만들어보세요.</p>}
          {animals.map((animal) => (
            <Link key={animal.id} to={`/animals/${animal.id}`} style={{ display: "block" }}>
              <Card>
                <h3 style={{ margin: "0 0 4px" }}>{animal.name}</h3>
                <p className="text-muted" style={{ margin: "0 0 12px" }}>
                  {TYPE_LABEL[animal.type] || animal.type} · Lv.{animal.level}
                </p>
                <div className="progress">
                  <div className="progress-bar" style={{ width: `${animal.experience % 100}%` }} />
                </div>
                <p className="text-muted" style={{ margin: "6px 0 0" }}>
                  경험치 {animal.experience % 100} / 100
                </p>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
