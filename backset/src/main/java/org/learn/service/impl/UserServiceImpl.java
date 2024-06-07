package org.learn.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.learn.mapper.UserMapper;
import org.learn.pojo.*;
import org.learn.service.UserService;
import org.learn.utils.Md5Util;
import org.learn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String[] findInList(String username) {
        String name1 = userMapper.findInStudent(username);
        String name2 = userMapper.findInTeacher(username);
        String[] name=new String[2];
        if(name1 != null ){
            name[0]=name1;name[1]="1";
            userMapper.updateStudentState(username);
        }
        else if(name2 != null ){
            name[0]=name2;name[1]="2";
            userMapper.updateTeacherState(username);
        }
        return name;
    }

    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String username, String password, String name, Integer root) {
        //加密
        String mdpassword = Md5Util.getMD5String(password);
        //添加
        userMapper.add(username,mdpassword,name,root);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object>map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        userMapper.updateAvatar(avatarUrl,username);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),username);
    }

    @Override
    public void login(String username) {
        userMapper.login(username);
    }

    @Override
    public PageBean<User> list(Integer pageNum, Integer pageSize, String root, String username) {
        //封装
        PageBean<User> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        List<User> u = userMapper.list(root,username);
        //得到记录的总条数和当前页面数据
        Page<User> p = (Page<User>) u;
        //把数据填充到PageBean对象中
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public void root(String username, Integer root) {
        userMapper.updateRoot(username,root);
    }

    @Override
    public PageBean<Teacher> teacherList(Integer pageNum, Integer pageSize, String state,String department) {
        //封装
        PageBean<Teacher> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        List<Teacher> t = userMapper.teacherList(state,department);
        //得到记录的总条数和当前页面数据
        Page<Teacher> p = (Page<Teacher>) t;
        //把数据填充到PageBean对象中
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public List<String> findDepartment() {
        return userMapper.findDepartment();
    }

    @Override
    public void addTeacher(Teacher teacher) {
        String name = userMapper.findInTeacher(teacher.getId());
        if(name != null){
            throw new  RuntimeException("已存在该老师!");
        }
        userMapper.addTeacher(teacher);
    }

    @Override
    public void updateTeacher(Teacher teacher) {
        //id被修改
        if(!Objects.equals(teacher.getId(), teacher.getOldId())){
            String name = userMapper.findInTeacher(teacher.getId());
            if(name != null){
                throw new  RuntimeException("教师编号已存在!");
            }
            userMapper.updateUser(teacher.getId(),teacher.getOldId());
        }

        //修改用户账号和课程表、课程信息
        userMapper.updateCourse(teacher);
        userMapper.updateCoursePlan(teacher);
        userMapper.updateTeacher(teacher);
    }

    @Override
    public void deleteTeacher(String id) {
        userMapper.deleteTeacher(id);
    }

    @Override
    public PageBean<Student> studentList(Integer pageNum, Integer pageSize, String state,String department,String spe,String classIn) {
        //封装
        PageBean<Student> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        List<Student> s = userMapper.studentList(state,department,spe,classIn);
        //得到记录的总条数和当前页面数据
        Page<Student> p = (Page<Student>) s;
        //把数据填充到PageBean对象中
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public List<String> findStudentDepartment() {
        return userMapper.findStudentDepartment();
    }

    @Override
    public List<String> findSpe() {
        return userMapper.findSpe();
    }

    @Override
    public List<String> findClass() {
        return userMapper.findClass();
    }

    @Override
    public void addStudent(Student student) {
        String name = userMapper.findInStudent(student.getId());
        if(name != null){
            throw new  RuntimeException("已存在该学生!");
        }
        userMapper.addStudent(student);
    }

    @Override
    public void updateStudent(Student student) {
        //id被修改
        if(!Objects.equals(student.getId(), student.getOldId())){
            String name = userMapper.findInStudent(student.getId());
            if(name != null){
                throw new  RuntimeException("学生编号已存在!");
            }
            userMapper.updateUser(student.getId(),student.getOldId());
        }
        userMapper.updateStudent(student);
    }

    @Override
    public void deleteStudent(String id) {
        userMapper.deleteStudent(id);
    }
}
