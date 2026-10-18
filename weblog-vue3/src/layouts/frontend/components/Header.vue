<template>
    <header class="sticky top-0 z-30">
        <nav
            class="bg-white/80 backdrop-blur-md border-b border-gray-200 dark:bg-gray-900/80 dark:border-gray-800 transition-colors">
            <div class="max-w-screen-xl flex flex-wrap items-center justify-between mx-auto px-4 py-3">
                <!-- 博客 LOGO 、博客名称 -->
                <a @click="router.push('/')" class="flex items-center cursor-pointer group">
                    <img v-if="blogSettingsStore.blogSettings.logo" :src="blogSettingsStore.blogSettings.logo"
                        class="h-9 w-9 mr-3 rounded-full object-cover ring-2 ring-white dark:ring-gray-800 shadow transition-transform group-hover:rotate-12"
                        alt="博客 Logo" />
                    <span
                        class="self-center text-xl md:text-2xl font-semibold whitespace-nowrap bg-gradient-to-r from-blue-600 to-cyan-500 bg-clip-text text-transparent dark:from-blue-400 dark:to-cyan-300">
                        {{ blogSettingsStore.blogSettings.name }}</span>
                </a>

                <div class="flex items-center gap-1 md:order-2">
                    <!-- 搜索框（桌面端） -->
                    <div class="relative hidden mr-2 md:block">
                        <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                            <svg class="w-4 h-4 text-gray-500 dark:text-gray-400" aria-hidden="true"
                                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                    stroke-width="2" d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
                            </svg>
                        </div>
                        <label for="search-navbar" class="sr-only">搜索</label>
                        <input type="text" id="search-navbar"
                            class="block w-56 p-2 pl-10 text-sm text-gray-900 border border-gray-200 rounded-full bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-800 dark:border-gray-700 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                            placeholder="请输入搜索关键词...">
                    </div>

                    <!-- 深色 / 浅色模式切换 -->
                    <ThemeToggle />

                    <!-- 登录 -->
                    <button type="button" v-if="!isLogined" @click="router.push('/login')"
                        class="ml-1 px-4 py-1.5 text-sm font-medium text-white rounded-full bg-blue-600 hover:bg-blue-700 transition-colors dark:bg-blue-500 dark:hover:bg-blue-600">
                        登录
                    </button>
                    <!-- 已经登录，展示用户头像 -->
                    <button id="dropdownDefaultButton" data-dropdown-toggle="dropdown" v-else aria-label="用户菜单"
                        class="ml-1 focus:ring-4 focus:ring-blue-300 rounded-full inline-flex items-center dark:focus:ring-blue-800"
                        type="button">
                        <img class="w-8 h-8 rounded-full object-cover" :src="blogSettingsStore.blogSettings.avatar"
                            alt="用户头像">
                    </button>

                    <!-- Dropdown menu -->
                    <div id="dropdown"
                        class="z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow-lg dark:bg-gray-700">
                        <ul class="py-2 text-sm text-gray-700 dark:text-gray-200" aria-labelledby="dropdownDefaultButton">
                            <li>
                                <a @click="router.push('/admin/index')"
                                    class="cursor-pointer block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white">
                                    进入后台
                                </a>
                            </li>
                            <li>
                                <a data-modal-target="popup-modal" data-modal-toggle="popup-modal"
                                    class="cursor-pointer block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white">
                                    退出登录
                                </a>
                            </li>
                        </ul>
                    </div>

                    <!-- 移动端菜单按钮 -->
                    <button data-collapse-toggle="navbar-search" type="button"
                        class="inline-flex items-center p-2 w-9 h-9 justify-center text-sm text-gray-500 rounded-lg md:hidden hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:text-gray-400 dark:hover:bg-gray-700 dark:focus:ring-gray-600"
                        aria-controls="navbar-search" aria-expanded="false">
                        <span class="sr-only">打开菜单</span>
                        <svg class="w-5 h-5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                            viewBox="0 0 17 14">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M1 1h15M1 7h15M1 13h15" />
                        </svg>
                    </button>
                </div>

                <!-- 导航菜单 -->
                <div class="items-center justify-between hidden w-full md:flex md:w-auto md:order-1" id="navbar-search">
                    <ul
                        class="flex flex-col p-4 md:p-0 mt-4 font-medium border border-gray-100 rounded-lg bg-gray-50 md:flex-row md:space-x-2 md:mt-0 md:border-0 md:bg-transparent dark:bg-gray-800 md:dark:bg-transparent dark:border-gray-700">
                        <li v-for="item in navs" :key="item.path">
                            <a @click="router.push(item.path)" :aria-current="isActive(item.path) ? 'page' : undefined"
                                class="cursor-pointer block py-2 px-3 rounded-full transition-colors"
                                :class="isActive(item.path)
                                    ? 'text-blue-600 bg-blue-50 dark:text-blue-400 dark:bg-blue-500/10'
                                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100 dark:text-gray-300 dark:hover:text-white dark:hover:bg-gray-800'">
                                {{ item.name }}
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <!-- 退出登录 -->
    <div id="popup-modal" tabindex="-1"
        class="fixed top-0 left-0 right-0 z-50 hidden p-4 overflow-x-hidden overflow-y-auto md:inset-0 h-[calc(100%-1rem)] max-h-full">
        <div class="relative w-full max-w-md max-h-full">
            <div class="relative bg-white rounded-lg shadow dark:bg-gray-700">
                <button type="button"
                    class="absolute top-3 right-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ml-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
                    data-modal-hide="popup-modal">
                    <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                        viewBox="0 0 14 14">
                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                    </svg>
                    <span class="sr-only">关闭</span>
                </button>
                <div class="p-6 text-center">
                    <svg class="mx-auto mb-4 text-gray-400 w-12 h-12 dark:text-gray-200" aria-hidden="true"
                        xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M10 11V6m0 8h.01M19 10a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
                    </svg>
                    <h3 class="mb-5 text-lg font-normal text-gray-500 dark:text-gray-400">是否确定退出登录?</h3>
                    <button @click="logout" data-modal-hide="popup-modal" type="button"
                        class="text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 dark:focus:ring-red-800 font-medium rounded-lg text-sm inline-flex items-center px-5 py-2.5 text-center mr-2">
                        确定
                    </button>
                    <button data-modal-hide="popup-modal" type="button"
                        class="text-gray-500 bg-white hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-gray-200 rounded-lg border border-gray-200 text-sm font-medium px-5 py-2.5 hover:text-gray-900 focus:z-10 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-500 dark:hover:text-white dark:hover:bg-gray-600 dark:focus:ring-gray-600">
                        取消</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { initCollapses, initDropdowns, initModals } from 'flowbite'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { showMessage } from '@/composables/util'
import ThemeToggle from '@/components/ThemeToggle.vue'

// 初始化 flowbit 相关组件
onMounted(() => {
    initCollapses();
    initDropdowns();
    initModals();
})

const router = useRouter()
const route = useRoute()

// 导航菜单
const navs = [
    { name: '首页', path: '/' },
    { name: '分类', path: '/category/list' },
    { name: '标签', path: '/tag/list' },
    { name: '归档', path: '/archive/list' },
]

// 当前路由是否匹配菜单（响应式，路由变化时自动更新）
const isActive = (path) => route.path === path

// 引入博客设置信息 store
const blogSettingsStore = useBlogSettingsStore()

// 是否登录，通过 userStore 中的 userInfo 对象是否有数据来判断
const userStore = useUserStore()
const isLogined = ref(Object.keys(userStore.userInfo || {}).length > 0)

// 退出登录
const logout = () => {
    userStore.logout()
    // 标记为未登录
    isLogined.value = false
    showMessage('退出登录成功')
}
</script>
