package com.mysite.sbb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    @DisplayName("GET /sbb 는 200 과 환영 문자열을 text/plain 으로 응답한다")
    void t1() {
        assertThat(mvc.get().uri("/sbb"))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo("안녕하세요 sbb에 오신 것을 환영합니다.");
    }

    @Test
    @DisplayName("GET / 는 질문 목록으로 리다이렉트(302)한다")
    void t2() {
        assertThat(mvc.get().uri("/"))
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/questions");
    }
}
