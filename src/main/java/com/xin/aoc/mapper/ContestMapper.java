package com.xin.aoc.mapper;

import com.xin.aoc.form.ContestForm;
import com.xin.aoc.form.LearnForm;
import com.xin.aoc.model.*;

// Ibatis documentation: https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/package-summary.html
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ContestMapper {
    @Select("select * from contests where start < CURRENT_TIMESTAMP() order by start desc")
    List<Contest> getContests();

    @Select("select * from contests order by start desc")
    List<Contest> getAllContests();

    @Select("select * from contests where contest_id=#{id} and stop > CURRENT_TIMESTAMP() and start < CURRENT_TIMESTAMP()")
    Contest getContestById(int id);

    @Select("select * from contests where contest_id=#{id}")
    Contest getAllContestById(int id);

    @Select("select * from contest_records where contest_id=#{contestId}")
    List<ContestRecord> getScoreById(int contestId);


    @Insert("insert into contests (title, start, stop, content, duration) " +
            "values (#{title},#{start},#{stop},#{content},#{duration})")
    void addContest(ContestForm contest);

    @Update("update contests set content=#{content}, title=#{title}, start=#{start}, stop=#{stop} " +
            "where contest_id=#{contestId}")
    void changeAllContestInfo(ContestForm contest);

    @Delete("delete from contests "+
            "where contest_id=#{contestId}")
    void delById(int id);

    @Update("update contest_records set score=score+1 where user_id=#{userId} and contest_id=#{contestId}")
    void addScore(int contestId, int userId);

    @Insert("insert into contest_records (user_id, user_name, image, contest_id) values (#{userId}, #{userName},#{image},#{contestId})")
    void addRecord(ContestRecord contestRecord);

    @Select("select count(*) from contest_records where user_id=#{userId} and contest_id=#{contestId}")
    boolean hasNotJoin(int userId, int contestId);

    @Update("update contest_records set user_name=#{userName}, image=#{image}" +
            "where user_id=#{userId}")
    void updateAll(UserInfo user);

    @Select("select count(*) from contest_records where start!='-1' and user_id=#{userId} and contest_id=#{contestId}")
    int hasStarted(int userId, int contestId);

    @Select("select count(*) from contests where start < CURRENT_TIMESTAMP() and contest_id=#{contestId}")
    int contestHasStarted(int contestId);

    @Select("select duration from contests where contest_id=#{contestId}")
    int getContestDuration(int contestId);

    @Update("update contest_records set start=#{start}, stop=#{stop} " +
            "where user_id=#{userId} and contest_id=#{contestId}")
    void startContest(long stop, long start,  int userId,int contestId);

    @Select("select stop from contest_records where contest_id=#{contestId} and user_id=#{userId}")
    long getStop(int userId,int contestId);

    @Select("select stop from contests where contest_id=#{contestId}")
    String getContestStop(int contestId);

}


