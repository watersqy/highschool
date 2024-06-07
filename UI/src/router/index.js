import {createRouter,createWebHistory} from 'vue-router'


const routes = [
    {
        path:'/login',
        component:()=>import ('@/views/Login.vue')
    },
    {
        path:'/home',
        component:()=>import('@/views/Home.vue'),
        //redirect:'/home/info',
        children:[
            {path:'courseTable',component:()=>import('@/views/CourseTable.vue')},
            {path:'course',component:()=>import('@/views/CourseCheck.vue')},
            {path:'courseManage',component:()=>import('@/views/CourseManage.vue')},
            {path:'classroom',component:()=>import('@/views/Classroom.vue')},
            {path:'teacher',component:()=>import('@/views/Teacher.vue')},
            {path:'student',component:()=>import('@/views/Student.vue')},
            {path:'root',component:()=>import('@/views/UserManager.vue')},
            {path:'search-class',component:()=>import('@/views/ClassCheck.vue')},
            {path:'search-teacher',component:()=>import('@/views/TeacherCheck.vue')},
            {path:'info',component:()=>import('@/views/UserInfo.vue')}
        ]
    }
]

const router=createRouter({
    history:createWebHistory(),
    routes:routes,
})

export default router;