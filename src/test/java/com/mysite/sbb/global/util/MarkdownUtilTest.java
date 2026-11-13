package com.mysite.sbb.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// 스프링 없이 객체만 만들어 시험하는 단위 테스트 (빠르다)
class MarkdownUtilTest {
    private final MarkdownUtil markdownUtil = new MarkdownUtil();

    @Test
    @DisplayName("마크다운 → HTML: 제목, 목록, 굵게")
    void t1() {
        String html = markdownUtil.toHtml("# 제목\n\n- 목록\n- **굵게**");

        assertThat(html).contains("<h1>제목</h1>", "<li>목록</li>", "<strong>굵게</strong>");
    }
}
