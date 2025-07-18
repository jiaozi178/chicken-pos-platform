<template>
  <Navbar title="个人中心" />
  <view class="page">
    <!-- 1、个人信息 -->
    <view class="my_info">
      <view class="info_bg"></view>
      <view class="info_content">
        <!-- 头像部分 -->
        <view class="head">
          <image class="head_image" :src="getImageUrl(user.pic)"></image>
          <view class="vip_badge">VIP</view>
        </view>
        <!-- 姓名、性别及手机号 -->
        <view class="phone_name">
          <!-- 姓名 -->
          <view class="name">
            <text class="name_text">{{ user.name }}</text>
            <image v-if="user.gender === 0" class="name_type" src="../../static/icon/girl.png"></image>
            <image v-else class="name_type" src="../../static/icon/boy.png"></image>
          </view>
          <!-- 电话号 -->
          <view class="phone">
            <image class="phone_icon" src="../../static/icon/phone.png"></image>
            <text class="phone_text">{{ user.phone }}</text>
          </view>
        </view>
        <view class="edit_btn" @click="goMyself">
          <image class="edit_icon" src="../../static/icon/edit.png"></image>
        </view>
      </view>
    </view>

    <!-- 2、功能入口 -->
    <view class="function_grid">
      <view class="grid_item" @click="goAddress">
        <view class="icon_wrap">
          <image class="icon" src="../../static/icon/address.png"></image>
        </view>
        <text class="grid_text">地址管理</text>
      </view>
      <view class="grid_item" @click="goHistory">
        <view class="icon_wrap">
          <image class="icon" src="../../static/icon/history.png"></image>
        </view>
        <text class="grid_text">历史订单</text>
      </view>
      <view class="grid_item" @click="goMyself">
        <view class="icon_wrap">
          <image class="icon" src="../../static/icon/setting.png"></image>
        </view>
        <text class="grid_text">信息设置</text>
      </view>
    </view>

    <!-- 3、最近订单 -->
    <view class="history_content">
      <view class="section_header">
        <text class="title">最近订单</text>
        <text class="view_all" @click="goHistory">查看全部</text>
      </view>
      
      <view
        class="history_item"
        v-for="(item, index) in historyOrders"
        :key="index"
        @click="toOrderDetail(item.id as number)"
      >
        <view class="item_header">
          <text class="order_number">订单号：{{ item.number }}</text>
          <text class="order_status" :class="`status-${item.status}`">{{ statusList[item.status as number].name }}</text>
        </view>
        
        <view class="item_body">
          <scroll-view class="scroll_container" scroll-x>
            <view v-for="(dish, index) in item.orderDetailList" :key="index" class="dish_image">
              <image :src="getImageUrl(dish.pic ?? '')" />
            </view>
          </scroll-view>
          
          <view class="order_info">
            <view class="order_time">
              <image class="time_icon" src="../../static/icon/time.png"></image>
              {{ item.orderTime }}
            </view>
            <view class="order_price">￥{{ item.amount }}</view>
          </view>
        </view>
        
        <view class="item_footer">
          <view class="btn reorder" @click.stop="reOrder(item.id as number)">再来一单</view>
          <view class="btn urge" v-if="item.status === 2" @click.stop="pushOrder(item.id as number)">
            催单
          </view>
        </view>
      </view>
      
    </view>
  </view>
  
  <!-- 催单massageBox -->
  <pushMsg ref="childComp"></pushMsg>
</template>

<script lang="ts" setup>
import pushMsg from '../../components/message/pushMsg.vue'
import {ref, reactive} from 'vue'
import {onLoad, onShow} from '@dcloudio/uni-app'
import {useUserStore} from '@/stores/modules/user'
import {getUserInfoAPI} from '@/api/user'
import {getOrderPageAPI, reOrderAPI, urgeOrderAPI} from '@/api/order'
import {cleanCartAPI} from '@/api/cart'
import type {OrderPageDTO, OrderVO} from '@/types/order'
import { getImageUrl } from '@/utils/imageUrl'
import Navbar from '@/components/navbar/Navbar.vue';

const userStore = useUserStore()
const childComp: any = ref(null)

const statusList = [
  {
    status: 0,
    name: '全部订单',
  },
  {
    status: 1,
    name: '待付款',
  },
  {
    status: 2,
    name: '待接单',
  },
  {
    status: 3,
    name: '已接单',
  },
  {
    status: 4,
    name: '派送中',
  },
  {
    status: 5,
    name: '已完成',
  },
  {
    status: 6,
    name: '已取消',
  },
]

