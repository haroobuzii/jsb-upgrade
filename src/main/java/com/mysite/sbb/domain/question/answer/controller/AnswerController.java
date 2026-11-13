package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.answer.service.AnswerService;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import com.mysite.sbb.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
// 답변은 질문에 속한 자원 → /questions/{questionId}/answers
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    public record WriteForm(
            @NotBlank(message = "01-content-내용을 입력해주세요.")
            String content
    ) {
    }

    // POST /questions/2/answers : 2번 질문에 답변 생성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String write(
            @PathVariable int questionId,
            @ModelAttribute("form") @Valid WriteForm form,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            // 상세 화면을 다시 그리려면 질문도 필요하다
            model.addAttribute("question", questionService.getQuestion(questionId));
            return "question/question/detail";
        }

        Answer answer = answerService.write(questionId, userService.getUser(principal.getName()), form.content());

        // 처리 후에는 상세 화면으로 리다이렉트 (새로고침해도 POST 가 다시 전송되지 않는다: PRG 패턴)
        // #answer_{id}: 그 답변 위치(앵커)로 바로 스크롤
        return "redirect:/questions/%d#answer_%d".formatted(questionId, answer.getId());
    }

    // 답변 수정 폼: GET /questions/{questionId}/answers/{id}/modify
    @GetMapping("/{id}/modify")
    @PreAuthorize("isAuthenticated()")
    public String showModify(@PathVariable int questionId, @PathVariable int id, Model model, Principal principal) {
        Answer answer = answerService.getAnswer(questionId, id);
        answerService.checkAuthor(answer, principal.getName(), "수정권한이 없습니다.");

        model.addAttribute("answer", answer);
        model.addAttribute("form", new WriteForm(answer.getContent()));
        return "question/answer/modify";
    }

    // 답변 수정 처리: PUT (폼은 POST + _method=PUT)
    @PutMapping("/{id}/modify")
    @PreAuthorize("isAuthenticated()")
    public String modify(
            @PathVariable int questionId,
            @PathVariable int id,
            @ModelAttribute("form") @Valid WriteForm form,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answerService.getAnswer(questionId, id));
            return "question/answer/modify";
        }

        answerService.modify(questionId, id, principal.getName(), form.content());

        return "redirect:/questions/%d#answer_%d".formatted(questionId, id);
    }

    // 답변 삭제: DELETE /questions/{questionId}/answers/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable int questionId, @PathVariable int id, Principal principal) {
        answerService.delete(questionId, id, principal.getName());

        return "redirect:/questions/" + questionId;
    }

    // 답변 추천: POST /questions/{questionId}/answers/{id}/vote
    @PostMapping("/{id}/vote")
    @PreAuthorize("isAuthenticated()")
    public String vote(@PathVariable int questionId, @PathVariable int id, Principal principal) {
        answerService.vote(questionId, id, userService.getUser(principal.getName()));

        return "redirect:/questions/%d#answer_%d".formatted(questionId, id);
    }
}
