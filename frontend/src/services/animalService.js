import api from "./api";

export async function getMyAnimals() {
  const { data } = await api.get("/animals");
  return data;
}

export async function getAnimalDetail(animalId) {
  const { data } = await api.get(`/animals/${animalId}`);
  return data;
}

export async function createAnimal({ name, type }) {
  const { data } = await api.post("/animals", { name, type });
  return data;
}
