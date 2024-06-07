<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'

import { ref } from 'vue'

import useUserInfoStore from '@/store/userInfo.js'
const userInfoStore = useUserInfoStore();
const userInfo = ref({...userInfoStore.info})

//用户搜索时选中的课程状态
const state=ref()
const type=ref()
const types=ref([
  {
    "id":"01",
    "name":"专业必修课"
  },
  {
    "id":"02",
    "name":"专业选修课"
  },
  {
    "id":"03",
    "name":"实验"
  },
  {
    "id":"04",
    "name":"通识教育"
  },
  {
    "id":"05",
    "name":"艺体教育"
  }
])

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref()//总条数
const pageSize = ref(10)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  courseList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  courseList();
}


import {
  courseListService,courseAddService,
  courseArrangeService,courseUpdateService,
  courseDeleteService
} from "@/api/course.js";
import {ElMessage, ElMessageBox} from "element-plus";

const courses = ref()
//获得课程列表
const courseList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    state:state.value?state.value:null,//可能不带条件
    type:type.value?type.value:null
  }
  let result= await courseListService(params);

  total.value=result.data.total;
  courses.value=result.data.items;

  //处理数据
  for(let i in courses.value){
    courses.value[i].state=courses.value[i].state===0?"未开课":"已开课";
    switch (courses.value[i].type){
      case "01":
        courses.value[i].type="专业必修课";
        break;
      case "02":
        courses.value[i].type="专业选修课";
        break;
      case "03":
        courses.value[i].type="实验";
        break;
      case "04":
        courses.value[i].type="通识教育";
        break;
      case "05":
        courses.value[i].type="艺体教育";
        break;
    }
  }
}
courseList();

//重置的时候刷新页面
const recourseList=()=>{
  state.value='';
  type.value='';
  courseList()
}

//添加课程
const title=ref();
const Visible=ref(false);

const courseModel = ref({
  oldId:'',
  id:'',
  courseName:'',
  type:'',
  teacherId:'',
  teacher:'',
  classes:'',
  week:'',
  weekTime:'',
  state:'',
})

const addCourse = async ()=>{
  let result= await courseAddService(courseModel.value);
  ElMessage.success(result.message?result.message:'添加成功');

  Visible.value=false;
  courseList();
}

//排课
const arrangeCourse = async ()=>{
  let result= await courseArrangeService(courseModel.value);
  ElMessage.success(result.message?result.message:'排课完成');
}

//编辑课程信息
const courseEdit =(row)=>{
  Visible.value=true;
  title.value='编辑课程';

  courseModel.value.oldId=row.id;
  courseModel.value.id=row.id;
  courseModel.value.courseName=row.courseName;
  switch (row.type){
    case "专业必修课":
      courseModel.value.type="01";
      break;
    case "专业选修课":
      courseModel.value.type="02";
      break;
    case "实验":
      courseModel.value.type="03";
      break;
    case "通识教育":
      courseModel.value.type="04";
      break;
    case "艺体教育":
      courseModel.value.type="05";
      break;
  }
  courseModel.value.teacherId=row.teacherId;
  courseModel.value.teacher=row.teacher;
  courseModel.value.classes=row.classes;
  courseModel.value.week=row.week;
  courseModel.value.weekTime=row.weekTime;
  courseModel.value.state=row.state==='已开课'?'1':'0';
}

const updateCourse=async ()=>{
  let result = await courseUpdateService(courseModel.value);

  ElMessage.success(result.message?result.message:'修改成功');

  courseList();
  Visible.value=false;
}

const clearData=()=>{
  courseModel.value.oldId='';
  courseModel.value.id='';
  courseModel.value.courseName='';
  courseModel.value.type='';
  courseModel.value.teacherId='';
  courseModel.value.teacher='';
  courseModel.value.classes='';
  courseModel.value.week='';
  courseModel.value.weekTime='';
  courseModel.value.state='';
}

