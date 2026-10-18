<template>
    <div class="bg-white py-5 flex items-center justify-center text-sm text-gray-500 shadow-none dark:bg-gray-900 dark:text-gray-400 transition-colors">
        <!-- Copyright 版权信息 -->
        Copyright © {{ year }} {{ blogSettingsStore.blogSettings.name || 'Weblog' }}. All rights reserved.
    </div>
</template>

<script setup>
import { useBlogSettingsStore } from '@/stores/blogsettings'

const blogSettingsStore = useBlogSettingsStore()
const year = new Date().getFullYear()
</script>
