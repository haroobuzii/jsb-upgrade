# 자료 분석과 결정 사항

이 문서는 진행 기준이다. 새 작업을 시작할 때 이 문서의 **결정 사항**과 **로드맵**을 따른다.
결정을 바꾸면 이 문서를 먼저 고치고 커밋한다.

## 1. 자료별 분석 요약

### 점프 투 스프링부트 (교재, sbb3)
- 교재: https://wikidocs.net/book/7601 — 2-01~2-16, 3-01~3-14 (4장 배포는 범위 밖)
  - wikidocs는 자동 요청(curl)을 막는다. 내장 브라우저로는 열린다 → 각 절 시작 시 본문을 직접 읽는다.
- 코드: https://github.com/pahkey/sbb3 — 절마다 태그(`v2.02`~`v3.13`)가 있어 절별 변경 파일을 정확히 볼 수 있다.
  - 최종 코드: Boot 3.2.1, Java 19, Groovy `build.gradle`
- 절별 주요 추가 사항
  - 2-03 JPA·H2 / 2-05 리포지터리 + JUnit / 2-07 Thymeleaf / 2-15 layout-dialect / 2-16 validation, 폼
  - 3-02 페이징(테스트로 300건 생성) / 3-05 Security / 3-06~07 `SiteUser`, 가입, 로그인 / 3-08 `author`, `@PreAuthorize`
  - 3-09 수정·삭제, `modifyDate` / 3-10 추천(`@ManyToMany voter`) / 3-12 commonmark, `CommonUtil` / 3-13 검색(Specification → JPQL)
- 교재 코드에서 고칠 점
  - Security 7에서 `AntPathRequestMatcher` 제거 → 교재 코드 그대로 동작하지 않음
  - 삭제·추천·로그아웃이 GET
  - 마크다운을 `th:utext`로 출력 → XSS
  - 엔티티 `@Setter`, 권한 오류를 400으로 응답, 테스트가 실제 DB에 데이터 생성, `answerList.size()` N+1

### 14163 점프 투 스프링부트 개선 (https://www.slog.gg/p/14163/raw, https://github.com/jhs512/p-14163-2)
- 39강, 교재 **2-14까지만** 다룸. 강마다 태그(002~039).
- 핵심 개선
  - Boot 4.1.2 / Java 25 / Kotlin DSL, Boot 4의 starter 이름 변경(`starter-webmvc`, `spring-boot-h2console`, `*-test`) (2강)
  - `application.yaml` + dev/test 프로파일, dev는 파일 DB, test는 메모리 DB (5강)
  - SQL 포맷·하이라이트, 바인딩 값·트랜잭션 TRACE 로그 (6강)
  - `TestInitData`로 샘플 데이터 생성 (14강, 27강에서 dev에도 적용)
  - 테스트 클래스에 `@Transactional` → 롤백으로 테스트 독립성 (18, 25강), t1~tN 누적, `@DisplayName`, AssertJ
  - `@OneToMany(cascade = {PERSIST, REMOVE})`, `question.addAnswer()`, 더티체킹 (21강)
  - `@ManyToOne(fetch = LAZY)`, EAGER 지양 (22~25강)
  - Thymeleaf 캐시 끄기 (29강), `DataNotFoundException` 3단계 (35강)
  - Bootstrap 5.3.8 cdnjs + Pretendard 폰트 (39강)

### 14168 JPA, 타임리프 개선 (https://www.slog.gg/p/14168/raw, https://github.com/jhs512/p-14168-2)
- 47강, 게시글·댓글 게시판. 태그 001~046-5.
- 핵심 개선
  - `application-dev/test.yaml`, `jdbc:h2:./db_dev;MODE=MySQL`, dev 템플릿 `prefix: file:src/...` (2강)
  - `BaseEntity`(`@MappedSuperclass`, Auditing, `@EnableJpaAuditing`), id 기준 `equals/hashCode` (2, 37, 38강)
  - `BaseInitData`(`ApplicationRunner`, `@Lazy self`로 자기 `@Transactional` 메서드 호출) (2, 33강)
  - `@Valid` + `BindingResult`, `@ModelAttribute("form")` (11~18강)
  - `form_errors` 조각, `01-title-메시지` 접두어 정렬, 첫 오류 필드 포커스 (14~25강)
  - `@ControllerAdvice` + `@ModelAttribute`로 전역 템플릿 변수 (19강)
  - 복수형 URL + HTTP 메서드로 구분 (26강)
  - `cascade = {PERSIST, REMOVE}, orphanRemoval = true`, `addComment/deleteComment` (37강)
  - 폼 DTO를 `record`로 (40강)
  - `spring.mvc.hiddenmethod.filter.enabled` + `_method`로 PUT/DELETE (41~44강)
  - layout-dialect, `layout:title-pattern`, `global.js`, `<meta name="_csrf">` (44, 45강)
  - Security 도입, 정적 파일 `/resource/**`, `th:action`으로 CSRF 토큰 자동 삽입 (46강)

