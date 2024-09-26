package com.xin.aoc.controller;

// Jakarta Servlet documentation: https://jakarta.ee/specifications/servlet/
import jakarta.servlet.http.HttpServletRequest;

// Spring Framework documentation: https://spring.io/projects/spring-framework
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AboutController {

    @RequestMapping(value = "/about")
    public String showAbout(Model model, HttpServletRequest request) {
        return "about2";
    }
}
