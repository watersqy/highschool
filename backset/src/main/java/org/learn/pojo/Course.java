package org.learn.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Course {
    private String oldId;
    @NotNull//可以通过（groups = 类名.class）决定在哪个分组生效
    private String id;//课程编号
    private String type;//课程属性
    @NotEmpty
    private String courseName;//课程名
    private String classroomId;
    private String place;//地点
    private String courseTime;//上课时间
    private String teacherId;//任课老师编号
    private String teacher;//任课老师
    private String classes;//开课班级
    private Integer weekTime;//周课时
    private Integer week;//课程周数
    private Integer state;//开课状态
}
