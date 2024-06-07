package org.learn.mapper;

import org.apache.ibatis.annotations.*;
import org.learn.pojo.Course;
import org.learn.pojo.Student;
import org.learn.pojo.Teacher;
import org.learn.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {
    //查找是否存在该学生或老师
    @Select("select name from student where id=#{username}")
    String findInStudent(String username);

    @Select("select name from teacher where id=#{username}")
    String findInTeacher(String username);
    //激活账号后更新信息
    @Update("update student set state=0 where id=#{username}")
    void updateStudentState(String username);
    @Update("update teacher set state=0 where id=#{username}")
    void updateTeacherState(String username);

    //查询
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);

    //加入信息
    @Insert("insert into user(username, password, name, root, create_time, update_time, login_time)"+
    "values(#{username},#{password}, #{name},#{root},now(),now(),now())")
    void add(String username, String password, String name, Integer root);

    //修改用户信息
    @Update("update user set email=#{email},update_time=#{updateTime} where username=#{username}")
    void update(User user);

    //更新用户头像
    @Update("update user set user_pic=#{avatarUrl},update_time=now() where username=#{username}")
    void updateAvatar(String avatarUrl,String username);

    //修改用户密码
    @Update("update user set password=#{md5String},update_time=now() where username=#{username}")
    void updatePwd(String md5String,String username);

    //更新登录时间
    @Update("update user set login_time=now() where username=#{username}")
    void login(String username);

    //更改用户权限
    @Update("update user set root=#{root} where username=#{username}")
    void updateRoot(String username,Integer root);

    //获得用户名单
    List<User> list(String root, String username);

    //管理者修改id后，账号这边也需要改
    @Update("update user set username=#{oldId} where username=#{id}")
    void updateUser(String id, String oldId);

    //老师
    //获得教师名单
    List<Teacher> teacherList(String state,String department);

    //获得老师部门列表
    @Select("select distinct department from teacher")
    List<String> findDepartment();

    @Insert("insert into teacher(id, department, name) values(#{id},#{department},#{name})")
    void addTeacher(Teacher teacher);

    @Update("update teacher set id=#{id},department=#{department},name=#{name} where id=#{oldId}")
    void updateTeacher(Teacher teacher);
    @Update("update course_plan set teacher_id=#{id},teacher=#{name} where teacher_id=#{oldId}")
    void updateCoursePlan(Teacher teacher);
    @Update("update course set teacher_id=#{id},teacher=#{name} where teacher_id=#{oldId}")
    void updateCourse(Teacher teacher);

    @Delete("delete from teacher where id=#{id}")
    void deleteTeacher(String id);

    //学生
    //获得学生名单
    List<Student> studentList(String state,String department,String spe,String classIn);

    //获得学生学院列表
    @Select("select distinct department from student")
    List<String> findStudentDepartment();

    //获得学生专业列表
    @Select("select distinct spe from student")
    List<String> findSpe();

    //获得学生班级列表
    @Select("select distinct class_in from student")
    List<String> findClass();

    @Insert("insert into student(id, department, spe, class_in, name) values(#{id},#{department},#{spe},#{classIn},#{name})")
    void addStudent(Student student);

    @Update("update student set id=#{id},department=#{department},spe=#{spe},class_in=#{classIn},name=#{name} where id=#{oldId}")
    void updateStudent(Student student);

    @Delete("delete from student where id=#{id}")
    void deleteStudent(String id);
}
