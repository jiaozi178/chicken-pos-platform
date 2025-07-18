<template>
  <Navbar title="历史订单" :show-back="true" />

  <view class="history-container">
    <!-- 状态筛选栏 -->
    <view class="status-filter">
      <view
        v-for="(item, index) in statusOptions"
        :key="index"
        class="filter-item"
        :class="{active: index === activeIndex}"
        @tap="getOrderPage(index, '更改状态')"
      >
        <text class="filter-text">{{ item.name }}</text>
        <view v-if="index === activeIndex" class="active-indicator"></view>
      </view>
    </view>
    
    <!-- 订单列表 -->
    <view class="order-list">
      <view
        class="order-item"
        v-for="(item, index) in historyOrders"
        :key="index"
        @click="toOrderDetail(item.id as number)"
      >
        <view class="item-header">
          <text class="order-number">订单号：{{ item.number }}</text>
          <text class="order-status" :class="`status-${item.status}`">
            {{ statusList[item.status as number].name }}
          </text>
        </view>
        
        <view class="item-body">
          <scroll-view class="scroll-container" scroll-x>
            <view v-for="(dish, index) in item.orderDetailList" :key="index" class="dish-image">
              <image :src="getImageUrl(dish.pic ?? '')" />
            </view>
          </scroll-view>
          
          <view class="order-info">
            <view class="order-time">
              <image class="time-icon" src="../../static/icon/time.png"></image>
              {{ item.orderTime }}
            </view>
            <view class="order-price">￥{{ item.amount }}</view>
          </view>
        </view>
        
        <view class="item-footer">
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
import {ref} from 'vue'
import {onLoad, onReachBottom} from '@dcloudio/uni-app'
import {getOrderPageAPI, reOrderAPI} from '@/api/order'
import {cleanCartAPI} from '@/api/cart'
import type {OrderPageDTO, OrderVO} from '@/types/order'
import { getImageUrl } from '@/utils/imageUrl'
import Navbar from '@/components/navbar/Navbar.vue'

const childComp: any = ref(null)

// 顶部tab栏
const statusOptions = [
  {
    status: 0,
    name: '全部订单',
  },
  {
    status: 1,
    name: '待付款',
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
// 所有状态
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

const activeIndex = ref(0)
const historyOrders = ref<OrderVO[]>([])
const orderDTO = ref<OrderPageDTO>({
  page: 1,
  pageSize: 6,
  // status: 0,
})
const total = ref(0)

onLoad(async () => {
  console.log('首先分页获取所有订单信息', orderDTO.value)
  // 分页获取所有订单信息（刚开始只展示前6条）
  const res = await getOrderPage(0)
})

// 页面上拉触底事件的处理函数
onReachBottom(() => {
  console.log('Page:', orderDTO.value.page)
  console.log('Page Size:', orderDTO.value.pageSize)
  if (orderDTO.value.page * orderDTO.value.pageSize >= total.value) {
    console.log('end!')
    // 没有下一页数据，提示用户
    uni.showToast({
      title: 'end!',
      icon: 'none',
    })
    return
  }
  orderDTO.value.page += 1
  getOrderPage(activeIndex.value)
})

const getOrderPage = async (index: number, type?: string) => {
  activeIndex.value = index
  console.log('根据status获取订单信息')
  // != 0 说明不是全部订单，需要传入status条件分页查询
  if (index !== 0) {
    orderDTO.value.status = statusOptions[index].status
  } else {
    delete orderDTO.value.status
  }
  console.log('orderDTO', orderDTO.value)
  const res = await getOrderPageAPI(orderDTO.value)
  if (type === '更改状态') {
    historyOrders.value = res.data.records
    orderDTO.value.page = 1
  } else {
    historyOrders.value = historyOrders.value.concat(res.data.records)
  }
  total.value = res.data.total
}

const toOrderDetail = (id: number) => {
  uni.navigateTo({
    url: '/pages/orderDetail/orderDetail?orderId=' + id,
  })
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
const pushOrder = (id: number) => {
  console.log('催单', id)
  childComp.value.openPopup()
  // uni.showToast({
  //   title: '已催单',
  //   icon: 'none',
  // })
}
</script>

<style lang="less" scoped>
.history-container {
  min-height: 100vh;
  padding: 20rpx 30rpx;
  background: linear-gradient(180deg, #FFF9E6 30%, #f8f9fa 100%);
}

.status-filter {
  display: flex;
  justify-content: space-around;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 20rpx 0;
  margin-bottom: 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.03);
  
  .filter-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    padding: 0 20rpx;
    
    .filter-text {
      font-size: 30rpx;
      color: #666;
      transition: all 0.3s;
    }
    
    .active-indicator {
      position: absolute;
      bottom: -10rpx;
      width: 40rpx;
      height: 6rpx;
      background: #FF9C10;
      border-radius: 3rpx;
      transition: all 0.3s;
    }
    
    &.active {
      .filter-text {
        color: #FF9C10;
        font-weight: bold;
      }
    }
  }
}

.order-list {
  .order-item {
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
    
    .item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24rpx 30rpx;
      background: #fafafa;
      border-bottom: 1rpx solid #f0f0f0;
      
      .order-number {
        font-size: 26rpx;
        color: #666;
      }
      
      .order-status {
        font-size: 26rpx;
        font-weight: 500;
        padding: 6rpx 16rpx;
        border-radius: 30rpx;
        
        &.status-1 { /* 待付款 */
          background: #fff2f0;
          color: #ff4d4f;
        }
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
    
    .item-body {
      padding: 30rpx;
      
      .scroll-container {
        width: 100%;
        height: 120rpx;
        margin-bottom: 30rpx;
        
        .dish-image {
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
      
      .order-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-top: 1rpx dashed #eee;
        padding-top: 24rpx;
        
        .order-time {
          display: flex;
          align-items: center;
          font-size: 26rpx;
          color: #999;
          
          .time-icon {
            width: 28rpx;
            height: 28rpx;
            margin-right: 10rpx;
          }
        }
        
        .order-price {
          font-size: 32rpx;
          font-weight: bold;
          color: #FF9C10;
        }
      }
    }
    
    .item-footer {
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