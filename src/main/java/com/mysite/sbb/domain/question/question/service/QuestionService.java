package com.mysite.sbb.domain.question.question.service;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


// 컨트롤러와 리포지터리 사이에서 실제 일(비즈니스 로직)을 처리한다.
// 트랜잭션 경계도 여기서 정한다. (결정 사항: @Transactional 은 Service 에)
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    // readOnly = true: 조회만 하는 트랜잭션.
    // 변경 감지(더티체킹)를 위한 스냅샷을 만들지 않아 조금 가볍고, 실수로 값을 바꿔도 UPDATE 되지 않는다.
    // page: 0부터 시작하는 페이지 번호. 한 페이지에 10개, 최신 글이 먼저(작성일시 내림차순)
    // kw: 검색어. 비어 있으면 like '%%' 라 전체가 나온다
    @Transactional(readOnly = true)
    public Page<Question> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        // 021-01 에서는 Specification(자바 코드로 조건 조립), 지금은 JPQL(@Query)로 같은 검색
        return questionRepository.findAllByKeyword(kw, pageable);
    }

    // 없는 id면 DataNotFoundException → 404 (get() 이었을 때는 NoSuchElementException → 500)
    @Transactional(readOnly = true)
    public Question getQuestion(int id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    @Transactional
    public Question write(SiteUser author, String subject, String content) {
        return questionRepository.save(new Question(author, subject, content));
    }

    // 수정: 글쓴이만. 아니면 403. 값만 바꾸면 트랜잭션 끝에 더티체킹으로 UPDATE (modifyDate 는 Auditing 이 갱신)
    @Transactional
    public Question modify(int id, String username, String subject, String content) {
        Question question = getQuestion(id);
        checkAuthor(question, username, "수정권한이 없습니다.");
        question.modify(subject, content);
        return question;
    }

    // 권한 오류는 400(교재)이 아니라 403 Forbidden (결정 사항)
    public void checkAuthor(Question question, String username, String message) {
        if (!question.isAuthor(username)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    // 삭제: 글쓴이만. cascade REMOVE 로 달린 답변도 먼저 삭제된다 (004-04)
    @Transactional
    public void delete(int id, String username) {
        Question question = getQuestion(id);
        checkAuthor(question, username, "삭제권한이 없습니다.");
        questionRepository.delete(question);
    }

    @Transactional
    public void vote(int id, SiteUser voter) {
        getQuestion(id).vote(voter);
    }
}
