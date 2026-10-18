<template>
    <!-- PV 折线图容器 -->
    <div ref="chartRef" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, watch, onBeforeUnmount } from 'vue'
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

// 初始化折线图
function initLineChat() {
    if (!chartRef.value || !props.value) return

    // 主题变化时需要销毁后重新初始化
    if (myChart) myChart.dispose()
    myChart = echarts.init(chartRef.value, isDark.value ? 'dark' : undefined)

    // 从 props.value 中获取日期集合和 pv 访问量集合
    const pvDates = props.value.pvDates
    const pvCounts = props.value.pvCounts

    const option = {
        backgroundColor: 'transparent',
        // 移动到折点上，显示数值
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 30, bottom: 30 },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: pvDates // x 轴数据
        },
        yAxis: {
            type: 'value',
            minInterval: 1
        },
        series: [
            {
                data: pvCounts, // 设置 pv 访问量
                type: 'line',
                smooth: true,
                symbolSize: 7,
                itemStyle: { color: '#3b82f6' },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(59, 130, 246, 0.35)' },
                        { offset: 1, color: 'rgba(59, 130, 246, 0)' },
                    ])
                },
            }
        ]
    };
    myChart.setOption(option);
}

// 数据或主题变化时重新渲染
watch([() => props.value, isDark], () => initLineChat())

onBeforeUnmount(() => myChart && myChart.dispose())
</script>
