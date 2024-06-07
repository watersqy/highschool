<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'

import { ref } from 'vue'

//用户搜索时选中的
const state=ref()
const department=ref()
const spe=ref()
const classIn=ref()

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref()//总条数
const pageSize = ref(10)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  studentList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  studentList();
}

import {
  studentListService, studentDepartmentListService, speListService, classListService,
  studentAddService, studentUpdateService, studentDeleteService, teacherDeleteService
} from "@/api/user.js";
import {ElMessage, ElMessageBox} from "element-plus";

const departments=ref();
const spes=ref();
const classes=ref();
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

const classList = async ()=>{
  let result=await classListService();
  classes.value=result.data;
}
classList();

//学生列表数据模型
const student = ref()
//获得学生列表
const studentList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    state:state.value?state.value:null,
    department:department.value?department.value:null,//不带条件则置空
    spe:spe.value?spe.value:null,
    classIn:classIn.value?classIn.value:null
  }
  let result= await studentListService(params);//获得结果

  total.value=result.data.total;//查询到的数据数量
  student.value=result.data.items;
  //数据处理
  for(let i in student.value){
      student.value[i].state=student.value[i].state===1?"激活":"未激活";
  }
}
studentList();

//重置的时候刷新页面
const reList=()=>{
  state.value='';
  department.value='';
  spe.value='';
  classIn.value='';
  studentList();
}

const title=ref();
const Visible=ref(false);

const studentModel=ref({
  oldId:'',
  id:'',
  name:'',
  department:'',
  spe:'',
  classIn:'',
  state:''
})

const addStudent=async ()=>{
  let result= await studentAddService(studentModel.value);
  ElMessage.success(result.message?result.message:'添加成功');

  Visible.value=false;
  studentList();
  departmentList();
  speList();
  classList();
}

const studentEdit=(row)=>{
  Visible.value=true;
  title.value='编辑学生信息';

  studentModel.value.oldId=row.id;
  studentModel.value.id=row.id;
  studentModel.value.name=row.name;
  studentModel.value.department=row.department;
  studentModel.value.spe=row.spe;
  studentModel.value.classIn=row.classIn;
  studentModel.value.state=row.state==='激活'?'1':'0';
}

const updateStudent=async ()=>{
  let result= await studentUpdateService(studentModel.value);
  ElMessage.success(result.message?result.message:'修改成功');

  studentList();
  departmentList();
  speList();
  classList();
  Visible.value=false;
}

const clearDate=()=>{
  studentModel.value.oldId='';
  studentModel.value.id='';
  studentModel.value.name='';
  studentModel.value.department='';
  studentModel.value.spe='';
  studentModel.value.classIn='';
  studentModel.value.state='';
}

const deleteStudent=async (row)=>{
  ElMessageBox.confirm(
      '您确认要删除该学生信息吗?',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        await studentDeleteService(row.id);
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
        studentList();
        departmentList();
        speList();
        classList();

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
        <span>信息管理 / 学生</span>
        <div class="extra">
          <el-button type="primary" @click="Visible=true ;title='添加学生' ;clearDate()">添加学生</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
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
      <el-form-item label="班级:">
        <el-select style="width: 200px" placeholder="请选择" v-model="classIn">
          <el-option v-for="c in classes" :label="c" :value="c">
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
        <el-button type="primary" @click="studentList">搜索</el-button>
        <el-button @click="reList">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 学生名单 -->
    <el-table :data="student" style="width: 100%">
      <el-table-column label="学号" prop="id"></el-table-column>
      <el-table-column label="姓名" prop="name"></el-table-column>
      <el-table-column label="学院" prop="department"></el-table-column>
      <el-table-column label="专业" prop="spe"></el-table-column>
      <el-table-column label="班级" prop="classIn"></el-table-column>/
      <el-table-column label="账号状态" prop="state"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="studentEdit(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" prop="id" @click="deleteStudent(row)"></el-button>
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
      <title></title>
      <el-from :model="studentModel" label-with="100px">
        <el-form-item label="学生学号:">
          <el-input v-model="studentModel.id" placeholder="请输入学生学号"></el-input>
        </el-form-item>
        <el-form-item label="学生姓名:">
          <el-input v-model="studentModel.name" placeholder="请输入学生姓名"></el-input>
        </el-form-item>
        <el-form-item label="所在学院:">
          <el-input v-model="studentModel.department" placeholder="请输入学生所在学院"></el-input>
        </el-form-item>
        <el-form-item label="所在专业:">
          <el-input v-model="studentModel.spe" placeholder="请输入学生所在专业"></el-input>
        </el-form-item>
        <el-form-item label="所在班级:">
          <el-input v-model="studentModel.classIn" placeholder="请输入学生所在班级"></el-input>
        </el-form-item>
      </el-from>
      <template #footer>
        <span class="dialog-footer">
        <el-button type="primary" @click="title==='添加学生'?addStudent():updateStudent()">确认</el-button>
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
