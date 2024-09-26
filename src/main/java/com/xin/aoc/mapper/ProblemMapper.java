package com.xin.aoc.mapper;

import org.apache.ibatis.annotations.Select;
import com.xin.aoc.model.Problem;
import java.util.List;

// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface ProblemMapper {
    @Select("select * from problems where problem_id=#{id} and time < CURRENT_TIMESTAMP()")
    Problem getProblemById(int id);

    @Select("select * from problems where problem_id=#{id}")
    Problem getContestProblemById(int id);


    @Select("select * from problems where problem_id=#{id}")
    Problem getProblemByIdAndContest(int id);



    @Select("select * from problems where title=#{name}")
    Problem getProblemByName(String name);

    @Select("select * from problems where time < CURRENT_TIMESTAMP() and contest_id=0 " +
            "order by problem_id desc")
    List<Problem> getAllProblems();

    @Update("update user_info set score=score+1 where user_id=#{id}")
    void addScoreById(int id);

    @Select("SELECT * FROM problems" +
            "  WHERE time < CURRENT_TIMESTAMP() and contest_id=0 "  + "AND (" +
            "  UPPER(title) LIKE UPPER(CONCAT('%', #{key}, '%'))" +
            "  OR UPPER(category) LIKE UPPER(CONCAT('%', #{key}, '%'))" +
            "  OR UPPER(problem) LIKE UPPER(CONCAT('%', #{key}, '%'))" +
            "  OR UPPER(difficulty) LIKE UPPER(CONCAT('%', #{key}, '%'))" +
            ")"
           )
    List<Problem> getAllProblemsByKey(String key);

    @Select("select * from problems where contest_id=#{id}")
    List<Problem>  getProblemsByContest(int id);
}
