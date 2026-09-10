import React, { createContext, useCallback, useState } from "react";
import * as authService from "../services/authService";

export const AuthContext = createContext(null);

/**
 * 서버에 "지금 로그인된 유저가 누구인지" 물어보는 /users/me 같은 엔드포인트를
 * MVP 범위에 안 넣었기 때문에(대시보드가 사실상 그 역할을 겸함), 이 컨텍스트는
 * "인증됐는지 여부"만 관리한다. 닉네임/포인트 같은 프로필 정보는 DashboardPage가
 * /dashboard 호출로 직접 받아온다 — 상태를 두 군데서 중복 관리하지 않기 위해.
 */
export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(!!authService.getStoredToken());

  const login = useCallback(async (credentials) => {
    await authService.login(credentials);
    setIsAuthenticated(true);
  }, []);

  const signup = useCallback(async (payload) => {
    return authService.signup(payload);
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setIsAuthenticated(false);
  }, []);

  const value = { isAuthenticated, login, signup, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
