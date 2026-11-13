package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/sbb")
    public void index() {
        System.out.println("index() 메서드 호출됨!");
    }
}
