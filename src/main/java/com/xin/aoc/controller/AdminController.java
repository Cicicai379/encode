package com.xin.aoc.controller;

import com.xin.aoc.form.LearnForm;
import com.xin.aoc.form.ProblemForm;
import com.xin.aoc.form.UserForm;
import com.xin.aoc.mapper.ProblemMapper;
import com.xin.aoc.model.Learn;
import com.xin.aoc.model.Problem;
import com.xin.aoc.model.UserInfo;
import com.xin.aoc.service.AdminService;
import com.xin.aoc.service.ProblemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Controller
public class AdminController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdminService adminService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private ProblemMapper problemMapper;



    @GetMapping(value="/admin/upload")
    public String upload(@ModelAttribute("ProblemForm") ProblemForm problem){
        return "admin/upload2";
    }
    @PostMapping(value="/admin/upload")
    public String add(@ModelAttribute("ProblemForm") @Validated ProblemForm problem,
                      BindingResult rs){
        if(problem.getTime().isEmpty()){
            problem.setTime("2023-02-25 03:07:09.530");
        }
        if (problem.getProblem() != null && problem.getTitle()  != null) {
            if (rs.hasErrors()) {
                for (ObjectError error : rs.getAllErrors()) {
                    System.out.println(error.getDefaultMessage());
                }
                return "admin/upload2";
            }

            adminService.addProblem(problem);
            logger.info("Problem: "+problem.getTitle()+"Content: "+problem.getProblem()+"Input: " +
                    ""+problem.getInput()+"Output: "+problem.getAnswer()+"Difficulty: "
                    +problem.getDifficulty()+"Category: "+problem.getCategory());
            return "redirect:/";
        }
        return "admin/upload2";
    }


    @GetMapping("/admin/edit")
    public String edit(@ModelAttribute("ProblemForm") ProblemForm problemForm,
                       @RequestParam(required=false,value="id") int id,
                       Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");

        Problem problem = problemService.getProblem(id);

        problemForm.setTitle(problem.getTitle());
        problemForm.setProblem(problem.getProblem());
        problemForm.setAnswer(problem.getAnswer());
        problemForm.setCategory(problem.getCategory());
        problemForm.setDifficulty(problem.getDifficulty());
        problemForm.setInput(problem.getInput());
        problemForm.setTime(problem.getTime());

        session.setAttribute("login_user", user);
        session.setAttribute("problem", problem);

        return "admin/edit2";
    }

    @GetMapping("/admin/edit_contest_problem")
    public String edit_contest(@ModelAttribute("ProblemForm") ProblemForm problemForm,
                       @RequestParam(required=false,value="id") int id,
                       Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");

        Problem problem = problemMapper.getContestProblemById(id);

        problemForm.setTitle(problem.getTitle());
        problemForm.setProblem(problem.getProblem());
        problemForm.setAnswer(problem.getAnswer());
        problemForm.setCategory(problem.getCategory());
        problemForm.setDifficulty(problem.getDifficulty());
        problemForm.setInput(problem.getInput());
        problemForm.setTime(problem.getTime());

        session.setAttribute("login_user", user);
        session.setAttribute("problem", problem);

        return "admin/edit_contest_problem2";
    }

    @PostMapping("/admin/edit")
    public String handleFileUpload(@ModelAttribute("ProblemForm") @Validated ProblemForm problem,
                                   @RequestParam(required=false,value="id") Integer id,
                                   @Validated UserForm editUser,
                                   HttpServletRequest request,
                                   BindingResult rs,
                                   Model model) {
        if (rs.hasErrors()) {
            return "admin/edit2";
        }

        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);

        Problem oldProblem = problemService.getProblem(id);
        Problem newProblem = new Problem();
        String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if (editUser!=null) {
            newProblem.setTitle(problem.getTitle());
            newProblem.setProblem(problem.getProblem());
            newProblem.setCategory(problem.getCategory());
            newProblem.setDifficulty(problem.getDifficulty());
            newProblem.setInput(problem.getInput());
            newProblem.setAnswer(problem.getAnswer());
            newProblem.setTime(problem.getTime());
            newProblem.setProblemId(id);

            adminService.changeAllProblemInfo(newProblem);

            problem.setTitle(newProblem.getTitle());
            problem.setProblem(newProblem.getProblem());
            problem.setCategory(newProblem.getCategory());
            problem.setDifficulty(newProblem.getDifficulty());
            problem.setInput(newProblem.getInput());
            problem.setAnswer(newProblem.getAnswer());
            problem.setTime(newProblem.getTime());

            model.addAttribute("msg", "reset success");
            return "redirect:/";
        }


        return "admin/edit2";
    }

    @PostMapping("/admin/edit_contest_problem")
    public String edit_contest_problem2(@ModelAttribute("ProblemForm") @Validated ProblemForm problem,
                                   @RequestParam(required=false,value="id") Integer id,
                                   @Validated UserForm editUser,
                                   HttpServletRequest request,
                                   BindingResult rs,
                                   Model model) {
        if (rs.hasErrors()) {
            return "admin/edit_contest_problem2";
        }

        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);

        Problem oldProblem = problemMapper.getContestProblemById(id);
        Problem newProblem = new Problem();
        String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if (editUser!=null) {
            newProblem.setTitle(problem.getTitle());
            newProblem.setProblem(problem.getProblem());
            newProblem.setCategory(problem.getCategory());
            newProblem.setDifficulty(problem.getDifficulty());
            newProblem.setInput(problem.getInput());
            newProblem.setAnswer(problem.getAnswer());
            newProblem.setTime(problem.getTime());
            newProblem.setProblemId(id);

            adminService.changeAllProblemInfo(newProblem);

            problem.setTitle(newProblem.getTitle());
            problem.setProblem(newProblem.getProblem());
            problem.setCategory(newProblem.getCategory());
            problem.setDifficulty(newProblem.getDifficulty());
            problem.setInput(newProblem.getInput());
            problem.setAnswer(newProblem.getAnswer());
            problem.setTime(newProblem.getTime());

            model.addAttribute("msg", "reset success");
            return "redirect:/contests";
        }


        return "admin/edit_contest_problem2";
    }




    @RequestMapping("/check_title")
    @ResponseBody
    public String checkUserName(@RequestParam(value = "title") String title, HttpServletRequest request) {
        logger.info(title+"!!title");
        Problem problem = problemService.getProblemByName(title);
        if((problem != null)){
            logger.info("exist!");
            return "exist";
        }else{
            return "ok";
        }
    }
    @PostMapping("/admin/delete")
    public String del(@RequestParam(value = "id") int problemId, HttpServletRequest request, Model model) {

        if(adminService.delById(problemId)){
            logger.info("success!");
            model.addAttribute("del", "delete successful");

        }else{
            model.addAttribute("del", "delete not successful");
        }
        return "redirect:/";
    }

}
