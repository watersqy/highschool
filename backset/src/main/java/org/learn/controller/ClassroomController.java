package org.learn.controller;

import org.learn.pojo.Classroom;
import org.learn.pojo.Course;
import org.learn.pojo.PageBean;
import org.learn.pojo.Result;
import org.learn.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classroom")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @PostMapping("/add")
    public Result add(@RequestBody Classroom classroom) {
        classroomService.add(classroom);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageBean<Classroom>> list(Integer pageNum, Integer pageSize,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) String area) {
        PageBean<Classroom> pd = classroomService.list(pageNum,pageSize,state,area);
        return Result.success(pd);
    }

    @GetMapping("/detail")
    public Result<Classroom> detail(String id) {
        Classroom c = classroomService.detail(id);
        return Result.success(c);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated Classroom classroom) {
        classroomService.update(classroom);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(String id) {
        classroomService.delete(id);
        return Result.success();
    }
}
