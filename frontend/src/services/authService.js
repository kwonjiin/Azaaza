import api, { TOKEN_STORAGE_KEY } from "./api";

export async function signup({ email, password, nickname }) {
  const { data } = await api.post("/auth/signup", { email, password, nickname });
  return data; // { id, email, nickname }
}

export async function login({ email, password }) {
  const { data } = await api.post("/auth/login", { email, password });
  localStorage.setItem(TOKEN_STORAGE_KEY, data.accessToken);
  return data; // { accessToken, tokenType, expiresIn }
}

export function logout() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
}

export function getStoredToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}
