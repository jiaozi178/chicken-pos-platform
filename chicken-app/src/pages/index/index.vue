<template>
	<view class="container">
		<view class="banner">
			<swiper
        class="banner-swiper"
        indicator-dots="true"
        autoplay="true"
        circular="true"
        interval="3000"
        duration="500"
      >
          <swiper-item>
              <image src="/static/images/index/bg1.png" class="bg"></image>
          </swiper-item>
          <swiper-item>
              <image src="/static/images/index/bg2.png" class="bg"></image>
          </swiper-item>
          <swiper-item>
              <image src="/static/images/index/bg3.png" class="bg"></image>
          </swiper-item>
          <swiper-item>
              <image src="/static/images/index/bg4.png" class="bg"></image>
          </swiper-item>
      </swiper>
		</view>
		<view class="content">

			<view class="entrance" >
				<image src="/static/images/index/buy_type.png" class="entrance-img" mode="widthFix"></image>
				<view class="item entrance-takein" @click="takein"></view>
				<view class="item entrance-takeout" @tap="takeout"></view>
			</view>

			<view class="info">
				<view class="integral_section">
					<view class="top">
						<text class="title">我的积分</text>
						<text class="value">404</text>
					</view>
					<view class="bottom">
						进入积分商城兑换正新券及小吃好礼
						<view class="iconfont iconarrow-right"></view>
					</view>
				</view>
				<view class="qrcode_section">
					<image src="/static/images/index/qrcode.png"></image>
					<text>会员码</text>
				</view>
			</view>
			
			<view class="member-news">
				<view class="header">
					<view class="title">会员新鲜事</view>
					<view class="iconfont iconRightbutton"></view>
				</view>
				<view class="list">
					<view class="item">
						<image src="/static/images/index/vip.jpg" mode="widthFix"></image>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
const takeout = () =>{
  uni.switchTab({
    url: '/pages/order/order',
  })
}

const takein = () => {
  uni.redirectTo({
    url: '/pages/address/address',
  })
}
</script>

<style lang="scss" scoped>
$font-size-lg: 36rpx;
$font-size-sm: 24rpx;
$font-size-base: 28rpx;
$text-color-base: #333;
$text-color-assist: #999;
$color-primary: #f39800;
$box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);


.banner {
	position: relative;
	width: 100%;
	height: 500rpx;
  
	.banner-swiper {
    width: 100%;
    height: 500rpx;
  }
	.bg {
		width: 100%;
		height: 500rpx;
	}
	
}

.content {
	padding: 0 30rpx;
}

.entrance {
	position: relative;
	margin-top: -80rpx;
	margin-bottom: 30rpx;
	border-radius: 10rpx;
	background-color: #ffffff;
	box-shadow: $box-shadow;
	padding: 0;
	display: flex;
	align-items: center;
	justify-content: center;
	
	.entrance-img {
        width: 100%;
		height: auto;
        display: block;
        border-radius: 10rpx;
    }
    .item {
        position: absolute;
        top: 0;
        width: 50%;
        height: 100%;
        z-index: 2;
    }
    .entrance-takein {
        left: 0;
    }
    .entrance-takeout {
        right: 0;
    }
}

.info {
	position: relative;
	margin-bottom: 30rpx;
	border-radius: 10rpx;
	background-color: #ffffff;
	box-shadow: $box-shadow;
	padding: 30rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	
	.integral_section {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
		
		.top {
			display: flex;
			align-items: center;
			
			.title {
				color: $text-color-base;
				font-size: $font-size-base;
				margin-right: 10rpx;
			}
			.value {
				font-size: 44rpx;
				font-weight: bold;
			}
		}
		
		.bottom {
			font-size: $font-size-sm;
			color: $text-color-assist;
			display: flex;
			align-items: center;
		}
	}
	
	.qrcode_section {
		color: $color-primary;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		font-size: $font-size-sm;
		
		image {
			width: 40rpx;
			height: 40rpx;
			margin-bottom: 10rpx;
		}
	}
}


.member-news {
	width: 100%;
	margin-bottom: 30rpx;
	.header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 0;
		
		.title {
			font-size: $font-size-lg;
			font-weight: bold;
		}
		
		.iconfont {
			font-size: 52rpx;
			color: $text-color-assist;
		}
	}
	
	.list {
		width: 100%;
		display: flex;
		flex-direction: column;
		
		.item {
			width: 100%;
			position: relative;
			
			image {
				width: 100%;
				display: block;
				border-radius: 8rpx;
			}
		}
	}
}
</style>
