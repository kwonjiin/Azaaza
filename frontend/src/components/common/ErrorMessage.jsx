import React from "react";

/** 백엔드 GlobalExceptionHandler가 내려주는 {code, message} 형식을 그대로 활용한다. */
export default function ErrorMessage({ error }) {
  if (!error) return null;
  const message = error.response?.data?.message || "요청 처리 중 문제가 발생했습니다.";
  return <p style={{ color: "var(--color-danger)" }}>{message}</p>;
}
