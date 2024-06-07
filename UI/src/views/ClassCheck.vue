<script setup>
import {Search} from '@element-plus/icons-vue'
import { ref } from 'vue'

//用户搜索时选中的
const department=ref()
const spe=ref()

const lessons = ref([
  "08.00-9.35",
  "9.55-11.30",
  "13.30-15.05",
  "15.25-17.00",
  "18:30-20:10"
]);
const courses = ref([
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
]);

const courseInfo = ref([
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
]);

const digital2Chinese=(num) =>{
  const character = ["一", "二", "三", "四", "五"];
  return character[num];
}

import {classTableService, classListService, courseTableService} from "@/api/course.js";
import {speListService, studentDepartmentListService} from "@/api/user.js";

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(9)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  classList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  classList();
}

const departments=ref();
const spes=ref();

const departmentList=async ()=>{
  let result=await studentDepartmentListService();
  departments.value=result.data;
}
departmentList();

const speList = async () => {
  let result=await speListService();
  spes.value=result.data;
}
speList();

const classes=ref();

const classList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    department:department.value?department.value:null,//可能不带条件
    spe:spe.value?spe.value:null
  }
  let result= await classListService(params);

  total.value=result.data.total;
  classes.value=result.data.items;
}
classList();

//重置的时候刷新页面
const reList=()=>{
  department.value='';
  spe.value='';
  classList();
}

const Visible=ref(false);
const title=ref();

//
const courseList=async (row)=>{
  //清空上一次的数据
  courses.value=[
    ["","","","",""],
    ["","","","",""],
    ["","","","",""],
    ["","","","",""],
    ["","","","",""],
  ]
  courseInfo.value=[
    ["","","","",""],
    ["","","","",""],
    ["","","","",""],
    ["","","","",""],
    ["","","","",""],
  ]

  Visible.value=true
  title.value=row.classIn;
  let result = await classTableService(row.classIn);
  let time=0,day=0,info="";let n=0;
  for(let c in result.data){
    n++;
    const item=result.data[c];
    time=parseInt(item.courseTime) - 1;
    day=Math.floor(time/5);
    info=item.courseName;
    courses.value[day][time%5]=info;
    info=item.teacher+"-"+item.place;
    courseInfo.value[day][time%5]=info;
  }
}

</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>课表查询 / 班级列表</span>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline >
      <el-form-item label="学院:">
        <el-select style="width: 200px" placeholder="请选择" v-model="department">
          <el-option v-for="d in departments" :label="d" :value="d">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="专业:">
        <el-select style="width: 200px" placeholder="请选择" v-model="spe">
          <el-option v-for="s in spes" :label="s" :value="s">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="classList">搜索</el-button>
        <el-button @click="reList">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 课程列表 -->
    <el-table :data="classes" style="width: 100%">
      <el-table-column label="学院" prop="department"></el-table-column>
      <el-table-column label="专业" prop="spe"></el-table-column>
      <el-table-column label="班级名称" prop="classIn"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button :icon="Search" type="primary" style="width: 100px" @click="courseList(row)"></el-button>
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
    <el-dialog v-model="Visible" :title="title" width="70%">
      <div class="table-wrapper">
        <div class="table-container">
          <table>
            <thead>
            <tr>
              <th>时间</th>
              <th v-for="(weekNum, weekIndex) in courses.length">
                {{ "周" + digital2Chinese(weekIndex, "week") }}
              </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(lesson, lessonIndex) in lessons">
              <td>
                <p>{{ "第" + digital2Chinese(lessonIndex) + "节" }}</p>
                <p class="period">{{ lesson }}</p>
              </td>

              <td v-for="(course,courseIndex) in courses">
                <!-- 课程名称-教师-教室 -->
                <p>{{courses[courseIndex][lessonIndex]}}</p>
                <p>{{courseInfo[courseIndex][lessonIndex]}}</p>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </el-dialog>
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

.table-wrapper {
  width: 100%;
  height: 100%;
  overflow: auto;
}
.table-container {
  font-size: 20px;
  margin: 20px;

  table {
    table-layout: fixed;
    width: 100%;
    word-wrap: break-word;
    word-break: break-all;
    border-collapse: collapse;

    thead {
      th {
        border: 1px solid gainsboro;
        line-height: 25px;
        font-weight: normal;
        font-size: 18px;
        color: grey;
      }
    }

    tbody {

      td {
        border: 1px solid gainsboro;
        font-size: 18px;
        color: black;
        line-height: 13px;
      }
    }

    th,
    td {
      width: 60px;
      padding: 14px 3px;
      font-size: 12px;
      text-align: center;
    }

    tr td:first-child {
      color: grey;

      .period {
        font-size: 15px;
      }
    }
  }
}
</style>