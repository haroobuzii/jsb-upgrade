package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.repository.AnswerRepository;
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
    @Autowired
    private AnswerRepository answerRepository;

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

    // 샘플: 질문 2의 답변 1(user1), 답변 2(user2)

    @Test
    @DisplayName("답변 수정: 글쓴이(user1)는 PUT 으로 수정, 남(user2)은 403")
    void t6() {
        assertThat(mvc.get().uri("/questions/2/answers/1/modify").with(user("user1")))
                .hasStatusOk().hasViewName("question/answer/modify");

        assertThat(mvc.put().uri("/questions/2/answers/1/modify").with(user("user2")).with(csrf()).param("content", "남의 답변 수정"))
                .hasStatus(HttpStatus.FORBIDDEN);

        assertThat(mvc.put().uri("/questions/2/answers/1/modify").with(user("user1")).with(csrf()).param("content", "수정된 답변"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/questions/2");

        assertThat(answerRepository.findById(1).get().getContent()).isEqualTo("수정된 답변");
    }

    @Test
    @DisplayName("답변 수정: URL 의 질문 번호와 답변의 질문이 다르면 404")
    void t7() {
        assertThat(mvc.get().uri("/questions/1/answers/1/modify").with(user("user1")))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("답변 삭제: 남(user1)은 403, 글쓴이(user2)는 DELETE 로 삭제 (orphanRemoval)")
    void t8() {
        assertThat(mvc.delete().uri("/questions/2/answers/2").with(user("user1")).with(csrf()))
                .hasStatus(HttpStatus.FORBIDDEN);
        assertThat(answerRepository.findById(2)).isPresent();

        assertThat(mvc.delete().uri("/questions/2/answers/2").with(user("user2")).with(csrf()))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/questions/2");
        // 테스트는 전체가 한 트랜잭션(롤백)이라 아직 DELETE 가 안 나갔다. 앱에서는 서비스 트랜잭션이 끝날 때 나간다
        answerRepository.flush();
        assertThat(answerRepository.findById(2)).isEmpty();
        assertThat(questionRepository.findById(2)).isPresent(); // 질문은 그대로
    }

    @Test
    @DisplayName("답변 추천: POST /questions/2/answers/1/vote, 같은 사람은 1표")
    void t9() {
        assertThat(mvc.post().uri("/questions/2/answers/1/vote").with(user("user2")).with(csrf()))
                .hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/questions/2");
        mvc.post().uri("/questions/2/answers/1/vote").with(user("user2")).with(csrf()).exchange();

        assertThat(answerRepository.findById(1).get().getVoters()).extracting("username").containsExactly("user2");
    }
}
