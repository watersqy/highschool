<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'

import { ref } from 'vue'

//用户搜索时选中的状态
const state=ref()
const department=ref()

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref()//总条数
const pageSize = ref(10)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  teacherList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  teacherList();
}

import {teacherListService,teacherDepartmentListService,
    teacherAddService,teacherUpdateService,teacherDeleteService
} from "@/api/user.js";

import {ElMessage, ElMessageBox} from "element-plus";
import {classroomDeleteService} from "@/api/classroom.js";

const departments=ref()
const departmentList=async ()=>{
  let result= await teacherDepartmentListService();
  departments.value=result.data;
}
departmentList();

const teacher = ref()
//获得教师列表
const teacherList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    state:state.value?state.value:null,//可能不带条件
    department:department.value?department.value:null
  }
  let result= await teacherListService(params);

  total.value=result.data.total;
  teacher.value=result.data.items;

  for(let i in teacher.value){
      teacher.value[i].state=teacher.value[i].state===0?"未激活":"激活";
  }
}
teacherList();

const reList=()=>{
  state.value='';
  department.value='';

  teacherList();
}

const title=ref();
const Visible=ref(false);

const teacherModel = ref({
  oldId:'',
  id:'',
  name:'',
  department:'',
  state:''
})

const addTeacher=async ()=>{
  let result= await teacherAddService(teacherModel.value);
  ElMessage.success(result.message?result.message:'添加成功');

  Visible.value=false;
  teacherList();
  departmentList();
}

const teacherEdit=(row)=>{
  Visible.value=true;
  title.value='编辑老师信息';

  teacherModel.value.oldId=row.id;
  teacherModel.value.id=row.id;
  teacherModel.value.name=row.name;
  teacherModel.value.department=row.department;
  teacherModel.value.state=row.state==='激活'?'1':'0';
}

const updateTeacher=async ()=>{
  let result= await teacherUpdateService(teacherModel.value);
  ElMessage.success(result.message?result.message:'修改成功');

  teacherList();
  departmentList();
  Visible.value=false;
}

const clearDate=()=>{
  teacherModel.value.oldId='';
  teacherModel.value.id='';
  teacherModel.value.name='';
  teacherModel.value.department='';
  teacherModel.value.state='';
}

const deleteTeacher=async (row)=>{
  ElMessageBox.confirm(
      '您确认要删除该老师信息吗?',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        await teacherDeleteService(row.id);
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
        teacherList();
        departmentList();

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
        <span>信息管理 / 教师</span>
        <div class="extra">
          <el-button type="primary" @click="Visible=true ;title='添加老师' ;clearDate()">添加教师</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="部门：">
        <el-select style="width: 200px" placeholder="请选择" v-model="department">
          <el-option v-for="d in departments" :label="d" :value="d">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="账号状态：">
        <el-select style="width: 200px" placeholder="请选择" v-model="state">
          <el-option label="激活" value=1></el-option>
          <el-option label="未激活" value=0></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="teacherList">搜索</el-button>
        <el-button @click="reList">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 教师名单 -->
    <el-table :data="teacher" style="width: 100%">
      <el-table-column label="教师编号" prop="id"></el-table-column>
      <el-table-column label="姓名" prop="name"></el-table-column>
      <el-table-column label="部门" prop="department"></el-table-column>
      <el-table-column label="账号状态" prop="state"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="teacherEdit(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" prop="id" @click="deleteTeacher(row)"></el-button>
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
    <el-dialog v-model="Visible" :title="title" width="30%">
      <el-from :model="teacherModel" label-with="100px">
        <el-form-item label="教师编号:">
          <el-input v-model="teacherModel.id" placeholder="请输入教师编号"></el-input>
        </el-form-item>
        <el-form-item label="教师姓名:">
          <el-input v-model="teacherModel.name" placeholder="请输入教师姓名"></el-input>
        </el-form-item>
        <el-form-item label="所在学院:">
          <el-input v-model="teacherModel.department" placeholder="请输入教师所在学院"></el-input>
        </el-form-item>
      </el-from>
      <template #footer>
        <span class="dialog-footer">
        <el-button type="primary" @click="title==='添加老师'?addTeacher():updateTeacher()">确认</el-button>
        <el-button type="info" @click="Visible=false">取消</el-button>
      </span>
      </template>
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

.el-dialog{
  .el-form-item {
    margin-left: 50px;
    margin-top: 45px;
    width: 70%;
    font-size: 18px;
  }

  .el-button{
    padding: 20px;
    margin-right: 130px;
    font-size: 18px;
  }
}
</style>
