package com.mysite.sbb.domain.user.user.entity;

import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

// 회원. 클래스 이름이 User 가 아닌 이유: user 는 여러 DB 에서 예약어라 테이블 이름으로 쓰기 곤란하다 (교재 3-06)
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SiteUser extends BaseEntity {
    // unique = true → DB 에 유니크 제약. 같은 값이 두 번 저장되면 DB 가 막는다
    @Column(unique = true)
    private String username;

    // 비밀번호는 암호화(BCrypt)된 값만 저장한다 (014-02)
    private String password;

    @Column(unique = true)
    private String email;

    public SiteUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
