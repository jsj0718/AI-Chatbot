## 🧾 Chatbot API – Use Case 기반 문서

### 📌 소개
이 프로젝트는 **사용자와 GPT 모델 간 대화**를 주고받는 챗봇 서비스입니다. 회원 가입, 로그인, 채팅, 대화 관리, 피드백, 관리자 통계 및 보고서 기능을 제공합니다.

---

## 👤 사용자 기능

### 1. 회원가입
- **설명**: 새로운 사용자를 등록합니다. (테스트 환경이기에 admin 값을 통해 권한이 member, admin으로 구분됩니다.)
- **Endpoint**: `POST /api/signup`
- **Request Body**:
```json
{
  "email": "test@example.com",
  "password": "12345678",
  "name": "홍길동",
  "admin": false
}
```
- **Response**: `200 OK` (본문 없음)

---

### 2. 로그인
- **설명**: JWT를 발급받습니다.
- **Endpoint**: `POST /api/login`
- **Request Body**:
```json
{
  "email": "test@example.com",
  "password": "12345678"
}
```
- **Response**: `200 OK`
```text
{JWT 토큰 문자열}
```

---

## 💬 대화 기능

### 3. 채팅 요청
- **설명**: 질문을 입력하면 GPT 모델이 응답합니다.
- **참고사항**: 개발 공수 및 설계적인 문제로 인해 스트리밍 기능은 제한됩니다.
- **Endpoint**: `POST /api/chat`
- **Header**: `Authorization: Bearer {JWT}`
- **Request Body**:
```json
{
  "question": "AI란 무엇인가요?",
  "model": "gpt-3.5"
}
```
- **Response**:
```json
{
  "answer": "AI는 인공지능의 약자로..."
}
```

---

### 4. 대화 내역 조회
- **설명**: 스레드 단위로 본인이 생성한 대화 기록을 조회합니다.
- **Endpoint**: `GET /api/chat/history`
- **Header**: `Authorization: Bearer {JWT}`
- **Query Parameters**:
    - `page`: 페이지 번호 (기본값: 0)
    - `size`: 페이지 크기 (기본값: 10)
    - `sort`: 정렬 순서 (`asc`, `desc`, 기본값: `desc`)
- **Response**:
```json
{
  "content": [
    {
      "threadId": "Thread's UUID",
      "threadCreatedAt": "2025-04-12T...",
      "chats": [
        {
          "chatId": "Chat's UUID",
          "question": "...",
          "answer": "...",
          "createdAt": "..."
        }
      ]
    }
  ]
}
```

---

### 5. 스레드 삭제
- **설명**: 자신이 생성한 스레드를 삭제합니다.
- **Endpoint**: `DELETE /api/thread/{Thread's UUID}`
- **Header**: `Authorization: Bearer {JWT}`
- **Response**: `204 No Content`

---

## 📢 피드백 기능

### 6. 피드백 등록
- **설명**: 본인의 대화에 대해 긍정/부정 피드백을 남깁니다.
- **Endpoint**: `POST /api/feedback`
- **Request Body**:
```json
{
  "chatId": "Chat's UUID",
  "positive": true
}
```
- **Response**:
```json
{
  "id": "Feedback's UUID",
  "chatId": "Chat's UUID",
  "positive": true,
  "status": "PENDING",
  "createdAt": "2025-04-12T..."
}
```

---

### 7. 피드백 목록 조회
- **설명**: 본인의 피드백 내역을 확인하거나 관리자는 전체 조회 가능
- **Endpoint**: `GET /api/feedback`
- **Query Parameters**:
    - `positive`: true/false (선택)
    - `page`: 페이지 번호 (기본값: 0)
    - `size`: 페이지 크기 (기본값: 10)
    - `sort`: 정렬 순서 (`asc`, `desc`, 기본값: `desc`)
- **Response**:
```json
[
  {
    "id": "Feedback's UUID",
    "chatId": "Chat's UUID",
    "positive": true,
    "status": "PENDING",
    "createdAt": "..."
  }
]
```

---

### 8. 피드백 상태 변경 (관리자만)
- **설명**: 피드백 상태를 `PENDING → RESOLVED` 등으로 변경
- **Endpoint**: `PATCH /api/feedback/{Feedback's UUID}?status=RESOLVED`
- **Response**: `204 No Content`

---

## 🛠️ 관리자 기능

### 9. 활동 통계 조회
- **설명**: 하루 동안의 가입, 로그인, 대화 생성 건수를 조회
- **Endpoint**: `GET /api/report/activity`
- **Header**: `Authorization: Bearer {ADMIN JWT}`
- **Response**:
```json
{
  "signupCount": 5,
  "loginCount": 12,
  "chatCount": 18
}
```

---

### 10. 하루치 대화 보고서 다운로드
- **설명**: 전날 생성된 모든 대화 내용을 CSV 파일로 다운로드
- **컬럼**: "질문", "답변", "생성일시", "이메일", "이름"
- **Endpoint**: `GET /api/report/daily`
- **Header**: `Authorization: Bearer {ADMIN JWT}`
- **Response**:
    - `Content-Type: text/csv`
    - 첨부파일 다운로드 (`Content-Disposition: attachment; filename=daily_report.csv`)

