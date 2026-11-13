package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.answer.controller.AnswerController;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
// final 필드를 매개변수로 받는 생성자를 만들어 준다 → 스프링이 그 생성자로 QuestionService 를 주입(생성자 주입)
@RequiredArgsConstructor
// 이 컨트롤러의 모든 URL 앞에 /questions 가 붙는다 (URL 프리픽스)
// 복수형 명사 + HTTP 메서드로 구분: GET /questions(목록), GET /questions/{id}(상세)
@RequestMapping("/questions")
public class QuestionController {
    // 컨트롤러는 리포지터리를 직접 쓰지 않고 서비스를 통한다.
    private final QuestionService questionService;

    @GetMapping // GET /questions?page=0
    // Model: 컨트롤러 → 템플릿으로 데이터를 넘기는 바구니. 스프링이 만들어서 넣어 준다.
    // @RequestParam(defaultValue = "0"): ?page= 가 없으면 첫 페이지(0)
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging", paging); // 템플릿에서 ${paging}: 현재 페이지 질문들 + 페이지 정보

        // @ResponseBody 가 없으므로 뷰 이름 → templates/question/question/list.html
        return "question/question/list";
    }

    // {id}: URL 경로의 일부를 변수로 받는다. /questions/2 → id = 2
    // 상세 화면에는 답변 등록 폼이 있으므로 빈 답변 폼(form)도 함께 넘긴다
    @GetMapping("/{id}")
    public String detail(
            @PathVariable int id,
            @ModelAttribute("form") AnswerController.WriteForm form,
            Model model
    ) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "question/question/detail";
    }

    // 폼 입력값을 담는 객체 (record: 필드·생성자·getter 를 한 줄로, 14168 40강)
    // 메시지 앞의 "01-subject-" : 정렬 순서-필드명-메시지. form_errors.html 이 나눠서 쓴다 (14168 24강)
    public record WriteForm(
            @NotBlank(message = "01-subject-제목을 입력해주세요.")
            @Size(max = 200, message = "02-subject-제목은 200자 이하로 입력해주세요.")
            String subject,
            @NotBlank(message = "03-content-내용을 입력해주세요.")
            String content
    ) {
    }

    // 같은 URL 이라도 HTTP 메서드로 구분한다 (14168 26강)
    // GET /questions/write : 등록 폼 보여 주기
    // @ModelAttribute("form"): 템플릿에서 ${form} 으로 쓴다. 처음엔 빈 값(null) 폼
    @GetMapping("/write")
    public String showWrite(@ModelAttribute("form") WriteForm form) {
        return "question/question/write";
    }

    // POST /questions/write : 등록 처리
    // @Valid: WriteForm 의 검증 규칙 실행. BindingResult: 실패 내용을 담는다(없으면 실패 시 바로 400 예외)
    @PostMapping("/write")
    public String write(@ModelAttribute("form") @Valid WriteForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "question/question/write"; // 입력값과 오류를 들고 폼으로

        Question question = questionService.write(form.subject(), form.content());

        return "redirect:/questions/" + question.getId();
    }
}
