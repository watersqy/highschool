import request from "@/utils/request.js";

//获得教室列表
export const classroomListService=(params)=>{
    //在pinia定义的响应数据不需要.value
    return  request.get("/classroom/list",{params:params})
}

//添加教室
export const classroomAddService=(classroomData)=>{
    return  request.post("/classroom/add",classroomData);
}

//编辑教室信息
export const classroomUpdateService=(classroomData)=>{
    return  request.put("/classroom/update",classroomData)
}

//删除教室
export const classroomDeleteService=(id)=>{
    return  request.delete(`/classroom/delete?id=`+id)
}