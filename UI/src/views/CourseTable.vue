<script setup>

import {ref} from "vue";

const lessons = ref([
  "08.00-9.35",
  "9.55-11.30",
  "13.30-15.05",
  "15.25-17.00",
  "18:30-20:10"
]);
const courses = ref([
      ["","","","",""],
      ["","","","",""],
      ["","","","",""],
      ["","","","",""],
      ["","","","",""],
]);

const courseInfo = ref([
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
  ["","","","",""],
]);

const digital2Chinese=(num) =>{
  const character = ["一", "二", "三", "四", "五"];
  return character[num];
}

import {courseTableService} from "@/api/course.js";

const courseList=async ()=>{
  let result = await courseTableService();
  let time=0,day=0,info="";//上课时间，星期几，信息
  for(let c in result.data){//遍历响应数据的值
    const item=result.data[c];
    time=parseInt(item.courseTime) - 1;//获取上课时间
    day=Math.floor(time/5);//计算得出星期位置
    info=item.courseName;//课程名
    courses.value[day][time%5]=info;//为数组赋值
    info=item.teacher+"-"+item.place;//老师+地点
    courseInfo.value[day][time%5]=info;
  }
}
courseList();
</script>

<template>
  <el-container>
    <div class="table-wrapper">
      <div class="table-container">
        <table>
          <thead>
          <tr>
            <th>时间</th>
            <th v-for="(weekNum, weekIndex) in courses.length">
              {{ "周" + digital2Chinese(weekIndex, "week") }}
            </th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(lesson, lessonIndex) in lessons">
            <td>
              <p>{{ "第" + digital2Chinese(lessonIndex) + "节" }}</p>
              <p class="period">{{ lesson }}</p>
            </td>

            <td v-for="(course,courseIndex) in courses">
              <p>{{courses[courseIndex][lessonIndex]}}</p>
              <p>{{courseInfo[courseIndex][lessonIndex]}}</p>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </el-container>


</template>

<style scoped>
.table-wrapper {
  width: 100%;
  height: 100%;
  overflow: auto;
}
.table-container {
  font-size: 20px;
  margin: 20px;

  table {
    table-layout: fixed;
    width: 100%;
    word-wrap: break-word;
    word-break: break-all;
    border-collapse: collapse;

    thead {
      th {
        border: 1px solid gainsboro;
        line-height: 25px;
        font-weight: normal;
        font-size: 18px;
        color: grey;
      }
    }

    tbody {

      td {
        border: 1px solid gainsboro;
        font-size: 18px;
        color: black;
        line-height: 13px;
      }
    }

    th,
    td {
      width: 60px;
      padding: 14px 3px;
      font-size: 12px;
      text-align: center;
    }

    tr td:first-child {
      color: grey;

      .period {
        font-size: 15px;
      }
    }
  }
}
</style>