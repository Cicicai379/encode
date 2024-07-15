package com.xin.aoc.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xin.aoc.model.UserInfo;
import com.xin.aoc.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BoardController {
    @Autowired
    private UserInfoService userinfoService;

    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
    public String list(@RequestParam(required = false, defaultValue = "1", value = "page") Integer page, Model model) {
        if (page == null || page <= 0) page = 1;
        int size = 8;
        PageHelper.startPage(page, size);//分页

        List<UserInfo> users = userinfoService.getAllUserInfo();
        PageInfo<UserInfo> pageInfo = new PageInfo<UserInfo>(users, size);
        model.addAttribute("pageInfo", pageInfo);
        return "leaderboard2";
    }
    @RequestMapping(value = "/leaderboard2", method = RequestMethod.GET)
    public String list( Model model) {
        return "leaderboard2";
    }
}

