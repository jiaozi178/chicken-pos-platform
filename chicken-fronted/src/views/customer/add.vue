<!--这个功能有点蠢,删掉了,忽略此页-->
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { addCustomerAPI } from '@/api/customer'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

// ------ 数据 ------
const formLabelWidth = '80px'
const form = reactive({
  id: 0,
  name: '',
  openid: '',
  phone: '',
  gender: '',
  pic: '',
})

const genders = [
  {
    value: 1,
    label: '男',
  },
  {
    value: 2,
    label: '女',
  }
]

const inputRef1 = ref<HTMLInputElement | null>(null)
const addRef = ref()

// 表单校验规则
const rules = {
  name: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { min: 2, message: '姓名长度不能少于2个字符', trigger: 'blur' },
    { max: 20, message: '姓名长度不能超过20个字符', trigger: 'blur' },
  ],
  openid: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { pattern: /^[a-zA-Z0-9_-]{1,28}$/, message: 'openid格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  gender: [
    { required: true, trigger: 'blur', message: '不能为空' },
  ],
}

// ------ 方法 ------

const router = useRouter()
const route = useRoute()

// 选择图片
const chooseImg = () => {
  if (inputRef1.value) {
    inputRef1.value.click()
  }
}

// 文件选择变化处理
const onFileChange1 = (e: Event) => {
  const target = e.target as HTMLInputElement
  const files = target.files;
  if (files && files.length > 0) {
    const fr = new FileReader()
    fr.readAsDataURL(files[0])
    fr.onload = () => {
      form.pic = fr.result as string
    }
  }
}

// 提交表单
const submit = async () => {
  try {
    const valid = await addRef.value.validate();
    if (valid) {
      const res = await addCustomerAPI(form)
      if (res.data.code !== 0) {
        console.log('新增客户失败！')
        return false
      }

      ElMessage({
        message: '新增客户成功',
        type: 'success',
      })
      router.push({
        path: '/customer',
      })
    } else {
      console.log('表单验证未通过');
      return false;
    }
  } catch (error) {
    console.error('执行过程中失败:', error);
  }
}

// 取消操作
const cancel = () => {
  router.push({
    path: '/customer',
  })
}

const init = async () => {
  console.log(route.query)
}

init()
</script>

<template>
  <h1>添加客户页</h1>
  <el-card>
    <el-form :model="form" :rules="rules" ref="addRef">
      <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
        <el-input v-model="form.name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="OpenID" :label-width="formLabelWidth" prop="openid">
        <el-input v-model="form.openid" autocomplete="off" />
      </el-form-item>
      <el-form-item label="电话" :label-width="formLabelWidth" prop="phone">
        <el-input v-model="form.phone" autocomplete="off" />
      </el-form-item>
      <el-form-item label="性别" :label-width="formLabelWidth" prop="gender">
        <el-select clearable v-model="form.gender" placeholder="选择性别">
          <el-option v-for="item in genders" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="头像" :label-width="formLabelWidth" prop="pic">
        <img class="the_img" v-if="!form.pic" src="/src/assets/image/user_default.png" alt="" />
        <img class="the_img" v-else :src="form.pic" alt="" />
        <input type="file" accept="image/*" style="display: none" ref="inputRef1" @change="onFileChange1" />
        <el-button type="danger" @click="chooseImg">
          <el-icon style="font-size: 15px; margin-right: 10px;">
            <Plus />
          </el-icon>
          选择图片
        </el-button>
      </el-form-item>
    </el-form>
    <el-form-item>
      <el-button class="submit_btn" type="danger" @click="submit">添加</el-button>
      <el-button class="cancel_btn" type="info" plain @click="cancel">取消</el-button>
    </el-form-item>
  </el-card>
</template>

<style lang="less" scoped>
h1 {
  font-size: 20px;
  text-align: center;
  margin: 20px;
}

.el-form {
  margin-top: 30px;
  width: 500px;
  margin: 0 auto;
}

img {
  width: 50px;
  height: 50px;
  margin-right: 20px;
}

.submit_btn {
  width: 100px;
  height: 40px;
  margin: 30px 0 0 400px;
}

.cancel_btn {
  width: 100px;
  height: 40px;
  margin: 30px 0 0 200px;
}
</style>
