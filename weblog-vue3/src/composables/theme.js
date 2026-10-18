import { useDark, useToggle } from '@vueuse/core'

/**
 * 深色 / 浅色模式
 * - 通过在 <html> 上切换 `dark` class 实现（Tailwind darkMode: 'class'、Element Plus 暗黑 CSS 变量均基于此）
 * - 选择结果持久化到 localStorage（key: weblog-theme），未选择过时跟随系统
 * - index.html 中有同 key 的内联脚本，在首屏渲染前就加上 class，避免闪白
 */
export const THEME_STORAGE_KEY = 'weblog-theme'

// 模块级单例，保证全站共享同一份状态
const isDark = useDark({
    storageKey: THEME_STORAGE_KEY,
    selector: 'html',
    attribute: 'class',
    valueDark: 'dark',
    valueLight: '',
})

const toggle = useToggle(isDark)

/**
 * 切换主题；浏览器支持 View Transitions API 时，以点击位置为圆心做扩散动画
 * @param {MouseEvent} [event]
 */
function toggleTheme(event) {
    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    if (!document.startViewTransition || !event || reduceMotion) {
        toggle()
        return
    }

    const x = event.clientX
    const y = event.clientY
    const endRadius = Math.hypot(Math.max(x, innerWidth - x), Math.max(y, innerHeight - y))

    const transition = document.startViewTransition(() => toggle())
    transition.ready.then(() => {
        document.documentElement.animate(
            { clipPath: [`circle(0px at ${x}px ${y}px)`, `circle(${endRadius}px at ${x}px ${y}px)`] },
            { duration: 450, easing: 'ease-in-out', pseudoElement: '::view-transition-new(root)' }
        )
    })
}

export function useTheme() {
    return { isDark, toggleTheme }
}
