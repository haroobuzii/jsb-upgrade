package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {
    @Autowired
    private MockMvcTester mvc;
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("GET /questions : 질문 목록 템플릿에 샘플 질문 2개가 보인다")
    void t1() {
        assertThat(mvc.get().uri("/questions"))
                .hasStatusOk()
                .hasViewName("question/question/list")
                .bodyText()
                .contains("sbb가 무엇인가요?", "스프링부트 모델 질문입니다.")
                .contains("href=\"/questions/1\"", "href=\"/questions/2\"") // 상세 링크
                .contains("<title>질문 목록 | SBB</title>")                   // layout.html 적용
                .contains("navbar-brand")                                      // navbar.html 적용
                .contains("<span class=\"text-danger small ms-2\">2</span>");  // 2번 질문의 답변 개수
    }

    @Test
    @DisplayName("GET /questions/2 : 질문 제목과 내용이 보인다")
    void t2() {
        assertThat(mvc.get().uri("/questions/2"))
                .hasStatusOk()
                .hasViewName("question/question/detail")
                .bodyText()
                .contains("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?")
                .contains("2개의 답변이 있습니다.", "네 자동으로 생성됩니다.", "맞습니다!")
                .contains("user2", "user1"); // 질문 글쓴이 user2, 답변 글쓴이 user1·user2
    }

    @Test
    @DisplayName("GET /questions/999 : 없는 질문이면 404")
    void t3() {
        assertThat(mvc.get().uri("/questions/999"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed()
                .failure()
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("question not found");
    }

    @Test
    @DisplayName("GET /questions/write : 질문 등록 폼")
    void t4() {
        assertThat(mvc.get().uri("/questions/write").with(user("user1")))
                .hasStatusOk()
                .hasViewName("question/question/write")
                .bodyText()
                .contains("name=\"_csrf\"");  // th:action 이 넣은 hidden 토큰
    }

    @Test
    @DisplayName("POST /questions/write : 질문이 저장되고 새 질문 상세로 리다이렉트")
    void t5() {
        assertThat(mvc.post().uri("/questions/write").with(csrf()).with(user("user1"))
                .param("subject", "새 질문")
                .param("content", "새 질문 내용"))
                .hasStatus(HttpStatus.FOUND)
                .redirectedUrl()
                .startsWith("/questions/");

        // 글쓴이는 로그인한 사용자
        assertThat(questionRepository.findBySubject("새 질문").get().getAuthor().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("POST /questions/write : 제목·내용이 비면 저장하지 않고 폼과 오류 메시지를 다시 보여 준다")
    void t6() {
        long before = questionRepository.count();

        assertThat(mvc.post().uri("/questions/write").with(csrf()).with(user("user1"))
                .param("subject", " ")
                .param("content", ""))
                .hasStatusOk()
                .hasViewName("question/question/write")
                .bodyText()
                .contains("제목을 입력해주세요.", "내용을 입력해주세요.");

        assertThat(questionRepository.count()).isEqualTo(before);
    }

    @Test
    @DisplayName("로그인하지 않으면 GET /questions/write 는 로그인 화면으로")
    void t7() {
        assertThat(mvc.get().uri("/questions/write"))
                .hasStatus(HttpStatus.FOUND)
                .redirectedUrl().endsWith("/users/login");
    }

    @Test
    @DisplayName("상세 답변 입력칸: 비로그인은 비활성화 + 로그인 링크, 로그인하면 입력 가능")
    void t8() {
        assertThat(mvc.get().uri("/questions/2"))
                .bodyText().contains("로그인 후 답변을 작성할 수 있습니다.", "로그인하고 답변하기").doesNotContain("답변등록");

        assertThat(mvc.get().uri("/questions/2").with(user("user1")))
                .bodyText().contains("답변등록").doesNotContain("로그인하고 답변하기");
    }

    @Test
    @DisplayName("질문 수정: 글쓴이(user2)는 PUT 으로 수정 가능")
    void t9() {
        assertThat(mvc.get().uri("/questions/2/modify").with(user("user2")))
                .hasStatusOk()
                .bodyText().contains("name=\"_method\" value=\"put\"", "스프링부트 모델 질문입니다.");

        assertThat(mvc.put().uri("/questions/2/modify").with(user("user2")).with(csrf())
                .param("subject", "수정된 제목").param("content", "수정된 내용"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/questions/2");

        assertThat(questionRepository.findById(2).get().getSubject()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("질문 수정: 글쓴이가 아니면(user1) 403")
    void t10() {
        assertThat(mvc.get().uri("/questions/2/modify").with(user("user1")))
                .hasStatus(HttpStatus.FORBIDDEN);

        assertThat(mvc.put().uri("/questions/2/modify").with(user("user1")).with(csrf())
                .param("subject", "남의 글 수정").param("content", "내용"))
                .hasStatus(HttpStatus.FORBIDDEN);

        assertThat(questionRepository.findById(2).get().getSubject()).isEqualTo("스프링부트 모델 질문입니다.");
    }
}
