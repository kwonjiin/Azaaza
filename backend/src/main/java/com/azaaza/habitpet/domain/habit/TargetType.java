package com.azaaza.habitpet.domain.habit;

/**
 * 습관의 수행 방식.
 * CHECK : 했다/안했다만 기록 (targetValue 없음)
 * NUMBER: 목표 수치를 정해두고 실제 수행량을 기록 (예: 물 500ml, 공부 60분)
 */
public enum TargetType {
    CHECK, NUMBER
}
