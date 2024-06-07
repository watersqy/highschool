package org.learn.controller;

import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.learn.pojo.*;
import org.learn.service.UserService;
import org.learn.utils.JwtUtil;
import org.learn.utils.Md5Util;
import org.learn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.concurrent.TimeUnit;

@RestController
//@ResponseBody
@RequestMapping("/user")
@Validated//自动校验
public class UserController {

    @Autowired
    private UserService userService;
    //@Autowired
    //private StringRedisTemplate stringRedisTemplate;

    //用户
    @RequestMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{4,16}$") String username, @Pattern(regexp = "^\\S{4,16}$") String password, @Pattern(regexp = "^\\S{4,16}$") String repassword) {
        //判断两次密码是否相同
        if(!password.equals(repassword)) {
            return Result.error("两次输入密码不同");
        }

        //在学生和老师名单中查找
        String[] name;
        name = userService.findInList(username);
        if(name[0]==null)
        {
            return Result.error("未在学校名单中找到该用户");
        }

        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) //没有占用，完成注册
        {
            userService.register(username,password,name[0],Integer.parseInt(name[1]));
            return Result.success();
        }
        else//被占用
        {
            return Result.error("用户名已注册");
        }
    }

    @RequestMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{4,16}$") String username, @Pattern(regexp = "^\\S{4,16}$") String password) {
        User loginuser = userService.findByUserName(username);

        //判断用户是否存在
        if(loginuser == null) {
            return Result.error("用户名错误");
        }

        //
        if(Md5Util.getMD5String(password).equals(loginuser.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            //获取jwt，token令牌
            claims.put("root",loginuser.getRoot());//权限
            claims.put("username",loginuser.getUsername());//用户名
            String token = JwtUtil.genToken(claims);
            //token传入redis中
            //ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            //ops.set(token,token,1, TimeUnit.DAYS);
            userService.login(username);
            return Result.success(token);
        }

        return Result.error("密码错误");
    }

    //返回用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    //更新用户信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }

    //更新用户头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    //重置密码
    @PutMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        //json格式转map，获得参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        //参数是否正确获得
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要参数");
        }

        //校验
        Map<String,Object>map = ThreadLocalUtil.get();
        String username = map.get("username").toString();System.out.println(username);
        User loginUser = userService.findByUserName(username);

        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码不正确");
        }

        if(!rePwd.equals(newPwd)){
            return Result.error("两次输入密码不一样");
        }

        if(oldPwd.equals(newPwd)){
            return Result.error("新密码与原密码相同");
        }

        userService.updatePwd(newPwd);
        //删除原token
        //ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //ops.getOperations().delete(token);
        return Result.success();
    }

    //获得用户列表
    @GetMapping("/list")
    public Result<PageBean<User>> list(Integer pageNum, Integer pageSize,
                                   @RequestParam(required = false) String root,
                                   @RequestParam(required = false) String username) {
        PageBean<User> u = userService.list(pageNum,pageSize,root,username);
        return Result.success(u);
    }

    //
    @PutMapping("/root")
    public Result root(@RequestBody Map<String,String> params) {
        String username = params.get("username");
        Integer root = Integer.parseInt(params.get("root"));
        userService.root(username,root);
        return Result.success();
    }

    //教师
    //获得教师列表
    @GetMapping("/teacherList")
    public Result<PageBean<Teacher>> teacherList(Integer pageNum, Integer pageSize,
                                                 @RequestParam(required = false) String state,
                                                 @RequestParam(required = false) String department){
        PageBean<Teacher> t = userService.teacherList(pageNum,pageSize,state,department);
        return Result.success(t);
    }

    //
    @GetMapping("/teacher/department")
    public Result<List<String>> findDepartment(){
        List<String> d = userService.findDepartment();
        return Result.success(d);
    }

    @PostMapping("/teacher/add")
    public Result addTeacher(@RequestBody @Validated Teacher teacher) {
        userService.addTeacher(teacher);
        return Result.success();
    }

    @PutMapping("/teacher/update")
    public Result updateTeacher(@RequestBody @Validated Teacher teacher) {
        userService.updateTeacher(teacher);
        return Result.success();
    }

    @DeleteMapping("/teacher/delete")
    public Result deleteTeacher(String id) {
        userService.deleteTeacher(id);
        return Result.success();
    }

    //学生
    //获得学生列表
    @GetMapping("/studentList")
    public Result<PageBean<Student>> studentList(Integer pageNum, Integer pageSize,
                                             @RequestParam(required = false) String state,
                                             @RequestParam(required = false) String department,
                                             @RequestParam(required = false) String spe,
                                             @RequestParam(required = false) String classIn){
        PageBean<Student> s = userService.studentList(pageNum,pageSize,state,department,spe,classIn);
        return Result.success(s);
    }

    //
    @GetMapping("/student/department")
    public Result<List<String>> findStudentDepartment(){
        List<String> s = userService.findStudentDepartment();
        return Result.success(s);
    }

    //
    @GetMapping("/spe")
    public Result<List<String>> findSpe(){
        List<String> s = userService.findSpe();
        return Result.success(s);
    }

    //
    @GetMapping("/class")
    public Result<List<String>> findClass(){
        List<String> c = userService.findClass();
        return Result.success(c);
    }

    @PostMapping("/student/add")
    public Result addStudent(@RequestBody @Validated Student student) {
        userService.addStudent(student);
        return Result.success();
    }

    @PutMapping("/student/update")
    public Result updateStudent(@RequestBody @Validated Student student) {
        userService.updateStudent(student);
        return Result.success();
    }

    @DeleteMapping("/student/delete")
    public Result deleteStudent(String id) {
        userService.deleteStudent(id);
        return Result.success();
    }
}
