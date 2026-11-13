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

자료 분석, 결정 사항, 강사 참고 강 번호까지 담은 전체 로드맵은 [docs/analysis.md](docs/analysis.md)에 있다.

| 번호 | 교재 | 기능 |
|---|---|---|
| 000 | 1장 | 개발 환경, 프로젝트 생성, 분석 문서 |
| 001 | 2-02 | hello 컨트롤러 |
| 002 | 2-03 | JPA, H2 (yaml + dev/test 프로파일) |
| 003 | 2-04 | 엔티티 (`BaseEntity`) |
| 004 | 2-05 | 리포지터리 테스트 |
| 005 | 2-06 | 도메인별 분류, 샘플 데이터 (`BaseInitData`) |
| 006 | 2-07~09 | 질문 목록, 루트 URL, 서비스 |
| 007 | 2-10~11 | 상세, URL 설계 |
| 008 | 2-12 | 답변 등록 |
| 009 | 2-13~15 | 디자인, 부트스트랩, 레이아웃 |
| 010 | 2-16 | 질문 등록, 검증 |
| 011 | 3-01 | 내비게이션 바 |
| 012 | 3-02~04 | 페이징, 번호, 답변 개수 |
| 013 | 3-05 | 스프링 시큐리티 |
| 014~015 | 3-06~07 | 회원가입, 로그인/로그아웃 |
| 016 | 3-08 | 글쓴이 |
| 017 | 3-09 | 수정, 삭제 (`_method`) |
| 018~021 | 3-10~13 | 추천, 앵커, 마크다운, 검색 |
| 022~030 | 3-14 | 도전과제: 답변 페이징·정렬, 댓글, 카테고리, 조회 수, 최근 답변·댓글, 프로필, 비밀번호, 마크다운 에디터, 소셜 로그인 |

## 작업내역

### 000-01 개발 환경, 시작 JDK 25 설정
- 링크: 없음 (리포지터리 생성 전 작업)
- 작업: Homebrew로 설치되어 있던 `openjdk@25`를 `~/.zshrc`의 `JAVA_HOME`, `PATH`에 등록
- 이유: Homebrew openjdk는 keg-only라 시스템이 `java`를 찾지 못함. 강사 프로젝트와 같은 Java 25를 쓰기 위함
- 결과: 새 쉘에서 `java -version` → `25.0.4.1` 확인

