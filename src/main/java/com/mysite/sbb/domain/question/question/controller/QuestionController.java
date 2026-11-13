package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
// final 필드를 매개변수로 받는 생성자를 만들어 준다 → 스프링이 그 생성자로 QuestionService 를 주입(생성자 주입)
@RequiredArgsConstructor
public class QuestionController {
    // 컨트롤러는 리포지터리를 직접 쓰지 않고 서비스를 통한다.
    private final QuestionService questionService;

    @GetMapping("/question/list")
    // Model: 컨트롤러 → 템플릿으로 데이터를 넘기는 바구니. 스프링이 만들어서 넣어 준다.
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList); // 템플릿에서 ${questionList}

        // @ResponseBody 가 없으므로 뷰 이름 → templates/question/question/list.html
        return "question/question/list";
    }
}
