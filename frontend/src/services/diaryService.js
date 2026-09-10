import api from "./api";

export async function generateTodayDiaries() {
  const { data } = await api.post("/diaries/generate-today");
  return data;
}

export async function getDiaries({ animalId, from, to }) {
  const { data } = await api.get("/diaries", { params: { animalId, from, to } });
  return data;
}

export async function getDiaryDetail(diaryId) {
  const { data } = await api.get(`/diaries/${diaryId}`);
  return data;
}
