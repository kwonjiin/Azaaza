# API 설계 (MVP 1단계)

- Base path: `/api`
- 인증: `Authorization: Bearer <accessToken>` (JWT). `/api/auth/**`를 제외한 모든 엔드포인트는 필수.
- **userId는 절대 요청 바디/경로 파라미터로 받지 않는다.** 항상 토큰에서 추출한 인증 사용자 기준으로 조회/수정 범위를 제한한다 (IDOR 방지).
- 날짜는 `yyyy-MM-dd`(ISO-8601 LocalDate), 시각은 `yyyy-MM-dd'T'HH:mm:ss`.
- 성공 응답은 리소스(또는 배열)를 그대로 반환하고, 리스트 래핑 객체(`{data: [...]}`)는 쓰지 않는다. 페이지네이션이 필요해지면 그때 `Page<T>` 형태로 확장.

## 공통 에러 응답

전역 예외 핸들러(`@RestControllerAdvice`)에서 아래 형식으로 통일한다.

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "요청 값이 올바르지 않습니다.",
  "errors": [
    { "field": "title", "reason": "must not be blank" }
  ]
}
```

| status | code | 예시 상황 |
|---|---|---|
| 400 | VALIDATION_ERROR | Bean Validation 실패 |
| 401 | UNAUTHORIZED | 토큰 없음/만료 |
| 403 | FORBIDDEN | 남의 리소스 접근 시도 |
| 404 | NOT_FOUND | 존재하지 않는 habit/animal 등 |
| 409 | DUPLICATE_RECORD | 같은 날 같은 습관 중복 기록, 이메일 중복 등 |

---

## 1. Auth

### POST /api/auth/signup
```json
// Request
{ "email": "user@example.com", "password": "P@ssw0rd!", "nickname": "지민" }
```
```json
// 201 Response
{ "id": 1, "email": "user@example.com", "nickname": "지민" }
```

### POST /api/auth/login
```json
// Request
{ "email": "user@example.com", "password": "P@ssw0rd!" }
```
```json
// 200 Response
{ "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer", "expiresIn": 3600 }
```

---

## 2. Animals

### POST /api/animals
```json
// Request
{ "name": "몽이", "type": "DOG" }
```
```json
// 201 Response
{ "id": 1, "name": "몽이", "type": "DOG", "level": 1, "experience": 0, "createdAt": "2026-09-09T04:00:00" }
```

### GET /api/animals
현재 사용자의 동물 전체 목록.
```json
// 200 Response
[
  { "id": 1, "name": "몽이", "type": "DOG", "level": 3, "experience": 45 }
]
```

### GET /api/animals/{animalId}
```json
// 200 Response
{ "id": 1, "name": "몽이", "type": "DOG", "level": 3, "experience": 45, "habitCount": 2, "createdAt": "2026-09-01T04:00:00" }
```

---

## 3. Habits

### POST /api/habits
```json
// Request
{
  "animalId": 1,
  "title": "물 2L 마시기",
  "description": "하루 목표 수분 섭취량",
  "category": "HEALTH",
  "targetType": "NUMBER",
  "targetValue": 2000,
  "targetUnit": "ml"
}
```
```json
// 201 Response
{
  "id": 5,
  "animalId": 1,
  "animalName": "몽이",
  "title": "물 2L 마시기",
  "description": "하루 목표 수분 섭취량",
  "category": "HEALTH",
  "targetType": "NUMBER",
  "targetValue": 2000,
  "targetUnit": "ml",
  "createdAt": "2026-09-09T10:00:00"
}
```
`animalId`는 요청한 사용자 소유의 동물이어야 하며, 아니면 `404 NOT_FOUND`(존재를 노출하지 않기 위해 403 대신 404).

### GET /api/habits?category=HEALTH&animalId=1
쿼리 파라미터는 모두 선택. 목록에는 날짜 종속 필드(오늘 수행 여부 등)를 넣지 않는다 — 그건 `/dashboard`의 책임.
```json
// 200 Response
[
  { "id": 5, "animalId": 1, "animalName": "몽이", "title": "물 2L 마시기", "category": "HEALTH", "targetType": "NUMBER", "targetValue": 2000, "targetUnit": "ml" }
]
```

### GET /api/habits/{habitId}
### PUT /api/habits/{habitId}
```json
// Request (animalId는 MVP에서 변경 불가 — 습관을 다른 동물로 옮기는 기능은 범위 밖)
{
  "title": "물 2.5L 마시기",
  "description": "여름철 목표 상향",
  "category": "HEALTH",
  "targetType": "NUMBER",
  "targetValue": 2500,
  "targetUnit": "ml"
}
```
```json
// 200 Response
{ "id": 5, "animalId": 1, "animalName": "몽이", "title": "물 2.5L 마시기", "category": "HEALTH", "targetType": "NUMBER", "targetValue": 2500, "targetUnit": "ml" }
```

### DELETE /api/habits/{habitId}
`204 No Content`. 연관된 `HabitRecord`는 cascade 삭제(습관을 지우면 그 기록도 의미가 없으므로).

---

## 4. Habit Records

습관에 종속된 리소스라 `/habits/{habitId}/records`로 중첩했다 (기록은 항상 특정 habit의 맥락에서만 조회/생성되기 때문 — Diary는 반대로 flat, 아래 참고).

### POST /api/habits/{habitId}/records
```json
// Request
{ "date": "2026-09-09", "completed": true, "actualValue": 1800 }
```
```json
// 201 Response
{
  "id": 42,
  "habitId": 5,
  "date": "2026-09-09",
  "completed": true,
  "actualValue": 1800,
  "earnedPoint": 10,
  "earnedExp": 10,
  "animal": { "id": 1, "level": 2, "experience": 30 }
}
```
- 같은 `(habitId, date)`로 재요청하면 `409 DUPLICATE_RECORD`.
- 응답에 `earnedPoint`/`earnedExp`와 갱신된 `animal` 스냅샷을 함께 내려줘서, 프론트가 별도 조회 없이 레벨업 연출(예: "레벨업!" 토스트)을 바로 트리거할 수 있게 했다.

### GET /api/habits/{habitId}/records?from=2026-09-01&to=2026-09-09
```json
// 200 Response
[
  { "id": 42, "date": "2026-09-09", "completed": true, "actualValue": 1800 },
  { "id": 41, "date": "2026-09-08", "completed": false, "actualValue": 500 }
]
```

---

## 5. Diaries

동물 하나가 여러 습관을 가질 수 있으므로, 일기는 특정 habit이 아니라 animal에 종속된다 → flat 리소스로 두고 `animalId` 쿼리로 필터링.

### POST /api/diaries/generate-today
스케줄러(매일 04:00) 또는 수동 트리거 겸용. **멱등하게 설계** — 이미 오늘 일기가 있는 동물은 재생성하지 않고 기존 것을 그대로 반환한다. 배치가 중간에 실패해 재시도되어도 중복 생성이 없다.
```json
// 201 Response (신규 생성 + 기존 것 혼재 가능)
[
  {
    "id": 100,
    "animalId": 1,
    "animalName": "몽이",
    "date": "2026-09-09",
    "content": "오늘 주인님이 물 2L 마시기를 완수해서 나도 기분이 좋았다. 10 포인트를 벌어서 츄르를 사고 싶지만 아직 참았다.",
    "createdAt": "2026-09-09T04:00:03"
  }
]
```

### GET /api/diaries?animalId=1&from=2026-09-01&to=2026-09-09
### GET /api/diaries/{diaryId}

---

## 6. Dashboard

### GET /api/dashboard
그날그날 계산해야 하는 값(오늘 수행 여부, 주간 수행률)을 한 번에 묶어서 내려줘 프론트가 여러 번 조합 호출을 하지 않게 한다.
```json
// 200 Response
{
  "user": { "nickname": "지민", "point": 230 },
  "animals": [
    { "id": 1, "name": "몽이", "type": "DOG", "level": 3, "experience": 45 }
  ],
  "habits": [
    {
      "id": 5,
      "title": "물 2L 마시기",
      "category": "HEALTH",
      "animalId": 1,
      "animalName": "몽이",
      "todayCompleted": true,
      "weeklyRate": 0.71
    }
  ],
  "stats": { "weeklyAverageRate": 0.68, "totalPoint": 230 }
}
```

---

## 엔드포인트 요약

| Method | Path | 설명 |
|---|---|---|
| POST | /api/auth/signup | 회원가입 |
| POST | /api/auth/login | 로그인, 토큰 발급 |
| POST | /api/animals | 동물 생성 |
| GET | /api/animals | 내 동물 목록 |
| GET | /api/animals/{id} | 동물 상세 |
| POST | /api/habits | 습관 생성 |
| GET | /api/habits | 습관 목록 (필터: category, animalId) |
| GET | /api/habits/{id} | 습관 상세 |
| PUT | /api/habits/{id} | 습관 수정 |
| DELETE | /api/habits/{id} | 습관 삭제 |
| POST | /api/habits/{id}/records | 오늘(또는 특정 날짜) 수행 기록 + 포인트/경험치 지급 |
| GET | /api/habits/{id}/records | 기록 조회 (기간 필터) |
| POST | /api/diaries/generate-today | 오늘자 일기 생성(멱등) |
| GET | /api/diaries | 일기 목록 (필터: animalId, 기간) |
| GET | /api/diaries/{id} | 일기 상세 |
| GET | /api/dashboard | 대시보드 집계 |
