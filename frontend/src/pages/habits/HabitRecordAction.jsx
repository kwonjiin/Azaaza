import React, { useState } from "react";
import Button from "../../components/common/Button";
import ErrorMessage from "../../components/common/ErrorMessage";
import { createHabitRecord } from "../../services/habitRecordService";
import { todayISO } from "../../utils/date";

/**
 * 습관 하나당 "오늘 기록"을 담당. 같은 (habitId, date)로 두 번 기록하면 백엔드가
 * 409 DUPLICATE_RECORD를 돌려주므로, 이미 기록한 뒤에는 결과 배지로 바꿔 재요청을 막는다.
 */
export default function HabitRecordAction({ habit }) {
  const [actualValue, setActualValue] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  const handleRecord = async (completed) => {
    setSubmitting(true);
    setError(null);
    try {
      const payload = { date: todayISO(), completed };
      if (habit.targetType === "NUMBER") {
        payload.actualValue = Number(actualValue);
      }
      const record = await createHabitRecord(habit.id, payload);
      setResult(record);
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  if (result) {
    return (
      <span className="badge badge-success">
        오늘 기록 완료 · +{result.earnedPoint}P · +{result.earnedExp}EXP (Lv.{result.animal.level})
      </span>
    );
  }

  if (habit.targetType === "CHECK") {
    return (
      <div className="record-action">
        <Button variant="secondary" disabled={submitting} onClick={() => handleRecord(true)}>
          오늘 완료로 기록
        </Button>
        <ErrorMessage error={error} />
      </div>
    );
  }

  return (
    <div className="record-action">
      <input
        type="number"
        min="0"
        className="record-value-input"
        placeholder={habit.targetUnit}
        value={actualValue}
        onChange={(e) => setActualValue(e.target.value)}
      />
      <Button
        variant="secondary"
        disabled={submitting || actualValue === ""}
        onClick={() => handleRecord(Number(actualValue) >= habit.targetValue)}
      >
        기록
      </Button>
      <ErrorMessage error={error} />
    </div>
  );
}
