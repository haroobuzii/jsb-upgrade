package com.mysite.sbb.domain.question.answer.entity;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Answer extends BaseEntity {
    // 답변(N) : 질문(1). answer 테이블에 question_id 외래키 칼럼이 생긴다.
    // @ManyToOne 의 기본값은 EAGER(답변을 조회할 때 질문도 즉시 조회)라서 LAZY(필요할 때 조회)로 바꾼다.
    @ManyToOne(fetch = LAZY)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String content;

    public Answer(Question question, String content) {
        this.question = question;
        this.content = content;
    }
}
