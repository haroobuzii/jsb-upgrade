package com.mysite.sbb.domain.question.question.service;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 컨트롤러와 리포지터리 사이에서 실제 일(비즈니스 로직)을 처리한다.
// 트랜잭션 경계도 여기서 정한다. (결정 사항: @Transactional 은 Service 에)
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    // readOnly = true: 조회만 하는 트랜잭션.
    // 변경 감지(더티체킹)를 위한 스냅샷을 만들지 않아 조금 가볍고, 실수로 값을 바꿔도 UPDATE 되지 않는다.
    // page: 0부터 시작하는 페이지 번호. 한 페이지에 10개, 최신 글이 먼저(작성일시 내림차순)
    // findAll(Pageable)은 JpaRepository 에 이미 있다 → SELECT ... LIMIT 10 OFFSET page*10 + 전체 개수 COUNT 쿼리
    @Transactional(readOnly = true)
    public Page<Question> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        return questionRepository.findAll(pageable);
    }

    // 없는 id면 DataNotFoundException → 404 (get() 이었을 때는 NoSuchElementException → 500)
    @Transactional(readOnly = true)
    public Question getQuestion(int id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    @Transactional
    public Question write(String subject, String content) {
        return questionRepository.save(new Question(subject, content));
    }
}
