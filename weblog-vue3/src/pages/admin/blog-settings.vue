<template>
    <div>
        <!-- 卡片组件， shadow="never" 指定 card 卡片组件没有阴影 -->
        <el-card shadow="never">
            <el-form ref="formRef" :model="form" label-width="160px" :rules="rules">
                <el-form-item label="博客名称" prop="name">
                    <el-input v-model="form.name" clearable />
                </el-form-item>
                <el-form-item label="作者名" prop="author">
                    <el-input v-model="form.author" clearable />
                </el-form-item>
                <el-form-item label="介绍语" prop="introduction">
                    <el-input v-model="form.introduction" type="textarea" />
                </el-form-item>

                <el-form-item label="博客 LOGO" prop="logo">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleLogoChange" :auto-upload="false"
                        :show-file-list="false" accept="image/*" v-loading="logoUploading">
                        <img v-if="form.logo" :src="form.logo" class="avatar object-cover" alt="博客 LOGO" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="作者头像" prop="avatar">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleAvatarChange" :auto-upload="false"
                        :show-file-list="false" accept="image/*" v-loading="avatarUploading">
                        <img v-if="form.avatar" :src="form.avatar" class="avatar object-cover" alt="作者头像" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <!-- 开启 Github 访问 -->
                <el-form-item label="开启 GitHub 访问">
                    <el-switch v-model="isGithubChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="githubSwitchChange" />
                </el-form-item>
                <el-form-item label="GitHub 主页访问地址" v-if="isGithubChecked">
                    <el-input v-model="form.githubHomepage" clearable placeholder="请输入 GitHub 主页访问的 URL" />
                </el-form-item>

                <!-- 开启 Gitee 访问 -->
                <el-form-item label="开启 Gitee 访问">
                    <el-switch v-model="isGiteeChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="giteeSwitchChange" />
                </el-form-item>
                <el-form-item label="Gitee 主页访问地址" v-if="isGiteeChecked">
                    <el-input v-model="form.giteeHomepage" clearable placeholder="请输入 Gitee 主页访问的 URL" />
                </el-form-item>

                <!-- 开启知乎访问 -->
                <el-form-item label="开启知乎访问">
                    <el-switch v-model="isZhihuChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="zhihuSwitchChange" />
                </el-form-item>
                <el-form-item label="知乎主页访问地址" v-if="isZhihuChecked">
                    <el-input v-model="form.zhihuHomepage" clearable placeholder="请输入知乎主页访问的 URL" />
                </el-form-item>

                <!-- 开启 CSDN 访问 -->
                <el-form-item label="开启 CSDN 访问">
                    <el-switch v-model="isCSDNChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="csdnSwitchChange" />
                </el-form-item>
                <el-form-item label="CSDN 主页访问地址" v-if="isCSDNChecked">
                    <el-input v-model="form.csdnHomepage" clearable placeholder="请输入 CSDN 主页访问的 URL" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :loading="btnLoading" @click="onSubmit">保存</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>
<script setup>
import { reactive, ref } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'
import { getBlogSettingsDetail, updateBlogSettings } from '@/api/admin/blogsettings'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { uploadFile } from '@/api/admin/file'
import { showMessage } from '@/composables/util'
// 是否开启 GitHub
const isGithubChecked = ref(false)
// 是否开启 Gitee
const isGiteeChecked = ref(false)
// 是否开启知乎
const isZhihuChecked = ref(false)
// 是否开启 CSDN
const isCSDNChecked = ref(false)
const btnLoading = ref(false)

// 表单引用
const formRef = ref(null)

// 保存当前博客设置
const onSubmit = () => {
    // 先验证 form 表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }

        // 显示保存按钮 loading
        btnLoading.value = true
        updateBlogSettings(form).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            // 重新渲染页面中的信息，并同步刷新全局博客设置（前台头像、Logo 等）
            initBlogSettings()
            useBlogSettingsStore().getBlogSettings(true)
            showMessage('保存成功')
        }).finally(() => btnLoading.value = false) // 隐藏保存按钮 loading
    })
}
// 表单对象
const form = reactive({
    name: '',
    author: '',
    logo: '',
    avatar: '',
    introduction: '',
    githubHomepage: '',
    giteeHomepage: '',
    zhihuHomepage: '',
    csdnHomepage: '',
})

