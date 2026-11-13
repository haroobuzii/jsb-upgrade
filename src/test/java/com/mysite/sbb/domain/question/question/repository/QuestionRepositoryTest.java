package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // 아래 테스트들은 BaseInitData 가 만든 샘플 데이터를 사용한다.
    // 1: sbb가 무엇인가요? / 2: 스프링부트 모델 질문입니다. (답변 2개)

    @Test
    @DisplayName("findAll: 전체 조회")
    void t2() {
        List<Question> questions = questionRepository.findAll();

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("findById: id로 조회, 결과는 Optional")
    void t3() {
        Question question = questionRepository.findById(1).get();

        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
        assertThat(questionRepository.findById(999)).isEmpty(); // 없으면 빈 Optional
    }

    @Test
    @DisplayName("findBySubject: 제목으로 조회")
    void t4() {
        // SELECT * FROM question WHERE subject = 'sbb가 무엇인가요?'
        Question question = questionRepository.findBySubject("sbb가 무엇인가요?").get();

        assertThat(question.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectAndContent: 제목과 내용으로 조회")
    void t5() {
        // SELECT * FROM question WHERE subject = ? AND content = ?
        Question question = questionRepository
                .findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.")
                .get();

        assertThat(question.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectLike: 제목이 sbb로 시작하는 질문")
    void t6() {
        // SELECT * FROM question WHERE subject LIKE 'sbb%'
        List<Question> questions = questionRepository.findBySubjectLike("sbb%");

        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getSubject()).isEqualTo("sbb가 무엇인가요?");
    }
}
