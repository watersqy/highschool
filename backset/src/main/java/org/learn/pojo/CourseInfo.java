package org.learn.pojo;

public interface CourseInfo {
    // 普通教室 01
    String NORMAL_CLASS_ROOM = "01";

    // 实验上机教室 02
    String LAB = "02";

    // 课程编号7位
    String COURSE_ID = "course_id";

    // 教师编号5位
    String TEACHER_ID = "teacher_id";

    // 班级编号8位
    String CLASS_ID = "class_id";

    // 课程属性2位
    String COURSE_TYPE = "courseType";

    // 上课时间2位
    String COURSE_TIME = "course_time";

    // 教室编号6位
    String CLASSROOM_ID = "classroom_id";


    // 设置各种类型的课程的适应度(码值)
    // 专业课必修课
    String MAIN_COURSE = "01";

    // 专业选修课
    String SECONDARY_COURSE = "02";

    // 实验课
    String EXPERIMENT_COURSE = "03";

    // 通识教育
    String COMMON_COURSE = "04";

    // 体育课
    String PHYSICAL_COURSE = "05";

    // 设置遗传代数
    int GENERATION = 50;

}