// 规则校验
const rules = {
    name: [{ required: true, message: '请输入博客名称', trigger: 'blur' }],
    author: [{ required: true, message: '请输入作者名', trigger: 'blur' }],
    logo: [{ required: true, message: '请上传博客 LOGO', trigger: 'blur' }],
    avatar: [{ required: true, message: '请上传作者头像', trigger: 'blur' }],
    introduction: [{ required: true, message: '请输入介绍语', trigger: 'blur' }],
}
// 允许上传的图片类型与大小（与后端校验保持一致）
const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/x-icon', 'image/vnd.microsoft.icon', 'image/bmp']
const MAX_IMAGE_SIZE_MB = 10

// 上传中状态
const logoUploading = ref(false)
const avatarUploading = ref(false)

// 通用图片上传：前端预校验 -> 上传 -> 回填链接
function uploadImage(file, field, loadingRef) {
    const raw = file.raw
    if (!raw) return
    if (!ALLOWED_IMAGE_TYPES.includes(raw.type)) {
        showMessage('仅支持 jpg、png、gif、webp、ico、bmp 格式的图片', 'warning')
        return
    }
    if (raw.size / 1024 / 1024 > MAX_IMAGE_SIZE_MB) {
        showMessage(`图片大小不能超过 ${MAX_IMAGE_SIZE_MB}MB`, 'warning')
        return
    }

    let formData = new FormData()
    formData.append('file', raw)
    loadingRef.value = true
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            showMessage(e.message || '上传失败', 'error')
            return
        }
        form[field] = e.data.url
        showMessage('上传成功')
    }).catch(() => {
        // 网络错误等已在 axios 拦截器中提示
    }).finally(() => loadingRef.value = false)
}

// 上传 logo 图片
const handleLogoChange = (file) => uploadImage(file, 'logo', logoUploading)

// 上传作者头像
const handleAvatarChange = (file) => uploadImage(file, 'avatar', avatarUploading)

// 监听 Github Switch 改变事件
const githubSwitchChange = (checked) => {
    if (checked == false) {
        form.githubHomepage = ''
    }
}

// 监听 Gitee Switch 改变事件
const giteeSwitchChange = (checked) => {
    if (checked == false) {
        form.giteeHomepage = ''
    }
}

// 监听知乎 Switch 改变事件
const zhihuSwitchChange = (checked) => {
    if (checked == false) {
        form.zhihuHomepage = ''
    }
}

// 监听 CSDN Switch 改变事件
const csdnSwitchChange = (checked) => {
    if (checked == false) {
        form.csdnHomepage = ''
    }
}
// 初始化博客设置数据，并渲染到页面上
function initBlogSettings() {
    // 请求后台接口
    getBlogSettingsDetail().then((e) => {
        if (e.success == true) {
            // 设置表单数据
            form.name = e.data.name
            form.author = e.data.author
            form.logo = e.data.logo
            form.avatar = e.data.avatar
            form.introduction = e.data.introduction

            // 第三方平台信息设置，先判断后端返回平台链接是否为空，若不为空，则将 switch 组件置为选中状态，并设置表单对应数据
            if (e.data.githubHomepage) {
                isGithubChecked.value = true
                form.githubHomepage = e.data.githubHomepage
            }

            if (e.data.giteeHomepage) {
                isGiteeChecked.value = true
                form.giteeHomepage = e.data.giteeHomepage
            }

            if (e.data.zhihuHomepage) {
                isZhihuChecked.value = true
                form.zhihuHomepage = e.data.zhihuHomepage
            }

            if (e.data.csdnHomepage) {
                isCSDNChecked.value = true
                form.csdnHomepage = e.data.csdnHomepage
            }
        }
    })
}
// 手动调用一下初始化方法
initBlogSettings()
</script>
<style>
/* 解决 textarea :focus 状态下，边框消失的问题 */
.el-textarea__inner:focus {
    outline: 0 !important;
    box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset !important;
}

.avatar-uploader .avatar {
    width: 100px;
    height: 100px;
    display: block;
}

/* 解决 textarea :focus 状态下，边框消失的问题 */
.el-textarea__inner:focus {
    outline: 0 !important;
    box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset !important;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
}
</style>
