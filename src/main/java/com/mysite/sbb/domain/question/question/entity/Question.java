package com.mysite.sbb.domain.question.question.entity;

import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Question(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
