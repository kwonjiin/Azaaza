package com.azaaza.habitpet.domain.animal;

/**
 * 선택 가능한 동물 종류. MVP에서는 마스터 데이터 테이블 대신 enum으로 고정한다.
 * 종류가 늘어나거나 종별로 속성(이미지, 성장 곡선 등)이 많아지면
 * AnimalSpecies 테이블로 분리하는 게 맞다 — 그 전까지는 enum이 더 단순하다.
 */
public enum AnimalType {
    DOG, CAT, BIRD, LIZARD
}
