package com.mysite.sbb.global.jpa.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

// 모든 엔티티가 공통으로 가지는 id, 작성일시, 수정일시
// @MappedSuperclass: 이 클래스 자체는 테이블이 되지 않고, 상속한 엔티티의 테이블에 필드(칼럼)만 물려준다.
@MappedSuperclass
// 저장(INSERT)·수정(UPDATE) 직전에 @CreatedDate, @LastModifiedDate 필드를 자동으로 채운다.
// 동작하려면 @EnableJpaAuditing 이 켜져 있어야 한다. (SbbApplication)
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    // 처음 저장할 때는 Auditing 이 작성·수정 일시를 같은 값으로 넣는다. 다르면 한 번 이상 수정된 것
    public boolean isModified() {
        return modifyDate != null && !modifyDate.equals(createDate);
    }
}