### 도전과제 3-14 (https://wikidocs.net/162814)
1. 답변 페이징과 정렬 (최신순, 추천순)
2. 댓글 (질문·답변에 `Comment` 엔티티 연결)
3. 카테고리 (질문답변 / 강좌 / 자유게시판, `Category` 엔티티)
4. 비밀번호 찾기(임시 비밀번호 메일 발송)와 변경(기존·새 비밀번호)
5. 프로필 화면 (기본 정보, 작성한 질문·답변·댓글)
6. 최근 답변과 최근 댓글 보기
7. 조회 수 표시
8. 소셜 미디어 로그인
9. 마크다운 에디터 (교재 추천: simpleMDE)

## 2. 결정 사항

### 진행 방식
| 항목 | 결정 |
|---|---|
| 스택 | Spring Boot 4.1.2, Java 25, Gradle Kotlin DSL |
| 범위 | 교재 2~3장 + 도전과제 9개, 4장(배포) 제외 |
| 강사 개선이 없는 구간 | 2-15·3장은 14168 방식을 이어서 적용, 나머지는 교재대로 하되 원칙(`@Setter` 금지, LAZY 등) 유지 |
| 승인 단위 | ~002: 커밋 1개 = 승인 1회. **003부터(2026-11-13 변경): 미션 1(001~021)은 AI가 승인 없이 먼저 끝까지 구현하고, 사용자에게는 나중에 설명한다.** 커밋 단위·자료 확인·테스트·브라우저 확인·README 기록은 그대로. 사용자에게 물을 결정은 추천안으로 정하고 이 문서에 `(AI 판단)`으로 표시. 미션 1이 끝나면 멈추고 "미션 1 완료, 이제 미션 2 차례"라고 알린다 |
| 비교 자료 | 작업 시작 시 교재 본문 / sbb3 태그 diff / 강사 태그 diff를 함께 제시, 커밋 `참고:`에 태그 기록 |
| IDE | 사용자는 IntelliJ로 코드를 보고, 실행·테스트는 Gradle |
| 커밋 제목 | `NNN-NN 기능명, 세부작업`, 첫 커밋 `시작`, 마지막 커밋 `완료` |
| 커밋 본문 | `작업 내용` / `작업 이유` / `참고` / `검증` (한국어) |
| README | 커밋마다 `## 작업내역`에 한 항목. 링크는 커밋 번호 태그 주소(`/commit/003-01`)라 같은 커밋에서 바로 적는다 (002-03까지는 해시 링크를 다음 커밋에서 채웠음) |
| 저장소 | https://github.com/haroobuzii/jsb-upgrade (public), `main` 직접 커밋. 태그: **모든 커밋에 커밋 번호 태그**(`003-01`) + 기능 완료 시 기능 번호 태그(`003`). `https://github.com/haroobuzii/jsb-upgrade/commit/003-01`처럼 번호로 바로 열림 |

### 설정
| 항목 | 결정 |
|---|---|
| 설정 파일 | `application.yaml`(`active: dev`) + `application-dev.yaml`(`jdbc:h2:./db_dev;MODE=MySQL`) + `application-test.yaml`(메모리 DB) |
| SQL 로그 | 포맷·하이라이트, 바인딩 값·트랜잭션 TRACE |
| Thymeleaf | dev에서 캐시 끄고 `prefix: file:src/main/resources/templates/` |

### 구조와 URL
| 항목 | 결정 |
|---|---|
| 패키지 | `com.mysite.sbb.domain.<도메인>.<하위>.{controller,entity,repository,service}`, `com.mysite.sbb.global.{jpa,initData,security,exception}`. 003(2-04)부터 이 구조로 바로 만들고, 005(2-06)에서는 교재의 파일 이동을 설명만 한다 (2026-11-13 결정) |
| URL | 복수형 리소스 + HTTP 메서드로 구분 (`/questions`, `/questions/{id}`, `/questions/write` GET/POST, `/questions/{id}/modify` GET/PUT, `/questions/{id}/answers` POST). 2-11에서 전환 |
| HTTP 메서드 | 수정 `_method=PUT`, 삭제 `_method=DELETE`, 추천·로그아웃 POST |

