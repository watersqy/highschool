
<script setup>
import {ref} from 'vue'
import {User,Lock} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

const isRegister = ref(false)

const UserData=ref({
  username:'',
  password:'',
  repassword:''
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
  username:[
    {required:true,message:'请输入用户名',trigger:'blur'},
    {min:4,max:16,message:'长度为4~16位非空字符',trigger:'blur' }
  ],
  password:[
    {required:true,message: '请输入密码',trigger:'blur'},
    {min:4,max:16,message:'长度为4~16位非空字符',trigger:'blur' }
  ],
  repassword:[
    {validator:checkRePassword,trigger:'blur'},
  ]
}

//调用后台接口
import {userRegisterService,userLoginService} from '@/api/user.js'

const register= async()=>{
  let result = await userRegisterService(UserData.value);//等待
  if(result.code===0){//判断状态码0为成功
    ElMessage.success(result.message?result.message:'注册成功');
    isRegister.value=false;//将界面切换回登录界面
  }
  else
    ElMessage.error('注册失败');
}

import {useTokenStore} from "@/store/token.js";
import {useRouter} from "vue-router";
const router=useRouter()
const tokenStore = useTokenStore();

const login= async()=>{
  let result = await userLoginService(UserData.value);
  if(result.code===0){
    ElMessage.success(result.message?result.message:'登录成功');
    tokenStore.setToken(result.data);
    router.push('/home');
  }
  else
    ElMessage.error('登录失败，用户名或密码错误');
}

//定义函数,清空数据模型的数据
const clearRegisterData = ()=>{
  UserData.value={
    username:'',
    password:'',
    rePassword:''
  }
}

</script>

<template>
    <div id="building">
    <div style="height: 60px; line-height: 60px; font-size: 20px; padding-left: 50px; color: white;
      background-color: rgba(0,0,0,0.5)">高校排课系统</div>
    <div style="margin: 150px auto; background-color: #fff; width: 450px; height: 380px; padding: 20px; border-radius: 10px">
      <el-row class="login-info">
        <el-col class="form">
          <!--注册-->
          <el-form ref="form" size="large" v-if="isRegister" :model="UserData" :rules="rules">
            <el-form-item>
              <h1 style="padding-left: 170px">账号激活</h1>
            </el-form-item>
            <el-form-item prop="username">
              <el-input :prefix-icon="User" placeholder="请输入用户名" clearable v-model="UserData.username"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" show-password v-model="UserData.password"></el-input>
            </el-form-item>
            <el-form-item prop="repassword">
              <el-input :prefix-icon="Lock" type="password" placeholder="请再次输入密码" show-password v-model="UserData.repassword"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button class="button" type="primary" style="min-width: 440px" auto-insert-space @click="register">
                激活账号
              </el-button>
            </el-form-item>
            <el-form-item class="is-flex">
              <el-link type="info" :underline="false" @click="isRegister=false ;clearRegisterData()">
                返回
              </el-link>
            </el-form-item>
          </el-form>

          <!--登录-->
          <el-form ref="form" size="large" autocomplete="off" v-else  :model="UserData" :rules="rules">
            <el-form-item>
              <h1 style="padding-left: 200px">登录</h1>
            </el-form-item>
            <el-form-item prop="username">
              <el-input :prefix-icon="User" placeholder="请输入用户名" clearable v-model="UserData.username"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" show-password v-model="UserData.password"></el-input>
            </el-form-item>
            <el-form-item class="is-flex">
              <div class="is-flex">
                <el-checkbox>记住我</el-checkbox>
                <el-link type="primary" :underline="false">
                  <el-col style="padding-left: 130px">忘记密码？</el-col>
                </el-link>
                <el-link style="padding-left: 50px" type="info" :underline="false" @click="isRegister=true ;clearRegisterData()">
                  激活账号
                </el-link>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button class="button" type="primary" style="min-width: 440px" auto-insert-space @click="login">
                登录
              </el-button>
            </el-form-item>
          </el-form>

        </el-col>
      </el-row>
    </div>
  </div>

</template>

<style lang="css" scoped>
#building {
  background: url("../assets/loginback.png") ;/*背景图片路径*/
  width: 100%;
  height: 100%;
  background-size: 100% 100%;
  position: fixed; /*大小随窗口变化*/
}
</style>