package com.mysite.sbb.global.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

// 마크다운 문자열 → HTML 문자열. 템플릿에서 ${@markdownUtil.toHtml(...)} 로 쓴다 (교재 3-12 CommonUtil)
@Component
public class MarkdownUtil {
    private final Parser parser = Parser.builder().build();
    // XSS 방지 (결정 사항)
    // escapeHtml(true): 글 안에 직접 쓴 HTML(<script> 등)을 태그가 아니라 글자(&lt;script&gt;)로 바꾼다
    // sanitizeUrls(true): 링크·이미지 주소의 javascript: 같은 위험한 스킴을 지운다 → [클릭](javascript:...) 차단
    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .escapeHtml(true)
            .sanitizeUrls(true)
            .build();

    public String toHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
