<template>
    <!-- 深色 / 浅色模式切换按钮 -->
    <button type="button" @click="toggleTheme" :aria-label="isDark ? '切换到浅色模式' : '切换到深色模式'"
        :title="isDark ? '切换到浅色模式' : '切换到深色模式'" :aria-pressed="isDark"
        class="theme-toggle inline-flex items-center justify-center rounded-full transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400"
        :class="sizeClass">
        <!-- 太阳（当前为深色时展示，点击切回浅色） -->
        <svg v-if="isDark" class="w-5 h-5 text-amber-300" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
            fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="4" />
            <path stroke-linecap="round"
                d="M12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32 1.41 1.41M2 12h2m16 0h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
        </svg>
        <!-- 月亮（当前为浅色时展示，点击切到深色） -->
        <svg v-else class="w-5 h-5 text-slate-600" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79Z" />
        </svg>
    </button>
</template>

<script setup>
import { computed } from 'vue'
import { useTheme } from '@/composables/theme'

const props = defineProps({
    // 按钮尺寸：md 用于前台导航栏，lg 用于后台 64px 高的顶栏
    size: { type: String, default: 'md' },
})

const { isDark, toggleTheme } = useTheme()

const sizeClass = computed(() => [
    props.size === 'lg' ? 'w-[42px] h-[42px]' : 'w-9 h-9',
    'hover:bg-gray-100 dark:hover:bg-gray-700',
])
</script>

<style scoped>
.theme-toggle svg {
    transition: transform 0.4s ease;
}

.theme-toggle:hover svg {
    transform: rotate(20deg);
}
</style>
