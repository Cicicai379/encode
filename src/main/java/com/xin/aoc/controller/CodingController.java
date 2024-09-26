package com.xin.aoc.controller;

import com.xin.aoc.form.ProblemForm;
import com.xin.aoc.form.UserForm;
import com.xin.aoc.model.Problem;
import com.xin.aoc.model.UserInfo;
import com.xin.aoc.service.AdminService;
import com.xin.aoc.service.CompilerService;
import com.xin.aoc.service.ProblemService;
import com.xin.aoc.service.RecordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// Simple Logging Facade for Java documentation: https://www.slf4j.org/docs.html
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Spring Framework documentation: https://spring.io/projects/spring-framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Random;

@Controller
public class CodingController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProblemService problemService;
    @Autowired
    private IndexController indexController;
    @Autowired
    private RecordService recordService;
    @Autowired
    private CompilerService compilerService;


    @GetMapping(value="/coding")
    public String add(){
        return "coding2";
    }

    @PostMapping(value="/coding")
    public String submit(@RequestParam(value="in_put") String input,
                         @RequestParam(value="code") String code,
                         Model model, HttpServletRequest request){

        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        if(input==null) input="0";
        String name="";
        String output="";
        Random rd = new Random();
        if(user!=null){
            name = "test"+user.getUserId();
        }else{
            name = "test"+rd.nextInt();
        }
        output = compilerService.compile(name,code);
        if(output.length() == 0){
            output = compilerService.execute(name, input);
        }
        logger.info("input "+input);
        logger.info("name "+name);
        logger.info("output "+output);
        model.addAttribute("msg", output);
        model.addAttribute("code", code);
        model.addAttribute("input", input);
        return "coding2";
    }
}