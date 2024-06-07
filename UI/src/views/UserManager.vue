<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'

import { ref } from 'vue'

//用户搜索时选中的状态
const username=ref()
const root=ref()

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(8)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  usersList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  usersList();
}

import {usersListService,userRootService} from "@/api/user.js";
import {ElMessage, ElMessageBox} from "element-plus";

const users = ref()
//获得用户列表
const usersList=async ()=>{
  let params = {
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    root:root.value?root.value:null,//可能不带条件
    username:username.value?username.value:null
  }
  let result= await usersListService(params);

  total.value=result.data.total;
  users.value=result.data.items;

  for(let i in users.value){
    users.value[i].state=users.value[i].state===0?"未激活":"激活";
  }
}
usersList();

const reList=()=>{
  root.value='';
  username.value='';

  usersList();
}

const Visible=ref(false);

const userModel=ref({
  username:'',
  name:'',
  root:''
})

const userEdit=(row)=>{
  Visible.value=true;

  userModel.value.username=row.username;
  userModel.value.name=row.name;
  userModel.value.root=row.root;
}

const updateRoot=async ()=>{
  let result = await userRootService(userModel.value);
  ElMessage.success(result.message?result.message:'修改成功');

  usersList();
  Visible.value=false;
}

</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>信息管理 / 用户名单</span>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="用户名:">
        <el-input v-model="username" clearable></el-input>
      </el-form-item>
      <el-form-item label="用户权限：">
        <el-select style="width: 200px" placeholder="请选择" v-model="root">
          <el-option label="1" value="1"></el-option>
          <el-option label="2" value="2"></el-option>
          <el-option label="3" value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="usersList">搜索</el-button>
        <el-button @click="reList">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 文章列表 -->
    <el-table :data="users" style="width: 100%">
      <el-table-column label="账号" prop="username"></el-table-column>
      <el-table-column label="姓名" prop="name"></el-table-column>
      <el-table-column label="邮箱" width="300px" prop="email"></el-table-column>
      <el-table-column label="注册时间" prop="createTime"></el-table-column>
      <el-table-column label="上次登录时间" prop="loginTime"></el-table-column>
      <el-table-column label="权限" prop="root"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="userEdit(row)"></el-button>
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
    <el-dialog v-model="Visible" title="修改权限" width="30%">
      <el-form :model="userModel">
        <el-form-item label="用户名:">
          <el-input v-model="userModel.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="用户姓名:">
          <el-input v-model="userModel.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="权限等级:">
          <el-select style="width: 200px" placeholder="请选择" v-model="userModel.root">
            <el-option label="1" value="1"></el-option>
            <el-option label="2" value="2"></el-option>
            <el-option label="3" value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
        <el-button type="primary" @click="updateRoot">确认</el-button>
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