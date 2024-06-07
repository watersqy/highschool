<script setup>
import {Lock, Plus, Upload} from "@element-plus/icons-vue";
import {ref} from "vue";

import useUserInfoStore from '@/store/userInfo.js'
import avatar from '@/assets/avatar.png'

const userInfoStore = useUserInfoStore();

const userInfo = ref({...userInfoStore.info})

const isRePassword = ref(false)

const UserData=ref({
  old_pwd:'',
  new_pwd:'',
  re_pwd:''
})

//再次输入密码确认
const checkRePassword=(rule,value,callback)=>{
  if(value==='')
  {
    callback(new Error('请再次确认密码'))
  }
  else if(value!==UserData.value.password)
  {
    callback(new Error('请确保两次输入的密码一样'))
  }
  else
    callback()
}

//规则书写
const rules={
  old_password:[
    {required:true,message:'请输入原密码',trigger:'blur'},
    {min:4,max:16,message:'长度为4~16位非空字符',trigger:'blur' }
  ],
  password:[
    {required:true,message: '请输入密码',trigger:'blur'},
    {min:4,max:16,message:'长度为4~16位非空字符',trigger:'blur' }
  ],
  repassword:[
    {validator:checkRePassword,trigger:'blur'},
  ],
  email:[
    {required:true,message:'请输入用户邮箱',trigger:'blur'},
    {type:'email',message: '邮箱格式不正确',trigger:'blur'},
  ]
}

import {userUpdateService,rePasswordService} from '@/api/user.js'
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

//修改用户资料
const updateUserInfo=async ()=>{
  let result= await userUpdateService(userInfo.value)
  ElMessage.success(result.message?result.message:'修改成功');

  //修改pinia中的信息
  userInfoStore.setInfo(userInfo.value);
}

//重置密码
const updatePassword=async ()=>{
  let result= await rePasswordService(UserData.value)
  ElMessage.success(result.message?result.message:'修改成功,请重新登录');
  router.push('/login');
}

</script>

<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>个人中心</span>
      </div>
    </template>
    <el-row style="height: 600px">
      <el-col :span=12>
        <el-form :model="userInfo" :rules="rules" label-width="150" size="large" style="padding-top: 10px">
          <el-form-item label="学号(账号) :">
          <el-input v-model="userInfo.username" style="width: 40%" disabled></el-input>
        </el-form-item>
          <el-form-item label="姓名 :">
            <el-input v-model="userInfo.name" style="width: 40%" disabled></el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱 :">
            <el-input v-model="userInfo.email" style="width: 40%" clearable></el-input>
          </el-form-item>
          <el-form-item style="padding-top: 30px">
            <el-button type="primary" @click="updateUserInfo">提交修改</el-button>
            <el-button type="primary" @click="isRePassword=!isRePassword">重置密码</el-button>
          </el-form-item>
        </el-form>
        <el-form :label-width="150" size="large" v-if="isRePassword" :model="UserData" :rules="rules">
          <el-form-item prop="old_pwd" label="原密码 :">
            <el-input style="width: 40%" :prefix-icon="Lock" placeholder="请输入原密码" v-model="UserData.old_pwd"></el-input>
          </el-form-item>
          <el-form-item prop="new_pwd" label="新密码 :">
            <el-input style="width: 40%" :prefix-icon="Lock" placeholder="请输入密码" v-model="UserData.new_pwd"></el-input>
          </el-form-item>
          <el-form-item prop="re_pwd" label="确认密码 :">
            <el-input style="width: 40%" :prefix-icon="Lock" placeholder="请再次输入密码" v-model="UserData.re_pwd"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword">确认重置</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span=12>
        <div style="padding-bottom: 30px;padding-top: 10px;font-size: 25px">头像</div>
        <img width="200px" :src="userInfo.userPic?userInfo.userPic:avatar">
        <br/>
        <el-button type="primary" :icon="Plus" size="large">
          选择图片
        </el-button>
        <el-button type="success" :icon="Upload" size="large">
          上传头像
        </el-button>
      </el-col>
    </el-row>
  </el-card>
</template>

<style scoped>
.header{
  width: 1850px;
}
</style>