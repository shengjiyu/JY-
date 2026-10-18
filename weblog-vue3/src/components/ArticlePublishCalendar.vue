<template>
    <!-- 日历热点图容器 -->
    <div ref="chartRef" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, watch, onBeforeUnmount } from 'vue'
import { format, subMonths } from 'date-fns'
import { useTheme } from '@/composables/theme'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Object, // 类型为对象
        default: null // 默认为 null
    }
})

const { isDark } = useTheme()
const chartRef = ref(null)
let myChart = null

// 当前日期
const currentDate = new Date();
// 半年前
const sixMonthsAgo = subMonths(currentDate, 6)

// 格式化后的开始、结束日期
const startDate = format(sixMonthsAgo, 'yyyy-MM-dd')
const endDate = format(currentDate, 'yyyy-MM-dd')

// 初始化日历热点图
function initCalendar() {
    if (!chartRef.value) return

    // 每次重新组装数据（原实现使用模块级数组不断 push，数据会重复累加）
    const map = props.value || {}
    const myData = Object.keys(map).map(key => [key, map[key]])

    // 主题变化时需要销毁后重新初始化
    if (myChart) myChart.dispose()
    myChart = echarts.init(chartRef.value, isDark.value ? 'dark' : undefined)

    const option = {
        backgroundColor: 'transparent',
        tooltip: {
            formatter: (params) => `${params.value[0]}：发布 ${params.value[1]} 篇`
        },
        visualMap: {
            show: false,
            min: 0,
            max: 10
        },
        calendar: { // 日历显示的范围：开始日期 - 结束日期
            range: [startDate, endDate],
            itemStyle: {
                color: isDark.value ? '#161b22' : '#ffffff',
                borderColor: isDark.value ? '#0f1115' : '#e5e7eb',
            },
            splitLine: { lineStyle: { color: isDark.value ? '#4b5563' : '#9ca3af' } },
            dayLabel: { color: isDark.value ? '#9ca3af' : '#6b7280' },
            monthLabel: { color: isDark.value ? '#9ca3af' : '#6b7280' },
            yearLabel: { show: false },
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: myData
        },
        gradientColor: [ // 自定义热点颜色，参考了 GitHub 代码提交的颜色
            isDark.value ? '#161b22' : '#ebedf0',
            '#40c463',
            '#30a14e',
            '#216e39',
        ]
    };

    myChart.setOption(option);
}

// 数据或主题变化时重新渲染
watch([() => props.value, isDark], () => initCalendar())

onBeforeUnmount(() => myChart && myChart.dispose())
</script>
