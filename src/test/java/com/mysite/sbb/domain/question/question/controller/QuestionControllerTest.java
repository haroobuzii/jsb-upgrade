package com.mysite.sbb.domain.question.question.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {
    @Autowired
    private MockMvcTester mvc;

    @Test
    @DisplayName("GET /question/list : 질문 목록 템플릿에 샘플 질문 2개가 보인다")
    void t1() {
        assertThat(mvc.get().uri("/question/list"))
                .hasStatusOk()
                .hasViewName("question/question/list")
                .bodyText()
                .contains("sbb가 무엇인가요?", "스프링부트 모델 질문입니다.");
    }

    @Test
    @DisplayName("GET /question/detail/2 : 질문 제목과 내용이 보인다")
    void t2() {
        assertThat(mvc.get().uri("/question/detail/2"))
                .hasStatusOk()
                .hasViewName("question/question/detail")
                .bodyText()
                .contains("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?");
    }
}
