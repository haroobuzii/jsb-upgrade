package com.mysite.sbb.domain.question.question.entity;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
// JPA는 조회 결과로 엔티티를 만들 때 매개변수 없는 생성자가 필요하다.
// PROTECTED로 두면 JPA는 쓸 수 있고, 우리 코드에서 값 없는 new Question()은 막힌다.
@NoArgsConstructor(access = PROTECTED)
public class Question extends BaseEntity {
    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 질문(1) : 답변(N). DB 칼럼은 생기지 않는다. 외래키는 answer.question_id 하나뿐.
    // mappedBy = "question": 관계의 주인은 Answer.question 필드
    // PERSIST: 질문을 저장(또는 더티체킹)할 때 answers 에 새로 추가된 답변도 함께 INSERT
    // REMOVE: 질문을 삭제하면 답변들도 먼저 삭제
    // orphanRemoval: answers 에서 빠진 답변은 DELETE
    // new ArrayList<>(): 새 질문에서 바로 answers.add() 해도 NPE가 나지 않게 초기화
    @OneToMany(mappedBy = "question", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<Answer> answers = new ArrayList<>();

    public Question(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
