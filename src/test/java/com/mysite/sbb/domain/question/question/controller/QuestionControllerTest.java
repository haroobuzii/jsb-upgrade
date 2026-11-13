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
                .contains("<title>질문 목록 | SBB</title>");                  // layout.html 적용
    }

    @Test
    @DisplayName("GET /questions/2 : 질문 제목과 내용이 보인다")
    void t2() {
        assertThat(mvc.get().uri("/questions/2"))
                .hasStatusOk()
                .hasViewName("question/question/detail")
                .bodyText()
                .contains("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?")
                .contains("2개의 답변이 있습니다.", "네 자동으로 생성됩니다.", "맞습니다!");
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
                .hasViewName("question/question/write");
    }

    @Test
    @DisplayName("POST /questions/write : 질문이 저장되고 새 질문 상세로 리다이렉트")
    void t5() {
        assertThat(mvc.post().uri("/questions/write")
                .param("subject", "새 질문")
                .param("content", "새 질문 내용"))
                .hasStatus(HttpStatus.FOUND)
                .redirectedUrl()
                .startsWith("/questions/");

        assertThat(questionRepository.findBySubject("새 질문")).isPresent();
    }
}
