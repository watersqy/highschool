package org.learn.utils;

import org.learn.pojo.CourseInfo;
import org.apache.commons.lang3.ArrayUtils;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class CourseUtil {
    private CourseUtil(){

    }

    //每周最多25节课
    public static final int MAX_CLASS_TIME = 25;

    public static final Random RANDOM = new Random();
    //根据需要随机生成num个25节课中的不重复的时间的列表
    public static  List<String> randomTime(int num){
        HashSet<String> Time = new HashSet<>();
        int time;
        while(Time.size()<num){
            time = RANDOM.nextInt(MAX_CLASS_TIME)+1;
            Time.add(time < 10 ? ("0" + time) : String.valueOf(time));
        }
        List<String> list = new ArrayList<>(Time);
        return list;
    }
    //随机生成一个
    public static String randomTime() {
        int temp = RANDOM.nextInt(MAX_CLASS_TIME) + 1;
        return temp < 10 ? ("0" + temp) : String.valueOf(temp);
    }

    //生成 01 - 25 的时间集合
    private static List<String> getAllTime() {
        return IntStream.rangeClosed(1, MAX_CLASS_TIME).mapToObj(i -> i < 10 ? ("0" + i) : String.valueOf(i)).collect(Collectors.toList());
    }

    //获取 01-25 内还未使用的时间
    private static String getFreeTime(Set<String> usedTimeList){
        List<String> allTime = getAllTime();

        boolean isRemoveSuccess = allTime.removeAll(usedTimeList);

        if(isRemoveSuccess && !allTime.isEmpty()){
            int randomIndex = RANDOM.nextInt(allTime.size());
            return allTime.get(randomIndex);
        }

        return randomTime();
    }

    /*
     * 编码规则: （位）
     * 课程编号：7
     * 讲师编号：5
     * 班级编号：6
     * 课程属性：2
     * 上课时间：2
     * 教室编号：6
     * 编码规则为：课程编号+教师编号+班级编号+课程属性+上课时间+教室编号
     */
    public static String cutGene(String aim, String source) {
        return switch (aim) {
            case CourseInfo.COURSE_ID -> source.substring(0, 7);
            case CourseInfo.TEACHER_ID -> source.substring(7, 12);
            case CourseInfo.CLASS_ID -> source.substring(12, 18);
            case CourseInfo.COURSE_TYPE -> source.substring(18, 20);
            case CourseInfo.COURSE_TIME -> source.substring(20, 22);
            case CourseInfo.CLASSROOM_ID -> source.substring(22, 28);
            default -> "";
        };
    }

    //计算每个个体的适应值
    public static double calculateExpectedValue(List<String> individualList) {
        // < 11912201
        // 专业课必修课 01
        double K1 = 0.2;
        // 专业课选修课所占权重 02
        double K2 = 0.2;
        // 实验课所占权重 03
        double K3 = 0.1;
        // 通识教育课所占权重 04
        double K4 = 0.2;
        // 体育课所占权重 05
        double K5 = 0.1;

        // 课程离散程度所占权重
        double K6 = 0.2;

        int F1 = 0; // 专业课必修课期望总值
        int F2 = 0; // 专业课选修课期望总值
        int F3 = 0; // 实验课期望总值
        int F4 = 0; // 通识教育期望总值
        int F5 = 0; // 体育课期望总值
        int F6;// 课程离散程度期望总值

        double Fx; // 总适应度值

        // 开始计算每一个个体的适应度
        for (String gene : individualList) {
            // 获得课程属性
            String courseType = cutGene(CourseInfo.COURSE_TYPE, gene);
            // 获得该课程的上课时间
            String classTime = cutGene(CourseInfo.COURSE_TIME, gene);

            switch (courseType) {
                case CourseInfo.MAIN_COURSE:
                    F1 += calculateMainExpect(classTime);
                    break;
                case CourseInfo.SECONDARY_COURSE:
                    F2 += calculateSecondaryExpect(classTime);
                    break;
                case CourseInfo.EXPERIMENT_COURSE:
                    F3 += calculateExperimentExpect(classTime);
                    break;
                case CourseInfo.COMMON_COURSE:
                    F4 += calculateCommon(classTime);
                    break;
                default:
                    F5 += calculatePhysicalExpect(classTime);
                    break;
            }
        }
        // 计算期望值
        F6 = calculateDiscreteExpect(individualList);
        // 总适应度 整个种群的适应度值
        Fx = K1 * F1 + K2 * F2 + K3 * F3 + K4 * F4 + K5 * F5 + K6 * F6;
        return Fx;
    }

    //计算专业课必修课的期望值
    private static int calculateMainExpect(String classTime) {
        // 专业课必修课期望值为10时的时间片值，放在第二节课
        String[] tenExpectValue = {"02", "07", "12", "17", "22"};
        // 专业课必修课期望值为8时的时间片值
        String[] eightExpectValue = {"01", "06", "11", "16", "21"};
        // 专业课必修课期望值为4时的时间片值
        String[] fourExpectValue = {"03", "08", "13", "18", "23"};
        // 专业课必修课期望值为2时的时间片值
        String[] twoExpectValue = {"04", "09", "14", "19", "24"};

        if (ArrayUtils.contains(tenExpectValue, classTime)) {
            return 10;
        } else if (ArrayUtils.contains(eightExpectValue, classTime)) {
            return 10;
        } else if (ArrayUtils.contains(fourExpectValue, classTime)) {
            return 4;
        } else if (ArrayUtils.contains(twoExpectValue, classTime)) {
            return 2;
        } else {
            return 0;
        }
    }

    //计算专业课选修课的期望值
    private static int calculateSecondaryExpect(String classTime) {
        // 专业课必修课期望值为10时的时间片值
        String[] tenExpectValue = {"03", "08", "13", "18", "23"};
        // 专业课必修课望值为8时的时间片值
        String[] eightExpectValue = {"02", "07", "12", "17", "22"};
        // 专业课必修课期望值为4时的时间片值
        String[] fourExpectValue = {"01", "04", "06", "09", "11", "16", "19", "21", "24"};

        if (ArrayUtils.contains(tenExpectValue, classTime)) {
            return 10;
        } else if (ArrayUtils.contains(eightExpectValue, classTime)) {
            return 8;
        } else if (ArrayUtils.contains(fourExpectValue, classTime)) {
            return 4;
        } else {
            return 0;
        }
    }

    //计算体育课的期望值
    private static int calculatePhysicalExpect(String classTime) {
        String[] tenExpectValue = {"04", "09", "14", "19"};//体育课期望值为10时的时间片值  24
        String[] eightExpectValue = {"03", "08", "13", "18"};//体育课期望值为8时的时间片值 23
        String[] fourExpectValue = {"02", "07", "12", "17", "22"};//体育课期望值为4时的时间片值

        if (ArrayUtils.contains(tenExpectValue, classTime)) {
            return 10;
        } else if (ArrayUtils.contains(eightExpectValue, classTime)) {
            return 8;
        } else if (ArrayUtils.contains(fourExpectValue, classTime)) {
            return 4;
        } else {
            return 0;
        }
    }

    //计算实验课的期望值
    private static int calculateExperimentExpect(String classTime) {
        String[] tenExpectValue = {"04", "09", "14", "19"};//实验课期望值为10时的时间片值
        String[] eightExpectValue = {"05", "10", "15", "20", "25"};//实验课期望值为8时的时间片值
        String[] sixExpectValue = {"03", "08", "13", "18"};//实验课期望值为6时的时间片值
        String[] fourExpectValue = {"02", "07", "12", "17", "22"};//实验课期望值为4时的时间片值

        if (ArrayUtils.contains(tenExpectValue, classTime)) {
            return 10;
        } else if (ArrayUtils.contains(eightExpectValue, classTime)) {
            return 8;
        } else if (ArrayUtils.contains(sixExpectValue, classTime)) {
            return 6;
        } else if (ArrayUtils.contains(fourExpectValue, classTime)) {
            return 4;
        } else {
            return 0;
        }
    }

    //计算通识教育课程的期望值
    private static int calculateCommon(String classTime) {
        String[] tenExpectValue = {"04", "09", "14", "19"};//通识教育课程期望值为10时的时间片值
        String[] eightExpectValue = {"05", "10", "15", "20", "25"};//通识教育课程期望值为8时的时间片值
        String[] sixExpectValue = {"03", "08", "13", "18"};//通识教育课程期望值为6时的时间片值
        String[] fourExpectValue = {"02", "07", "12", "17", "22"};//通识教育课程期望值为4时的时间片值

        if (ArrayUtils.contains(tenExpectValue, classTime)) {
            return 10;
        } else if (ArrayUtils.contains(eightExpectValue, classTime)) {
            return 8;
        } else if (ArrayUtils.contains(sixExpectValue, classTime)) {
            return 6;
        } else if (ArrayUtils.contains(fourExpectValue, classTime)) {
            return 4;
        } else {
            return 0;
        }
    }

    //计算课程离散度期望值
    private static int calculateDiscreteExpect(List<String> individualList) {
        // 离散程度期望值
        int F6 = 0;
        // 返回每个班级的对应课程下面的排序上课时间
        Map<String, List<String>> classTimeMap = courseGrouping(individualList);

        for (List<String> classTimeList : classTimeMap.values()) {
            if (classTimeList.size() > 1) {
                for (int i = 0; i < classTimeList.size() - 1; ++i) {
                    // 计算一门课上课的时间差
                    int temp = Integer.parseInt(classTimeList.get(++i)) - Integer.parseInt(classTimeList.get(i - 1));
                    F6 += judgingDiscreteValues(temp);
                }
            }
        }
        return F6;
    }

    //将一个班级的同一门课程的所有上课时间进行统计，并且进行分组
    private static Map<String, List<String>> courseGrouping(List<String> individualList) {
        Map<String, List<String>> classTimeMap = new HashMap<>();
        // 先将一个班级课表所上的课程区分出来（排除掉重复的课程）
        for (String gene : individualList) {
            classTimeMap.put(cutGene(CourseInfo.COURSE_ID, gene), null);
        }
        // 遍历课程编号
        for (String courseId : classTimeMap.keySet()) {
            List<String> classTimeList = new ArrayList<>();
            for (String gene : individualList) {
                // 获得同一门课程的所有上课时间片
                if (cutGene(CourseInfo.COURSE_ID, gene).equals(courseId)) {
                    classTimeList.add(cutGene(CourseInfo.COURSE_TIME, gene));
                }
            }
            // 将课程的时间片进行排序
            Collections.sort(classTimeList);
            // 每一门课对应的上课时间集合(classNo, List)
            classTimeMap.put(courseId, classTimeList);
        }
        return classTimeMap;
    }

    //判断两课程的时间差在哪个区间
    private static int judgingDiscreteValues(int temp) {
        int[] tenExpectValue = {5, 6, 7, 8}; // 期望值为10时两课之间的时间差
        int[] sixExpectValue = {4, 9, 10, 11, 12, 13}; // 期望值为6时两课之间的时间差
        int[] fourExpectValue = {3, 14, 15, 16, 17, 18}; // 期望值为4时两课之间的时间差
        int[] twoExpectValue = {2, 19, 20, 21, 22, 23}; // 期望值为2时两课之间的时间差

        if (ArrayUtils.contains(tenExpectValue, temp)) {
            return 10;
        } else if (ArrayUtils.contains(sixExpectValue, temp)) {
            return 6;
        } else if (ArrayUtils.contains(fourExpectValue, temp)) {
            return 4;
        } else if (ArrayUtils.contains(twoExpectValue, temp)) {
            return 2;
        } else {
            return 0;
        }
    }

    //同一个班级同时有两个时间在上课的冲突问题
    public static String randomTimeForClassConflict(List<String> geneList, String classID) {
        //找出当前班级在 01-25 时间之间还未使用的时间
        Set<String> usedTimeList = geneList.stream().filter(item->cutGene(CourseInfo.CLASS_ID,item).equals(classID))
                .map(item->cutGene(CourseInfo.COURSE_TIME,item)).collect(Collectors.toSet());
        return getFreeTime(usedTimeList);
    }

    //同一个老师在同一时间上课
    public static String randomTimeForTeacherConflict(List<String> geneList, String teacherId) {

        // 找出当前教师在 01-25 时间之间还未使用的时间
        Set<String> usedTimeList = geneList.stream().filter(item -> cutGene(CourseInfo.TEACHER_ID, item).equals(teacherId))
                        .map(item -> cutGene(CourseInfo.COURSE_TIME, item)).collect(Collectors.toSet());

        return getFreeTime(usedTimeList);
    }
}
