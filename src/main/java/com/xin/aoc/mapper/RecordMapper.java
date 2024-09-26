package com.xin.aoc.mapper;

import com.xin.aoc.form.UserForm;
import com.xin.aoc.model.UserInfo;

// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;

public interface RecordMapper {
    @Insert("insert into records (cur_date, user_id, problem_id) values (#{curDate},#{userId},#{problemId})")
    void addRecord(@Param("curDate") String curDate,
                   @Param("userId") int userId,
                   @Param("problemId") int problemId);

    @Select("SELECT count(*) from records WHERE user_id=#{userId} and problem_id=#{problemId}")
    int checkExist(@Param("userId") int userId,
                   @Param("problemId") int problemId);

    @Select("SELECT problem_id from records WHERE user_id=#{userId}")
    ArrayList<Integer> findSolved(int user);

    @Select("SELECT problem_id from records WHERE contest_id=#{id}")
    ArrayList<Integer> findSolvedByContestId(int id);

    @Update("update contest_records set user_name=#{userName}, image=#{image}" +
            "where user_id=#{userId}")
    void updateAll(UserInfo user);
}
