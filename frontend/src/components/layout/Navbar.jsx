import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import Button from "../common/Button";
import "./Navbar.css";

const LINKS = [
  { to: "/dashboard", label: "대시보드" },
  { to: "/habits", label: "습관 관리" },
  { to: "/animals", label: "동물 프로필" },
  { to: "/diaries", label: "일기" },
];

export default function Navbar() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <nav className="navbar">
      <span className="navbar-brand">HabitPet</span>
      <div className="navbar-links">
        {LINKS.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? "navbar-link active" : "navbar-link")}
          >
            {link.label}
          </NavLink>
        ))}
      </div>
      <Button variant="secondary" onClick={handleLogout}>
        로그아웃
      </Button>
    </nav>
  );
}
