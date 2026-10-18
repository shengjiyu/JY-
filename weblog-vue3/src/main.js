// Element Plus 暗黑模式 CSS 变量（html.dark 时生效）
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/main.css'
import 'animate.css';
import 'nprogress/nprogress.css'

import { createApp } from 'vue'
// 引入全局状态管理 Pinia
import pinia from '@/stores'
import App from '@/App.vue'
// 导入路由
import router from '@/router'
// 导入全局路由守卫
import '@/permission'
// 图片点击放大
import 'viewerjs/dist/viewer.css'
import VueViewer from 'v-viewer'

// 导入 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 应用 Pinia（需在路由之前注册，路由守卫中会用到 store）
app.use(pinia)
// 应用路由
app.use(router)
app.use(VueViewer)
// 引入图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')
