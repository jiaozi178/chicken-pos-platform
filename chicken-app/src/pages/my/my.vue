<template>
  <Navbar title="个人中心" />
  <view class="page">
    <!-- 1、个人信息 -->
    <view class="my_info">
      <!-- 头像部分 -->
      <view class="head">
        <image class="head_image" :src="getImageUrl(user.pic)"></image>
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
          <text class="phone_text">{{ user.phone }}</text>
        </view>
      </view>
    </view>
    <!-- 2、地址管理 + 历史订单 -->
    <view class="white_box">
      <view class="bottom_text" @click="goAddress">
        <image class="icon" src="../../static/icon/address.png"></image>
        <view class="text_left">地址管理</view>
        <view class="right_image">
          <image class="to_right" src="../../static/icon/toRight.png"></image>
        </view>
      </view>
      <view class="bottom_text" @click="goHistory">
        <image class="icon" src="../../static/icon/history.png"></image>
        <view class="text_left">历史订单</view>
        <view class="right_image">
          <image class="to_right" src="../../static/icon/toRight.png"></image>
        </view>
      </view>
      <view class="bottom_text" @click="goMyself">
        <image class="icon" src="../../static/icon/my.png"></image>
        <view class="text_left">信息设置</view>
        <view class="right_image">
          <image class="to_right" src="../../static/icon/toRight.png"></image>
        </view>
      </view>
    </view>
    <view class="history_content">
      <view class="title">最近订单</view>
      <view
        class="history_item"
        v-for="(item, index) in historyOrders"
        :key="index"
        @click="toOrderDetail(item.id as number)"
      >
        <view class="item_info_box">
          <view class="history_item_left">
            <view class="history_item_order_id">订单号：{{ item.number }}</view>
            <scroll-view class="scroll_container" scroll-x>
              <view v-for="(dish, index) in item.orderDetailList" :key="index" class="image_box">
                <image :src="getImageUrl(dish.pic ?? '')" />
              </view>
            </scroll-view>
            <view class="history_item_order_time">{{ item.orderTime }}</view>
          </view>
          <view class="history_item_right">
            <view class="history_item_status">{{ statusList[item.status as number].name }}</view>
            <view class="history_item_price">￥{{ item.amount }}</view>
            <view class="history_item_dish_amount">共{{ item.packAmount }}份</view>
          </view>
        </view>
        <view class="btn_box">
          <view class="history_item_reOrder" @click.stop="reOrder(item.id as number)">再来一单</view>
          <view class="history_item_push_order" v-if="item.status === 2" @click.stop="pushOrder(item.id as number)">
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
import {onLoad, onReachBottom} from '@dcloudio/uni-app'
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
  pageSize: 6,
})
const total = ref(0)

onLoad(async (options) => {
  console.log('options', options)
  console.log('userStore', userStore.profile)
  const res = await getUserInfo(user.id)
  // 获取所有订单信息
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
  historyOrders.value = historyOrders.value.concat(res.data.records)
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
  // uni.showToast({
  //   title: '已催单',
  //   icon: 'none',
  // })
}

// 页面上拉触底事件的处理函数
onReachBottom(() => {
  console.log('Page:', orderDTO.value.page)
  console.log('Page Size:', orderDTO.value.pageSize)
  if (orderDTO.value.page * orderDTO.value.pageSize >= Math.min(total.value, 12)) {
    console.log('end!')
    // 达到最近订单展示上限
    uni.showToast({
      title: '更多订单信息请到历史订单查看！',
      icon: 'none',
    })
    return
  }
  orderDTO.value.page += 1
  getOrderPage()
})

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
  background-color: #f7e293;
  min-height: 100vh;
  padding-bottom: 40rpx;
  padding-top: 20rpx;
}

