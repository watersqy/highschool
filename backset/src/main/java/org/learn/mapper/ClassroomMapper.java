package org.learn.mapper;

import org.apache.ibatis.annotations.*;
import org.learn.pojo.Classroom;
import org.learn.pojo.PageBean;

import java.util.List;

@Mapper
public interface ClassroomMapper {
    //新增教室
    @Insert("insert into classroom(id, position, area, state, update_time)"+
    "values(#{id},#{position},#{area},#{state},now())")
    void add(Classroom classroom);

    //获得教室名单
    List<Classroom> list(Integer pageNum, Integer pageSize, String state, String area);

    //获得可用教室名单
    @Select("select * from classroom where state=1")
    List<Classroom> useList();

    //获得教室信息
    @Select("select * from classroom where id=#{id}")
    Classroom detail(String id);

    //更新教室信息
    @Update("update classroom set id=#{id}, position=#{position}, state=#{state}, area=#{area},update_time=now() "+
            "where id=#{oldId}")
    void update(Classroom classroom);

    @Update("update course_plan set classroom_id=#{id}, place=#{position} "+
            "where classroom_id=#{oldId}")
    void updatePlan(Classroom classroom);

    //删除教室
    @Delete("delete from classroom where id=#{id}")
    void delete(String id);

    //根据区域查找教室
    @Select("select * from classroom where area=#{area}")
    List<Classroom> findByArea(String area);

    //通过id返回教室名
    @Select("select position from classroom where id=#{id}")
    String findById(String id);
}
