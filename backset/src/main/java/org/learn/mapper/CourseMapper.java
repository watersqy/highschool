package org.learn.mapper;

import org.apache.ibatis.annotations.*;
import org.learn.pojo.Course;
import org.learn.pojo.Student;

import java.util.List;

@Mapper
public interface CourseMapper {
    //新增课程
    @Insert("insert into course(id,course_name, teacher, classes,week,state,type,teacher_id,week_time)"+
    "values(#{id},#{courseName},#{teacher},#{classes},#{week},#{state},#{type},#{teacherId},#{weekTime})")
    void add(Course course);

    //查看对应用户的课程列表
    List<Course> userCourseList(String userId,String state,String type);

    //
    List<Course> courseList(String userId,String state,String type);

    //获得课程信息
    @Select("select * from course where id=#{id}")
    Course findById(String id);

    //更新课程信息
    @Update("update course set id=#{id},course_name=#{courseName},teacher=#{teacher},"+
            "state=#{state},classes=#{classes},week=#{week},type=#{type},teacher_id=#{teacherId},week_time=#{weekTime} "+
            "where id=#{oldId}")
    void update(Course course);

    //删除课程
    @Delete("delete from course where id=#{id}")
    void delete(String id);

    @Delete("delete from course_plan where id=#{id}")
    void deletePlan(String id);

    //获得所有已经开课的课程名单
    @Select("select * from course where state=1")
    List<Course> getAll();

    //获得已经开课的班级名单
    @Select("select distinct classes from course")
    List<String> selectClass();

    //删除所有排课信息
    @Delete("delete from course_plan")
    void deleteAll();

    //排课后写入课程信息
    @Insert("insert into course_plan(id,course_name,classroom_id, place,course_time,teacher_id, teacher,classes)"+
    "values (#{id},#{courseName},#{classroomId},#{place},#{courseTime},#{teacherId},#{teacher},#{classes})")
    void insertPlan(Course course);

    //查看对应用户的课程列表
    @Select("select * from course_plan where classes = (select student.class_in from student where student.id=#{userId})"+
            "or teacher_id = (select teacher.id from teacher where teacher.id=#{userId})")
    List<Course> coursePlan(String userId);

    //
    List<Student>classList(String department, String spe);

    //根据班级查询课程列表
    @Select("select * from course_plan where classes=#{classId}")
    List<Course> findByClassId(String classId);

    //根据班级查询课程列表
    @Select("select * from course_plan where teacher_id=#{teacherId}")
    List<Course> findByTeacherId(String teacherId);
}
