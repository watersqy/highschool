package org.learn.service;

import org.learn.pojo.PageBean;
import org.learn.pojo.Student;
import org.learn.pojo.Teacher;
import org.learn.pojo.User;

import java.util.List;

public interface UserService {
    //用户
    //在名单中查找
    String[] findInList(String username);

    //查找用户
    User findByUserName(String username);

    //注册
    void register(String username, String password, String name, Integer root);

    //更新数据
    void update(User user);

    //更新头像
    void updateAvatar(String avatarUrl);

    //更新密码
    void updatePwd(String newPwd);

    //更新登录时间
    void login(String username);

    //查询用户名单
    PageBean<User> list(Integer pageNum, Integer pageSize, String root, String username);

    //
    void root(String username, Integer root);


    //老师
    //查找教师名单
    PageBean<Teacher> teacherList(Integer pageNum, Integer pageSize, String state,String department);

    //
    List<String> findDepartment();

    void addTeacher(Teacher teacher);

    void updateTeacher(Teacher teacher);

    void deleteTeacher(String id);

    //学生
    //查找学生名单
    PageBean<Student> studentList(Integer pageNum, Integer pageSize, String state,String department,String spe,String classIn);

    //
    List<String> findStudentDepartment();

    //
    List<String> findSpe();

    //
    List<String> findClass();

    void addStudent(Student student);

    void updateStudent(Student student);

    void deleteStudent(String id);
}
