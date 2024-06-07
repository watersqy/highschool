package org.learn.service.impl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.learn.mapper.ClassroomMapper;
import org.learn.mapper.CourseMapper;
import org.learn.pojo.*;
import org.learn.service.CourseService;
import org.learn.utils.CourseUtil;
import org.learn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public void add(Course course) {
        courseMapper.add(course);
    }

    @Override
    public Course findById(String id) {
        return courseMapper.findById(id);
    }

    @Override
    public void update(Course course) {
        courseMapper.update(course);
    }

    @Override
    public void delete(String id) {
        courseMapper.delete(id);
        courseMapper.deletePlan(id);
    }

    @Override
    public PageBean<Course> userCourseList(Integer pageNum, Integer pageSize, String state,String type) {
        //封装
        PageBean<Course> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = map.get("username").toString();
        List<Course> c = courseMapper.userCourseList(userId,state,type);
        //得到记录的总条数和当前页面数据
        Page<Course> p = (Page<Course>) c;
        //把数据填充到PageBean对象中
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public PageBean<Course> courseList(Integer pageNum, Integer pageSize, String state,String type) {
        //封装
        PageBean<Course> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = map.get("username").toString();
        List<Course> c = courseMapper.courseList(userId,state,type);
        //得到记录的总条数和当前页面数据
        Page<Course> p = (Page<Course>) c;
        //把数据填充到PageBean对象中
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public List<Course> coursePlan() {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = map.get("username").toString();
        return courseMapper.coursePlan(userId);
    }

    @Override
    public PageBean<Student> classList(Integer pageNum, Integer pageSize, String department, String spe) {
        //封装
        PageBean<Student> pageBean = new PageBean<>();
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        List<Student> s = courseMapper.classList(department,spe);
        //得到记录的总条数和当前页面数据
        Page<Student> p = (Page<Student>) s;
        //把数据填充到PageBean对象中
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public List<Course> findByClassId(String classId) {
        return courseMapper.findByClassId(classId);
    }

    @Override
    public List<Course> findByTeacherId(String teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }

    @Override
    public void arrange() {
        try {
            //获得所有已开课的课程的名单
            List<Course> courselist = courseMapper.getAll();

            if(courselist==null||courselist.isEmpty()){
                return;
            }

            //学时是否超过课表的容纳值
            checkWeeksNumber(courselist);
            //将开课任务的各项信息进行编码成染色体
            List<String> geneMap = codeSet(courselist);

            //基因编码成为以班级分类的个体
            Map<String , List<String>> ClassMap= transformIndividual(geneMap);
            //给基因编码随机分配时间,得到班级内部课程时间不重复的时间编码
            ClassMap = codingTime(ClassMap);
            //遗传进化
            ClassMap = geneticEvolution(ClassMap);

            //分配教室并做教室冲突检测
            List<String> resultList = finalResult(ClassMap);
            //解码
            List<Course> coursePlan = decoding(resultList);
            //清除课程表数据库，准备写入新数据
            courseMapper.deleteAll();
            //写入数据库
            for (Course course : coursePlan) {
                courseMapper.insertPlan(course);
                courseMapper.update(course);
            }
        }
        catch (Exception e){
            arrange();
        }
    }

    private void checkWeeksNumber(List<Course> courselist) {
        courselist.stream().collect(Collectors.groupingBy(Course::getClasses)).forEach((key, value) -> {
            int sum=value.stream().mapToInt(Course::getWeekTime).sum();
            //一节课两个课时
            if(sum> CourseUtil.MAX_CLASS_TIME *2){
                throw new RuntimeException(String.format("班级：%s 的课时时超过 %s，不能排课", key, CourseUtil.MAX_CLASS_TIME * 2));
            }

        });
    }

    /*
     * 编码规则: （位）
     * 课程编号：7
     * 教师编号：5
     * 班级编号：8
     * 课程属性：2
     * 编码规则为：课程编号+教师编号+班级编号+课程属性
     */
    private List<String> codeSet(List<Course> courselist) {
        List<String> geneMap = new ArrayList<>();
        for(Course course : courselist){
            // 得到每周上课的节数，设定2学时为一节课
            int size=course.getWeekTime()/2;
            for(int i=0;i<size;i++){
                String gene = course.getId()+course.getTeacherId()+course.getClasses()
                        +course.getType();
                geneMap.add(gene);
            }
        }
        return geneMap;
    }

    //班级编号的集合，去重
    private Map<String, List<String>> transformIndividual(List<String> geneMap) {
        Map<String, List<String>> ClassMap = new HashMap<>();
        List<String> ClassIDList = courseMapper.selectClass();

        for(String classID : ClassIDList){
            List<String> geneList = new ArrayList<>();
            for(String gene : geneMap){
                if(classID.equals(CourseUtil.cutGene(CourseInfo.CLASS_ID, gene))){
                    geneList.add(gene);

                }
            }
            if(!geneList.isEmpty()){
                ClassMap.put(classID, geneList);
            }
        }
        return ClassMap;
    }

    //给以班级分类的基因编码随机分配时间
    private Map<String , List<String>> codingTime(Map<String , List<String>> ClassMap) {
        Map<String , List<String>> ResultMap= new HashMap<>();
        for(String ClassID : ClassMap.keySet()){
            List<String> time = CourseUtil.randomTime(ClassMap.get(ClassID).size());
            List<String> geneList = new ArrayList<>();
            int i=0;
            for (String gene: ClassMap.get(ClassID)){
                gene = gene + time.get(i++);
                geneList.add(gene);
            }
            ResultMap.put(ClassID, geneList);
        }
        return ResultMap;
    }

    /*
     * 遗传进化(每个班级中多条基因编码)
     * 步骤：
     * 1、初始化种群
     * 2、交叉，选择
     * 3、变异
     * 4、重复2,3步骤
     * 5、直到达到终止条件
     */
    private Map<String, List<String>> geneticEvolution(Map<String, List<String>> ClassMap) {
        List<String> resultGeneList;

        for (int i = 0; i < CourseInfo.GENERATION; ++i) {
            hybridization(ClassMap);
            List<String> allIndividual = collectGene(ClassMap);
            resultGeneList = geneMutation(allIndividual);
            List<String> list = conflictResolution(resultGeneList);
            ClassMap.clear();
            ClassMap = transformIndividual(list);
        }

        return ClassMap;
    }

    //给每个班级交叉：一个班级看作一个种群
    private void hybridization(Map<String, List<String>> ClassMap) {
        for (Map.Entry<String, List<String>> entry : ClassMap.entrySet()) {
            String classID = entry.getKey();
            List<String> individualList = ClassMap.get(classID);
            List<String> oldIndividualList = new ArrayList<>(individualList);

            selectGene(individualList);

            // 计算并对比子父代的适应度值，高的留下进行下一代遗传，相当于进化，
            if (CourseUtil.calculateExpectedValue(individualList) >= CourseUtil.calculateExpectedValue(oldIndividualList)) {
                ClassMap.put(classID, individualList);
            } else {
                ClassMap.put(classID, oldIndividualList);
            }
        }
    }

    //个体中随机选择基因进行交叉(交换上课时间)
    private List<String> selectGene(List<String> individualList) {
        int size = individualList.size();
        boolean flag;
        do {
            //随机生成两个下标
            int firstIndex = CourseUtil.RANDOM.nextInt(size);
            int secondIndex = CourseUtil.RANDOM.nextInt(size);

            //得到基因编码
            String firstGene = individualList.get(firstIndex);
            String secondGene = individualList.get(secondIndex);

            if (firstIndex == secondIndex) {
                flag = false;
            }
            else {
                String firstClassTime = CourseUtil.cutGene(CourseInfo.COURSE_TIME, firstGene);
                String secondClassTime = CourseUtil.cutGene(CourseInfo.COURSE_TIME, secondGene);
                firstGene = firstGene.substring(0, 20) + secondClassTime;
                secondGene = secondGene.substring(0, 20) + firstClassTime;
                individualList.set(firstIndex, firstGene);
                individualList.set(secondIndex, secondGene);
                flag = true;
            }
        } while (!flag);
        return individualList;
    }

    //重新合拢交叉后的个体,即不分班级的基因编码，得到所有的编码
    private List<String> collectGene(Map<String, List<String>> ClassMap) {
        List<String> resultList = new ArrayList<>();
        for(List<String> ClassList: ClassMap.values()){
            resultList.addAll(ClassList);
        }
        return resultList;
    }

    //基因变异
    private List<String> geneMutation(List<String> geneList) {
        final double mutationRate = 0.005;
        int mutationNumber = (int)(geneList.size()*mutationRate);//突变个数

        if(mutationNumber < 1){
            mutationNumber = 1;
        }

        for (int i = 0; i < mutationNumber; ++i) {
            int randomIndex = CourseUtil.RANDOM.nextInt(geneList.size());
            String gene = geneList.get(randomIndex);
            String newCourseTime = CourseUtil.randomTime();
            gene = gene.substring(0, 20) + newCourseTime;
            geneList.remove(randomIndex);
            geneList.add(randomIndex,gene);
        }
        return geneList;
    }

    //冲突消除
    private List<String> conflictResolution(List<String> geneList) {
        for (int i = 0; i < geneList.size(); ++i) {
            String gene = geneList.get(i);
            String teacherID = CourseUtil.cutGene(CourseInfo.TEACHER_ID, gene);
            String classID = CourseUtil.cutGene(CourseInfo.CLASS_ID, gene);
            String courseTime = CourseUtil.cutGene(CourseInfo.COURSE_TIME, gene);

            for (int j = i+1; j < geneList.size(); ++j) {
                String tGene = geneList.get(j);
                String tTeacherID = CourseUtil.cutGene(CourseInfo.TEACHER_ID, gene);
                String tClassID = CourseUtil.cutGene(CourseInfo.CLASS_ID, gene);
                String tCourseTime = CourseUtil.cutGene(CourseInfo.COURSE_TIME, gene);
                //冲突检测
                if(courseTime.equals(tCourseTime) && classID.equals(tClassID)){
                    String newCourseTime = CourseUtil.randomTimeForClassConflict(geneList, classID);
                    replaceConflictTime(geneList,tGene,newCourseTime);
                }
                if(courseTime.equals(tCourseTime) && teacherID.equals(tTeacherID)){
                    String newCourseTime = CourseUtil.randomTimeForTeacherConflict(geneList, teacherID);
                    replaceConflictTime(geneList, tGene, newCourseTime);
                }

            }
        }
        return geneList;
    }

    private void replaceConflictTime(List<String> geneList, String gene, String newClassTime) {
        String newGene = gene.substring(0, 20) + newClassTime;

        // 替换新的随机时间给剩余大种群里面的编码
        for (int i = 0; i < geneList.size(); i++) {
            if (geneList.get(i).equals(gene)) {
                geneList.set(i, newGene);
                break;
            }
        }
    }

    //开始给进化完的基因编码分配教室，即在原来的编码中加上教室编号
    private List<String> finalResult(Map<String, List<String>> classMap) {
        List<String> resultList = new ArrayList<>();
        List<String> resultGeneList = collectGene(classMap);
        List<Classroom> classroomList = classroomMapper.useList();

        String classroomId = "";
        for (String gene : resultGeneList) {
            classroomId = issueClassroom(gene, classroomList, resultList);
            gene += classroomId;
            resultList.add(gene);
        }
        return resultList;
    }

    private String issueClassroom(String gene, List<Classroom> classroomList, List<String> resultList) {
        // 处理特殊课程，实验课，体育课
        List<Classroom> sportBuilding = classroomMapper.findByArea("体育中心");
        sportBuilding.addAll(classroomMapper.findByArea("西北操场"));
        List<Classroom> experimentBuilding = classroomMapper.findByArea("物联网");
        String courseType = CourseUtil.cutGene(CourseInfo.COURSE_TYPE, gene);

        if (courseType.equals(CourseInfo.EXPERIMENT_COURSE)) {
            // 03 为实验课
            return chooseClassroom(gene, experimentBuilding, resultList);
        } else if (courseType.equals(CourseInfo.PHYSICAL_COURSE)) {
            // 05为体育课
            return chooseClassroom(gene, sportBuilding, resultList);
        } else {
            // 剩下课程都放在普通的教室
            return chooseClassroom(gene, classroomList, resultList);
        }
    }

    //给不同课程的基因编码随机选择一个教室
    private String chooseClassroom(String gene, List<Classroom> classroomList, List<String> resultList) {
        int max = classroomList.size() - 1, temp;
        Classroom classroom=classroomList.get(max);
        // 分配教室
        boolean isClassRoomSuitable = false;
        while (!isClassRoomSuitable) {
            temp = (int) (Math.random() * (max + 1));
            classroom = classroomList.get(temp);
            isClassRoomSuitable = judgeClassroom(gene, classroom, resultList);
        }
        return classroom.getId();
    }

    //判断教室是否符合上课班级所需
    private boolean judgeClassroom(String gene, Classroom classroom, List<String> resultList) {
        String courseType = CourseUtil.cutGene(CourseInfo.COURSE_TYPE, gene);
        if (courseType.equals(CourseInfo.MAIN_COURSE)||courseType.equals(CourseInfo.SECONDARY_COURSE)||courseType.equals(CourseInfo.COMMON_COURSE)) {
            if(classroom.getArea().equals("一教")||classroom.getArea().equals("二教")){
                return isFree(gene, resultList, classroom);
            }else return false;
        }
        else {
            return isFree(gene, resultList, classroom);
        }

    }

    //判断同一时间同一个教室是否有多个班级使用
    private Boolean isFree(String gene, List<String> resultList, Classroom classroom) {
        if (resultList.isEmpty()) {
            return true;
        } else {
            for (String resultGene : resultList) {
                if (CourseUtil.cutGene(CourseInfo.CLASSROOM_ID, resultGene).equals(classroom.getId())
                        && (CourseUtil.cutGene(CourseInfo.COURSE_TIME, gene).equals(CourseUtil.cutGene(CourseInfo.COURSE_TIME, resultGene)))) {
                    return false;
                }
            }
        }
        return true;
    }

    /* 解码
     * 编码规则: （位）
     * 课程编号：7
     * 讲师编号：5
     * 班级编号：8
     * 课程属性：2
     * 上课时间：2
     * 教室编号：6
     * 编码规则为：课程编号+教师编号+班级编号+课程属性+上课时间+教室编号
     */
    private List<Course> decoding(List<String> resultList) {
        List<Course> courseList = new ArrayList<>();
        for (String gene : resultList) {
            Course course = courseMapper.findById(CourseUtil.cutGene(CourseInfo.COURSE_ID, gene));
            course.setOldId(course.getId());
            course.setCourseTime(CourseUtil.cutGene(CourseInfo.COURSE_TIME, gene));
            course.setClassroomId(CourseUtil.cutGene(CourseInfo.CLASSROOM_ID,gene));
            course.setPlace(classroomMapper.findById(CourseUtil.cutGene(CourseInfo.CLASSROOM_ID,gene)));
            courseList.add(course);
        }
        return courseList;
    }
}