const deleteCourse=async (row)=>{
    ElMessageBox.confirm(
        '您确认要删除该课程吗?',
        '温馨提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(async () => {
          await courseDeleteService(row.id);
          ElMessage({
            type: 'success',
            message: '删除成功',
          })
          courseList();
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '用户取消了删除',
          })
        })
}

</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>信息管理 / 课程列表</span>
        <div class="extra">
          <el-button type="primary" @click="Visible=true ;title='添加课程' ;clearData()">添加课程</el-button>
          <el-button type="primary" @click="arrangeCourse" v-if="userInfo.root===3">排课</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="课程属性：">
        <el-select style="width: 200px" placeholder="请选择" v-model="type">
          <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开课状态：">
        <el-select style="width: 200px" placeholder="请选择" v-model="state">
          <el-option label="已开课" value= 1></el-option>
          <el-option label="未开课" value= 0></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="courseList">搜索</el-button>
        <el-button @click="recourseList">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 课程列表 -->
    <el-table :data="courses" style="width: 100%">
      <el-table-column label="课程代码" width="200" prop="id"></el-table-column>
      <el-table-column label="课程名" width="300" prop="courseName"></el-table-column>
      <el-table-column label="课程属性" prop="type"></el-table-column>
      <el-table-column label="任课老师编号" prop="teacherId"></el-table-column>
      <el-table-column label="任课老师" prop="teacher"></el-table-column>
      <el-table-column label="开课班级" prop="classes"></el-table-column>/
      <el-table-column label="课程周数" prop="week"></el-table-column>
      <el-table-column label="周课时"  prop="weekTime"></el-table-column>
      <el-table-column label="状态" prop="state"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }" >
          <el-button :icon="Edit" circle plain type="primary" @click="courseEdit(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" prop="id" @click="deleteCourse(row)"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <!-- 分页条 -->
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5 ,10, 15]"
                   layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
                   @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
    <!--添加/编辑课程-->
    <el-drawer v-model="Visible" :title="title" direction="rtl" size="50%">
      <title></title>
      <el-from :model="courseModel" label-with="100px">
        <el-form-item label="课程编号:">
          <el-input v-model="courseModel.id" placeholder="请输入课程编号"></el-input>
        </el-form-item>
        <el-form-item label="课程名称:">
          <el-input v-model="courseModel.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程属性:">
          <el-select placeholder="请选择" v-model="courseModel.type">
            <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教师编号:">
          <el-input v-model="courseModel.teacherId" placeholder="请输入教师编号"></el-input>
        </el-form-item>
        <el-form-item label="教师名称:">
          <el-input v-model="courseModel.teacher" placeholder="请输入教师名称"></el-input>
        </el-form-item>
        <el-form-item label="开课班级:">
          <el-input v-model="courseModel.classes" placeholder="例:数媒2201"></el-input>
        </el-form-item>
        <el-form-item label="课程周数:">
          <el-input v-model="courseModel.week" placeholder="请输入课程周数"></el-input>
        </el-form-item>
        <el-form-item label="每周课时:">
          <el-input v-model="courseModel.weekTime" placeholder="请输入周课时"></el-input>
        </el-form-item>
        <el-form-item label="开课状态：">
          <el-select style="width: 200px" placeholder="请选择" v-model="courseModel.state">
            <el-option label="已开课" value= 1></el-option>
            <el-option label="未开课" value= 0></el-option>
          </el-select>
        </el-form-item>
        <el-form-item >
          <el-button type="primary" @click="title==='添加课程'?addCourse():updateCourse()">确认</el-button>
          <el-button type="info" @click="Visible=false">取消</el-button>
        </el-form-item>
      </el-from>
    </el-drawer>
  </el-card>
</template>
<style lang="scss" scoped>
.page-container {
  min-width: 100%;
  min-height: 100%;
  box-sizing: border-box;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.el-drawer{
  .el-form-item {
    margin-left: 100px;
    margin-top: 45px;
    width: 70%;
    font-size: 18px;
  }

  .el-button{
    padding: 20px;
    margin-left: 200px;
    font-size: 18px;
  }
}

</style>
