import request from '@/utils/request.js'
//用户
//注册
export const userRegisterService=(UserDate)=>{
    const params=new URLSearchParams()
    for(let key in UserDate){
        params.append(key,UserDate[key])
    }
    return  request.post('/user/register',params);
}

//登录
export const userLoginService=(UserDate)=>{
    const params=new URLSearchParams()
    params.append("username",UserDate["username"]);
    params.append("password",UserDate["password"]);
    return  request.post('/user/login',params);
}

//获得用户信息
export const userInfoService=()=>{
    return  request.get("/user/userInfo");
}

export const userRootService=(UserDate)=>{
    const params=new URLSearchParams()
    params.append("username",UserDate["username"]);
    params.append("root",UserDate["root"]);
    return  request.put("/user/root",params);
}

//更新用户信息
export const userUpdateService=(UserDate)=>{
    return  request.put("/user/update",UserDate)
}

//重置密码
export const rePasswordService=(UserDate)=>{
    return request.put('/user/updatePwd',UserDate);
}

//获得用户名单
export const usersListService=(params)=>{
    return  request.get("/user/list",{params:params})
}


//老师
//获得老师名单
export const teacherListService=(params)=>{
    return  request.get("/user/teacherList", {params:params})
}

//
export const teacherDepartmentListService=()=>{
    return  request.get("/user/teacher/department")
}

export const teacherAddService=(teacherData)=>{
    return  request.post("/user/teacher/add",teacherData);
}

export const teacherUpdateService=(teacherData)=>{
    return  request.put("/user/teacher/update",teacherData);
}

export const teacherDeleteService=(id)=>{
    return  request.delete('/user/teacher/delete?id='+id)
}

//学生
//获得学生名单
export const studentListService=(params)=>{
    return  request.get("/user/studentList",{params:params})
}

export const studentDepartmentListService=()=>{
    return  request.get("/user/student/department")
}

export const speListService=()=>{
    return request.get("/user/spe")
}

export const classListService=()=>{
    return request.get("/user/class")
}

export const studentAddService=(studentData)=>{
    return  request.post("/user/student/add",studentData);
}

export const studentUpdateService=(studentData)=>{
    return  request.put("/user/student/update",studentData);
}

export const studentDeleteService=(id)=>{
    return  request.delete('/user/student/delete?id='+id)
}