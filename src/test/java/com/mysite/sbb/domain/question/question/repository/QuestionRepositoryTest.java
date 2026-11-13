package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
// 각 테스트가 끝나면 롤백한다. 테스트끼리 데이터가 섞이지 않는다(테스트 독립성).
@Transactional
class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("save: 저장하면 id가 생기고 작성·수정 일시가 자동으로 채워진다")
    void t1() {
        Question q1 = new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
        assertThat(q1.getId()).isEqualTo(0); // int 기본값, 아직 저장 전

        questionRepository.save(q1);

        assertThat(q1.getId()).isGreaterThan(0);      // INSERT 후 DB가 만든 id
        assertThat(q1.getCreateDate()).isNotNull(); // @CreatedDate (JPA Auditing)
        assertThat(q1.getModifyDate()).isNotNull(); // @LastModifiedDate
    }
}
