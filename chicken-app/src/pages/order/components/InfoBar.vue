<template>
  <!-- 餐厅简介弹窗 -->
  <view v-if="showInfo" class="info fade-in-out"  :style="{ top: navBarHeight + 'rpx' }">
    <view class="info1">
      <view class="status">{{ status === true ? '营业中' : '打烊中' }}</view>
      <uni-icons custom-prefix="iconfont" type="icon-qian" size="15"></uni-icons>
      <text class="price">配送费6元</text>
    </view>
    <view class="info2">
      <text class="address">餐厅地址：长沙市岳麓区牛耳科教</text>
      <uni-icons @click="phone" custom-prefix="iconfont" type="icon-dianhua" size="20"></uni-icons>
    </view>
  </view>
</template>

<script setup lang="ts">
import {getStatusAPI} from '@/api/shop'
import {onLoad} from '@dcloudio/uni-app'
import {ref} from 'vue'

// 添加导航栏高度获取
const systemInfo = uni.getSystemInfoSync()
const navBarHeight = (systemInfo?.statusBarHeight || 0) * 2// 状态栏高度 + 导航栏高度

// 控制简介显示
const showInfo = ref(false)

// 店铺营业状态
const status = ref(true)

// 暴露方法给父组件
const showRestaurantInfo = () => {
  showInfo.value = true;
  setTimeout(() => {
    showInfo.value = false;
  }, 3000);
}

// 暴露方法给父组件
defineExpose({
  showRestaurantInfo
});

// 页面加载
onLoad(async () => {
  const res = await getStatusAPI()
  console.log('店铺状态---------', res)
  status.value = res.data === 1 ? true : false
})

const phone = () => {
  uni.makePhoneCall({phoneNumber: '1999'})
}
</script>

<style lang="less" scoped>
.info {
  position: fixed;
  z-index: 1000;
  top: 100rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  height: 120rpx;
  margin: 16rpx 20rpx;
  color: #333;
  font-size: 28rpx;
  border-radius: 10rpx;
  background-color: #fff;
  box-shadow: 0 5rpx 10rpx 5rpx rgba(0,0,0,0.1);
  opacity: 0; /* 初始透明 */
  
  .status {
    display: inline-block;
    margin: 10rpx 20rpx;
    padding: 5rpx;
    border-radius: 5rpx;
    font-size: 25rpx;
    background-color: #0d8;
    color: #fff;
  }
  .price {
    margin: 10rpx;
    font-size: 28rpx;
    color: #666;
  }
  .info2 {
    position: relative;
    .address {
      margin: 10rpx 20rpx;
      font-size: 25rpx;
      color: #666;
    }
    uni-icons {
      position: absolute;
      right: 30rpx;
    }
  }
}

/* 弹出动画 */
.fade-in-out {
  animation: fadeInOut 3s ease-in-out forwards;
}

@keyframes fadeInOut {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(-20rpx);
  }
  10% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  90% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-20rpx);
  }
}
</style>