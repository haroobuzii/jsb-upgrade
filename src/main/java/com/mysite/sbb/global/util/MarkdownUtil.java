package com.mysite.sbb.global.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

// 마크다운 문자열 → HTML 문자열. 템플릿에서 ${@markdownUtil.toHtml(...)} 로 쓴다 (교재 3-12 CommonUtil)
@Component
public class MarkdownUtil {
    private final Parser parser = Parser.builder().build();
    // 주의: 교재 그대로의 설정. 글 안의 HTML(<script> 등)도 그대로 통과시킨다 → 020-02에서 막는다
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public String toHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
