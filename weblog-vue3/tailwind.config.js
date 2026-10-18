/** @type {import('tailwindcss').Config} */
export default {
  // 通过 <html class="dark"> 手动切换深色模式（见 src/composables/theme.js）
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
    "./node_modules/flowbite/**/*.js"
  ],
  theme: {
    extend: {},
  },
  plugins: [
    require('flowbite/plugin')
  ]
}