const user = reactive({
  id: userStore.profile!.id,
  name: '',
  gender: 1,
  phone: '未设置',
  pic: '',
})
const historyOrders = ref<OrderVO[]>([])
const orderDTO = ref<OrderPageDTO>({
  page: 1,
  pageSize: 10,
})
const total = ref(0)

onLoad(async (options) => {
  console.log('options', options)
  console.log('userStore', userStore.profile)
  const res = await getUserInfo(user.id)
  // 获取所有订单信息
  await getOrderPage()
})

onShow(async () => {
  console.log('页面 onShow 触发！')
  await getUserInfo(user.id)
  await getOrderPage()
})

const getUserInfo = async (id: number) => {
  const res = await getUserInfoAPI(id)
  console.log('用户信息', res)
  user.name = res.data.name as string
  user.gender = res.data.gender ?? 1 // 之前没设置就默认男士
  user.phone = res.data.phone as string
  user.pic = res.data.pic as string
}

const getOrderPage = async () => {
  console.log('orderDTO', orderDTO.value)
  const res = await getOrderPageAPI(orderDTO.value)
  historyOrders.value = res.data.records
  total.value = res.data.total
}

// 再来一单
const reOrder = async (id: number) => {
  console.log('再来一单', id)
  // 菜品批量加入购物车之前，要先清空购物车，避免批量加入购物车后数据并不完全一样
  await cleanCartAPI()
  // 再来一单会将当前订单的菜品批量加入购物车，跳转到订单页面后，购物车将高亮显示
  await reOrderAPI(id as number)

  uni.redirectTo({
    url: '/pages/order/order',
  })
}

// 催单
const pushOrder = async (id: number) => {
  console.log('催单', id)
  await urgeOrderAPI(id)
  childComp.value.openPopup()
}



const toOrderDetail = (id: number) => {
  uni.navigateTo({
    url: '/pages/orderDetail/orderDetail?orderId=' + id,
  })
}

const goAddress = () => {
  uni.navigateTo({
    url: '/pages/address/address',
  })
}
const goHistory = () => {
  uni.navigateTo({
    url: '/pages/history/history',
  })
}
const goMyself = () => {
  uni.navigateTo({
    url: '/pages/updateMy/updateMy',
  })
}

</script>

<style lang="less" scoped>

.page {
  min-height: 100vh;
  padding-bottom: 40rpx;
  padding-top: 20rpx;
  background: linear-gradient(180deg, #FFF9E6 30%, #f8f9fa 100%);
}

/* ===== 个人信息区域 ===== */
.my_info {
  position: relative;
  height: 280rpx;
  margin: 0 30rpx 30rpx;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 10rpx 30rpx rgba(255, 156, 16, 0.15);
  
  .info_bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #FF9C10 0%, #FFCE76 100%);
    opacity: 0.95;
  }
  
  .info_content {
    position: relative;
    display: flex;
    align-items: center;
    padding: 40rpx;
    height: 100%;
    z-index: 2;
    
    .head {
      position: relative;
      width: 160rpx;
      height: 160rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .head_image {
        width: 140rpx;
        height: 140rpx;
        border-radius: 50%;
        background-color: #fff;
        border: 4rpx solid rgba(255, 255, 255, 0.5);
        box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
        object-fit: cover;
      }
      
      .vip_badge {
        position: absolute;
        bottom: 0;
        right: 0;
        background: linear-gradient(90deg, #FFD700, #FFA500);
        color: #fff;
        font-size: 20rpx;
        font-weight: bold;
        padding: 4rpx 12rpx;
        border-radius: 20rpx;
        transform: scale(0.9);
      }
    }
    
    .phone_name {
      flex: 1;
      margin-left: 32rpx;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .name {
        display: flex;
        align-items: center;
        margin-bottom: 16rpx;
        
        .name_text {
          font-size: 40rpx;
          font-weight: bold;
          color: #fff;
          margin-right: 16rpx;
          text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
        }
        
        .name_type {
          width: 36rpx;
          height: 36rpx;
        }
      }
      
      .phone {
        display: flex;
        align-items: center;
        
        .phone_icon {
          width: 32rpx;
          height: 32rpx;
          margin-right: 10rpx;
          filter: brightness(0) invert(1);
        }
        
        .phone_text {
          font-size: 30rpx;
          color: rgba(255, 255, 255, 0.9);
        }
      }
    }
    
    .edit_btn {
      width: 60rpx;
      height: 60rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;
      
      &:active {
        background: rgba(255, 255, 255, 0.3);
        transform: scale(0.95);
      }
      
      .edit_icon {
        width: 32rpx;
        height: 32rpx;
        filter: brightness(0) invert(1);
      }
    }
  }
}

/* ===== 功能入口区域 ===== */
.function_grid {
  display: flex;
  justify-content: space-around;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx 0;
  margin: 0 30rpx 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.03);
  
  .grid_item {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .icon_wrap {
      width: 90rpx;
      height: 90rpx;
      border-radius: 50%;
      background: #FFF8E8;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 16rpx;
      transition: all 0.3s;
      
      .icon {
        width: 50rpx;
        height: 50rpx;
      }
    }
    
    .grid_text {
      font-size: 28rpx;
      color: #666;
    }
    
    &:active .icon_wrap {
      transform: scale(0.95);
      background: #FFEBC1;
    }
  }
}

