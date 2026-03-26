# FitLog - Backend
<img width="800" alt="image" src="https://github.com/user-attachments/assets/a120dda1-b2ae-45f0-990a-dcc06ea4bad1" />

운동 기록 및 투두 관리 REST API 서버입니다.  
Spring Boot + MyBatis + MySQL 기반으로 구축되었으며, JWT 인증 체계와 Spring Security를 활용합니다.

<br/>

---

## Backend Architecture Structure

본 프로젝트의 백엔드는 명확한 계층 분리와 유지보수성을 확보하기 위해 **Layered Architecture** 를 기반으로 설계되었습니다.  
Spring Security + JWT 이중 토큰 전략으로 인증/인가를 처리하며, MyBatis XML Mapper를 통해 SQL을 명시적으로 관리합니다.

### Key Architectural Concepts

**Layered Architecture + Security Filter Chain**

- 각 계층(Presentation → Application → Data Access)이 단방향 의존성을 갖도록 설계하여 관심사를 분리했습니다.
- Spring Security의 **Filter Chain** 을 커스터마이징하여, 모든 요청이 컨트롤러에 도달하기 전 JWT 검증을 수행합니다.
- `SecurityUtil` 유틸리티를 통해 서비스 계층 어디서든 현재 인증된 사용자 정보를 꺼낼 수 있습니다.

```mermaid
graph TD
    subgraph Client ["Client"]
        HTTP["HTTP Request\n(Authorization: Bearer Token)"]
    end

    subgraph Security ["Security Layer"]
        Filter["JwtAuthenticationFilter\nOncePerRequestFilter"]
        Entry["CustomAuthenticationEntryPoint\n401 Unauthorized"]
    end

    subgraph Presentation ["Presentation Layer (Controller)"]
        AuthCtrl["AuthController"]
        TodoCtrl["TodoController"]
        ExCtrl["ExerciseController"]
        ProCtrl["ProfileController"]
    end

    subgraph Application ["Application Layer (Service)"]
        AuthSvc["AuthService"]
        TodoSvc["TodoService"]
        ExSvc["ExerciseService"]
        ProSvc["ProfileService"]
    end

    subgraph DataAccess ["Data Access Layer (MyBatis Mapper + XML)"]
        AuthMap["AuthMapper"]
        TodoMap["TodoMapper"]
        ExMap["ExerciseMapper"]
        ProMap["ProfileMapper"]
    end

    subgraph DB ["Database"]
        MySQL[("MySQL\nusers · todos · exercises")]
    end

    HTTP --> Filter
    Filter -- "토큰 유효 → SecurityContext 등록" --> Presentation
    Filter -- "토큰 만료/없음" --> Entry

    AuthCtrl --> AuthSvc
    TodoCtrl --> TodoSvc
    ExCtrl --> ExSvc
    ProCtrl --> ProSvc

    AuthSvc --> AuthMap
    TodoSvc --> TodoMap
    ExSvc --> ExMap
    ExSvc --> TodoMap
    ProSvc --> ProMap

    AuthMap --> MySQL
    TodoMap --> MySQL
    ExMap --> MySQL
    ProMap --> MySQL
```

<br/>

---

**JWT 이중 토큰 전략 (Access Token + Refresh Token)**

보안성과 사용자 경험을 동시에 확보하기 위해 수명이 다른 두 토큰을 운용합니다.

