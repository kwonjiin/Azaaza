import React from "react";

export default function LoadingSpinner({ label = "불러오는 중..." }) {
  return <p style={{ color: "var(--color-text-muted)" }}>{label}</p>;
}
