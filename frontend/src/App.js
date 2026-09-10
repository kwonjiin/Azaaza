import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./components/common/ProtectedRoute";
import AppLayout from "./components/layout/AppLayout";
import LoginPage from "./pages/auth/LoginPage";
import SignupPage from "./pages/auth/SignupPage";
import DashboardPage from "./pages/dashboard/DashboardPage";
import HabitsPage from "./pages/habits/HabitsPage";
import AnimalsPage from "./pages/animals/AnimalsPage";
import AnimalDetailPage from "./pages/animals/AnimalDetailPage";
import DiariesPage from "./pages/diaries/DiariesPage";
import DiaryDetailPage from "./pages/diaries/DiaryDetailPage";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />

      {/* 인증 필요한 화면들: ProtectedRoute가 게이트, AppLayout이 공통 뼈대(Navbar) */}
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/habits" element={<HabitsPage />} />
          <Route path="/animals" element={<AnimalsPage />} />
          <Route path="/animals/:animalId" element={<AnimalDetailPage />} />
          <Route path="/diaries" element={<DiariesPage />} />
          <Route path="/diaries/:diaryId" element={<DiaryDetailPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
