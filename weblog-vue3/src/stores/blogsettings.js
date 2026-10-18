import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getBlogSettingsDetail } from '@/api/frontend/blogsettings'

export const useBlogSettingsStore = defineStore('blogsettings', () => {
  // 博客设置信息
  const blogSettings = ref({})
  // 是否已加载过（避免每次路由切换都请求一次）
  let loaded = false
  let pending = null

  /**
   * 获取博客设置信息（前台公开接口，访客无需登录也能拿到头像、名称等）
   * @param {boolean} force 是否强制刷新（如后台修改设置后）
   */
  function getBlogSettings(force = false) {
    if (pending) return pending
    if (loaded && !force) return Promise.resolve(blogSettings.value)

    pending = getBlogSettingsDetail().then(res => {
      if (res.success) {
        blogSettings.value = res.data || {}
        loaded = true
      }
      return blogSettings.value
    }).catch(error => {
      // 错误提示已在 axios 拦截器中统一处理，这里静默失败
      console.error('获取博客设置失败:', error)
    }).finally(() => {
      pending = null
    })
    return pending
  }

  return { blogSettings, getBlogSettings }
})
