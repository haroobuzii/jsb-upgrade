# jsb-upgrade

『점프 투 스프링부트』(SBB)를 교재 순서대로 구현하면서, 장희성 강사의 14163·14168 수업에서 더 나은 방법이 있으면 적용한 리포지터리입니다.

- 목표: 완성된 소스코드를 **한 줄도 빠짐없이 이해**하는 것
- 진행 방식: 작은 작업 하나 → 설명 → 승인 → 구현 → 실행·테스트 → README 기록 → 커밋

## 참고 자료

- 점프 투 스프링부트: [교재](https://wikidocs.net/book/7601) / [GitHub](https://github.com/pahkey/sbb3) / [도전과제(3-14)](https://wikidocs.net/162814)
- 14163 점프 투 스프링부트 개선: [강의](https://www.slog.gg/p/14163/raw) / [GitHub](https://github.com/jhs512/p-14163-2)
- 14168 JPA, 타임리프 개선: [강의](https://www.slog.gg/p/14168/raw) / [GitHub](https://github.com/jhs512/p-14168-2)

## 개발 환경

- Java 25 (Homebrew `openjdk@25`)
- Spring Boot 4.1.2, Gradle 9.8.2 (Kotlin DSL)
- H2 Database, Thymeleaf, Spring Data JPA, Spring Security (단계별로 추가)

## 커밋 규칙

- 제목: `NNN-NN 기능명, 세부작업` (NNN = 기능 번호, NN = 기능 안의 세부 순번)
- 기능의 첫 커밋은 `시작`, 마지막 커밋은 `완료`를 붙인다.
- 본문: `작업 내용` / `작업 이유` / `참고` / `검증`
- 커밋 링크는 커밋 후에 해시가 생기므로 **다음 커밋에서 직전 항목의 링크를 채운다.**

## 로드맵

| 번호 | 교재 | 기능 | 적용할 강사 개선점 |
|---|---|---|---|
| 000 | 1장 | 개발 환경, 프로젝트 생성 | Boot 4.1.2, Java 25, `build.gradle.kts` |
| 001 | 2-01~02 | hello 컨트롤러 | 14163: `@ResponseBody`, 요청 처리 흐름 |
| 002 | 2-03 | JPA, H2 설정 | 14168: `application-dev/test.yml` 분리, 테스트는 메모리 DB |
| 003 | 2-04 | 엔티티 | 14168: `BaseEntity`, Auditing / 14163: `LAZY`, `cascade`, `orphanRemoval` |
| 004 | 2-05 | 리포지터리 테스트 | 14163: `@ActiveProfiles("test")`, `@Transactional` 롤백 |
| 005 | 2-06 | 도메인별 분류, 샘플 데이터 | 14168: `BaseInitData`(ApplicationRunner) |
| 006 | 2-07~09 | 질문 목록, 루트 URL, 서비스 | `@Transactional(readOnly = true)` |
| 007 | 2-10~11 | 상세 페이지, URL 프리픽스 | `DataNotFoundException` |
| 008 | 2-12 | 답변 등록 | 14163: `question.addAnswer()`, 더티체킹 |
| 009 | 2-13~15 | 디자인, 부트스트랩, 표준 HTML | 14168: `thymeleaf-layout-dialect` |
| 010 | 2-16 | 질문 등록, 검증 | 14168: `record` 폼, `@Valid`, `form_errors` |
| 011 | 3-01 | 내비게이션 바 | |
| 012 | 3-02~04 | 페이징, 번호, 답변 개수 | |
| 013 | 3-05 | 스프링 시큐리티 | 14168: `th:action` CSRF |
| 014~015 | 3-06~07 | 회원가입, 로그인/로그아웃 | |
| 016 | 3-08 | 글쓴이 | |
| 017 | 3-09 | 수정, 삭제 | 14168: `_method`(hiddenmethod)로 PUT/DELETE |
| 018~021 | 3-10~13 | 추천, 앵커, 마크다운, 검색 | |
| 022~030 | 3-14 | 도전과제 9개 | 답변 페이징·정렬, 댓글, 카테고리, 비밀번호 찾기·변경, 프로필, 최근 답변·댓글, 조회 수, 소셜 로그인, 마크다운 에디터 |

## 작업내역

### 000-01 개발 환경, 시작 JDK 25 설정
- 링크: 없음 (리포지터리 생성 전 작업)
- 작업: Homebrew로 설치되어 있던 `openjdk@25`를 `~/.zshrc`의 `JAVA_HOME`, `PATH`에 등록
- 이유: Homebrew openjdk는 keg-only라 시스템이 `java`를 찾지 못함. 강사 프로젝트와 같은 Java 25를 쓰기 위함
- 결과: 새 쉘에서 `java -version` → `25.0.4.1` 확인

### 000-02 개발 환경, 프로젝트 생성
- 링크: (다음 커밋에서 추가)
- 작업: Spring Initializr로 프로젝트 생성(webmvc, lombok, devtools), `.gitignore`에 H2 DB 파일·`.DS_Store` 추가, README 작성
- 이유: 의존성은 처음 필요해지는 단계에서 하나씩 추가하여 각 의존성의 역할을 이해하기 위함 (JPA·H2는 002, Thymeleaf는 006)
- 결과: `./gradlew build` 성공, `bootRun` 후 `localhost:8080` 접속 시 Whitelabel 404 (컨트롤러가 없으므로 정상)
