package com.mysite.sbb.domain.question.question.service;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;

    @Test
    @DisplayName("getList: 한 페이지 10개, 최신 글 먼저, 전체 개수·페이지 수 제공")
    void t1() {
        // 샘플 2개 + 12개 = 14개 → 0페이지 10개, 1페이지 4개
        for (int i = 1; i <= 12; i++) questionService.write(null, "페이징 %02d".formatted(i), "내용");

        Page<Question> page0 = questionService.getList(0, "");
        Page<Question> page1 = questionService.getList(1, "");

        assertThat(page0.getTotalElements()).isEqualTo(14);
        assertThat(page0.getTotalPages()).isEqualTo(2);
        assertThat(page0.getContent()).hasSize(10);
        assertThat(page1.getContent()).hasSize(4);
        assertThat(page0.getContent().get(0).getSubject()).isEqualTo("페이징 12"); // 가장 최근
    }

    // 샘플: 질문1 "sbb가 무엇인가요?"(user1, 답변 없음) / 질문2 "스프링부트 모델 질문입니다."(user2, 답변 user1 "네 자동으로 생성됩니다.", user2 "맞습니다!")

    @Test
    @DisplayName("검색: 제목·답변 내용·글쓴이로 찾고, 답변이 여러 개여도 질문은 한 번만")
    void t2() {
        assertThat(questionService.getList(0, "sbb").getContent())
                .extracting("subject").containsExactly("sbb가 무엇인가요?");          // 제목

        assertThat(questionService.getList(0, "맞습니다").getContent())
                .extracting("subject").containsExactly("스프링부트 모델 질문입니다."); // 답변 내용

        assertThat(questionService.getList(0, "user1").getContent())
                .extracting("subject")                                                  // 질문 글쓴이 + 답변 글쓴이
                .containsExactlyInAnyOrder("sbb가 무엇인가요?", "스프링부트 모델 질문입니다.");

        assertThat(questionService.getList(0, "user2").getTotalElements()).isEqualTo(1); // 답변 2개와 조인돼도 1건(distinct)
        assertThat(questionService.getList(0, "없는검색어").getTotalElements()).isZero();
    }
}
