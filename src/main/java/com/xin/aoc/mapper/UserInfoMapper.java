package com.xin.aoc.mapper;

import com.xin.aoc.model.UserInfo;
// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserInfoMapper {
    @Select("select * from user_info where user_name=#{userName}")
    public UserInfo getUserInfo(@Param("userName") String username);

    @Insert("insert into user_info (user_name, nick_name, email, password) " +
            "values (#{userName},#{nickName},#{email},#{password})")
    void insert(UserInfo user);

    @Update("update user_info set password=#{password} " +
            "where user_name=#{userName}")
    void change(UserInfo user);

    @Update("update user_info set password=#{password}, user_name=#{userName}, nick_name=#{nickName}, email=#{email} " +
            "where user_id=#{userId}")
    void changeAll(UserInfo user);

    @Update("update user_info set image=#{src} " +
            "where user_id=#{id}")
    void updateImage(int id, String src);

    @Select("select user_name, score, image from user_info order by score desc")
    public List<UserInfo> getAllUserInfo();

    @Select("select nick_name from user_info where user_id=#{userId}")
    public String getCurNickName(int userId);
}
