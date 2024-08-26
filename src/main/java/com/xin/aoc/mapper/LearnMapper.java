package com.xin.aoc.mapper;

import com.xin.aoc.form.LearnForm;
import com.xin.aoc.model.Discussion;
import com.xin.aoc.model.Learn;
import com.xin.aoc.model.Problem;
import com.xin.aoc.model.UserInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface LearnMapper {
    @Select("select * from learns order by cur_date desc")
    List<Learn> getLearn();

    @Select("select * from learns where learn_id=#{id}")
    Learn getLearnById(int id);

    @Update("update learns set nick_name=#{nickName}, image=#{image}" +
            "where user_id=#{userId}")
    void updateAll(UserInfo user);

    @Insert("insert into learns (category, title, user_id, nick_name, image, cur_date, content, difficulty) " +
            "values (#{category},#{title},#{userId},#{nickName},#{image},#{curDate},#{content},#{difficulty})")
    void addLearn(Learn learn);

    @Update("update learns set content=#{content}, title=#{title}, category=#{category}, difficulty=#{difficulty} " +
            "where learn_id=#{learnId}")
    void changeAllLearnInfo(Learn learn);

    @Delete("delete from learns "+
            "where learn_id=#{learnId}")
    void delById(int id);
}