.my_info {
  height: 220rpx;
  background-color: #fffbe6; /* 修改背景颜色 */
  display: flex;
  align-items: center;
  padding: 0 40rpx;
  box-sizing: border-box;
  border-radius: 20rpx; /* 添加圆角 */
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); /* 添加阴影 */
  margin: 0 20rpx; /* 添加上左右边距 */

  .head {
    width: 160rpx;
    height: 160rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    .head_image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      background-color: #fff;
      box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08);
      object-fit: cover;
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
      .name_text {
        font-size: 36rpx;
        font-weight: 600;
        color: #333;
        margin-right: 16rpx;
      }
      .name_type {
        width: 32rpx;
        height: 32rpx;
        margin-bottom: 4rpx;
      }
    }
    .phone {
      margin-top: 12rpx;
      .phone_text {
        font-size: 28rpx;
        color: #666;
      }
    }
  }
}

.white_box {
  margin: 24rpx 20rpx;
  background-color: #fffbe6;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
  .bottom_text {
    display: flex;
    align-items: center;
    padding: 0 24rpx;
    height: 100rpx;
    border-bottom: 1rpx solid #f0e3b0;
    &:last-child {
      border-bottom: none;
    }
    .icon {
      width: 48rpx;
      height: 48rpx;
      margin-right: 18rpx;
    }
    .text_left {
      flex: 1;
      font-size: 32rpx;
      color: #333;
      font-weight: 500;
    }
    .right_image {
      .to_right {
        width: 28rpx;
        height: 28rpx;
        vertical-align: middle;
      }
    }
  }
}

.history_content {
  padding: 0 20rpx 20rpx 20rpx;
  .title {
    font-size: 30rpx;
    color: #333;
    font-weight: bold;
    margin: 24rpx 0 12rpx 0;
  }
  .history_item {
    background-color: #fff;
    border-radius: 20rpx;
    box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
    margin-top: 20rpx;
    padding: 36rpx 24rpx 24rpx 24rpx;
    transition: box-shadow 0.2s;
    &:hover {
      box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.12);
    }
    .item_info_box {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      width: 100%;
      .history_item_left {
        flex: 1;
        .history_item_order_id {
          font-size: 28rpx;
          color: #333;
          margin-bottom: 16rpx;
        }
        .scroll_container {
          width: 400rpx;
          height: 120rpx;
          overflow-x: auto;
          white-space: nowrap;
          .image_box {
            width: 100rpx;
            display: inline-block;
            margin-right: 16rpx;
            image {
              border-radius: 12rpx;
              width: 100rpx;
              height: 100rpx;
              box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06);
              object-fit: cover;
              background: #f7e293;
            }
          }
        }
        .history_item_order_time {
          font-size: 24rpx;
          color: #999;
          margin-top: 8rpx;
        }
      }
      .history_item_right {
        min-width: 180rpx;
        text-align: right;
        .history_item_status {
          font-size: 28rpx;
          color: #0af;
          margin-bottom: 32rpx;
          font-weight: 500;
        }
        .history_item_price {
          font-size: 30rpx;
          color: #333;
          margin-bottom: 8rpx;
        }
        .history_item_dish_amount {
          font-size: 24rpx;
          color: #999;
          margin-bottom: 32rpx;
        }
      }
    }
    .btn_box {
      width: 100%;
      display: flex;
      justify-content: flex-end;
      margin-top: 16rpx;
      gap: 16rpx;
      .history_item_reOrder {
        padding: 0 24rpx;
        height: 56rpx;
        line-height: 56rpx;
        border: 2rpx solid #0af;
        border-radius: 28rpx;
        font-size: 28rpx;
        color: #0af;
        background: #f7faff;
        cursor: pointer;
        transition: background 0.2s;
        &:active {
          background: #e6f7ff;
        }
      }
      .history_item_push_order {
        padding: 0 24rpx;
        height: 56rpx;
        line-height: 56rpx;
        background-color: #0af;
        border-radius: 28rpx;
        font-size: 28rpx;
        color: #fff;
        cursor: pointer;
        box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.08);
        transition: background 0.2s;
        &:active {
          background: #0080ff;
        }
      }
    }
  }
}
</style>

<!-- <style>
page {
  background-color: #f8f8f8;
}
</style> -->
