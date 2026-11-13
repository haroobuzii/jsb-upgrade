package com.mysite.sbb.domain.user.user.controller;

import com.mysite.sbb.domain.user.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvcTester mvc;
    @Autowired
    private UserRepository userRepository;

    private MockMvcTester.MockMvcRequestBuilder signup(String username, String pw1, String pw2, String email) {
        return mvc.post().uri("/users/signup").with(csrf())
                .param("username", username).param("password1", pw1)
                .param("password2", pw2).param("email", email);
    }

    @Test
    @DisplayName("POST /users/signup : 가입 성공 → 회원 저장, 메인으로 리다이렉트")
    void t1() {
        assertThat(signup("signupuser", "pw1234!", "pw1234!", "signup@test.com"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/");

        assertThat(userRepository.findByUsername("signupuser")).isPresent();
    }

    @Test
    @DisplayName("POST /users/signup : 비밀번호 확인이 다르면 가입 안 됨")
    void t2() {
        assertThat(signup("signupuser", "pw1234!", "different", "signup@test.com"))
                .hasStatusOk()
                .hasViewName("user/user/signup")
                .bodyText().contains("2개의 비밀번호가 일치하지 않습니다.");

        assertThat(userRepository.findByUsername("signupuser")).isEmpty();
    }

    @Test
    @DisplayName("POST /users/signup : 이미 있는 아이디면 아이디 칸 오류")
    void t3() {
        signup("sameuser", "pw1234!", "pw1234!", "first@test.com").exchange();

        assertThat(signup("sameuser", "pw1234!", "pw1234!", "second@test.com"))
                .hasStatusOk()
                .bodyText().contains("이미 사용 중인 아이디입니다.");
    }

    @Test
    @DisplayName("POST /users/login : 샘플 회원 user1 로그인 성공 → / 로, 인증됨")
    void t4() {
        assertThat(mvc.post().uri("/users/login").with(csrf())
                .param("username", "user1").param("password", "1234"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/")
                .matches(authenticated().withUsername("user1"));
    }

    @Test
    @DisplayName("POST /users/login : 비밀번호가 틀리면 /users/login?error, 인증 안 됨")
    void t5() {
        assertThat(mvc.post().uri("/users/login").with(csrf())
                .param("username", "user1").param("password", "wrong"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/users/login?error")
                .matches(unauthenticated());
    }

    @Test
    @DisplayName("내비게이션 바: 비로그인은 로그인·회원가입, 로그인하면 아이디와 로그아웃")
    void t6() {
        assertThat(mvc.get().uri("/questions"))
                .bodyText().contains("/users/login", "/users/signup").doesNotContain("로그아웃");

        assertThat(mvc.get().uri("/questions").with(user("user1")))
                .bodyText().contains("user1", "로그아웃").doesNotContain("/users/signup");
    }

    @Test
    @DisplayName("POST /users/logout : 로그아웃 후 / 로, 인증 해제")
    void t7() {
        assertThat(mvc.post().uri("/users/logout").with(user("user1")).with(csrf()))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/")
                .matches(unauthenticated());
    }
}
