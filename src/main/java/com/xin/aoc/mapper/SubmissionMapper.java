package com.xin.aoc.mapper;
import com.xin.aoc.form.UserForm;
import com.xin.aoc.model.Problem;
import com.xin.aoc.model.Submission;
import com.xin.aoc.model.UserInfo;

// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubmissionMapper {
    @Insert("insert into submissions (cur_date, user_id, problem_id, code, status, title) values (#{curDate},#{userId},#{problemId},#{code},#{status},#{title})")
    void updateCodeById(@Param("problemId") int problemId,
                        @Param("userId")int userId,
                        @Param("code")String code,
                        @Param("curDate") String curDate,
                        @Param("status") int status,
                        @Param("title") String title);

    @Select("SELECT * from submissions WHERE user_id=#{userId} and problem_id=#{problemId}")
    List<Submission> getSubmissionsByUserId(@Param("userId") int userId,
                                         @Param("problemId") int problemId);

    @Select("SELECT * from submissions WHERE user_id=#{userId} and problem_id=#{problemId} and cur_date=#{curDate}")
    Submission getSubmissionByUserId(

                                     @Param("userId") int userId,
                                     @Param("problemId") int problemId,
                                     @Param("curDate") String curDate);
    @Select("SELECT code from submissions WHERE user_id=#{userId} and problem_id=#{problemId} and cur_date=(SELECT max(cur_date) FROM submissions)")
    String getLastSubmission(  @Param("problemId") int problemId,
                      @Param("userId") int userId);
}
