package com.mysite.sbb.domain.question.question.entity;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
// JPA는 조회 결과로 엔티티를 만들 때 매개변수 없는 생성자가 필요하다.
// PROTECTED로 두면 JPA는 쓸 수 있고, 우리 코드에서 값 없는 new Question()은 막힌다.
@NoArgsConstructor(access = PROTECTED)
public class Question extends BaseEntity {
    // 글쓴이. 질문(N) : 회원(1). question.author_id 외래키. 016 이전에 만든 질문은 null 일 수 있다
    @ManyToOne(fetch = LAZY)
    private SiteUser author;

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

    // 추천한 회원들. 질문(N) : 회원(N) → 연결 테이블 question_voters(question_id, voters_id)가 생긴다
    // Set: 같은 회원이 두 번 들어가지 않는다 (한 사람은 한 번만 추천)
    @ManyToMany
    private final Set<SiteUser> voters = new HashSet<>();

    public Question(SiteUser author, String subject, String content) {
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    // 글쓴이인지 확인 (수정·삭제 권한). 016 이전 글처럼 글쓴이가 없으면 아무도 수정할 수 없다
    public boolean isAuthor(String username) {
        return author != null && author.getUsername().equals(username);
    }

    // setter 대신 "수정"이라는 의미가 드러나는 메서드로만 값을 바꾼다.
    // 트랜잭션 안에서 값만 바꾸면 끝날 때 JPA가 변경을 감지(더티체킹)해 UPDATE 한다. save 불필요.
    public void modify(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    // 답변은 질문을 통해서 만든다.
    // answers 에 넣어 두면 cascade PERSIST 덕분에 질문이 저장(또는 더티체킹)될 때 답변도 INSERT 된다.
    // 양쪽(answer.question, question.answers)을 한 번에 맞춰 주므로 객체 상태가 어긋나지 않는다.
    public Answer addAnswer(SiteUser author, String content) {
        Answer answer = new Answer(this, author, content);
        answers.add(answer);
        return answer;
    }

    // 답변 삭제: 목록에서 빼기만 하면 orphanRemoval 로 DELETE (14168 Post.deleteComment)
    // 같은 트랜잭션에서 꺼낸 답변은 answers 안의 객체와 "같은 인스턴스"라 equals 없이도 remove 된다
    public boolean deleteAnswer(Answer answer) {
        return answers.remove(answer);
    }

    // 추천: 이미 추천했으면 Set 이라 그대로. 트랜잭션 끝에 연결 테이블에 INSERT
    public void vote(SiteUser voter) {
        voters.add(voter);
    }
}
