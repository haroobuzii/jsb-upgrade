package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
// final 필드를 매개변수로 받는 생성자를 만들어 준다 → 스프링이 그 생성자로 QuestionService 를 주입(생성자 주입)
@RequiredArgsConstructor
// 이 컨트롤러의 모든 URL 앞에 /questions 가 붙는다 (URL 프리픽스)
// 복수형 명사 + HTTP 메서드로 구분: GET /questions(목록), GET /questions/{id}(상세)
@RequestMapping("/questions")
public class QuestionController {
    // 컨트롤러는 리포지터리를 직접 쓰지 않고 서비스를 통한다.
    private final QuestionService questionService;

    @GetMapping // GET /questions
    // Model: 컨트롤러 → 템플릿으로 데이터를 넘기는 바구니. 스프링이 만들어서 넣어 준다.
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList); // 템플릿에서 ${questionList}

        // @ResponseBody 가 없으므로 뷰 이름 → templates/question/question/list.html
        return "question/question/list";
    }

    // {id}: URL 경로의 일부를 변수로 받는다. /questions/2 → id = 2
    @GetMapping("/{id}")
    public String detail(@PathVariable int id, Model model) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "question/question/detail";
    }
}
