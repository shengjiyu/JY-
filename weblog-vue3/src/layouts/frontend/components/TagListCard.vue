<template>
    <div v-if="tags && tags.length > 0" class="w-full p-5 mb-4 bg-white border border-gray-200 rounded-xl shadow-sm dark:bg-gray-800 dark:border-gray-700 transition-colors">
        <!-- 标签标题 -->
        <h2 class="mb-3 flex items-center font-bold text-gray-900 dark:text-white"><span class="inline-block w-1 h-4 mr-2 rounded bg-green-500"></span>标签</h2>
        <!-- 标签列表 -->
        <span v-for="(tag, index) in tags" :key="index" @click="goTagArticleListPage(tag.id, tag.name)"
            class="inline-block mb-2 cursor-pointer bg-green-100 text-green-800 text-xs font-medium mr-2 px-2.5 py-1 rounded-full transition-colors hover:bg-green-200 hover:text-green-900 dark:bg-green-900/60 dark:text-green-300 dark:hover:bg-green-800">
            # 
            {{ tag.name }}
        </span>
    </div>
</template>

<script setup>
import { getTagList } from '@/api/frontend/tag'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
// 所有标签
const tags = ref([])
getTagList().then((res) => {
    if (res.success) {
        tags.value = res.data || []
    }
})
// 跳转标签文章列表页
const goTagArticleListPage = (id, name) => {
    // 跳转时通过 query 携带参数（标签 ID、标签名称）
    router.push({ path: '/tag/article/list', query: { id, name } })
}
</script>
