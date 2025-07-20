<script setup lang="ts">
import { reactive, ref } from 'vue'
import { getCustomerPageListAPI, deleteCustomerAPI } from '@/api/customer'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserInfoStore } from '@/store'

// ------ .d.ts 属性类型接口 ------
interface customer {
  id: number
  name: string
  openid: string
  phone: string
  gender: number
  pic: string
  createTime: string
}

// ------ 数据 ------
let userInfoStore = useUserInfoStore()
// 当前页的客户列表
const customerList = ref<customer[]>([])
// 带查询的分页参数
const pageData = reactive({
  name: '',
  page: 1,
  pageSize: 6,
  total: 0
})

// ------ 方法 ------

// 页面初始化
const init = async () => {
  const { data: res } = await getCustomerPageListAPI({
    page: pageData.page,
    pageSize: pageData.pageSize,
    name: pageData.name
  })
  console.log(res)
  console.log('客户列表')
  console.log(res.data)
  customerList.value = res.data.records
  pageData.total = res.data.total
}

init() // 页面初始化

// 监听翻页和每页显示数量的变化
const handleCurrentChange = (val: number) => {
  pageData.page = val
  init()
}

const handleSizeChange = (val: number) => {
  pageData.pageSize = val
  init()
}

// 修改客户(路径传参，到update页面后，根据id查询客户信息，回显到表单中)
const router = useRouter()
const update_btn = (row: any) => {
  console.log('要修改的行数据')
  console.log(row)
  router.push({
    name: 'customer_update',
    query: {
      id: row.id
    }
  })
}

// 删除客户
const delete_btn = (row: any) => {
  console.log('要删除的行数据')
  console.log(row)
  ElMessageBox.confirm('该操作会永久删除客户，是否继续？', 'Warning', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(async () => {
      console.log('要删除的行数据')
      console.log(row)
      await deleteCustomerAPI(row.id)
      // 删除后刷新页面，更新数据
      init()
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除'
      })
    })
}
</script>

<template>
  <el-card>
    <div class="horizontal">
      <el-input
        size="large"
        class="input"
        v-model="pageData.name"
        placeholder="请输入想查询的客户名"
      />
      <el-button size="large" class="btn" round type="danger" @click="init()">查询客户</el-button>
<!--      <el-button size="large" class="btn" type="danger" @click="router.push('/customer/add')">-->
<!--        <el-icon style="font-size: 15px; margin-right: 10px">-->
<!--          <Plus />-->
<!--        </el-icon>-->
<!--        添加客户-->
<!--      </el-button>-->
    </div>
    <el-table :data="customerList" stripe>
      <el-table-column prop="name" label="姓名" align="center" />
      <el-table-column prop="openid" label="账号" align="center" />
      <el-table-column prop="phone" label="手机号" width="120px" align="center" />
      <el-table-column
        prop="gender"
        label="性别"
        align="center"
        :formatter="(row) => (row.gender === 1 ? '男' : '女')"
      />
      <el-table-column prop="pic" label="头像" align="center">
        <template #default="scope">
          <img v-if="scope.row.pic" :src="scope.row.pic" alt="" />
          <img v-else src="/src/assets/image/user_default.png" alt="" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="120px" align="center" />
      <el-table-column label="操作" width="200px" align="center">
        <template #default="scope">
          <el-button
            @click="update_btn(scope.row)"
            type="danger"
            :disabled="userInfoStore.userInfo?.account.includes('admin') ? false : true"
          >
            修改
          </el-button>
          <el-button
            @click="delete_btn(scope.row)"
            type="danger"
            :disabled="userInfoStore.userInfo?.account.includes('admin') ? false : true"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description=" 没有数据" />
      </template>
    </el-table>

    <el-pagination
      class="page"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pageData.total"
      :page-sizes="[2, 4, 6, 8]"
      v-model:current-page="pageData.page"
      v-model:page-size="pageData.pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </el-card>
</template>

<style lang="less" scoped>
// element-plus的样式修改
.el-table {
  width: 90%;
  height: 500px;
  margin: 3rem auto;
  text-align: center;
  border: 1px solid #e4e4e4;
}

:deep(.el-table tr) {
  font-size: 12px;
}

.el-button {
  width: 45px;
  font-size: 12px;
}

.el-pagination {
  justify-content: center;
}

// 自定义样式
.horizontal {
  display: flex;
  justify-content: flex-start; /* 改为从左侧开始排列 */
  align-items: center;        /* 垂直居中（保持不变） */
  margin: 0 80px;             /* 保留外边距（根据需求调整） */
  gap: 16px;                  /* 可选：添加子元素间距（替代 space-around） */

  .input {
    width: 240px;
  }

  .btn {
    width: 120px;
    margin-left: 100px;
  }
}

img {
  width: 50px;
  height: 50px;
  border-radius: 10px;
}
</style>
