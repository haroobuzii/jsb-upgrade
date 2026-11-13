package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

// JpaRepository<엔티티 타입, id 타입>
// save, findById, findAll, count, delete 같은 기본 메서드를 구현 없이 바로 쓸 수 있다.
// JpaSpecificationExecutor: findAll(Specification, Pageable) 처럼 "조건 객체"로 조회하는 메서드를 추가로 제공
public interface QuestionRepository extends JpaRepository<Question, Integer>, JpaSpecificationExecutor<Question> {
    // 메서드 이름만으로 쿼리가 만들어진다. (쿼리 메서드)
    // findBy + 필드명 → WHERE subject = ?
    // 결과가 없을 수 있으므로 Optional 로 받는다. (교재는 Question, 14163은 Optional)
    Optional<Question> findBySubject(String subject);

    // And → WHERE subject = ? AND content = ?
    Optional<Question> findBySubjectAndContent(String subject, String content);

    // Like → WHERE subject LIKE ?  (% 는 호출하는 쪽에서 붙인다: "sbb%")
    List<Question> findBySubjectLike(String subjectLike);
}
