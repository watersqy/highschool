<script setup>

import { ref } from 'vue'

//用户搜索时选中的课程状态
const state=ref('')
const type=ref('')

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref()//总条数
const pageSize = ref(10)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  courseList()
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  courseList()
}

const courses = ref()


import {userCourseListService} from '@/api/course.js'

//获取课程列表
const courseList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    state:state.value?state.value:null,//可能不带条件
    type:type.value?type.value:null
  }
  let result= await userCourseListService(params);

  total.value=result.data.total;
  courses.value=result.data.items;

  //处理数据
  for(let i = 0; i < courses.value.length; ++i){
    courses.value[i].state=courses.value[i].state===0?"未开课":"开课";
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
  courseList()
}

</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>课程列表</span>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="课程属性：">
        <el-select style="width: 200px" placeholder="请选择" v-model="type">
          <el-option label="专业必修课" value="01"></el-option>
          <el-option label="专业选修课" value="02"></el-option>
          <el-option label="实验" value="03"></el-option>
          <el-option label="通识教育" value="04"></el-option>
          <el-option label="艺体教育" value="05"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开课状态：">
        <el-select style="width: 200px" placeholder="请选择" v-model="state">
          <el-option label="已开课" value=1></el-option>
          <el-option label="未开课" value=0></el-option>
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
      <el-table-column label="课程周数" prop="week"></el-table-column>
      <el-table-column label="周课时"  prop="weekTime"></el-table-column>
      <el-table-column label="状态" prop="state"></el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <!-- 分页条 -->
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5 ,10, 15]"
                   layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
                   @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
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
</style>
