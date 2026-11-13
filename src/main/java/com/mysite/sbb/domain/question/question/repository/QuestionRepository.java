package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔티티 타입, id 타입>
// save, findById, findAll, count, delete 같은 기본 메서드를 구현 없이 바로 쓸 수 있다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
