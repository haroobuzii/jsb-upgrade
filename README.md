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
- 모든 커밋에 커밋 번호와 같은 태그를 단다(`003-01`). 기능이 끝나면 기능 번호 태그도 단다(`003`).
- 그래서 `https://github.com/haroobuzii/jsb-upgrade/commit/003-01`처럼 **번호만으로 커밋을 열 수 있고**, 작업내역의 링크도 같은 커밋에서 바로 적는다.

## 패키지 구조

교재 2-06은 `com.mysite.sbb` 하나에 모인 파일을 `question`, `answer`, `user` 패키지로 옮긴다.
이 프로젝트는 14168 방식대로 처음(003)부터 아래 구조로 만들었다.

```text
com.mysite.sbb
├── SbbApplication, MainController
├── domain                         # 기능(도메인)별
│   └── question                   # 질문 도메인
│       ├── question               # 질문 자체
│       │   ├── entity             # Question
│       │   ├── repository         # QuestionRepository
│       │   ├── service            # (006~)
│       │   └── controller         # (006~)
│       └── answer                 # 질문에 달리는 답변
│           ├── entity             # Answer
│           └── repository         # AnswerRepository
└── global                         # 특정 도메인에 속하지 않는 공통 코드
    ├── jpa/entity                 # BaseEntity
    └── initData                   # BaseInitData (샘플 데이터)
```

