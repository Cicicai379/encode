package com.xin.aoc.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xin.aoc.model.Problem;
import com.xin.aoc.model.UserInfo;
import com.xin.aoc.service.ProblemService;
import com.xin.aoc.service.RecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
public class IndexController {
    @Autowired
    private ProblemService problemService;
    @Autowired
    private RecordService recordService;


    @RequestMapping(value = "/")
    public String search(@RequestParam(required = false, defaultValue = "1", value = "page")
                         Integer page,
                         @RequestParam(required = false,  value = "key") String key,
                         Model model, HttpServletRequest request) {
        if (page == null || page <= 0) page = 1;
        int size = 8;
        PageHelper.startPage(page, size);
        List<Problem> problems = problemService.getProblems();
        if(key!=null){
             problems = problemService.getProblemsByKey(key);
        }

        PageInfo<Problem> pageInfo = new PageInfo<Problem>(problems, size);
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        HttpSession session = request.getSession();

        ArrayList<Integer> statusInfo=null;
        if(user!=null){
            session.setAttribute("login_user", user);
            statusInfo = recordService.findSolved(user);
        }

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("statusInfo", statusInfo);
        return "index2";
    }
    @RequestMapping(value = "/home")
    public String search() {
        return "index2";
    }
}
