package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
// 답변은 질문에 속한 자원 → /questions/{questionId}/answers
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {
    private final AnswerService answerService;

    // POST /questions/2/answers : 2번 질문에 답변 생성
    // @RequestParam: 폼의 name="content" 값을 받는다
    @PostMapping
    public String write(@PathVariable int questionId, @RequestParam String content) {
        answerService.write(questionId, content);

        // 처리 후에는 상세 화면으로 리다이렉트 (새로고침해도 POST 가 다시 전송되지 않는다: PRG 패턴)
        return "redirect:/questions/" + questionId;
    }
}
