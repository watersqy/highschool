<script setup>
import {
  Calendar,
  Files,
  Management,
  Search,
  SwitchButton,
  User,
} from "@element-plus/icons-vue";
import avatar from '@/assets/avatar.png'

import {ref} from "vue";

const root=ref();

//条目被点击后,调用的函数
import {useRouter} from 'vue-router'
const router = useRouter();
import {ElMessage,ElMessageBox} from 'element-plus'
//退出
const handleCommand = (command)=>{
  //判断指令
  if(command === 'logout'){
    //退出登录
    ElMessageBox.confirm(
        '您确认要退出吗?',
        '温馨提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(async () => {
          //退出登录
          //1.清空pinia中存储的token以及个人信息
          //tokenStore.removeToken()
          //userInfoStore.removeInfo()

          //2.跳转到登录页面
          router.push('/login')
          ElMessage({
            type: 'success',
            message: '退出登录成功',
          })

        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '用户取消了退出登录',
          })
        })
  }else{
    //路由
    router.push('/home/info')
  }
}

import {userInfoService} from "@/api/user.js";
import useUserInfoStore from "@/store/userInfo.js";
const userInfoStore = useUserInfoStore();

const getUserInfo=async ()=>{
  let result= await userInfoService();
  userInfoStore.setInfo(result.data);
  root.value=result.data.root;
}
getUserInfo();
</script>

<template>
  <el-container class="home-container">
    <!--头部区域-->
    <el-header class="el-header">
      <el-menu mode="horizontal">
        <el-menu mode="horizontal" router>
          <div style="line-height: 50px; font-size: 20px; padding-left: 20px; padding-right: 50px">高校排课系统</div>
          <el-menu-item index="/home/courseTable" v-if="root<3">
            <el-icon><Calendar/></el-icon>
            <span style="font-size: 16px">我的日程</span>
            </el-menu-item>
          <el-menu-item  index="/home/course" v-if="root<3">
            <el-icon><Management/></el-icon>
            <span style="font-size: 16px" >我的课程</span>
          </el-menu-item>
          <el-sub-menu v-if="root>1" index="1">
            <template #title>
              <el-icon><Files/></el-icon>
              <span style="font-size: 16px">信息管理</span>
            </template>
            <el-menu-item index="/home/courseManage">课程管理</el-menu-item>
            <el-menu-item index="/home/classroom">教室管理</el-menu-item>
            <el-menu-item index="/home/teacher">教师管理</el-menu-item>
            <el-menu-item index="/home/student">学生管理</el-menu-item>
            <el-menu-item index="/home/root" v-if="root===3">用户权限管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu v-if="root>1" index="2">
            <template #title>
              <el-icon><Search/></el-icon>
              <span style="font-size: 16px">课表查询</span>
            </template>
            <el-menu-item index="/home/search-class">按班级</el-menu-item>
            <el-menu-item index="/home/search-teacher">按教师</el-menu-item>
          </el-sub-menu>
        </el-menu>
        <div style="font-size: 16px ;width: 200px">
          你好，<strong>{{ userInfoStore.info.name }}</strong>
        </div>
        <el-dropdown placement="bottom-end" @command="handleCommand">
              <span class="el-dropdown__box">
                <el-avatar :src="userInfoStore.info.userPic? userInfoStore.info.userPic:avatar" size="large"/>
              </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="/home/info" :icon="User" >个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-menu>
    </el-header>
    <!--中间区域-->
    <el-main style="min-height:750px">
      <router-view/>
    </el-main>
    <!--底部区域-->
    <el-footer>
      高校排课系统 ©2024
    </el-footer>
  </el-container>
</template>

<style lang='css' scoped>
.home-container {
  .el-header {
    .el-menu{
      flex-basis:50%;
      align-items: center;
      justify-content: space-between;
    }
    .el-dropdown__box{
      display: flex;
      padding-left: 20px;
      padding-right: 20px;
      align-items: center;
      &:active,
      &:focus {
        outline: none;
      }
    }
  }
  .el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666;
  }
}

</style>