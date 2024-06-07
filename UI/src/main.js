import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from '@/router'
import {createPinia} from 'pinia'
import locale from 'element-plus/dist/locale/zh-cn.js'
import {createPersistedState} from "pinia-persistedstate-plugin";

const pinia=createPinia();
const app = createApp(App)
const persist= createPersistedState();
pinia.use(persist)
app.use(router)
app.use(ElementPlus,locale)
app.use(pinia)

//生命周期 钩子函数 mounted
app.mount('#app')
