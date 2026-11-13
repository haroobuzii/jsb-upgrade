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
        assertThat(mvc.post().uri("/questions/2/answers").param("content", "새 답변"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/questions/2");

        assertThat(questionRepository.findById(2).get().getAnswers())
                .extracting("content")
                .contains("새 답변");
    }

    @Test
    @DisplayName("POST /questions/999/answers : 없는 질문이면 404")
    void t2() {
        assertThat(mvc.post().uri("/questions/999/answers").param("content", "새 답변"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .failure()
                .isInstanceOf(DataNotFoundException.class);
    }
}