### 엔티티와 JPA
| 항목 | 결정 |
|---|---|
| `BaseEntity` | 2-04부터. `@MappedSuperclass`, `id`, `createDate`/`modifyDate`(Auditing). id 기준 `equals/hashCode`는 **필요해지는 시점(컬렉션에서 자식 삭제: 017 또는 023)에 실패를 먼저 보이고 추가** (14168 37강 방식, 2026-11-13 변경). 017-04 답변 삭제는 같은 영속성 컨텍스트의 같은 인스턴스를 remove 하므로 equals 없이 동작 → 아직 미도입 |
| id 타입 | `int` (강사 방식). `Integer/Long` 관례와의 차이는 도입 시 설명 |
| 변경 방식 | 엔티티 `@Setter` 금지, `modify()`·`addAnswer()` 같은 의미 있는 메서드로만 변경. 기본 생성자는 `@NoArgsConstructor(access = PROTECTED)` (14168은 public, JPA에는 protected로 충분하고 빈 엔티티 생성을 막음, 2026-11-13 결정) |
| 연관관계 | 모든 `@ManyToOne`에 `fetch = LAZY`, `@OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)`. 컬렉션 필드 이름은 강사 방식 `answers`(교재 `answerList`), `new ArrayList<>()`로 초기화 |
| 서비스·트랜잭션 | Service 계층 유지, `@Transactional`은 Service에. 답변 생성은 Service 안에서 `question.addAnswer()` |
| 조회 실패 | 리포지터리 `Optional` 반환 → 서비스에서 `orElseThrow(DataNotFoundException)` → 404 |
| OSIV | 기본값(켜짐) 유지, 처음 나올 때 설명하고 끈 결과도 한 번 보여줌. 실제로 처음 나오는 곳은 016이 아니라 008-01(상세 템플릿에서 LAZY `question.answers` 조회) → 그때 설명·시연 `(AI 판단)` |

### 샘플 데이터와 테스트
| 항목 | 결정 |
|---|---|
| 샘플 데이터 | `global/initData/BaseInitData` (`ApplicationRunner`, `@Lazy self`, 데이터 있으면 건너뜀), dev·test 모두. 004(2-05) 테스트에 데이터가 필요해 005에서 004로 앞당김. 3-02의 300건은 `DevInitData`(`@Profile("dev")`, `@Order(2)`)로 dev에만 — test에 넣으면 질문 2개를 전제로 한 테스트가 깨짐 `(AI 판단)` |
| 테스트 | `@SpringBootTest` + `@ActiveProfiles("test")` + 클래스 `@Transactional`(롤백), t1~tN 누적, `@DisplayName`, AssertJ. 화면 기능마다 `MockMvc` 테스트 |
| 컨트롤러 테스트 | 교재·강사에는 없음 → 이 프로젝트에서 **추가**. `MockMvcTester`(AssertJ 방식)로 리포지터리 테스트와 문법 통일. 기능마다 1~3개(정상 흐름 + 꼭 필요한 실패 흐름: 검증 실패, 403, 404 등). 화면 모양·JS는 테스트하지 않고 브라우저로만 확인. 목적은 브라우저 확인의 대체가 아니라 이후 변경(Security 등)으로부터 기존 기능을 지키는 것 (2026-11-13 결정) |

### 화면
| 항목 | 결정 |
|---|---|
| Bootstrap | cdnjs 5.3.8 + Pretendard 폰트 |
| 레이아웃 | `thymeleaf-layout-dialect`, `layout:title-pattern`(009). `global.js`는 14168에서 `_method` DELETE 링크 처리용이므로 필요해지는 017에서, `<meta name="_csrf">`는 Security를 넣는 013에서 추가 `(AI 판단)` |
| 폼 | 컨트롤러 안 `record` 폼 DTO, `@ModelAttribute("form") @Valid` + `BindingResult`, `form_errors` 조각, `01-필드-` 접두어 정렬, 첫 오류 필드 포커스 |
| 클라이언트 검증 | `onsubmit` JS 검증 추가, 서버 검증이 진짜 방어선임을 테스트로 확인 |
| 정적 파일 | `/resource/**` 아래에 모음 |

### 보안과 기능
| 항목 | 결정 |
|---|---|
| 권한 오류 | 403 Forbidden |
| 마크다운 XSS | commonmark `escapeHtml(true)`, `sanitizeUrls(true)`. 적용 전 `<script>` 실행을 먼저 보여줌 |
| 검색 | 교재대로 Specification → JPQL `@Query` 두 단계, QueryDSL 제외 |

