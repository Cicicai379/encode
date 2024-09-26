package com.xin.aoc.controller;

import jakarta.servlet.http.HttpServletRequest;

// Spring Framework documentation: https://spring.io/projects/spring-framework
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Spring Framework documentation: https://spring.io/projects/spring-framework
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class LocaleController {
    static Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/locale")
    public String localeHandler(HttpServletRequest request, Locale locale) {
        String lastUrl = request.getHeader("referer");
        lastUrl = lastUrl != null ? lastUrl : "/";
        return "redirect:" + lastUrl;
    }
}
