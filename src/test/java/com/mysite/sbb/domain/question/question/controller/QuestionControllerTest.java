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
        assertThat(mvc.get().uri("/questions/write"))
                .hasStatusOk()
                .hasViewName("question/question/write")
                .bodyText()
                .contains("name=\"_csrf\"");  // th:action 이 넣은 hidden 토큰
    }

    @Test
    @DisplayName("POST /questions/write : 질문이 저장되고 새 질문 상세로 리다이렉트")
    void t5() {
        assertThat(mvc.post().uri("/questions/write").with(csrf())
                .param("subject", "새 질문")
                .param("content", "새 질문 내용"))
                .hasStatus(HttpStatus.FOUND)
                .redirectedUrl()
                .startsWith("/questions/");

        assertThat(questionRepository.findBySubject("새 질문")).isPresent();
    }

    @Test
    @DisplayName("POST /questions/write : 제목·내용이 비면 저장하지 않고 폼과 오류 메시지를 다시 보여 준다")
    void t6() {
        long before = questionRepository.count();

        assertThat(mvc.post().uri("/questions/write").with(csrf())
                .param("subject", " ")
                .param("content", ""))
                .hasStatusOk()
                .hasViewName("question/question/write")
                .bodyText()
                .contains("제목을 입력해주세요.", "내용을 입력해주세요.");

        assertThat(questionRepository.count()).isEqualTo(before);
    }
}
