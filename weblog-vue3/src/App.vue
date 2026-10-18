<template>
	<!-- 设置语言为中文 -->
	<el-config-provider :locale="locale">
		<router-view></router-view>
	</el-config-provider>
</template>

<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
// 初始化主题（读取本地存储 / 系统偏好，并同步到 <html> 的 class 上）
import { useTheme } from '@/composables/theme'

const locale = zhCn
useTheme()
</script>
