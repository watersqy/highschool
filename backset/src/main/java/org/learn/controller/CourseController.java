package org.learn.controller;

import org.learn.pojo.Course;
import org.learn.pojo.PageBean;
import org.learn.pojo.Result;
import org.learn.pojo.Student;
import org.learn.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/add")
    public Result add(@RequestBody @Validated Course course) {
        courseService.add(course);
        return Result.success();
    }

    @GetMapping("/detail")
    public Result<Course> detail(String id) {
        Course c = courseService.findById(id);
        return Result.success(c);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated Course course) {
        courseService.update(course);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(String id) {
        courseService.delete(id);
        return Result.success();
    }

    @GetMapping("/user")
    public Result<PageBean<Course>> userCourseList(Integer pageNum, Integer pageSize,
                                                 @RequestParam(required = false) String state,
                                                 @RequestParam(required = false) String type) {
        PageBean<Course> pb = courseService.userCourseList(pageNum,pageSize,state,type);
        return Result.success(pb);
    }

    @GetMapping("/list")
    public Result<PageBean<Course>> courseList(Integer pageNum, Integer pageSize,
                                                 @RequestParam(required = false) String state,
                                                 @RequestParam(required = false) String type) {
        PageBean<Course> pb = courseService.courseList(pageNum,pageSize,state,type);
        return Result.success(pb);
    }

    @PostMapping("/arrange")
    public Result arrange() {
        courseService.arrange();
        return Result.success();
    }

    @GetMapping("/courseTable")
    public Result<List<Course>> coursePlan(){
        List<Course> cs = courseService.coursePlan();
        return Result.success(cs);
    }

    //获得班级列表
    @GetMapping("/classList")
    public Result<PageBean<Student>> studentList(Integer pageNum, Integer pageSize,
                                                 @RequestParam(required = false) String department,
                                                 @RequestParam(required = false) String spe){
        PageBean<Student> s = courseService.classList(pageNum,pageSize,department,spe);
        return Result.success(s);
    }

    @GetMapping("/class/{classId}")
    public Result<List<Course>> classPlan(@PathVariable("classId")String classId) {
        try {
            String enCode= URLEncoder.encode(classId,"UTF-8");
            List<Course> cs = courseService.findByClassId(classId);
            return Result.success(cs);
        }catch (UnsupportedEncodingException e){
            throw new RuntimeException("编码错误");
        }

    }

    @GetMapping("/teacher/{teacherId}")
    public Result<List<Course>> TeacherPlan(@PathVariable("teacherId")String teacherId) {
        List<Course> cs = courseService.findByTeacherId(teacherId);
        return Result.success(cs);
    }
}
