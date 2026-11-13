package com.mysite.sbb.domain.question.answer.service;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;

    // 답변 생성: answerRepository.save 를 쓰지 않는다.
    // 트랜잭션 안에서 question.addAnswer → 끝날 때 더티체킹 + cascade PERSIST 로 INSERT
    @Transactional
    public Answer write(int questionId, SiteUser author, String content) {
        Question question = questionService.getQuestion(questionId); // 없으면 404
        return question.addAnswer(author, content);
    }

    // URL 의 질문 번호와 답변이 속한 질문이 다르면 없는 답변으로 본다 (/questions/1/answers/5 인데 5번이 2번 질문의 답변이면 404)
    @Transactional(readOnly = true)
    public Answer getAnswer(int questionId, int id) {
        return answerRepository.findById(id)
                .filter(answer -> answer.getQuestion().getId() == questionId)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    @Transactional
    public Answer modify(int questionId, int id, String username, String content) {
        Answer answer = getAnswer(questionId, id);
        checkAuthor(answer, username, "수정권한이 없습니다.");
        answer.modify(content);
        return answer;
    }

    public void checkAuthor(Answer answer, String username, String message) {
        if (!answer.isAuthor(username)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    // 답변 삭제: answerRepository.delete 대신 질문의 답변 목록에서 뺀다 (orphanRemoval)
    @Transactional
    public void delete(int questionId, int id, String username) {
        Answer answer = getAnswer(questionId, id);
        checkAuthor(answer, username, "삭제권한이 없습니다.");
        answer.getQuestion().deleteAnswer(answer);
    }

    @Transactional
    public void vote(int questionId, int id, SiteUser voter) {
        getAnswer(questionId, id).vote(voter);
    }
}
