package com.mysite.sbb.domain.question.answer.repository;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AnswerRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    // 샘플 데이터: 질문 2에 답변 1 "네 자동으로 생성됩니다.", 답변 2 "맞습니다!"

    @Test
    @DisplayName("답변 생성: question.addAnswer 후 flush 하면 save 없이 INSERT (cascade PERSIST)")
    void t1() {
        Question question = questionRepository.findById(2).get();

        Answer answer = question.addAnswer("새 답변입니다.");
        assertThat(answer.getId()).isEqualTo(0); // 아직 INSERT 전

        questionRepository.flush(); // 더티체킹 + cascade PERSIST → insert into answer

        assertThat(answer.getId()).isGreaterThan(0);
        assertThat(answerRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("답변 조회: answer.getQuestion()은 LAZY라 처음엔 빈 껍데기(프록시), 쓰는 순간 SELECT")
    void t2() {
        Answer answer = answerRepository.findById(1).get(); // select ... from answer

        Question question = answer.getQuestion();
        assertThat(Hibernate.isInitialized(question)).isFalse(); // 아직 question 은 조회 안 됨
        assertThat(question.getId()).isEqualTo(2);               // id 는 answer.question_id 로 이미 알고 있음

        assertThat(question.getSubject()).isEqualTo("스프링부트 모델 질문입니다."); // 이 순간 select ... from question
        assertThat(Hibernate.isInitialized(question)).isTrue();
    }

    @Test
    @DisplayName("질문에서 답변 목록 조회: question.getAnswers() (객체지향적 방법)")
    void t3() {
        Question question = questionRepository.findById(2).get();

        List<Answer> answers = question.getAnswers(); // 쓰는 순간 select ... from answer where question_id=?

        assertThat(answers).hasSize(2);
        assertThat(answers.get(0).getContent()).isEqualTo("네 자동으로 생성됩니다.");
    }

    @Test
    @DisplayName("orphanRemoval: answers 에서 빼기만 해도 DELETE")
    void t4() {
        Question question = questionRepository.findById(2).get();

        question.getAnswers().remove(0);
        questionRepository.flush(); // delete from answer where id=?

        assertThat(answerRepository.count()).isEqualTo(1);
    }

    @Test
    // 이 테스트만 트랜잭션 없이 실행한다.
    @Transactional(propagation = NOT_SUPPORTED)
    @DisplayName("트랜잭션이 없으면 LAZY 컬렉션을 꺼낼 때 LazyInitializationException (교재 2-05의 오류)")
    void t5() {
        // 트랜잭션이 없으면 findById 가 끝나는 순간 DB 세션(영속성 컨텍스트)도 닫힌다.
        Question question = questionRepository.findById(2).get();

        // 답변 목록은 아직 안 가져왔는데 세션이 닫혀 있어 가져올 수 없다.
        assertThatThrownBy(() -> question.getAnswers().size())
                .isInstanceOf(LazyInitializationException.class);
    }
}