### 도전과제
| 항목 | 결정 |
|---|---|
| 순서 | ① 답변 페이징·정렬 → ② 댓글 → ③ 카테고리 → ④ 조회 수 → ⑤ 최근 답변·댓글 → ⑥ 프로필 → ⑦ 비밀번호 변경·찾기 → ⑧ 마크다운 에디터 → ⑨ 소셜 로그인 |
| 메일 | 개발은 Mailpit(Docker), 실제는 Gmail SMTP + 앱 비밀번호. 비밀값은 `.gitignore`된 `application-secret.yaml`에 사용자가 직접 입력 |
| 에디터 | EasyMDE (simpleMDE 후속) |
| 소셜 로그인 | Google. OAuth 앱 등록·키 발급은 사용자가 직접, 단계는 안내 |

## 3. 로드맵

| 번호 | 교재 | 기능 | 적용할 개선점 | 강사 참고 |
|---|---|---|---|---|
| 000 | 1장 | 개발 환경, 프로젝트 생성, 분석 문서 | Boot 4.1.2, Java 25, Kotlin DSL | 14163 2강 |
| 001 | 2-02 | hello 컨트롤러 | `@ResponseBody`, 요청 처리 흐름, 404 과정 | 14163 3~4강 |
| 002 | 2-03 | JPA, H2 | yaml + dev/test 프로파일, H2 콘솔, SQL 로그 | 14163 5~6강, 14168 2강 |
| 003 | 2-04 | 엔티티 | `BaseEntity`, Auditing, `int` id, LAZY, cascade, `@Setter` 금지, domain 패키지 | 14163 7~9·22~24강, 14168 2·37·38강 |
| 004 | 2-05 | 리포지터리 테스트 | `@ActiveProfiles("test")`, `@Transactional` 롤백, t1~tN, `Optional` | 14163 14~18·25강 |
| 005 | 2-06 | 도메인별 분류, 샘플 데이터 | `BaseInitData`, `@Lazy self` | 14168 2·33강 |
| 006 | 2-07~09 | 질문 목록, 루트 URL, 서비스 | Thymeleaf 캐시 끄기, Service `@Transactional(readOnly = true)` | 14163 27·29강 |
| 007 | 2-10~11 | 상세, URL 설계 | `DataNotFoundException`, 복수형 URL 전환 | 14163 35강, 14168 26강 |
| 008 | 2-12 | 답변 등록 | `question.addAnswer()`, 더티체킹 | 14163 21·37강 |
| 009 | 2-13~15 | 디자인, 부트스트랩, 표준 HTML | CDN + Pretendard, layout-dialect, title-pattern, `global.js` | 14163 39강, 14168 44~45강 |
| 010 | 2-16 | 질문 등록, 검증 | `record` 폼, `form_errors`, 포커스, JS 검증 | 14168 11~25·40강 |
| 011 | 3-01 | 내비게이션 바 | | |
| 012 | 3-02~04 | 페이징, 번호, 답변 개수 | 300건은 `BaseInitData` | |
| 013 | 3-05 | 스프링 시큐리티 | Security 7 방식, `/resource/**`, `th:action` CSRF | 14168 46강 |
| 014 | 3-06 | 회원가입 | | |
| 015 | 3-07 | 로그인, 로그아웃 | 로그아웃 POST | |
| 016 | 3-08 | 글쓴이 | LAZY + OSIV 설명 | |
| 017 | 3-09 | 수정, 삭제 | `_method` PUT/DELETE, 403 | 14168 41~44강 |
| 018 | 3-10 | 추천 | POST | |
| 019 | 3-11 | 앵커 | | |
| 020 | 3-12 | 마크다운 | XSS 방지 | 14168 16강(`th:text`/`th:utext`) |
| 021 | 3-13 | 검색 | Specification → JPQL | |
| 022 | 3-14 ① | 답변 페이징·정렬 | | |
| 023 | 3-14 ② | 댓글 | aggregate, `orphanRemoval` | 14168 37강 |
| 024 | 3-14 ③ | 카테고리 | | |
| 025 | 3-14 ⑦ | 조회 수 | | |
| 026 | 3-14 ⑥ | 최근 답변·댓글 | | |
| 027 | 3-14 ⑤ | 프로필 | | |
| 028 | 3-14 ④ | 비밀번호 변경·찾기 | Mailpit, Gmail SMTP | |
| 029 | 3-14 ⑨ | 마크다운 에디터 | EasyMDE | |
| 030 | 3-14 ⑧ | 소셜 로그인 | Google OAuth | |