### 000-02 개발 환경, 프로젝트 생성
- 링크: [ad8df08](https://github.com/haroobuzii/jsb-upgrade/commit/ad8df08)
- 작업: Spring Initializr로 프로젝트 생성(webmvc, lombok, devtools), `.gitignore`에 H2 DB 파일·`.DS_Store` 추가, README 작성
- 이유: 의존성은 처음 필요해지는 단계에서 하나씩 추가하여 각 의존성의 역할을 이해하기 위함 (JPA·H2는 002, Thymeleaf는 006)
- 결과: `./gradlew build` 성공, `bootRun` 후 `localhost:8080` 접속 시 Whitelabel 404 (컨트롤러가 없으므로 정상)

### 000-03 개발 환경, GitHub 저장소 연결
- 링크: [1be3ce8](https://github.com/haroobuzii/jsb-upgrade/commit/1be3ce8)
- 작업: `gh repo create`로 public 저장소 `haroobuzii/jsb-upgrade` 생성, `origin` 연결 후 `main` push, 000-02 커밋 링크 기록
- 이유: 작업내역의 각 항목을 실제 커밋 페이지로 연결하여 단계별 코드 변화를 언제든 다시 볼 수 있게 하기 위함
- 결과: GitHub 저장소 페이지에서 커밋과 README 확인

### 000-04 개발 환경, 완료 자료 분석과 결정 사항 정리
- 링크: [dfddb37](https://github.com/haroobuzii/jsb-upgrade/commit/dfddb37)
- 작업: 교재·sbb3·14163·14168·도전과제를 분석해 `docs/analysis.md` 작성 (자료별 요약, 결정 사항, 강사 참고 강 번호가 붙은 로드맵), README 로드맵 갱신
- 이유: 무엇을 왜 개선하는지 근거를 먼저 확정하고, 이후 모든 작업이 같은 기준을 따르게 하기 위함
- 결과: 결정 사항 28개 확정, 로드맵 000~030 확정

### 001-01 hello 컨트롤러, 시작 void 액션 메서드
- 링크: [f4305a4](https://github.com/haroobuzii/jsb-upgrade/commit/f4305a4)
- 작업: `MainController`에 `@GetMapping("/sbb")`가 붙은 `void index()` 추가 (콘솔에 `index() 메서드 호출됨!` 출력만 함)
- 이유: "URL 매핑은 됐는데 응답을 정하지 않은 상태"를 먼저 겪어 보고, 다음 커밋에서 `@ResponseBody`가 왜 필요한지 이해하기 위함 (교재 2-02, 14163 3강 흐름)
- 결과: `/sbb` 응답이 404 → 500(`Circular view path [sbb]`)으로 바뀜. 서버 콘솔에는 `index() 메서드 호출됨!` 출력 → 메서드는 실행됐지만 응답 생성에서 실패

### 001-02 hello 컨트롤러, @ResponseBody로 문자열 응답
- 링크: [cce8380](https://github.com/haroobuzii/jsb-upgrade/commit/cce8380)
- 작업: `index()`의 반환형을 `String`으로 바꾸고 `@ResponseBody` 추가, `"안녕하세요 sbb에 오신 것을 환영합니다."` 반환
- 이유: `@ResponseBody`가 없으면 반환값(또는 URL)을 뷰 이름으로 해석한다. `@ResponseBody`를 붙이면 반환값이 응답 본문 그 자체가 되어 뷰를 찾지 않음 (교재 2-02, 14163 4강)
- 결과: `/sbb` 응답이 500 → 200, `Content-Type: text/plain;charset=UTF-8`, 브라우저에 환영 문자열 출력

### 001-03 hello 컨트롤러, 완료 MockMvc 테스트
- 링크: [047627c](https://github.com/haroobuzii/jsb-upgrade/commit/047627c)
- 작업: `MainControllerTest` 추가. `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester`(AssertJ 방식)로 `GET /sbb`의 상태·Content-Type·본문 검증
- 이유: 브라우저로 눈으로 확인한 동작을 테스트 코드로 고정해, 이후 변경으로 깨지면 바로 알 수 있게 하기 위함. 결정 사항(테스트는 AssertJ)에 맞춰 예전 `perform().andExpect()` 대신 `MockMvcTester` 사용
- 결과: `./gradlew test` 2개 통과. 기대 문자열을 일부러 틀리게 바꾸면 `expected / but was` 메시지로 실패하는 것도 확인 후 원복
- 정정(002-01에서 추가): 커밋 메시지의 "교재·강사 방식은 `perform().andExpect()`"라는 설명은 틀림. 교재·강사에는 컨트롤러(MockMvc) 테스트가 없음. 이 테스트는 교재·강사에 없는 것을 `docs/analysis.md` 결정 사항에 따라 **추가**한 것

### 002-01 JPA·H2, 시작 application.yaml로 전환
- 링크: [afe312e](https://github.com/haroobuzii/jsb-upgrade/commit/afe312e)
- 작업: `application.properties` → `application.yaml` (내용은 `spring.application.name: sbb` 그대로), `docs/analysis.md`에 컨트롤러 테스트 결정 추가, 001-03 정정 메모
- 이유: 다음 커밋부터 dev/test 프로파일, JPA, 로그 설정이 늘어난다. yaml은 들여쓰기로 계층을 표현해 `spring.jpa.properties.hibernate...` 같은 긴 키의 반복이 없어 읽기 쉬움 (14163·14168 모두 yaml 사용)
- 결과: 앱 재시작 로그에 `[sbb]`(앱 이름) 그대로 출력 → yaml을 읽음, `/sbb` 200, `./gradlew test` 통과

### 002-02 JPA·H2, H2와 dev/test 프로파일
- 링크: [5f1960c](https://github.com/haroobuzii/jsb-upgrade/commit/5f1960c)
- 작업: `h2`, `spring-boot-h2console` 의존성 추가, `profiles.active: dev`, `application-dev.yaml`(`jdbc:h2:./db_dev;MODE=MySQL`), `application-test.yaml`(`jdbc:h2:mem:db_test;MODE=MySQL`), 테스트 2개에 `@ActiveProfiles("test")`
- 이유: 개발 DB와 테스트 DB를 분리해 테스트가 개발 데이터를 건드리지 않게 하고(테스트는 메모리 DB라 끝나면 사라짐), DB 파일을 프로젝트 폴더에 둬 이 프로젝트 전용으로 만들기 위함. dev·test 모두 `MODE=MySQL`로 같은 SQL 문법 사용 (14163 `005`, 14168 2강)
- 결과: 앱은 `profile is active: "dev"`, 테스트 2개는 `"test"`로 실행. `/h2-console` 접속 가능하지만 연결 시 `Database ... db_dev not found` → 앱에 아직 JDBC(HikariCP)가 없어 DB 파일이 만들어지지 않음. 002-03에서 JPA를 넣으면 해결 (교재가 빈 `local.mv.db`를 직접 만든 이유와 같음)

### 002-03 JPA·H2, 완료 JPA와 SQL 로그
- 링크: (다음 커밋에서 추가)
- 작업: `spring-boot-starter-data-jpa`, `spring-boot-starter-data-jpa-test` 추가. `ddl-auto: update`, SQL 출력(`show-sql`, `format_sql`, `highlight_sql`, `use_sql_comments`), 바인딩·추출 값과 트랜잭션 TRACE 로그, `ansi` 컬러 로그, `hikari.auto-commit: false`
- 이유: JPA가 JDBC 커넥션 풀(HikariCP)을 함께 가져와 앱이 시작하며 DB에 연결 → 002-02의 "db_dev not found" 해결. 실행되는 SQL과 값을 로그로 봐야 JPA 동작을 이해할 수 있음 (14163 `006`, 14168 `002`). 교재의 `hibernate.dialect`는 Hibernate가 자동 선택하므로 생략
- 결과: 시작 로그 `HikariPool-1 - Added connection ... jdbc:h2:./db_dev`, `Database dialect: H2Dialect`(자동), 프로젝트 폴더에 `db_dev.mv.db` 생성·git 추적 제외 확인. 브라우저 H2 콘솔 연결 성공. 테스트는 `jdbc:h2:mem:db_test`에 연결, `./gradlew test` 통과