- **Access Token**: 짧은 수명(1시간)으로 탈취 피해를 최소화. HTTP 응답 body로 전달합니다.
- **Refresh Token**: 긴 수명(7일)으로 재로그인 빈도를 낮춤. `HttpOnly Cookie` 로 전달하여 XSS 공격을 차단합니다.
- Refresh Token은 DB에 저장하여 서버에서 강제 폐기(로그아웃)가 가능합니다.

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant JWT as JwtTokenProvider
    participant DB as Database

    Note over C,DB: 1. 로그인
    C->>AC: POST /auth/login
    AC->>AS: login(loginId, password)
    AS->>JWT: createAccessToken(loginId)
    AS->>JWT: createRefreshToken(loginId)
    JWT-->>AS: "accessToken, refreshToken"
    AS->>DB: "UPDATE users SET refresh_token"
    AS-->>AC: LoginResponseDTO
    AC-->>C: "body: accessToken / cookie: refreshToken (HttpOnly)"

    Note over C,DB: 2. 인증된 API 요청
    C->>AC: "Any API / Authorization: Bearer accessToken"
    AC->>JWT: validateToken(token)
    alt 토큰 유효
        JWT-->>AC: "true"
        AC->>AC: "SecurityContext에 loginId 등록"
    else 토큰 만료/없음
        JWT-->>AC: "false"
        AC-->>C: 401 Unauthorized
    end

    Note over C,DB: 3. 토큰 재발급
    C->>AC: "POST /auth/refresh (쿠키: refreshToken)"
    AC->>AS: refreshAccessToken(request)
    AS->>DB: "SELECT refresh_token WHERE loginId"
    DB-->>AS: storedToken
    AS->>JWT: validateToken(refreshToken)
    AS-->>AC: "새 accessToken"
    AC-->>C: "accessToken"

    Note over C,DB: 4. 로그아웃
    C->>AC: POST /auth/logout
    AC->>AS: logout(request)
    AS->>DB: "UPDATE users SET refresh_token = NULL"
    AC-->>C: "Set-Cookie: refreshToken MaxAge=0"

```

<br/>

---

**운동 기록 관리 흐름 (Todo Domain)**

`workout_id` 전략으로 세트 그룹을 관리합니다. 운동 항목 생성 시 `todo_id`를 `workout_id`로 사용하며, 이후 추가되는 세트들은 동일한 `workout_id`를 공유합니다.

```mermaid
flowchart TD
    Start(["POST /todos — 운동 항목 생성"])
    Insert["todos 테이블에 INSERT\nsets_number=1, workout_id=0 임시"]
    SetWorkout["workout_id = 생성된 todo_id\n자기 자신을 그룹 키로 사용"]

    AddSet(["POST /todos/{todoId}/sets — 세트 추가"])
    FindWorkout["workout_id 조회\n기준 todoId 기반"]
    CalcNext["MAX(sets_number) + 1 계산"]
    InsertSet["동일 workout_id로 새 세트 INSERT"]

    Delete(["DELETE /todos/{todoId} — 세트 삭제"])
    DelRow["해당 todo_id 행 삭제"]
    Reorder["workout_id 기준 sets_number 재정렬\nROW_NUMBER OVER ORDER BY"]

    Start --> Insert --> SetWorkout
    AddSet --> FindWorkout --> CalcNext --> InsertSet
    Delete --> DelRow --> Reorder
```

<br/>

---

**칼로리 계산 전략 (MET 기반)**

단순 횟수 × 계수가 아닌, 실제 운동 시간을 추정하여 보다 정확한 소모 칼로리를 계산합니다.

```mermaid
graph LR
    subgraph Input
        R["reps_target"]
        W["weight (kg)"]
        S["sets_number"]
        RT["rest_time (초)"]
        MET["caloriesPerRep (MET 계수)"]
    end

    subgraph Calc ["CalorieCalculator — ExerciseService"]
        RepSec["rep당 소요 시간 보정\n≥80kg → 4.0s / ≥50kg → 3.5s / 기본 → 2.5s"]
        Total["총 운동 시간(초)\n= sets × reps × repSec + (sets-1) × restTime"]
        Result["소모 칼로리\n= MET × 60kg × (시간 / 3600)"]
    end

    W --> RepSec
    R & S & RT --> Total
    RepSec --> Total
    Total & MET --> Result
