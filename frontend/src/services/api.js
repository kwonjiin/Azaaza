import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api";
const TOKEN_STORAGE_KEY = "habitpet_access_token";

const api = axios.create({ baseURL: BASE_URL });

// 요청마다 토큰을 붙이는 걸 서비스 함수 하나하나에 반복하지 않으려고 인터셉터로 뺐다.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 401이 오면 토큰이 만료/무효한 것 — 로그인 페이지로 보내기 전에 로컬 토큰부터 지운다.
// AuthContext가 아니라 여기서 처리하는 이유: axios 인터셉터는 React 트리 바깥에서도 동작해야
// 하고(React 컴포넌트 라이프사이클과 무관하게), "인증 실패 시 무엇을 할지"는 API 레이어의 책임으로 뒀다.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export { TOKEN_STORAGE_KEY };
export default api;
