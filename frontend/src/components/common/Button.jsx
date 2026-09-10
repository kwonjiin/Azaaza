import React from "react";
import "./Button.css";

/** variant: "primary" | "secondary" | "danger" — 페이지마다 버튼 스타일을 새로 정의하지 않게 한다. */
export default function Button({ variant = "primary", children, ...rest }) {
  return (
    <button className={`btn btn-${variant}`} {...rest}>
      {children}
    </button>
  );
}
