package com.xin.aoc.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xin.aoc.form.LearnForm;
import com.xin.aoc.form.ProblemForm;
import com.xin.aoc.form.UserForm;
import com.xin.aoc.mapper.DiscussionMapper;
import com.xin.aoc.mapper.LearnMapper;
import com.xin.aoc.mapper.ProblemMapper;
import com.xin.aoc.model.Discussion;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;

@Controller
public class LearnController {
    @Autowired
    LearnMapper learnMapper;
    @Autowired
    ProblemService problemService;
    @Autowired
    private AdminService adminService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/learn")
    public String discuss(
            Model model, HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page) {
        if (page == null || page <= 0) page = 1;
        int size = 8;
        PageHelper.startPage(page, size);

        UserInfo user = (UserInfo) request.getSession().getAttribute("login_user");

        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);

        List<Learn> learns = learnMapper.getLearn();

        PageInfo<Learn> pageInfo = new PageInfo<Learn>(learns, size);
        model.addAttribute("pageInfo", pageInfo);
        return "learn2";
    }


    @GetMapping(value = "/learn/read")
    public String discuss(
            Model model, HttpServletRequest request,
            @RequestParam(required = false, value="id") int learnId) {
        Learn learn = learnMapper.getLearnById(learnId);
        model.addAttribute(learn);
        return "read2";
    }

    @GetMapping(value = "/admin/write")
    public String discuss(@ModelAttribute("LearnForm") LearnForm learn) {
        return "admin/write2";
    }

@PostMapping(value="/admin/write")
public String add(@ModelAttribute("LearnForm") @Validated LearnForm learn,
                  BindingResult rs, HttpServletRequest request){

    if (learn.getContent() != null && learn.getTitle()  != null) {
        if (rs.hasErrors()) {
            for (ObjectError error : rs.getAllErrors()) {
                System.out.println(error.getDefaultMessage());
            }
            return "admin/write2";
        }
        Learn newlearn=new Learn();
        String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        UserInfo user = (UserInfo) request.getSession().getAttribute("login_user");

        newlearn.setContent(learn.getContent());
        newlearn.setCategory(learn.getCategory());
        newlearn.setDifficulty(learn.getDifficulty());
        newlearn.setTitle(learn.getTitle());
        newlearn.setNickName(user.getNickName());
        newlearn.setUserId(user.getUserId());
        newlearn.setCurDate(time);
        newlearn.setImage(user.getImage());
        learnMapper.addLearn(newlearn);

        logger.info("Title: "+learn.getTitle()+"Content: "+learn.getContent()+"Difficulty: " +
                learn.getDifficulty()+"Category: "+learn.getCategory());

        return "redirect:/learn";
    }
    return "admin/write2";
}


    @GetMapping("/admin/edit_learn")
    public String edit(@ModelAttribute("LearnForm") LearnForm learn,
                       @RequestParam(required=false,value="id") int id,
                       Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        model.addAttribute("learnId",id);

        Learn oldlearn = learnMapper.getLearnById(id);

        learn.setTitle(oldlearn.getTitle());
        learn.setCategory(oldlearn.getCategory());
        learn.setDifficulty(oldlearn.getDifficulty());
        learn.setContent(oldlearn.getContent());

        session.setAttribute("learn", learn);
        session.setAttribute("login_user", user);

        return "admin/edit_learn2";
    }

    @PostMapping("/admin/edit_learn")
    public String handleFileUpload(@ModelAttribute("LearnForm") @Validated LearnForm learn,
                                   @RequestParam(required=false,value="id") int id,
                                   @Validated UserForm editUser,
                                   HttpServletRequest request,
                                   BindingResult rs,
                                   Model model) {
        if (rs.hasErrors()) {
            return "admin/edit_learn2";
        }

        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        model.addAttribute("learnId",id);

        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);

        Learn oldlearn = learnMapper.getLearnById(id);
        Learn newlearn = new Learn();
        String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if (editUser!=null) {
            newlearn.setTitle(learn.getTitle());
            newlearn.setContent(learn.getContent());
            newlearn.setCategory(learn.getCategory());
            newlearn.setDifficulty(learn.getDifficulty());
            newlearn.setLearnId(id);

            logger.info("newuser "+newlearn.getTitle()+id);

            learnMapper.changeAllLearnInfo(newlearn);
            learn.setContent(newlearn.getContent());
            learn.setCategory(newlearn.getCategory());
            learn.setDifficulty(newlearn.getDifficulty());
            learn.setTitle(newlearn.getTitle());
            learn.setNickName(user.getNickName());
            learn.setUserId(user.getUserId());
            learn.setCurDate(time);
            learn.setImage(user.getImage());
            model.addAttribute("msg", "reset success");
            return "redirect:/learn";
        }


        return "admin/edit_learn2";
    }

    @PostMapping("/admin/delete_learn")
    public String del(@RequestParam(value = "id") int learnId, HttpServletRequest request, Model model) {

        learnMapper.delById(learnId);
            logger.info("11"+String.valueOf(learnId));
            model.addAttribute("del", "delete successful");

        return "redirect:/learn";
    }

}