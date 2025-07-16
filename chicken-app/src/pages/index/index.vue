<template>
  <view class="container">
    <swiper
      :autoplay="true"
      :circular="true"
      :indicator-dots="true"
      indicator-color="#ffffff"
      indicator-active-color="#22ccff"
      :interval="2000"
    >
      <swiper-item>
        <image mode="widthFix" src="../../static/images/swp4.png" />
      </swiper-item>
      <swiper-item>
        <image mode="widthFix" src="../../static/images/swp6.png" />
      </swiper-item>
      <swiper-item>
        <image mode="widthFix" src="../../static/images/swp5.png" />
      </swiper-item>
    </swiper>
    <view class="title" @tap="toOrderPage">点击开始点餐</view>
  </view>
</template>

<script setup lang="ts">
const toOrderPage = () => {
  uni.navigateTo({
    url: '/pages/order/order',
  })
}
</script>

<style lang="less" scoped>
swiper {
  width: 100%;
  height: calc(100vw * 360 / 810);
  image {
    //图片自适应屏幕大小
    width: 100%;
  }
}

.page {
	background-color: #f7e293;
}

.container {
  //margin: 20rpx 80rpx 300rpx 80rpx;
  // margin-bottom: 100rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f7e293;
}

image {
  margin-top: 40rpx;
  width: 300rpx;
  height: 300rpx;
  text-align: center;
}

.title {
  width: 300rpx;
  height: 80rpx;
  background-color: #e42218;
  border-radius: 20rpx;
  font-size: 30rpx;
  color: #fff;
  text-align: center;
  line-height: 80rpx;
  margin-top: 20rpx;
}
</style>
