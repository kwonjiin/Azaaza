import React from "react";
import "./Card.css";

export default function Card({ title, action, children }) {
  return (
    <section className="card">
      {(title || action) && (
        <header className="card-header">
          {title && <h3>{title}</h3>}
          {action}
        </header>
      )}
      <div className="card-body">{children}</div>
    </section>
  );
}
