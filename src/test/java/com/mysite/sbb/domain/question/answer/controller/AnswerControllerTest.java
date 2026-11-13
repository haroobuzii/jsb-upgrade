package com.mysite.sbb.domain.question.answer.controller;

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
class AnswerControllerTest {
    @Autowired
    private MockMvcTester mvc;
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("POST /questions/2/answers : 답변이 생기고 상세로 리다이렉트")
    void t1() {
        assertThat(mvc.post().uri("/questions/2/answers").with(csrf()).with(user("user1")).param("content", "새 답변"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/questions/2");

        assertThat(questionRepository.findById(2).get().getAnswers())
                .filteredOn(a -> a.getContent().equals("새 답변"))
                .singleElement()
                .extracting(a -> a.getAuthor().getUsername())
                .isEqualTo("user1"); // 글쓴이는 로그인한 사용자
    }

    @Test
    @DisplayName("POST /questions/999/answers : 없는 질문이면 404")
    void t2() {
        assertThat(mvc.post().uri("/questions/999/answers").with(csrf()).with(user("user1")).param("content", "새 답변"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .failure()
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    @DisplayName("POST /questions/2/answers : 내용이 비면 저장하지 않고 상세 화면에 오류 메시지")
    void t3() {
        int before = questionRepository.findById(2).get().getAnswers().size();

        assertThat(mvc.post().uri("/questions/2/answers").with(csrf()).with(user("user1")).param("content", "  "))
                .hasStatusOk()
                .hasViewName("question/question/detail")
                .bodyText()
                .contains("내용을 입력해주세요.");

        assertThat(questionRepository.findById(2).get().getAnswers()).hasSize(before);
    }

    @Test
    @DisplayName("CSRF 토큰 없이 POST 하면 403 (Spring Security 기본 보호)")
    void t4() {
        assertThat(mvc.post().uri("/questions/2/answers").with(user("user1")).param("content", "토큰 없음"))
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("로그인하지 않고 답변 POST 하면 로그인 화면으로, 저장 안 됨")
    void t5() {
        int before = questionRepository.findById(2).get().getAnswers().size();

        assertThat(mvc.post().uri("/questions/2/answers").with(csrf()).param("content", "비로그인 답변"))
                .hasStatus(HttpStatus.FOUND)
                .redirectedUrl().endsWith("/users/login");

        assertThat(questionRepository.findById(2).get().getAnswers()).hasSize(before);
    }
}
