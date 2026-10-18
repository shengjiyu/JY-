import axios from "axios";
import { getToken } from "@/composables/cookie"
import { showMessage } from '@/composables/util'
import { useUserStore } from '@/stores/user'

// 创建 Axios 实例
const instance = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API, // 你的 API 基础 URL
    timeout: 15000, // 请求超时时间（上传图片等场景 7s 偏短）
})

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
    const token = getToken()

    // 当 token 不为空时，添加请求头, key 为 Authorization，value 值的前缀为 'Bearer '
    // 注意：不要在控制台打印 Token，避免泄露
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }

    return config;
}, function (error) {
    return Promise.reject(error)
});

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数
    return response.data
}, function (error) {
    // 超出 2xx 范围的状态码，或网络错误 / 超时（此时 error.response 为 undefined）
    const response = error.response

    if (!response) {
        const isTimeout = error.code === 'ECONNABORTED' || /timeout/i.test(error.message || '')
        showMessage(isTimeout ? '请求超时，请稍后重试' : '网络异常，请检查网络或稍后重试', 'error')
        return Promise.reject(error)
    }

    // 状态码 401：Token 失效，退出登录
    if (response.status == 401) {
        let userStore = useUserStore()
        userStore.logout()
        // 刷新页面
        location.reload()
    }

    // 若后台有错误提示就用提示文字，默认提示为 '请求失败'
    let errorMsg = (response.data && response.data.message) || '请求失败'
    showMessage(errorMsg, 'error')

    return Promise.reject(error)
})

// 暴露出去
export default instance;
