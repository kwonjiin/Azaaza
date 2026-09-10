import React, { useState } from "react";
import Button from "../../components/common/Button";
import ErrorMessage from "../../components/common/ErrorMessage";
import useSubmit from "../../hooks/useSubmit";
import { createAnimal } from "../../services/animalService";

const ANIMAL_TYPES = [
  { value: "DOG", label: "강아지" },
  { value: "CAT", label: "고양이" },
  { value: "BIRD", label: "새" },
  { value: "LIZARD", label: "도마뱀" },
];

/** 동물 생성 폼. 목록은 AnimalsPage가 소유하므로, 생성 성공 시 onCreated로 알리기만 한다. */
export default function AnimalForm({ onCreated }) {
  const [form, setForm] = useState({ name: "", type: "DOG" });
  const { submit, submitting, error } = useSubmit(createAnimal);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await submit(form);
      setForm({ name: "", type: "DOG" });
      onCreated();
    } catch {
      // 에러는 useSubmit이 이미 담아뒀다.
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-row-inline">
        <div className="form-row">
          <label htmlFor="animal-name">이름</label>
          <input
            id="animal-name"
            name="name"
            required
            placeholder="몽이"
            value={form.name}
            onChange={handleChange}
          />
        </div>
        <div className="form-row">
          <label htmlFor="animal-type">종류</label>
          <select id="animal-type" name="type" value={form.type} onChange={handleChange}>
            {ANIMAL_TYPES.map((t) => (
              <option key={t.value} value={t.value}>
                {t.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <ErrorMessage error={error} />

      <div className="form-actions">
        <Button type="submit" disabled={submitting}>
          {submitting ? "만드는 중..." : "동물 만들기"}
        </Button>
      </div>
    </form>
  );
}

export { ANIMAL_TYPES };
