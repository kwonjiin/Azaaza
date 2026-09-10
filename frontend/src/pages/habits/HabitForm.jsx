import React, { useState } from "react";
import Button from "../../components/common/Button";
import ErrorMessage from "../../components/common/ErrorMessage";
import useSubmit from "../../hooks/useSubmit";

const CATEGORIES = [
  { value: "HEALTH", label: "건강" },
  { value: "STUDY", label: "공부" },
  { value: "ROUTINE", label: "루틴" },
  { value: "ETC", label: "기타" },
];

const TARGET_TYPES = [
  { value: "CHECK", label: "체크형 (했다/안했다)" },
  { value: "NUMBER", label: "수치형 (목표량 입력)" },
];

const emptyForm = {
  animalId: "",
  title: "",
  description: "",
  category: "HEALTH",
  targetType: "CHECK",
  targetValue: "",
  targetUnit: "",
};

/**
 * 생성/수정 겸용 폼. animalId는 습관을 만든 뒤 바꿀 수 없다(API.md 참고)라서
 * 수정 모드(initial이 있을 때)에는 select 대신 동물 이름만 보여준다.
 */
export default function HabitForm({ animals, initial, onSubmit, onCancel, submitLabel }) {
  const [form, setForm] = useState(() =>
    initial
      ? {
          animalId: initial.animalId,
          title: initial.title,
          description: initial.description || "",
          category: initial.category,
          targetType: initial.targetType,
          targetValue: initial.targetValue ?? "",
          targetUnit: initial.targetUnit || "",
        }
      : { ...emptyForm, animalId: animals[0]?.id ?? "" }
  );
  const { submit, submitting, error } = useSubmit(onSubmit);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...form,
        animalId: Number(form.animalId),
        targetValue: form.targetType === "NUMBER" ? Number(form.targetValue) : undefined,
        targetUnit: form.targetType === "NUMBER" ? form.targetUnit : undefined,
      };
      await submit(payload);
    } catch {
      // 에러는 useSubmit이 이미 담아뒀다 — 폼은 그대로 열어둔 채 사용자가 고쳐 다시 낼 수 있게 한다.
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-row-inline">
        <div className="form-row">
          <label htmlFor="habit-animal">동물</label>
          {initial ? (
            <input id="habit-animal" value={initial.animalName} disabled />
          ) : (
            <select id="habit-animal" name="animalId" required value={form.animalId} onChange={handleChange}>
              {animals.map((animal) => (
                <option key={animal.id} value={animal.id}>
                  {animal.name}
                </option>
              ))}
            </select>
          )}
        </div>
        <div className="form-row">
          <label htmlFor="habit-category">분류</label>
          <select id="habit-category" name="category" value={form.category} onChange={handleChange}>
            {CATEGORIES.map((c) => (
              <option key={c.value} value={c.value}>
                {c.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="form-row">
        <label htmlFor="habit-title">제목</label>
        <input
          id="habit-title"
          name="title"
          required
          placeholder="물 2L 마시기"
          value={form.title}
          onChange={handleChange}
        />
      </div>

      <div className="form-row">
        <label htmlFor="habit-description">설명</label>
        <textarea
          id="habit-description"
          name="description"
          rows={2}
          placeholder="하루 목표 수분 섭취량"
          value={form.description}
          onChange={handleChange}
        />
      </div>

      <div className="form-row">
        <label htmlFor="habit-target-type">수행 방식</label>
        <select id="habit-target-type" name="targetType" value={form.targetType} onChange={handleChange}>
          {TARGET_TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>
      </div>

      {form.targetType === "NUMBER" && (
        <div className="form-row-inline">
          <div className="form-row">
            <label htmlFor="habit-target-value">목표량</label>
            <input
              id="habit-target-value"
              name="targetValue"
              type="number"
              min="1"
              required
              value={form.targetValue}
              onChange={handleChange}
            />
          </div>
          <div className="form-row">
            <label htmlFor="habit-target-unit">단위</label>
            <input
              id="habit-target-unit"
              name="targetUnit"
              placeholder="ml, 분 ..."
              required
              value={form.targetUnit}
              onChange={handleChange}
            />
          </div>
        </div>
      )}

      <ErrorMessage error={error} />

      <div className="form-actions">
        <Button type="submit" disabled={submitting || (!initial && animals.length === 0)}>
          {submitting ? "저장 중..." : submitLabel}
        </Button>
        {onCancel && (
          <Button type="button" variant="secondary" onClick={onCancel}>
            취소
          </Button>
        )}
      </div>
    </form>
  );
}

export { CATEGORIES };
