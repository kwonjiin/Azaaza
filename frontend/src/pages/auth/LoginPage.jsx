import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Card from "../../components/common/Card";
import Button from "../../components/common/Button";
import ErrorMessage from "../../components/common/ErrorMessage";
import useAuth from "../../hooks/useAuth";
import useSubmit from "../../hooks/useSubmit";
import "./AuthPage.css";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const { submit, submitting, error } = useSubmit(login);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await submit(form);
      navigate("/dashboard", { replace: true });
    } catch {
      // 에러는 useSubmit이 이미 담아뒀다 — 여기선 성공했을 때 할 일만 신경 쓴다.
    }
  };

  return (
    <div className="auth-page">
      <Card>
        <div className="auth-card">
          <h1>다시 오신 걸 환영해요</h1>
          <p className="subtitle">오늘의 습관을 기록하러 가볼까요.</p>

          <form onSubmit={handleSubmit}>
            <div className="auth-field">
              <label htmlFor="email">이메일</label>
              <input id="email" name="email" type="email" required value={form.email} onChange={handleChange} />
            </div>
            <div className="auth-field">
              <label htmlFor="password">비밀번호</label>
              <input
                id="password"
                name="password"
                type="password"
                required
                value={form.password}
                onChange={handleChange}
              />
            </div>

            <ErrorMessage error={error} />

            <Button type="submit" className="auth-submit" disabled={submitting}>
              {submitting ? "로그인 중..." : "로그인"}
            </Button>
          </form>

          <p className="auth-switch">
            아직 계정이 없나요? <Link to="/signup">회원가입</Link>
          </p>
        </div>
      </Card>
    </div>
  );
}
