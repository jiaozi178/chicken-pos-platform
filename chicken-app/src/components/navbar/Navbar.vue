<template>
  <!-- 固定定位的导航条容器 -->
  <view class="custom-navbar">
    <!-- 导航条主体 -->
    <view class="navbar" :style="{ paddingTop: (safeAreaInsets?.top * 2) + 'rpx' }">
      <!-- 左侧区域（可选返回按钮） -->
      <view class="left" v-if="showBack" @click="handleBack">
        <image src="@/static/icon/back.png" class="back-icon"></image>
      </view>
      
      <!-- 中间标题区域 -->
      <view class="title">
        <text class="title-text">{{ title }}</text>
      </view>
      
      <!-- 右侧区域（可选其他图标） -->
      <view class="right">
        <slot name="right"></slot>
      </view>
    </view>
  </view>
  <view :style="`height: ${placeholderHeight}rpx;`"></view>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// 定义组件属性
const props = defineProps({
  title: {
    type: String,
    default: '' // 默认标题
  },
  showBack: {
    type: Boolean,
    default: false // 是否显示返回按钮
  }
});

// 获取安全区域信息
const NAVBAR_HEIGHT = 100;
const { safeAreaInsets = { top: 0 }  } = uni.getSystemInfoSync();
const placeholderHeight = NAVBAR_HEIGHT + (safeAreaInsets?.top || 0) * 2;

// 返回按钮处理函数
const handleBack = () => {
  try {
    uni.navigateBack({
      fail() {
        uni.switchTab({ url: '/pages/index/index' });
      }
    });
  } catch (e) {
    uni.switchTab({ url: '/pages/index/index' });
  }
};
</script>

<style lang="less" scoped>
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 999;
}
.navbar {
  background-image: url('@/static/images/navigator_bg1.png');
  background-size: cover;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20rpx;

    .left, .right {
    width: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .back-icon {
    width: 48rpx;
    height: 48rpx;
  }

  .title {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    
    .title-text {
      color: #fff;
      font-size: 36rpx;
      font-weight: bold;
      text-align: center;
    }
  }
}

</style>