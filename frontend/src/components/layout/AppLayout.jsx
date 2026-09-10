import React from "react";
import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";

/** 인증된 화면 전부가 공유하는 뼈대. 페이지마다 Navbar를 반복해서 넣지 않는다. */
export default function AppLayout() {
  return (
    <div>
      <Navbar />
      <main style={{ maxWidth: 1080, margin: "0 auto", padding: "24px" }}>
        <Outlet />
      </main>
    </div>
  );
}