```

> 마지막 세트의 휴식시간은 0으로 처리하여 불필요한 시간이 칼로리에 포함되지 않도록 합니다.

<br/>

---

**프로필 이미지 처리 파이프라인**

외부 스토리지 없이 DB에 직접 Base64로 저장하는 방식으로, 별도 인프라 없이 이미지를 관리합니다.

```mermaid
flowchart LR
    Upload(["MultipartFile 업로드"])
    SizeCheck{"파일 크기\n≤ 5MB"}
    TypeCheck{"MIME 타입\njpeg·png·jpg·webp"}
    ImageCheck{"실제 이미지\nBufferedImage 파싱"}
    Resize["300×300 리사이징\n비율 유지, SCALE_SMOOTH"]
    Convert["JPEG 변환 → Base64 인코딩"]
    Save["DB 저장\ndata:image/jpeg;base64,..."]
    Error(["BusinessException 반환"])

    Upload --> SizeCheck
    SizeCheck -- "초과" --> Error
    SizeCheck -- "통과" --> TypeCheck
    TypeCheck -- "불일치" --> Error
    TypeCheck -- "통과" --> ImageCheck
    ImageCheck -- "실패" --> Error
    ImageCheck -- "통과" --> Resize
    Resize --> Convert --> Save
```

<br/>

---

## 📁 프로젝트 구조

```
src/main/java/com/ureca/fitlog/
├── auth/
│   ├── controller/         AuthController.java
│   ├── service/            AuthService.java
│   ├── mapper/             AuthMapper.java
│   ├── jwt/                JwtTokenProvider.java
│   │                       JwtAuthenticationFilter.java
│   └── dto/                request/ · response/
│
├── todos/
│   ├── controller/         TodoController.java
│   ├── service/            TodoService.java
│   ├── mapper/             TodoMapper.java
│   └── dto/                request/ · response/
│
├── exercise/
│   ├── controller/         ExerciseController.java
│   ├── service/            ExerciseService.java
│   ├── mapper/             ExerciseMapper.java
│   └── dto/                response/
│
├── profile/
│   ├── controller/         ProfileController.java
│   ├── service/            ProfileService.java
│   ├── mapper/             ProfileMapper.java
│   └── dto/                request/ · response/
│
├── common/
│   ├── SecurityUtil.java
│   ├── dto/                ApiMessageResponse.java
│   └── exception/          BusinessException.java
│                           ExceptionStatus.java
│                           GlobalExceptionHandler.java
│
└── config/
    ├── SecurityConfig.java
    ├── CorsConfig.java
    ├── SwaggerConfig.java
    └── CustomAuthenticationEntryPoint.java

src/main/resources/
├── mapper/
│   ├── AuthMapper.xml
│   ├── TodoMapper.xml
│   ├── ExerciseMapper.xml
│   └── ProfileMapper.xml
└── application.yml
```

<br/>

---

## ⚙️ 실행 방법

**1. 사전 조건**

- Java 17+
- MySQL 8.x 실행 중 + `fitlog` 데이터베이스 생성

**2. `application.yml` 설정**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fitlog
    username: root
    password: your_password

jwt:
  secret: your_secret_key_here   # 최소 32바이트 권장
  access-expiration: 3600000     # 1시간 (ms)
  refresh-expiration: 604800000  # 7일 (ms)
```

> 배포 시 `SecurityConfig` 내 `refreshCookie.setSecure(false)` → `true` 변경 필수

**3. 빌드 및 실행**

```bash
./gradlew bootRun
```

**4. API 문서 (Swagger)**

서버 실행 후 → `http://localhost:8080/swagger-ui`

<br/>

---

## 🔐 API 인증 방법

```
1. POST /auth/login        →  accessToken 수령
2. 모든 요청 헤더에 포함   →  Authorization: Bearer <accessToken>
3. 401 응답 시             →  POST /auth/refresh  →  새 accessToken 수령
                               (HttpOnly 쿠키의 refreshToken 자동 전송)
```

<br/>

---

## 🧑‍💻 팀원

|                  이름                  | 역할               |
| :------------------------------------: | :----------------- |
|  [김주희](https://github.com/joooii)   | PM, FE, BE, Design |
| [박준형](https://github.com/joonhyong) | FE, BE, Design     |