- 교재 `com.mysite.sbb.question.Question` → 여기서는 `com.mysite.sbb.domain.question.question.entity.Question`
- 도메인 아래를 다시 `entity / repository / service / controller`로 나눈 이유: 같은 종류의 클래스가 늘어나도 위치가 정해져 있어 찾기 쉽다 (14168)
- 답변이 `domain.question` 아래에 있는 이유: 답변은 질문 없이 존재할 수 없다 (14168의 `post.postComment`와 같은 배치)

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
- 링크: [000-02](https://github.com/haroobuzii/jsb-upgrade/commit/000-02)
- 작업: Spring Initializr로 프로젝트 생성(webmvc, lombok, devtools), `.gitignore`에 H2 DB 파일·`.DS_Store` 추가, README 작성
- 이유: 의존성은 처음 필요해지는 단계에서 하나씩 추가하여 각 의존성의 역할을 이해하기 위함 (JPA·H2는 002, Thymeleaf는 006)
- 결과: `./gradlew build` 성공, `bootRun` 후 `localhost:8080` 접속 시 Whitelabel 404 (컨트롤러가 없으므로 정상)

### 000-03 개발 환경, GitHub 저장소 연결
- 링크: [000-03](https://github.com/haroobuzii/jsb-upgrade/commit/000-03)
- 작업: `gh repo create`로 public 저장소 `haroobuzii/jsb-upgrade` 생성, `origin` 연결 후 `main` push, 000-02 커밋 링크 기록
- 이유: 작업내역의 각 항목을 실제 커밋 페이지로 연결하여 단계별 코드 변화를 언제든 다시 볼 수 있게 하기 위함
- 결과: GitHub 저장소 페이지에서 커밋과 README 확인

### 000-04 개발 환경, 완료 자료 분석과 결정 사항 정리
- 링크: [000-04](https://github.com/haroobuzii/jsb-upgrade/commit/000-04)
- 작업: 교재·sbb3·14163·14168·도전과제를 분석해 `docs/analysis.md` 작성 (자료별 요약, 결정 사항, 강사 참고 강 번호가 붙은 로드맵), README 로드맵 갱신
- 이유: 무엇을 왜 개선하는지 근거를 먼저 확정하고, 이후 모든 작업이 같은 기준을 따르게 하기 위함
- 결과: 결정 사항 28개 확정, 로드맵 000~030 확정

### 001-01 hello 컨트롤러, 시작 void 액션 메서드
- 링크: [001-01](https://github.com/haroobuzii/jsb-upgrade/commit/001-01)
- 작업: `MainController`에 `@GetMapping("/sbb")`가 붙은 `void index()` 추가 (콘솔에 `index() 메서드 호출됨!` 출력만 함)
- 이유: "URL 매핑은 됐는데 응답을 정하지 않은 상태"를 먼저 겪어 보고, 다음 커밋에서 `@ResponseBody`가 왜 필요한지 이해하기 위함 (교재 2-02, 14163 3강 흐름)
- 결과: `/sbb` 응답이 404 → 500(`Circular view path [sbb]`)으로 바뀜. 서버 콘솔에는 `index() 메서드 호출됨!` 출력 → 메서드는 실행됐지만 응답 생성에서 실패

### 001-02 hello 컨트롤러, @ResponseBody로 문자열 응답
- 링크: [001-02](https://github.com/haroobuzii/jsb-upgrade/commit/001-02)
- 작업: `index()`의 반환형을 `String`으로 바꾸고 `@ResponseBody` 추가, `"안녕하세요 sbb에 오신 것을 환영합니다."` 반환
- 이유: `@ResponseBody`가 없으면 반환값(또는 URL)을 뷰 이름으로 해석한다. `@ResponseBody`를 붙이면 반환값이 응답 본문 그 자체가 되어 뷰를 찾지 않음 (교재 2-02, 14163 4강)
- 결과: `/sbb` 응답이 500 → 200, `Content-Type: text/plain;charset=UTF-8`, 브라우저에 환영 문자열 출력

### 001-03 hello 컨트롤러, 완료 MockMvc 테스트
- 링크: [001-03](https://github.com/haroobuzii/jsb-upgrade/commit/001-03)
- 작업: `MainControllerTest` 추가. `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester`(AssertJ 방식)로 `GET /sbb`의 상태·Content-Type·본문 검증
- 이유: 브라우저로 눈으로 확인한 동작을 테스트 코드로 고정해, 이후 변경으로 깨지면 바로 알 수 있게 하기 위함. 결정 사항(테스트는 AssertJ)에 맞춰 예전 `perform().andExpect()` 대신 `MockMvcTester` 사용
- 결과: `./gradlew test` 2개 통과. 기대 문자열을 일부러 틀리게 바꾸면 `expected / but was` 메시지로 실패하는 것도 확인 후 원복
- 정정(002-01에서 추가): 커밋 메시지의 "교재·강사 방식은 `perform().andExpect()`"라는 설명은 틀림. 교재·강사에는 컨트롤러(MockMvc) 테스트가 없음. 이 테스트는 교재·강사에 없는 것을 `docs/analysis.md` 결정 사항에 따라 **추가**한 것

### 002-01 JPA·H2, 시작 application.yaml로 전환
- 링크: [002-01](https://github.com/haroobuzii/jsb-upgrade/commit/002-01)
- 작업: `application.properties` → `application.yaml` (내용은 `spring.application.name: sbb` 그대로), `docs/analysis.md`에 컨트롤러 테스트 결정 추가, 001-03 정정 메모
- 이유: 다음 커밋부터 dev/test 프로파일, JPA, 로그 설정이 늘어난다. yaml은 들여쓰기로 계층을 표현해 `spring.jpa.properties.hibernate...` 같은 긴 키의 반복이 없어 읽기 쉬움 (14163·14168 모두 yaml 사용)
- 결과: 앱 재시작 로그에 `[sbb]`(앱 이름) 그대로 출력 → yaml을 읽음, `/sbb` 200, `./gradlew test` 통과

### 002-02 JPA·H2, H2와 dev/test 프로파일
- 링크: [002-02](https://github.com/haroobuzii/jsb-upgrade/commit/002-02)
- 작업: `h2`, `spring-boot-h2console` 의존성 추가, `profiles.active: dev`, `application-dev.yaml`(`jdbc:h2:./db_dev;MODE=MySQL`), `application-test.yaml`(`jdbc:h2:mem:db_test;MODE=MySQL`), 테스트 2개에 `@ActiveProfiles("test")`
- 이유: 개발 DB와 테스트 DB를 분리해 테스트가 개발 데이터를 건드리지 않게 하고(테스트는 메모리 DB라 끝나면 사라짐), DB 파일을 프로젝트 폴더에 둬 이 프로젝트 전용으로 만들기 위함. dev·test 모두 `MODE=MySQL`로 같은 SQL 문법 사용 (14163 `005`, 14168 2강)
- 결과: 앱은 `profile is active: "dev"`, 테스트 2개는 `"test"`로 실행. `/h2-console` 접속 가능하지만 연결 시 `Database ... db_dev not found` → 앱에 아직 JDBC(HikariCP)가 없어 DB 파일이 만들어지지 않음. 002-03에서 JPA를 넣으면 해결 (교재가 빈 `local.mv.db`를 직접 만든 이유와 같음)

### 002-03 JPA·H2, 완료 JPA와 SQL 로그
- 링크: [002-03](https://github.com/haroobuzii/jsb-upgrade/commit/002-03)
- 작업: `spring-boot-starter-data-jpa`, `spring-boot-starter-data-jpa-test` 추가. `ddl-auto: update`, SQL 출력(`show-sql`, `format_sql`, `highlight_sql`, `use_sql_comments`), 바인딩·추출 값과 트랜잭션 TRACE 로그, `ansi` 컬러 로그, `hikari.auto-commit: false`
- 이유: JPA가 JDBC 커넥션 풀(HikariCP)을 함께 가져와 앱이 시작하며 DB에 연결 → 002-02의 "db_dev not found" 해결. 실행되는 SQL과 값을 로그로 봐야 JPA 동작을 이해할 수 있음 (14163 `006`, 14168 `002`). 교재의 `hibernate.dialect`는 Hibernate가 자동 선택하므로 생략
- 결과: 시작 로그 `HikariPool-1 - Added connection ... jdbc:h2:./db_dev`, `Database dialect: H2Dialect`(자동), 프로젝트 폴더에 `db_dev.mv.db` 생성·git 추적 제외 확인. 브라우저 H2 콘솔 연결 성공. 테스트는 `jdbc:h2:mem:db_test`에 연결, `./gradlew test` 통과

### 003-01 엔티티, 시작 BaseEntity와 JPA Auditing
- 링크: [003-01](https://github.com/haroobuzii/jsb-upgrade/commit/003-01)
- 작업: `global/jpa/entity/BaseEntity`(`@MappedSuperclass`, `int id` IDENTITY, `@CreatedDate createDate`, `@LastModifiedDate modifyDate`, `@EntityListeners(AuditingEntityListener)`), `SbbApplication`에 `@EnableJpaAuditing`. 진행 방식 변경(미션 1은 AI가 먼저 구현 후 설명)과 커밋 번호 태그 규칙을 문서에 반영, 기존 링크를 태그 주소로 변경, 003 결정 4개 기록
- 이유: 교재는 엔티티마다 `id`, `createDate`를 쓰고 작성일을 직접 넣는다. 14168처럼 공통 필드를 부모 클래스로 모으고 작성·수정 일시는 JPA Auditing이 자동으로 채우게 함
- 결과: 앱 재시작 시 `create table` 없음 → `@MappedSuperclass`는 테이블이 아님을 확인, 테스트 통과

### 003-02 엔티티, Question
- 링크: [003-02](https://github.com/haroobuzii/jsb-upgrade/commit/003-02)
- 작업: `domain/question/question/entity/Question extends BaseEntity`. `subject`(`@Column(length = 200)`), `content`(`columnDefinition = "TEXT"`), `@Getter`, `@NoArgsConstructor(access = PROTECTED)`, 생성자 `Question(subject, content)`. `@Setter` 없음
- 이유: 교재도 "엔티티에는 Setter를 쓰지 않기를 권한다(이 책은 설명 편의상 사용)"고 한다. 14168처럼 생성자로만 값을 넣어 엔티티가 아무 데서나 바뀌지 않게 함. 기본 생성자는 JPA용으로만 열어 둠(PROTECTED)
- 결과: 재시작 로그에 `create table question (id, create_date, modify_date, content TEXT, subject varchar(200))` → BaseEntity 칼럼 상속, `createDate` → `create_date`(snake_case) 확인. H2 콘솔에 `QUESTION` 테이블 표시

### 003-03 엔티티, Answer와 @ManyToOne(LAZY)
- 링크: [003-03](https://github.com/haroobuzii/jsb-upgrade/commit/003-03)
- 작업: `domain/question/answer/entity/Answer extends BaseEntity`. `@ManyToOne(fetch = LAZY) Question question`, `content`(TEXT), 생성자 `Answer(question, content)`
- 이유: 답변 여러 개(N)가 질문 하나(1)에 속하므로 `@ManyToOne`. 기본값 EAGER는 답변을 조회할 때마다 질문까지 즉시 조회하므로 14163·14168처럼 LAZY로 지정 (차이는 004에서 SQL로 확인)
- 결과: `create table answer (..., question_id integer, ...)`와 `alter table answer add constraint FK... foreign key (question_id) references question` → `@ManyToOne`이 외래키 칼럼과 제약을 만든다는 것 확인 (14163 7강)

### 003-04 엔티티, 완료 Question.answers @OneToMany
- 링크: [003-04](https://github.com/haroobuzii/jsb-upgrade/commit/003-04)
- 작업: `Question`에 `@OneToMany(mappedBy = "question", cascade = {PERSIST, REMOVE}, orphanRemoval = true) private final List<Answer> answers = new ArrayList<>()`
- 이유: 질문에서 `question.getAnswers()`로 답변을 바로 꺼내는 객체지향 방식(14163 8~9강). PERSIST는 이후 `question.addAnswer()`로 답변을 만들 때 자동 INSERT, REMOVE는 질문 삭제 시 답변 삭제(교재), orphanRemoval은 목록에서 뺀 답변 삭제(14168 37강). `new ArrayList<>()` 초기화로 NPE 방지(14163 23강)
- 결과: 재시작 시 DDL 없음 → `@OneToMany`는 DB 칼럼을 만들지 않는다(외래키는 `answer.question_id` 하나). 테스트 통과

### 004-01 리포지터리, 시작 QuestionRepository와 저장 테스트
- 링크: [004-01](https://github.com/haroobuzii/jsb-upgrade/commit/004-01)
- 작업: `QuestionRepository`, `AnswerRepository`(`JpaRepository<엔티티, Integer>`), `QuestionRepositoryTest` t1(save)
- 이유: 리포지터리는 인터페이스만 선언하면 Spring Data JPA가 구현체를 만들어 준다(교재 2-05). 테스트는 14163 방식(`@ActiveProfiles("test")`, 클래스 `@Transactional` 롤백, `@DisplayName`, AssertJ)
- 결과: INSERT SQL과 바인딩 값 로그에서 `create_date`·`modify_date`가 자동으로 채워짐(Auditing) 확인. 저장 전 id 0 → 저장 후 1 이상. 테스트는 메모리 DB를 써서 교재처럼 "서버를 먼저 꺼야 하는(파일 잠금)" 문제가 없음

### 004-02 리포지터리, BaseInitData 샘플 데이터와 addAnswer
- 링크: [004-02](https://github.com/haroobuzii/jsb-upgrade/commit/004-02)
- 작업: `global/initData/BaseInitData`(`ApplicationRunner` 빈, `@Lazy self`, `@Transactional work1()`, 데이터가 있으면 건너뜀). 질문 2개 + 2번 질문에 답변 2개. `Question.addAnswer(content)` 추가
- 이유: 조회·수정·삭제 테스트에는 미리 들어 있는 데이터가 필요하다. 교재는 테스트 안에서 저장하고, 14163은 14강부터 `TestInitData`, 14168은 `BaseInitData`로 앱 시작 시 생성. 로드맵에서는 005였지만 004의 테스트에 필요해서 앞당김(AI 판단). 답변은 `addAnswer()`로 만들어 `save` 없이 cascade PERSIST로 저장(14163 21강)
- 결과: H2 콘솔에서 질문 2건, 2번 질문의 답변 2건 확인. 로그에 `BaseInitData.work1` 트랜잭션 완료

### 004-03 리포지터리, 조회 쿼리 메서드
- 링크: [004-03](https://github.com/haroobuzii/jsb-upgrade/commit/004-03)
- 작업: `QuestionRepository`에 `findBySubject`, `findBySubjectAndContent`(둘 다 `Optional`), `findBySubjectLike`. 테스트 t2~t6(findAll, findById, findBySubject, And, Like)
- 이유: 메서드 이름 규칙(findBy + 필드 + And/Like…)만으로 JPA가 쿼리를 만든다(교재 2-05, 14163 16~17강). 결과가 없을 수 있는 단건 조회는 결정 사항대로 `Optional`
- 결과: 테스트 로그의 SQL `where q1_0.subject=?`, `where q1_0.subject=? and q1_0.content=?`, `where q1_0.subject like ? escape '\'` 확인. 6개 통과

### 004-04 리포지터리, 수정(더티체킹)과 삭제(cascade REMOVE)
- 링크: [004-04](https://github.com/haroobuzii/jsb-upgrade/commit/004-04)
- 작업: `Question.modify(subject, content)` 추가. 테스트 t7(수정: `modify` → `flush` → 조회), t8(답변 2개 달린 질문 삭제 → 답변도 삭제)
- 이유: setter 대신 의미 있는 메서드로 값을 바꾸고(14168 `Post.modify`), 트랜잭션 안에서는 `save` 없이 더티체킹으로 UPDATE된다. 교재는 `setSubject` 후 `save`. 삭제는 교재처럼 하되 답변이 달린 질문을 지워 `cascade = REMOVE` 동작을 확인
- 결과: SQL `update question set ..., modify_date=? ... where id=?` (수정 일시 자동 갱신), `delete from answer` 2번 → `delete from question` 순서 확인. 테스트 8개 통과

### 004-05 리포지터리, 완료 답변 테스트와 LAZY
- 링크: [004-05](https://github.com/haroobuzii/jsb-upgrade/commit/004-05)
- 작업: `AnswerRepositoryTest` t1(addAnswer + flush로 INSERT), t2(`answer.getQuestion()`은 LAZY 프록시, 쓰는 순간 SELECT), t3(`question.getAnswers()`), t4(orphanRemoval), t5(트랜잭션 없이 LAZY 컬렉션 접근 → `LazyInitializationException`)
- 이유: 교재 2-05는 답변 저장·조회 후 "세션이 종료되어 오류"를 겪고 `@Transactional`로 해결한다. 그 오류를 테스트로 재현해 왜 트랜잭션이 필요한지 남김(14163 25강). LAZY의 실제 동작(프록시 → 사용 시 조회)을 `Hibernate.isInitialized`로 확인(14163 22~24강)
- 결과: 5개 통과, 전체 15개 통과

### 005-01 도메인별 분류, 완료 패키지 구조 정리
- 링크: [005-01](https://github.com/haroobuzii/jsb-upgrade/commit/005-01)
- 작업: README에 `## 패키지 구조` 추가 (교재 2-06의 패키지와 이 프로젝트 패키지 대응)
- 이유: 교재 2-06은 파일을 도메인별 패키지로 옮기는 절이다. 우리는 003부터 이미 `domain.<도메인>.<하위>.<계층>` 구조로 만들었으므로 옮길 파일이 없다. 대신 구조와 그렇게 나눈 이유를 문서로 남김. 샘플 데이터(`BaseInitData`)는 004-02로 앞당겼음
- 결과: 코드 변경 없음, 테스트 15개 통과


### 006-01 질문 목록, 시작 Thymeleaf 도입
- 링크: [006-01](https://github.com/haroobuzii/jsb-upgrade/commit/006-01)
- 작업: `spring-boot-starter-thymeleaf`, `spring-boot-starter-thymeleaf-test` 추가. dev 프로파일에 `thymeleaf.cache: false`, `prefix: file:src/main/resources/templates/`
- 이유: 질문 목록을 HTML로 보여 주려면 템플릿 엔진이 필요하다(교재 2-07). 개발 중에는 캐시를 끄고(14163 29강 "타임리프 캐시는 오직 개발환경에서만 꺼야"), 소스 폴더 템플릿을 직접 읽어(14168) html 수정이 새로고침만으로 반영되게 함
- 결과: 앱 재시작 정상, 테스트 통과

### 006-02 질문 목록, /question/list와 템플릿
- 링크: [006-02](https://github.com/haroobuzii/jsb-upgrade/commit/006-02)
- 작업: `QuestionController`(`@RequiredArgsConstructor` 생성자 주입, `GET /question/list`, `Model`에 `questionList`), `templates/question/question/list.html`(`th:each`, `th:text`), `QuestionControllerTest` t1
- 이유: 교재 2-07 그대로 컨트롤러가 리포지터리로 목록을 조회해 Model로 템플릿에 넘긴다(서비스는 006-04). 템플릿 위치는 패키지와 같은 `question/question/list.html`(14168 `post/post/list.html` 방식, AI 판단). 템플릿 주석은 응답에 나가지 않는 Thymeleaf 주석 `<!--/* */-->` 사용
- 결과: 브라우저에서 `/question/list`에 번호·제목·작성일시 2건 표시. 템플릿 수정이 재컴파일 없이 새로고침만으로 반영(006-01 prefix 설정). 테스트 16개 통과

### 006-03 질문 목록, 루트 URL 리다이렉트
- 링크: [006-03](https://github.com/haroobuzii/jsb-upgrade/commit/006-03)
- 작업: `MainController.root()` — `GET /` → `redirect:/question/list`. `MainControllerTest` t2(302, Location)
- 이유: 사이트 주소만 입력해도 질문 목록이 보이게 함(교재 2-08, 14163 33강). `redirect:`는 302 응답으로 브라우저가 새 주소로 다시 요청하게 한다
- 결과: `curl -i /` → `302`, `Location: http://localhost:8080/question/list`. 브라우저에서 `/` 접속 시 주소창이 `/question/list`로 바뀌며 목록 표시. 테스트 17개 통과
- 정정(006-04에서 추가): 006-03 커밋 메시지의 "forward는 주소창이 바뀌지 않는다(교재 설명)"에서 "(교재 설명)"은 틀림. 교재 2-08에는 forward 설명이 없다. forward 설명 자체(서버 안에서 넘겨 주소창이 바뀌지 않음)는 맞는 내용

### 006-04 질문 목록, 완료 QuestionService
- 링크: [006-04](https://github.com/haroobuzii/jsb-upgrade/commit/006-04)
- 작업: `QuestionService.getList()`(`@Transactional(readOnly = true)`), 컨트롤러가 리포지터리 대신 서비스 사용
- 이유: 교재 2-09 — 여러 컨트롤러가 쓰는 처리를 모듈화하고, 컨트롤러는 요청·응답만 담당. 결정 사항대로 트랜잭션 경계는 서비스에 두고 조회는 `readOnly`. 교재가 권장하는 DTO는 교재·강사 모두 만들지 않으므로 엔티티를 템플릿에 그대로 넘김
- 결과: 로그 `Getting transaction for [...QuestionService.getList]` → select → `Completing transaction`. 목록 화면 동일, 테스트 17개 통과

### 007-01 상세, 시작 질문 상세 화면
- 링크: [007-01](https://github.com/haroobuzii/jsb-upgrade/commit/007-01)
- 작업: `GET /question/detail/{id}`(`@PathVariable`), `QuestionService.getQuestion(id)`(우선 `findById(id).get()`), `templates/question/question/detail.html`, 컨트롤러 테스트 t2
- 이유: 교재 2-10. 14163 35강처럼 3단계(상세 표시 → 목록에서 링크 → 없는 질문 404)로 나눠 진행. 1단계에서는 없는 id 처리 없이 `get()`만 써서 문제를 먼저 본다
- 결과: `/question/detail/2` 제목·내용 표시. `/question/detail/999`는 500(`No value present`) — 다음 단계에서 404로 개선

### 007-02 상세, 목록 제목에 상세 링크
- 링크: [007-02](https://github.com/haroobuzii/jsb-upgrade/commit/007-02)
- 작업: 목록의 제목을 `<a th:href="@{|/question/detail/${question.id}|}">`로 감쌈, 컨트롤러 테스트 t1에 링크 확인 추가
- 이유: 교재 2-10, 14163 35강 2단계. `@{...}`는 URL을 만들 때(컨텍스트 경로 자동 포함), `|...|`는 문자열 안에 값을 끼워 넣을 때 쓴다(14163 35강 비교표)
- 결과: 목록 HTML에 `href="/question/detail/1"`, `"/question/detail/2"`. 브라우저에서 제목 클릭 → 상세 화면 이동. 테스트 18개 통과

### 007-03 상세, 없는 질문은 404 (DataNotFoundException)
- 링크: [007-03](https://github.com/haroobuzii/jsb-upgrade/commit/007-03)
- 작업: `global/exception/DataNotFoundException`(`@ResponseStatus(NOT_FOUND, reason = "entity not found")`), `getQuestion`을 `findById(id).orElseThrow(() -> new DataNotFoundException("question not found"))`로 변경, 컨트롤러 테스트 t3(404)
- 이유: "없는 데이터"는 서버 고장(500)이 아니라 요청한 대상이 없는 것(404)이다. 구체적인 예외가 문제 파악에 도움이 된다(14163 35강 "커스텀 예외를 만드는 이유"). 교재의 `isPresent()/get()/else throw` 대신 결정 사항대로 `orElseThrow` 한 줄
- 결과: `/question/detail/999` → 500(`No value present`)에서 404(`entity not found`)로 바뀜. 테스트 19개 통과

### 007-04 상세, URL 프리픽스(@RequestMapping)
- 링크: [007-04](https://github.com/haroobuzii/jsb-upgrade/commit/007-04)
- 작업: `QuestionController`에 `@RequestMapping("/question")`, 메서드는 `@GetMapping("/list")`, `@GetMapping("/detail/{id}")`
- 이유: 교재 2-11 — 한 컨트롤러의 URL이 모두 `/question`으로 시작하므로 클래스에 한 번만 적는다. URL은 그대로
- 결과: `/question/list`, `/question/detail/2` 모두 200, 테스트 19개 그대로 통과(URL이 바뀌지 않았다는 증거)

### 007-05 상세, 완료 복수형 URL로 전환
- 링크: [007-05](https://github.com/haroobuzii/jsb-upgrade/commit/007-05)
- 작업: `@RequestMapping("/questions")`, 목록 `GET /questions`, 상세 `GET /questions/{id}`. 루트 리다이렉트·목록 링크·테스트 URL 변경
- 이유: 결정 사항(14168 26강) — URL은 자원(복수형 명사)을 나타내고, 하는 일은 HTTP 메서드로 구분한다. `/question/list`, `/question/detail/{id}`의 `list`, `detail`은 "무엇을 하는지"라 URL에 넣을 필요가 없다. 교재·14163은 `/question/list` 유지
- 결과: `/` → 302 `/questions`, `/questions` 200, `/questions/2` 200, `/questions/999` 404, 옛 주소 `/question/list` 404. 테스트 19개 통과

### 008-01 답변, 시작 상세에 답변 목록과 OSIV
- 링크: [008-01](https://github.com/haroobuzii/jsb-upgrade/commit/008-01)
- 작업: 상세 템플릿에 `#lists.size(question.answers)`개의 답변, `th:each`로 답변 내용·작성일시. 컨트롤러 테스트 t2에 답변 확인 추가
- 이유: 교재 2-12. 이때 템플릿이 LAZY 컬렉션 `question.answers`를 읽는데, 서비스 트랜잭션은 이미 끝난 뒤다. 그래도 동작하는 이유가 OSIV(Open Session In View, 기본 켜짐: 요청이 끝날 때까지 영속성 컨텍스트를 열어 둠)
- 결과: 답변 2개 표시. `spring.jpa.open-in-view: false`로 잠시 바꾸자 같은 화면이 500(`LazyInitializationException`) → 원래대로 되돌림. 테스트 19개 통과
