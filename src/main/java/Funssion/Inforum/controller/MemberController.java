package Funssion.Inforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
    @PostMapping("/api/users")
    public String test(Model model){
        return "test";
    }
}
