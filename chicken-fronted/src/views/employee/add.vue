<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { addEmployeeAPI } from '@/api/employee'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

// ------ 数据 ------
const formLabelWidth = '60px'
const form = reactive({
  id: 0,
  name: '',
  account: '',
  password: '',
  phone: '',
  age: '',
  gender: '',
  pic: '',
})
const genders = [
  {
    value: 1,
    label: '男',
  },
  {
    value: 0,
    label: '女',
  }
]
const inputRef1 = ref<HTMLInputElement | null>(null)
const addRef = ref()

// 表单校验
const checkAge = (rule: any, value: string, callback: (error?: Error) => void) => {
  if (value === '' || value === undefined) {
    callback(new Error('请输入年龄'));
  } else if (!/^\d+$/.test(value)) {
    callback(new Error('年龄必须为数字'));
  } else {
    const age = parseInt(value);
    if (age < 3) {
      callback(new Error('年龄不能小于3岁'));
    } else if (age > 99) {
      callback(new Error('年龄不能大于99岁'));
    } else {
      callback();
    }
  }
}
const rules = {
  name: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { min: 2, message: '姓名长度不能少于2个字符', trigger: 'blur' },
    { max: 20, message: '姓名长度不能超过20个字符', trigger: 'blur' },
  ],
  account: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { pattern: /^[a-zA-Z0-9]{1,10}$/, message: '用户名必须是1-10的字母数字', trigger: 'blur' }
  ],
  password: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { pattern: /^\S{6,15}$/, message: '密码必须是6-15的非空字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  age: [
    { required: true, trigger: 'blur', message: '不能为空' },
    { validator: checkAge, trigger: 'blur' }
  ],
  gender: [
    { required: true, trigger: 'blur', message: '不能为空' },
  ],
}


// ------ 方法 ------

const router = useRouter()
const route = useRoute()

// 选择图片->点击事件->让选择框出现
const chooseImg = () => {
  // 模拟点击input框的行为，通过点击按钮触发另一个input框的事件，移花接木
  // 否则直接调用input框，其样式不太好改    input框中有个inputRef1属性，让inputRef1去click模拟点击行为
  if (inputRef1.value) {
    inputRef1.value.click() // 当input框的type是file时，click()方法会触发选择文件的对话框(弹出文件管理器)
  }
}

// 在文件管理器中选择图片后触发的改变事件：预览
const onFileChange1 = (e: Event) => {
  // 获取用户选择的文件列表（伪数组）
  console.log(e)
  const target = e.target as HTMLInputElement
  const files = target.files;
  if (files && files.length > 0) {
    // 选择了图片
    console.log(files[0])
    // 文件 -> base64字符串  (可以发给后台)
    // 1. 创建 FileReader 对象
    const fr = new FileReader()
    // 2. 调用 readAsDataURL 函数，读取文件内容
    fr.readAsDataURL(files[0])
    // 3. 监听 fr 的 onload 事件，文件转为base64字符串成功后会触发该事件
    fr.onload = () => {
      // 4. 通过 e.target.result 获取到读取的结果，值是字符串（base64 格式的字符串）
      form.pic = fr.result as string
      console.log('avatar')
      console.log(form.pic)
    }
  }
}

// 添加员工信息后提交（只有管理员才能对其他员工进行修改，否则普通员工只能对自己进行修改）
const submit = async () => {
  try {
    const valid = await addRef.value.validate();
    if (valid) {
      console.log('submit')
      console.log(form)
      // 在这里执行表单提交操作
      const res = await addEmployeeAPI(form)
      if (res.data.code !== 0) {
        console.log('新增员工失败！')
        return false
      }
      // 然后进行 消息提示，页面跳转 等操作
      ElMessage({
        message: '新增员工成功',
        type: 'success',
      })
      router.push({
        path: '/employee',
      })
    } else {
      console.log('form not valid!');
      return false;
    }
  } catch (error) {
    console.error('执行过程中失败:', error);
  }
}
// 取消修改
const cancel = () => {
  router.push({
    path: '/employee',
  })
}

const init = async () => {
  console.log(route.query)
}

init()
</script>

<template>
  <h1>添加员工页</h1>
  <el-card>
    <el-form :model="form" :rules="rules" ref="addRef">
      <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
        <el-input v-model="form.name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="账号" :label-width="formLabelWidth" prop="account">
        <el-input v-model="form.account" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
        <el-input v-model="form.password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="电话" :label-width="formLabelWidth" prop="phone">
        <el-input v-model="form.phone" autocomplete="off" />
      </el-form-item>
      <el-form-item label="年龄" :label-width="formLabelWidth" prop="age">
        <el-input v-model="form.age" autocomplete="off" />
      </el-form-item>
      <el-form-item label="性别" :label-width="formLabelWidth" prop="gender">
        <el-select clearable v-model="form.gender" placeholder="选择分类类型">
          <el-option v-for="item in genders" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <!-- <el-input v-model="form.gender" autocomplete="off" /> -->
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
