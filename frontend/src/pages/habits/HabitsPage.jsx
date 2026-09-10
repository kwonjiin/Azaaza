import React, { useCallback } from "react";
import Card from "../../components/common/Card";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorMessage from "../../components/common/ErrorMessage";
import useFetch from "../../hooks/useFetch";
import { getHabits, createHabit } from "../../services/habitService";
import { getMyAnimals } from "../../services/animalService";
import HabitForm from "./HabitForm";
import HabitListItem from "./HabitListItem";

export default function HabitsPage() {
  const { data: habits, loading: habitsLoading, error: habitsError, refetch: refetchHabits } = useFetch(
    useCallback(() => getHabits(), [])
  );
  const { data: animals, loading: animalsLoading, error: animalsError } = useFetch(
    useCallback(() => getMyAnimals(), [])
  );

  const loading = habitsLoading || animalsLoading;
  const error = habitsError || animalsError;

  const handleCreate = async (payload) => {
    await createHabit(payload);
    refetchHabits();
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div className="stack">
      <h2>습관 관리</h2>

      <Card title="새 습관 만들기">
        {animals.length === 0 ? (
          <p className="text-muted">습관을 만들려면 먼저 동물 프로필을 하나 만들어야 해요.</p>
        ) : (
          <HabitForm animals={animals} submitLabel="습관 만들기" onSubmit={handleCreate} />
        )}
      </Card>

      <Card>
        {habits.length === 0 ? (
          <p className="text-muted">아직 등록한 습관이 없어요.</p>
        ) : (
          <ul className="habit-list">
            {habits.map((habit) => (
              <HabitListItem key={habit.id} habit={habit} animals={animals} onChanged={refetchHabits} />
            ))}
          </ul>
        )}
      </Card>
    </div>
  );
}
