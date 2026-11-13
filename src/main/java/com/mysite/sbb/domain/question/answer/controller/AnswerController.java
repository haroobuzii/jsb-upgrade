package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.service.AnswerService;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
// 답변은 질문에 속한 자원 → /questions/{questionId}/answers
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    public record WriteForm(
            @NotBlank(message = "01-content-내용을 입력해주세요.")
            String content
    ) {
    }

    // POST /questions/2/answers : 2번 질문에 답변 생성
    @PostMapping
    public String write(
            @PathVariable int questionId,
            @ModelAttribute("form") @Valid WriteForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            // 상세 화면을 다시 그리려면 질문도 필요하다
            model.addAttribute("question", questionService.getQuestion(questionId));
            return "question/question/detail";
        }

        answerService.write(questionId, form.content());

        // 처리 후에는 상세 화면으로 리다이렉트 (새로고침해도 POST 가 다시 전송되지 않는다: PRG 패턴)
        return "redirect:/questions/" + questionId;
    }
}
