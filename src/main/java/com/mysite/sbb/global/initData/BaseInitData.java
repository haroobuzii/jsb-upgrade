package com.mysite.sbb.global.initData;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

// 앱이 시작되면 샘플 데이터를 만든다. (dev, test 모두)
@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    // 자기 자신의 "프록시"를 주입받는다.
    // this.work1() 처럼 내부에서 직접 부르면 @Transactional 이 적용되지 않는다.
    // 스프링이 만든 프록시(self)를 통해 불러야 트랜잭션이 시작된다.
    // @Lazy: 자기 자신을 주입받는 순환 참조를 실제 사용 시점까지 미룬다.
    @Autowired
    @Lazy
    private BaseInitData self;

    private final QuestionRepository questionRepository;

    @Bean
    @Order(1) // DevInitData(2)보다 먼저: 빈 DB에서 기본 샘플(질문 1·2)이 먼저 들어가야 id가 1, 2로 고정된다
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> self.work1();
    }

    @Transactional
    public void work1() {
        if (questionRepository.count() > 0) return; // 이미 있으면 다시 만들지 않는다 (dev 파일 DB)

        Question q1 = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        Question q2 = questionRepository.save(new Question("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?"));

        // save 를 다시 부르지 않아도 된다. 트랜잭션이 끝날 때 더티체킹 + cascade PERSIST 로 INSERT
        q2.addAnswer("네 자동으로 생성됩니다.");
        q2.addAnswer("맞습니다!");
    }
}
