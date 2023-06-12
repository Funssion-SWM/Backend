package Funssion.Inforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class test_controller {
    @GetMapping("test")
    public String test(Model model){
        return "test";
    }
}
