package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// JpaRepository<엔티티 타입, id 타입>
// save, findById, findAll, count, delete 같은 기본 메서드를 구현 없이 바로 쓸 수 있다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    // 메서드 이름만으로 쿼리가 만들어진다. (쿼리 메서드)
    // findBy + 필드명 → WHERE subject = ?
    // 결과가 없을 수 있으므로 Optional 로 받는다. (교재는 Question, 14163은 Optional)
    Optional<Question> findBySubject(String subject);

    // And → WHERE subject = ? AND content = ?
    Optional<Question> findBySubjectAndContent(String subject, String content);

    // Like → WHERE subject LIKE ?  (% 는 호출하는 쪽에서 붙인다: "sbb%")
    List<Question> findBySubjectLike(String subjectLike);

    // 검색 (교재 3-13 2단계): 021-01 의 Specification 과 같은 조건을 JPQL 로 직접 쓴다
    // JPQL: 테이블이 아니라 엔티티(Question, Answer)와 필드(q.author)를 대상으로 쓰는 쿼리
    // %:kw% : Spring Data 가 파라미터 앞뒤에 % 를 붙여 준다
    // 정렬·페이징은 Pageable 로 그대로 넘긴다 (ORDER BY, LIMIT/OFFSET, COUNT 쿼리 자동)
    @Query("""
            select distinct q
            from Question q
            left join q.author u1
            left join q.answers a
            left join a.author u2
            where q.subject like %:kw%
               or q.content like %:kw%
               or u1.username like %:kw%
               or a.content like %:kw%
               or u2.username like %:kw%
            """)
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
