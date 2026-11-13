package com.mysite.sbb.global.initData;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

// 개발(dev) 환경에서만: 페이징을 눈으로 확인할 수 있게 질문 300개를 만든다. (교재 3-02)
// test 에는 넣지 않는다 → 테스트는 BaseInitData 의 질문 2개를 전제로 한다.
@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DevInitData {
    @Autowired
    @Lazy
    private DevInitData self;

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Bean
    @Order(2)
    ApplicationRunner devInitDataApplicationRunner() {
        return args -> self.work1();
    }

    @Transactional
    public void work1() {
        if (questionRepository.count() >= 300) return; // 이미 만들었으면 건너뜀

        SiteUser user1 = userRepository.findByUsername("user1").get();

        for (int i = 1; i <= 300; i++) {
            questionRepository.save(new Question(user1, "테스트 데이터입니다:[%03d]".formatted(i), "내용무"));
        }
    }
}
