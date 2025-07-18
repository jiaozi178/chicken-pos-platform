<template>
  <Navbar title="修改信息" :show-back="true" />
  
  <view class="update-container">
    <form @submit="submit" class="update-form">
      <!-- 头像上传 -->
      <view class="avatar-upload" @click="picChange">
        <view class="avatar-container">
          <image 
            v-if="!user.pic" 
            class="avatar-image" 
            src="../../static/images/user_default.png" 
            mode="aspectFill"
          />
          <image 
            v-else 
            class="avatar-image" 
            :src="getImageUrl(user.pic)" 
            mode="aspectFill"
          />
        </view>
        <text class="upload-text">点击更换头像</text>
      </view>
      
      <!-- 性别选择 -->
      <view class="form-section gender-section">
        <text class="section-title">性别</text>
        <view class="gender-options">
          <view 
            v-for="(item, index) in items" 
            :key="index" 
            class="gender-option"
            :class="{active: item.value === user.gender}"
            @click="genderChange(item.value)"
          >
            <radio 
              class="gender-radio"
              color="#FF9C10"
              :value="String(item.value)"
              :checked="item.value === user.gender"
            />
            <text class="gender-label">{{ item.name }}</text>
          </view>
        </view>
      </view>
      
      <!-- 昵称输入 -->
      <view class="form-section">
        <text class="section-title">昵称</text>
        <input 
          class="form-input"
          placeholder="请输入昵称"
          placeholder-class="placeholder"
          v-model="user.name"
          maxlength="12"
        />
      </view>
      
      <!-- 手机号输入 -->
      <view class="form-section">
        <text class="section-title">手机号</text>
        <input 
          class="form-input"
          placeholder="请输入手机号"
          placeholder-class="placeholder"
          v-model="user.phone"
          type="number"
          maxlength="11"
        />
      </view>
      
      <!-- 提交按钮 -->
      <button form-type="submit" class="submit-btn">保存修改</button>
    </form>
  </view>
</template>

<script lang="ts" setup>
import {ref, reactive} from 'vue'
import {onLoad} from '@dcloudio/uni-app'
import {useUserStore} from '@/stores/modules/user'
import {getUserInfoAPI, updateUserAPI} from '@/api/user'
import { getImageUrl } from '@/utils/imageUrl'
import Navbar from '@/components/navbar/Navbar.vue'

const userStore = useUserStore()

const user = reactive({
  id: userStore.profile!.id,
  name: '',
  gender: 1,
  phone: '未设置',
  pic: '',
})
const items = [
  {
    value: 1,
    name: '男士',
  },
  {
    value: 0,
    name: '女士',
  },
]

onLoad(async () => {
  console.log('userStore', userStore.profile)
  await getUserInfo(user.id)
})

const getUserInfo = async (id: number) => {
  const res = await getUserInfoAPI(id)
  console.log('用户信息', res)
  user.name = res.data.name as string
  user.gender = res.data.gender ?? 1 // 之前没设置就默认男士
  user.phone = res.data.phone as string
  user.pic = res.data.pic as string
  console.log('user', user)
}

const picChange = () => {
  console.log('picChange')
  uni.chooseMedia({
    count: 1,
    mediaType: ['image'],
    sourceType: ['album', 'camera'],
    maxDuration: 15,
    camera: 'back',
    // 获取图片成功的回调
    success: (res) => {
      console.log(res)
      // 解构获得图片的临时路径
      const {tempFilePath} = res.tempFiles[0]
      let base64String = ''
      wx.getFileSystemManager().readFile({
        filePath: tempFilePath,
        encoding: 'base64',
        // 图片转base64成功的回调
        success: (res) => {
          base64String = 'data:image/png;base64,' + res.data
          console.log(base64String)
          user.pic = base64String
        },
      })
    },
  })
}
const genderChange = (val: number) => {
  user.gender = val
  console.log(user.gender)
}

const validateForm = (): boolean => {
  let valid = true
  // 验证昵称
  if (!user.name) {
    uni.showToast({
      title: '昵称不能为空',
      icon: 'none',
    })
    valid = false
  }
  // 验证手机号格式
  const phonePattern = /^1[3-9]\d{9}$/
  if (!user.phone) {
    uni.showToast({
      title: '手机号不能为空',
      icon: 'none',
    })
    valid = false
  } else if (!phonePattern.test(user.phone)) {
    uni.showToast({
      title: '手机号格式不正确',
      icon: 'none',
    })
    valid = false
  }
  return valid
}

const submit = async () => {
  console.log('submit', user)
  if (!validateForm()) {
    return
  }
  const res = await updateUserAPI(user)
  if (res.code === 0) {
    uni.showToast({
      title: '修改成功',
      icon: 'success',
    })
    uni.switchTab({
      url: '/pages/my/my',
    })
  }
}
</script>

<style lang="less" scoped>
.update-container {
  min-height: 100vh;
  padding: 30rpx;
  background: linear-gradient(180deg, #FFF9E6 30%, #f8f9fa 100%);
}

.update-form {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.03);
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
  
  .avatar-container {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 20rpx;
    
    .avatar-image {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      border: 4rpx solid #FFF8E8;
      box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
    }
    

  }
  
  .upload-text {
    font-size: 26rpx;
    color: #999;
  }
}

.form-section {
  margin-bottom: 40rpx;
  padding-bottom: 30rpx;
  border-bottom: 1rpx dashed #f0f0f0;
  
  .section-title {
    display: block;
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }
  
  .form-input {
    width: 100%;
    height: 80rpx;
    font-size: 28rpx;
    color: #333;
    padding: 0 10rpx;
    border-radius: 8rpx;
    background: #FFFCF5;
  }
  
  .placeholder {
    font-size: 26rpx;
    color: #999;
  }
}

.gender-section {
  .gender-options {
    display: flex;
    margin-top: 20rpx;
    
    .gender-option {
      display: flex;
      align-items: center;
      margin-right: 60rpx;
      
      &.active {
        .gender-label {
          color: #FF9C10;
        }
      }
      
      .gender-radio {
        transform: scale(0.8);
        margin-right: 8rpx;
      }
      
      .gender-label {
        font-size: 28rpx;
        color: #666;
      }
    }
  }
}

.submit-btn {
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 88rpx;
  background: linear-gradient(to right, #FFB74D, #FF9C10);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 60rpx;
  box-shadow: 0 8rpx 20rpx rgba(255, 156, 16, 0.3);
  transition: all 0.3s;
  
  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }
}
</style>
