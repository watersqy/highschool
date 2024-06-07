package org.learn.service;

import org.learn.pojo.Course;
import org.learn.pojo.PageBean;
import org.learn.pojo.Student;

import java.util.List;

public interface CourseService {
    //新增课程
    void add(Course course);

    //根据id查询课程
    Course findById(String id);

    //更新课程数据
    void update(Course course);

    //删除课程
    void delete(String id);

    //分页查询
    PageBean<Course> userCourseList(Integer pageNum, Integer pageSize, String state,String type);

    //
    PageBean<Course> courseList(Integer pageNum, Integer pageSize, String state,String type);

    //排课
    void arrange();

    //获得课程表
    List<Course> coursePlan();

    PageBean<Student> classList(Integer pageNum, Integer pageSize, String department, String spe);

    //根据班级id查询课程表
    List<Course> findByClassId(String classId);

    //根据老师id查询课程表
    List<Course> findByTeacherId(String teacherId);
}
