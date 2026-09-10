import api from "./api";

export async function createHabitRecord(habitId, { date, completed, actualValue }) {
  const { data } = await api.post(`/habits/${habitId}/records`, { date, completed, actualValue });
  return data; // { ...record, earnedPoint, earnedExp, animal }
}

export async function getHabitRecords(habitId, { from, to }) {
  const { data } = await api.get(`/habits/${habitId}/records`, { params: { from, to } });
  return data;
}
