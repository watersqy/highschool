<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'

import { ref } from 'vue'

//用户搜索时选中的区域
const area=ref('')

//用户搜索时选中的教室状态
const state=ref('')

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref()//总条数
const pageSize = ref(10)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  classroomList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  classroomList();
}

import {
  classroomListService,classroomAddService,
  classroomUpdateService,classroomDeleteService
} from "@/api/classroom.js";
import {ElMessage, ElMessageBox} from "element-plus";

//教室列表数据模型
const classroom = ref()
//获得教室列表
const classroomList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    state:state.value?state.value:null,//可能不带条件
    area:area.value?area.value:null
  }

  let result= await classroomListService(params);

  total.value=result.data.total;
  classroom.value=result.data.items;

  for(let i in classroom.value){
    classroom.value[i].state=classroom.value[i].state===0?"维护中":"正常";
  }
}
classroomList();

//重置的时候刷新页面
const reList=()=>{
  state.value='';
  area.value='';
  classroomList()
}

const title=ref();
const Visible=ref(false);

const classroomModel = ref({
  oldId:'',
  id:'',
  area:'',
  position:'',
  state:''
})

const addClassroom=async ()=>{
  let result= await classroomAddService(classroomModel.value);
  ElMessage.success(result.message?result.message:'添加成功');

  Visible.value=false;
  classroomList();
}

const classroomEdit=(row)=>{
  Visible.value=true;
  title.value='编辑教室信息';

  classroomModel.value.oldId=row.id;
  classroomModel.value.id=row.id;
  classroomModel.value.area=row.area;
  classroomModel.value.position=row.position;
  classroomModel.value.state=row.state==='正常'?'1':'0';
}

//编辑教室信息
const updateClassroom=async ()=>{
  let result= await classroomUpdateService(classroomModel.value);
  ElMessage.success(result.message?result.message:'修改成功');

  classroomList();
  Visible.value=false;
}

const clearDate = () => {
  classroomModel.value.oldId='';
  classroomModel.value.id='';
  classroomModel.value.area='';
  classroomModel.value.position='';
  classroomModel.value.state='';
}

//删除教室
const classroomDelete=async (row)=>{
  ElMessageBox.confirm(
      '您确认要删除该教室吗?',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        await classroomDeleteService(row.id);
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
        classroomList();
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
        <span>信息管理 / 教室</span>
        <div class="extra">
          <el-button type="primary" @click="Visible=true ;title='添加教室' ;clearDate()">添加教室</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="区域：">
        <el-select style="width: 200px" placeholder="请选择" v-model="area">
          <el-option label="一教" value="一教"></el-option>
          <el-option label="二教" value="二教"></el-option>
          <el-option label="物联网" value="物联网"></el-option>
          <el-option label="西北操场" value="西北操场"></el-option>
          <el-option label="体育中心" value="体育中心"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="教室状态：">
        <el-select style="width: 200px" placeholder="请选择" v-model="state">
          <el-option label="正常" value="1"></el-option>
          <el-option label="维护中" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="classroomList">搜索</el-button>
        <el-button @click="reList">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 文章列表 -->
    <el-table :data="classroom" style="width: 100%">
      <el-table-column label="教室编号" prop="id"></el-table-column>
      <el-table-column label="区域" prop="area"></el-table-column>
      <el-table-column label="具体位置" prop="position"></el-table-column>
      <el-table-column label="上次更改时间" prop="updateTime"></el-table-column>
      <el-table-column label="状态" prop="state"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="classroomEdit(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" prop="id"  @click="classroomDelete(row)"></el-button>
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
    <el-dialog v-model="Visible" :title="title" width="40%">
      <title></title>
      <el-from :model="classroomModel" label-with="100px">
        <el-form-item label="教室编号:">
          <el-input v-model="classroomModel.id" placeholder="请输入教室编号"></el-input>
        </el-form-item>
        <el-form-item label="教室区域:">
          <el-select placeholder="请选择" v-model="classroomModel.area">
            <el-option label="一教" value="一教"></el-option>
            <el-option label="二教" value="二教"></el-option>
            <el-option label="物联网" value="物联网"></el-option>
            <el-option label="西北操场" value="西北操场"></el-option>
            <el-option label="体育中心" value="体育中心"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="具体位置:">
          <el-input v-model="classroomModel.position" placeholder="请输入教室具体位置"></el-input>
        </el-form-item>
        <el-form-item label="教室状态:">
          <el-select style="width: 200px" placeholder="请选择" v-model="classroomModel.state">
            <el-option label="正常" value= 1></el-option>
            <el-option label="维护中" value=0></el-option>
          </el-select>
        </el-form-item>
      </el-from>
      <template #footer>
        <span class="dialog-footer">
        <el-button type="primary" @click="title==='添加教室'?addClassroom():updateClassroom()">确认</el-button>
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
    margin-right: 200px;
    font-size: 18px;
  }
}
</style>