/* ===== 最近订单区域 ===== */
.history_content {
  padding: 0 30rpx;
  
  .section_header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    
    .title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      position: relative;
      padding-left: 20rpx;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 8rpx;
        height: 36rpx;
        background: #FF9C10;
        border-radius: 4rpx;
      }
    }
    
    .view_all {
      font-size: 28rpx;
      color: #999;
      display: flex;
      align-items: center;
      
      &::after {
        content: '›';
        margin-left: 8rpx;
        font-size: 36rpx;
      }
    }
  }
  
  .history_item {
    background-color: #fff;
    border-radius: 24rpx;
    overflow: hidden;
    margin-bottom: 30rpx;
    box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.03);
    transition: all 0.3s;
    
    &:active {
      transform: translateY(-4rpx);
      box-shadow: 0 10rpx 25rpx rgba(0, 0, 0, 0.08);
    }
    
    .item_header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24rpx 30rpx;
      background: #fafafa;
      border-bottom: 1rpx solid #f0f0f0;
      
      .order_number {
        font-size: 26rpx;
        color: #666;
      }
      
      .order_status {
        font-size: 26rpx;
        font-weight: 500;
        padding: 6rpx 16rpx;
        border-radius: 30rpx;
        
        &.status-2 { /* 待接单 */
          background: #e6f7ff;
          color: #1890ff;
        }
        &.status-3, &.status-4 { /* 已接单/派送中 */
          background: #fff7e6;
          color: #fa8c16;
        }
        &.status-5 { /* 已完成 */
          background: #f6ffed;
          color: #52c41a;
        }
        &.status-6 { /* 已取消 */
          background: #f9f9f9;
          color: #999;
        }
      }
    }
    
    .item_body {
      padding: 30rpx;
      
      .scroll_container {
        width: 100%;
        height: 120rpx;
        margin-bottom: 30rpx;
        
        .dish_image {
          width: 120rpx;
          height: 120rpx;
          display: inline-block;
          margin-right: 20rpx;
          border-radius: 16rpx;
          overflow: hidden;
          box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
          
          image {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }
      
      .order_info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-top: 1rpx dashed #eee;
        padding-top: 24rpx;
        
        .order_time {
          display: flex;
          align-items: center;
          font-size: 26rpx;
          color: #999;
          
          .time_icon {
            width: 28rpx;
            height: 28rpx;
            margin-right: 10rpx;
          }
        }
        
        .order_price {
          font-size: 32rpx;
          font-weight: bold;
          color: #FF9C10;
        }
      }
    }
    
    .item_footer {
      display: flex;
      justify-content: flex-end;
      padding: 0 30rpx 30rpx;
      gap: 20rpx;
      
      .btn {
        height: 64rpx;
        line-height: 64rpx;
        padding: 0 36rpx;
        border-radius: 40rpx;
        font-size: 28rpx;
        font-weight: 500;
        transition: all 0.3s;
        
        &.reorder {
          background: #fff;
          border: 1rpx solid #FF9C10;
          color: #FF9C10;
          
          &:active {
            background: #fff8e6;
          }
        }
        
        &.urge {
          background: #FF9C10;
          color: #fff;
          box-shadow: 0 4rpx 12rpx rgba(255, 156, 16, 0.3);
          
          &:active {
            background: #e68c0e;
          }
        }
      }
    }
  }

}
</style>
