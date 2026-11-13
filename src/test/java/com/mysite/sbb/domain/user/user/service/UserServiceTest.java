package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("join: 비밀번호는 BCrypt 로 암호화되어 저장된다")
    void t1() {
        SiteUser user = userService.join("newuser", "password1!", "new@test.com");

        assertThat(user.getId()).isGreaterThan(0);
        assertThat(user.getPassword()).isNotEqualTo("password1!").startsWith("$2a$"); // BCrypt 해시
        assertThat(passwordEncoder.matches("password1!", user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("join: 아이디·이메일이 이미 있으면 어느 칸이 문제인지 알려 주는 예외")
    void t2() {
        userService.join("dupuser", "password1!", "dup@test.com");

        assertThatThrownBy(() -> userService.join("dupuser", "password1!", "other@test.com"))
                .isInstanceOf(ServiceException.class)
                .hasMessage("이미 사용 중인 아이디입니다.")
                .extracting("field").isEqualTo("username");

        assertThatThrownBy(() -> userService.join("otheruser", "password1!", "dup@test.com"))
                .isInstanceOf(ServiceException.class)
                .extracting("field").isEqualTo("email");
    }
}
