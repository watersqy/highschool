package org.learn.service;

import org.learn.pojo.Classroom;
import org.learn.pojo.PageBean;

import java.util.List;


public interface ClassroomService {
    //新增教室
    void add(Classroom classroom);

    //获得教室名单
    PageBean<Classroom> list(Integer pageNum, Integer pageSize, String state,String area);

    //获得教室信息
    Classroom detail(String id);

    //更新教室信息
    void update(Classroom classroom);

    //删除教室信息
    void delete(String id);
}
