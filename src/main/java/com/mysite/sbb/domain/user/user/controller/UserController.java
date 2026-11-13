package com.mysite.sbb.domain.user.user.controller;

import com.mysite.sbb.domain.user.user.service.UserService;
import com.mysite.sbb.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // 메시지 앞 "NN-필드명-" 은 form_errors.html 이 정렬·분리해서 쓴다
    public record SignupForm(
            @NotBlank(message = "01-username-아이디를 입력해주세요.")
            @Size(min = 3, max = 25, message = "02-username-아이디는 3자 이상 25자 이하로 입력해주세요.")
            String username,
            @NotBlank(message = "03-password1-비밀번호를 입력해주세요.")
            String password1,
            @NotBlank(message = "04-password2-비밀번호 확인을 입력해주세요.")
            String password2,
            @NotBlank(message = "06-email-이메일을 입력해주세요.")
            @Email(message = "07-email-이메일 형식이 올바르지 않습니다.")
            String email
    ) {
    }

    @GetMapping("/signup")
    public String showSignup(@ModelAttribute("form") SignupForm form) {
        return "user/user/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("form") @Valid SignupForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "user/user/signup";

        // 두 칸이 같은지는 필드 하나만 봐서는 알 수 없으므로 직접 확인한다 (교재와 같음)
        if (!form.password1().equals(form.password2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "05-password2-2개의 비밀번호가 일치하지 않습니다.");
            return "user/user/signup";
        }

        try {
            userService.join(form.username(), form.password1(), form.email());
        } catch (ServiceException e) {
            // 서비스가 알려 준 칸에 오류를 붙여 폼을 다시 보여 준다
            bindingResult.rejectValue(e.getField(), "duplicate", "08-%s-%s".formatted(e.getField(), e.getMessage()));
            return "user/user/signup";
        }

        return "redirect:/";
    }
}
