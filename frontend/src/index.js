import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import { AuthProvider } from "./contexts/AuthContext";
import "./index.css";

const root = ReactDOM.createRoot(document.getElementById("root"));

// Provider 순서: Router가 바깥, AuthProvider가 안쪽 — AuthContext가 useNavigate 등
// 라우터 훅을 나중에 쓸 수도 있어서(로그아웃 시 리다이렉트 등) Router 하위에 둔다.
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
