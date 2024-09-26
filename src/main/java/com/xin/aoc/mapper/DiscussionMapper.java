package com.xin.aoc.mapper;

import com.xin.aoc.form.ProblemForm;
import com.xin.aoc.model.Discussion;
import com.xin.aoc.model.Problem;
import com.xin.aoc.model.UserInfo;

// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface DiscussionMapper {
    @Select("select * from discussions where problem_id=#{id} order by cur_date desc")
    List<Discussion> getDiscussionById(int id);

    @Insert("insert into discussions (user_id, nick_name, image, cur_date, problem_id, content) " +
            "values (#{userId},#{nickName},#{image},#{curDate},#{problemId},#{content})")
    void addDiscussion(Discussion discussion);


    @Delete("delete from discussions "+
            "where problem_id=#{problemId} and user_id=#{userId} and cur_date=#{curDate}")
    void delDiscussion(String problemId, String userId, String curDate);
    @Update("update discussions set nick_name=#{nickName}, image=#{image}" +
            "where user_id=#{userId}")
     void updateAll(UserInfo user);

}
