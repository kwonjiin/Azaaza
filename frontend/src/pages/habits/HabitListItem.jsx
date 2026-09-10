import React, { useState } from "react";
import Button from "../../components/common/Button";
import HabitForm, { CATEGORIES } from "./HabitForm";
import HabitRecordAction from "./HabitRecordAction";
import { updateHabit, deleteHabit } from "../../services/habitService";

const CATEGORY_LABEL = Object.fromEntries(CATEGORIES.map((c) => [c.value, c.label]));

export default function HabitListItem({ habit, animals, onChanged }) {
  const [editing, setEditing] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const handleUpdate = async (payload) => {
    await updateHabit(habit.id, payload);
    setEditing(false);
    onChanged();
  };

  const handleDelete = async () => {
    if (!window.confirm(`"${habit.title}" 습관을 삭제할까요? 그동안의 기록도 함께 사라져요.`)) return;
    setDeleting(true);
    try {
      await deleteHabit(habit.id);
      onChanged();
    } catch {
      setDeleting(false);
    }
  };

  if (editing) {
    return (
      <li className="habit-item">
        <HabitForm
          animals={animals}
          initial={habit}
          submitLabel="수정 완료"
          onSubmit={handleUpdate}
          onCancel={() => setEditing(false)}
        />
      </li>
    );
  }

  return (
    <li className="habit-item">
      <div className="habit-item-header">
        <div>
          <span className="badge">{CATEGORY_LABEL[habit.category] || habit.category}</span>{" "}
          <strong>{habit.title}</strong>{" "}
          <span className="text-muted">— {habit.animalName}과 함께</span>
          {habit.description && <p className="text-muted" style={{ margin: "4px 0 0" }}>{habit.description}</p>}
        </div>
        <div className="habit-item-buttons">
          <Button variant="secondary" onClick={() => setEditing(true)}>
            수정
          </Button>
          <Button variant="danger" disabled={deleting} onClick={handleDelete}>
            {deleting ? "삭제 중..." : "삭제"}
          </Button>
        </div>
      </div>
      <HabitRecordAction habit={habit} />
    </li>
  );
}
