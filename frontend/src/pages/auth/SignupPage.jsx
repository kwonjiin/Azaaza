import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Card from "../../components/common/Card";
import Button from "../../components/common/Button";
import ErrorMessage from "../../components/common/ErrorMessage";
import useAuth from "../../hooks/useAuth";
import useSubmit from "../../hooks/useSubmit";
import "./AuthPage.css";

export default function SignupPage() {
  const { signup } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "", nickname: "" });
  const { submit, submitting, error } = useSubmit(signup);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await submit(form);
      // 회원가입은 토큰을 안 내려주므로(POST /auth/signup 응답에 accessToken 없음, API.md 참고)
      // 여기서 자동 로그인하지 않고, 로그인 페이지로 보내 같은 자격증명으로 다시 로그인하게 한다.
      navigate("/login", { replace: true });
    } catch {
      // 에러는 useSubmit이 이미 담아뒀다.
    }
  };

  return (
    <div className="auth-page">
      <Card>
        <div className="auth-card">
          <h1>습관 실험을 시작해요</h1>
          <p className="subtitle">동물 동반자와 함께할 계정을 만들어요.</p>

          <form onSubmit={handleSubmit}>
            <div className="auth-field">
              <label htmlFor="nickname">닉네임</label>
              <input id="nickname" name="nickname" required value={form.nickname} onChange={handleChange} />
            </div>
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
                minLength={8}
                value={form.password}
                onChange={handleChange}
              />
            </div>

            <ErrorMessage error={error} />

            <Button type="submit" className="auth-submit" disabled={submitting}>
              {submitting ? "가입 중..." : "회원가입"}
            </Button>
          </form>

          <p className="auth-switch">
            이미 계정이 있나요? <Link to="/login">로그인</Link>
          </p>
        </div>
      </Card>
    </div>
  );
}
