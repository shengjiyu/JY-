import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading, hidePageLoading } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogsettings'

// 全局路由前置守卫
router.beforeEach((to, from, next) => {
    // 展示页面加载 Loading
    showPageLoading()

    let token = getToken()

    // 博客设置（名称、Logo、头像）前后台都会用到，且为公开接口，访客未登录也需要加载
    // store 内部有缓存，只会请求一次
    useBlogSettingsStore().getBlogSettings()

    if (!token && to.path.startsWith('/admin')) {
        // 若用户想访问后台（以 /admin 为前缀的路由）
        // 未登录，则强制跳转登录页
        showMessage('请先登录', 'warning')
        next({ path: '/login' })
    } else if (token && to.path == '/login') {
        // 若用户已经登录，且重复访问登录页
        showMessage('请勿重复登录', 'warning')
        // 跳转后台首页
        next({ path: '/admin/index' })
    } else {
        next()
    }
})

// 全局路由后置守卫
router.afterEach((to, from) => {
    // 动态设置页面 Title
    let title = (to.meta.title ? to.meta.title : '') + ' - Weblog'
    document.title = title

    // 隐藏页面加载 Loading
    hidePageLoading()
})
