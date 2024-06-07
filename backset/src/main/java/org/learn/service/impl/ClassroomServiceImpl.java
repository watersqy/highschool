package org.learn.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.learn.mapper.ClassroomMapper;
import org.learn.pojo.Classroom;
import org.learn.pojo.Course;
import org.learn.pojo.PageBean;
import org.learn.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public void add(Classroom classroom) {
        classroomMapper.add(classroom);
    }

    @Override
    public PageBean<Classroom> list(Integer pageNum, Integer pageSize, String state, String area) {
        //封装
        PageBean<Classroom> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        List<Classroom> c = classroomMapper.list(pageNum,pageSize,state,area);
        Page<Classroom> p = (Page<Classroom>) c;

        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());

        return pageBean;
    }

    @Override
    public Classroom detail(String id) {
        return classroomMapper.detail(id);
    }

    @Override
    public void update(Classroom classroom) {
        classroomMapper.update(classroom);
        classroomMapper.updatePlan(classroom);
    }

    @Override
    public void delete(String id) {
        classroomMapper.delete(id);
    }
}
