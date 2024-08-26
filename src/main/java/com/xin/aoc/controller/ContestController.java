package com.xin.aoc.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xin.aoc.form.ContestForm;
import com.xin.aoc.form.LearnForm;
import com.xin.aoc.form.ProblemForm;
import com.xin.aoc.form.UserForm;
import com.xin.aoc.mapper.*;
import com.xin.aoc.model.*;
import com.xin.aoc.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContestController {
    @Autowired
    ContestMapper contestMapper;
    @Autowired
    ProblemService problemService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private CompilerService compilerService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private ProblemMapper problemMapper;

    private static SimpleDateFormat sDateTimeFormat = null;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/contests")
    public String discuss(
            Model model, HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page) {
        if (page == null || page <= 0) page = 1;
        int size = 8;
        PageHelper.startPage(page, size);

        UserInfo user = (UserInfo) request.getSession().getAttribute("login_user");

        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);
        List<Contest> contests = contestMapper.getContests();

        PageInfo<Contest> pageInfo = new PageInfo<Contest>(contests, size);
        model.addAttribute("pageInfo", pageInfo);
        return "contest/contests2";
    }


    @GetMapping(value = "/contest")
    public String contest(
            Model model, HttpServletRequest request,
            @RequestParam(required = false, value="id") int contestId) {
        System.out.println("!!!!!"+contestId);
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        HttpSession session = request.getSession();
        if(user!=null && contestMapper.contestHasStarted(contestId)!=1 && user.getIsAdmin()!=1){
            return "redirect:/contests";
        }
        Contest contest = contestMapper.getAllContestById(contestId);
        System.out.println("!!!!!"+contest);
        model.addAttribute("contest",contest);
        model.addAttribute("contestId",contestId);

        int page = 1;
        int size = 8;
        PageHelper.startPage(page, size);
        ArrayList<Integer> statusInfo=null;
        List<Problem> problems=null;
        List<ContestRecord> ranks = contestMapper.getScoreById(contestId);

        long timeLeft = -1;
        if(user!=null){
            session.setAttribute("login_user", user);
            statusInfo = recordService.findSolved(user);
            if(contestMapper.hasStarted(user.getUserId(),contestId)==1){
               timeLeft = (contestMapper.getStop(user.getUserId(), contestId) - System.currentTimeMillis())/1000;
            }
        }

        PageInfo<ContestRecord> rankInfo = new PageInfo<ContestRecord>(ranks, size);

        problems = problemService.getProblemsByContest(contestId);
        PageInfo<Problem> pageInfo = new PageInfo<Problem>(problems, size);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("rankInfo", rankInfo);
        model.addAttribute("statusInfo", statusInfo);
        model.addAttribute("timeLeft", timeLeft);

        return "contest/contest2";
    }

    @PostMapping(value = "user/start_contest")
    public String start(
            Model model, HttpServletRequest request,
            @RequestParam(required = false, value="id") int contestId) {
            UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
            HttpSession session = request.getSession();

            if(user!=null && !contestMapper.hasNotJoin(user.getUserId(),contestId)){
                ContestRecord contestRecord = new ContestRecord();
                contestRecord.setImage(user.getImage());
                contestRecord.setUserName(user.getUserName());
                contestRecord.setUserId(user.getUserId());
                contestRecord.setContestId(contestId);
                contestMapper.addRecord(contestRecord);
            }

            long start = System.currentTimeMillis();
            long stop = System.currentTimeMillis()+(long)contestMapper.getContestDuration(contestId)*3600*1000;

            logger.info(start+" "+stop+" "+user.getUserId()+contestId);
            contestMapper.startContest(stop,start, user.getUserId(), contestId);
            return "redirect: /contest?id="+contestId;
    }

    @GetMapping(value = "/admin/upload_contest")
    public String discuss(@ModelAttribute("ContestForm") ContestForm contest) {
        return "contest/upload2";
    }

    @PostMapping(value="/admin/upload_contest")
    public String add(@ModelAttribute("ContestForm") @Validated ContestForm contest,
                      BindingResult rs, HttpServletRequest request){
        if (contest.getContent() != null && contest.getTitle()  != null) {
            if (rs.hasErrors()) {
                for (ObjectError error : rs.getAllErrors()) {
                    System.out.println(error.getDefaultMessage());
                }
                return  "contest/upload2";
            }
            String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            UserInfo user = (UserInfo) request.getSession().getAttribute("login_user");
            contestMapper.addContest(contest);

            logger.info("Contest: "+contest.getTitle()+"Details: "+contest.getContent()+"Duration: " +
                    ""+contest.getDuration()+"Date: "+contest.getStart());
            return "redirect:/contests";
        }
        return  "contest/upload2";
    }


    @GetMapping("/admin/edit_contest")
    public String edit(@ModelAttribute("ContestForm") ContestForm contest,
                       @RequestParam(required=false,value="id") int id,
                       Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        model.addAttribute("contestId",id);
        Contest oldcontest = contestMapper.getAllContestById(id);
        contest.setStop(oldcontest.getStop());
        contest.setStart(oldcontest.getStart());
        contest.setContent(oldcontest.getContent());
        contest.setDuration(oldcontest.getDuration());
        contest.setTitle(oldcontest.getTitle());

        session.setAttribute("contest", contest);
        session.setAttribute("login_user", user);

        return "contest/edit2";
    }

    @PostMapping("/admin/edit_contest")
    public String handleFileUpload(@ModelAttribute("ContestForm") @Validated ContestForm contest,
                                   @RequestParam(required=false,value="id") int id,
                                   @Validated UserForm editUser,
                                   HttpServletRequest request,
                                   BindingResult rs,
                                   Model model) {
        logger.info("edit_contest post");
        if (rs.hasErrors()) {
            logger.info("rs error");
            for (ObjectError error : rs.getAllErrors()) {
                System.out.println(error.getDefaultMessage());
            }
            return "contest/edit2?id="+id;
        }

        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        model.addAttribute("contestId",id);

        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);
        logger.info(contest.getTitle()+"new contest title!");
        contest.setContestId(id);
        contestMapper.changeAllContestInfo(contest);
        model.addAttribute("msg", "reset success");
        return "redirect:/contests";

    }

    @PostMapping("/admin/delete_contest")
    public String del(@RequestParam(value = "id") int contestId, HttpServletRequest request, Model model) {
        System.out.println("del start");
        contestMapper.delById(contestId);
        logger.info("11"+String.valueOf(contestId));
        model.addAttribute("del", "delete successful");
System.out.println("!!!!!");
        return "redirect:/contests";
    }

    @GetMapping(value="/admin/upload_contest_problem")
    public String upload( @RequestParam(value = "id") int contestId, @ModelAttribute("ProblemForm") ProblemForm problem, Model model){
        model.addAttribute("contestId",contestId);
        Contest contest = contestMapper.getAllContestById(contestId);
        model.addAttribute("contest",contest);
        return "contest/upload_problem2";
    }

    @PostMapping(value="/admin/upload_contest_problem")
    public String add(@ModelAttribute("ProblemForm") @Validated ProblemForm problem,
                      BindingResult rs,  @RequestParam(value = "id") int contestId){
        Contest contest = contestMapper.getAllContestById(contestId);
        problem.setTime(contest.getStop());
        problem.setContestId(contestId);

        if (problem.getProblem() != null && problem.getTitle()  != null) {
            if (rs.hasErrors()) {
                for (ObjectError error : rs.getAllErrors()) {
                    System.out.println(error.getDefaultMessage());
                }
                return "contest/upload_problem2";
            }

            adminService.addProblem(problem);
            return "redirect:/contest?id="+contestId;
        }
        return "contest/upload_problem2";
    }


    @GetMapping(value="/problems_contest")
    public String add(@RequestParam(required=false,value="id") int id,
                      @RequestParam(required=false,value="contestId") int contestId,Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        if(user==null || contestMapper.hasStarted(user.getUserId(),contestId)!=1) {
            return "redirect:/contest?id="+contestId;
        }
            Problem problem = problemMapper.getProblemByIdAndContest(id);
        if(problem.getContestId()==0){
            logger.info("!!contest");
            return "redirect:/";
        }
        logger.info(problem.getProblemId()+"id!!");
        model.addAttribute("contestId",contestId);
        model.addAttribute("problemInfo", problem);

        // handle submission column display
        List<Submission> submissions = submissionService.getSubmissionsByUserId(user.getUserId(),id);
        submissions = submissions.subList(0, Math.min(3, submissions.size()));

        // handle submission column display
//        List<Submission> submissions = new LinkedList<>(submissionService.getSubmissionsByUserId(user.getUserId(), id));
//        submissions = submissions.subList(0, Math.min(3, submissions.size()));

        logger.info(submissions.toString()+"!!!!");
        PageInfo<Submission> subInfo = new PageInfo<Submission>(submissions);
        model.addAttribute("submissions", subInfo);
        return "contest/contest_problem2";
    }

    @GetMapping(value="/user/submit_contest")
    public String submit(
            @RequestParam(required=false,value="id") int problemId,
            @RequestParam(required=false,value="msg") String msg,
            @RequestParam(required=false,value="result") String result,
            @RequestParam(required=false,value="contestId") int contestId,
            Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes){
        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");

        if(user==null || contestMapper.hasStarted(user.getUserId(),contestId)!=1) {
            return "redirect:/contest?id="+contestId;
        }

        Calendar calendar = Calendar.getInstance();
        sDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(contestMapper.getStop(user.getUserId(),contestId)<System.currentTimeMillis()
        ||sDateTimeFormat.format(calendar.getTime()).compareTo(contestMapper.getContestStop(contestId))>0) {
            logger.info(sDateTimeFormat.toString()+" "+contestMapper.getContestStop(contestId));
            return "redirect:/contest?id="+contestId;
        }

        logger.info("!!proid"+problemId);
        String lastSubmission = submissionMapper.getLastSubmission(problemId, user.getUserId());
        model.addAttribute("lastSubmission", lastSubmission);
        Problem problem = problemMapper.getProblemByIdAndContest(problemId);
        model.addAttribute("problemInfo", problem);
        model.addAttribute("result", result);
        model.addAttribute("msg", msg);
        model.addAttribute("contestId",contestId);

        // handle submission column display
        List<Submission> submissions = submissionService.getSubmissionsByUserId(user.getUserId(),problemId);
        submissions = submissions.subList(0, Math.min(3, submissions.size()));
        logger.info(submissions.toString()+"!!!!");
        PageInfo<Submission> subInfo = new PageInfo<Submission>(submissions);
        model.addAttribute("submissions", subInfo);

        return "contest/contest_submit2";
    }

    @RequestMapping(value = "/user/submission_contest")
    public String search(@RequestParam(required = false, defaultValue = "1", value = "page")
                         Integer page,
                         @RequestParam(required = false,  value = "key") String key,
                         @RequestParam(required = false,  value = "id") int problemId,
                         @RequestParam(required=false,value="contestId") int contestId,
                         Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        if(user==null || contestMapper.hasStarted(user.getUserId(),contestId)!=1) {
            return "redirect:/contest?id="+contestId;
        }

        Problem problem = problemMapper.getProblemByIdAndContest(problemId);
        model.addAttribute("problemInfo", problem);
        if (page == null || page <= 0) page = 1;
        int size = 5;
        PageHelper.startPage(page, size);

        List<Submission> submissions = submissionService.getSubmissionsByUserId(user.getUserId(),problemId);
        logger.info(submissions.toString()+"!!!!");
        PageInfo<Submission> pageInfo = new PageInfo<Submission>(submissions, size);
        model.addAttribute("submissions", pageInfo);
        model.addAttribute("contestId",contestId);
        return "contest/contest_submission2";
    }
    @PostMapping(value="/user/submit_contest")
    public String submit(@RequestParam(value="file") MultipartFile file,
                         @RequestParam(required=false,value="code") String word_code,
                         @RequestParam(required=false,value="id") int id,
                         @RequestParam(required=false,value="contestId") int contestId,
                         Model model, HttpServletRequest request,
                         HttpServletResponse response){

        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        if(user==null || contestMapper.hasStarted(user.getUserId(),contestId)!=1) {
            return "redirect:/contest?id="+contestId;
        }

        Problem problem = problemMapper.getProblemByIdAndContest(id);
        model.addAttribute("problemInfo", problem);
        model.addAttribute("contestId",contestId);

        String code="";
        if((file==null && word_code==null) || user==null){
            return "redirect:/contest/contest_submit";
        }

        if(!Objects.equals(word_code, "")){
            code=word_code;
        }else {
            if (!file.isEmpty()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        code += line;
                        code += '\n';
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String name="";
        String output="";
        if(user!=null){
            name = "test"+user.getUserId();
        }else{
            return  "redirect:/login";
        }
        output = compilerService.compile(name,code);
        String msg="";
        String result="";
        if(output.length() == 0){
            output = compilerService.execute(name, problem.getInput());
            msg="";
        }else{
            msg=output;
        }

        logger.info("code: "+code);
        logger.info("output "+output);

        String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        int status=0;
        if(output.equals(problem.getAnswer()) && user!=null){
            result="Your answer is correct!";
            if(recordService.exist(user.getUserId(),problem.getProblemId())<1) {
                problemService.addScore(user.getUserId());
                contestMapper.addScore(contestId, user.getUserId());
                recordService.addRecord(time, user.getUserId(),problem.getProblemId());
                status = 1;
            }
        }else{
            result="Your answer is incorrect, please try again.";
        }
        model.addAttribute("msg",msg);
        model.addAttribute("result",result);
        model.addAttribute("lastSubmission",code);
        submissionService.update(problem.getProblemId(),user.getUserId(), code, time, status, problem.getTitle());

        // handle submission column display
        List<Submission> submissions = submissionService.getSubmissionsByUserId(user.getUserId(),id);
        submissions = submissions.subList(0, Math.min(3, submissions.size()));
        logger.info(submissions.toString()+"!!!!");
        PageInfo<Submission> subInfo = new PageInfo<Submission>(submissions);
        model.addAttribute("submissions", subInfo);
        return "contest/contest_submit2";
    }
    @RequestMapping(value = "/user/code_contest")
    public String codes(
            @RequestParam(required = false,  value = "problem_id") int problemId,
            @RequestParam(required = false,  value = "user_id") int userId,
            @RequestParam(required = false,  value = "cur_date") String curDate,
            @RequestParam(required=false,value="contestId") int contestId,
            Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserInfo user = (UserInfo)request.getSession().getAttribute("login_user");
        if(contestMapper.hasStarted(user.getUserId(),contestId)!=1) {
            return "redirect:/contest?id="+contestId;
        }

        Submission submission = submissionService.getSubmission(userId,problemId,curDate);
        logger.info("!!!sumbisiion"+problemId);
        model.addAttribute("submission", submission);
        Problem problem = problemMapper.getProblemByIdAndContest(problemId);
        model.addAttribute("problemInfo", problem);
        model.addAttribute("contestId",contestId);

        return "contest/contest_code2";
    }


    @GetMapping(value = "/admin/all_contests")
    public String all(
            Model model, HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page) {
        if (page == null || page <= 0) page = 1;
        int size = 8;
        PageHelper.startPage(page, size);

        UserInfo user = (UserInfo) request.getSession().getAttribute("login_user");

        HttpSession session = request.getSession();
        session.setAttribute("login_user", user);
        List<Contest> contests = contestMapper.getAllContests();

        PageInfo<Contest> pageInfo = new PageInfo<Contest>(contests, size);
        model.addAttribute("pageInfo", pageInfo);
        return "contest/all_contests2";
    }

    @RequestMapping("/check_started")
    @ResponseBody
    public String checkUserName(@RequestParam(required=false,value="id") int id,
                                @RequestParam(required=false,value="contestId") int contestId, HttpServletRequest request) {
        logger.info("checkstart!");
        if(contestMapper.hasStarted(id,contestId)==1){
            return "exist";
        }else{
            return "ok";
        }
    }


}