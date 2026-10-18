<template>
    <footer class="mt-10 border-t border-gray-200 bg-white dark:bg-gray-900 dark:border-gray-800 transition-colors">
        <div
            class="w-full mx-auto max-w-screen-xl py-6 px-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <span class="text-sm text-gray-500 dark:text-gray-400">
                © {{ year }} {{ blogSettingsStore.blogSettings.author || blogSettingsStore.blogSettings.name || 'Weblog' }}. All Rights
                Reserved.
            </span>
            <!-- 备案号：在 .env.production 中配置 VITE_APP_ICP，未配置则不展示 -->
            <a v-if="icp" href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer"
                class="text-sm text-gray-500 hover:underline dark:text-gray-400">{{ icp }}</a>
        </div>
    </footer>
</template>

<script setup>
import { useBlogSettingsStore } from '@/stores/blogsettings'

const blogSettingsStore = useBlogSettingsStore()
const year = new Date().getFullYear()
const icp = import.meta.env.VITE_APP_ICP
</script>
