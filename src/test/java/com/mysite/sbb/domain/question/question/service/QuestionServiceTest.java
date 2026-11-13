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
        for (int i = 1; i <= 12; i++) questionService.write("페이징 %02d".formatted(i), "내용");

        Page<Question> page0 = questionService.getList(0);
        Page<Question> page1 = questionService.getList(1);

        assertThat(page0.getTotalElements()).isEqualTo(14);
        assertThat(page0.getTotalPages()).isEqualTo(2);
        assertThat(page0.getContent()).hasSize(10);
        assertThat(page1.getContent()).hasSize(4);
        assertThat(page0.getContent().get(0).getSubject()).isEqualTo("페이징 12"); // 가장 최근
    }
}
