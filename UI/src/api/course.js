import request from "@/utils/request.js";

//获得用户课表
export const courseTableService=()=>{
    //在pinia定义的响应数据不需要.value
    return  request.get("/course/courseTable")
}

//用户课程列表查询
export const userCourseListService=(params)=>{
    return  request.get("/course/user",{params:params})
}

//课程列表查询
export const courseListService=(params)=>{
    return  request.get("/course/list",{params:params})
}

//添加课程
export const courseAddService=(courseData)=>{
    return  request.post("/course/add",courseData)
}

//重新排课
export const courseArrangeService=()=>{
    return  request.post("/course/arrange")
}

//编辑课程
export const courseUpdateService=(courseData)=>{
    return  request.put("/course/update",courseData)
}

//删除课程
export const courseDeleteService=(id)=>{
    return  request.delete("/course/delete?id="+id)
}

//
export const classListService=(params)=>{
    return  request.get("/course/classList",{params:params})
}

//
export const classTableService=(classId)=>{
    return  request.get("/course/class/"+classId)
}

//
export const teacherTableService=(teacherId)=>{
    return  request.get("/course/teacher/"+teacherId)
}

