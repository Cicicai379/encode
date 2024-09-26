package com.xin.aoc.mapper;

import com.xin.aoc.form.ProblemForm;
import com.xin.aoc.form.UserForm;
import com.xin.aoc.model.Problem;

// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

public interface AdminMapper {
    @Insert("insert into problems (title, problem, answer, in_put, category, difficulty, time, contest_id) values (#{title},#{problem},#{answer},#{input},#{category},#{difficulty},#{time},#{contestId})")
    void insert(ProblemForm problem);

    @Update("update problems set title=#{title}, problem=#{problem}, difficulty=#{difficulty}, answer=#{answer}, in_put=#{input}, category=#{category}, time=#{time} " +
            "where problem_id=#{problemId}")
    void changeAll(Problem problem);

    @Delete("delete from problems "+
            "where problem_id=#{problemId}")
    void delById(int id);
}


