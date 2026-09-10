import api from "./api";

export async function getHabits({ category, animalId } = {}) {
  const { data } = await api.get("/habits", { params: { category, animalId } });
  return data;
}

export async function getHabitDetail(habitId) {
  const { data } = await api.get(`/habits/${habitId}`);
  return data;
}

export async function createHabit(payload) {
  // payload: { animalId, title, description, category, targetType, targetValue, targetUnit }
  const { data } = await api.post("/habits", payload);
  return data;
}

export async function updateHabit(habitId, payload) {
  const { data } = await api.put(`/habits/${habitId}`, payload);
  return data;
}

export async function deleteHabit(habitId) {
  await api.delete(`/habits/${habitId}`);
}
