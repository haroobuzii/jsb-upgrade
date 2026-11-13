package com.mysite.sbb.domain.question.answer.service;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionService questionService;

    // 답변 생성: answerRepository.save 를 쓰지 않는다.
    // 트랜잭션 안에서 question.addAnswer → 끝날 때 더티체킹 + cascade PERSIST 로 INSERT
    @Transactional
    public Answer write(int questionId, SiteUser author, String content) {
        Question question = questionService.getQuestion(questionId); // 없으면 404
        return question.addAnswer(author, content);
    }
}
