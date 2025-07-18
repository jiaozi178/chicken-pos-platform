<template>
  <Navbar title="地址管理" :show-back="true" />
  
  <view class="address-container" :style="{paddingTop: statusBarHeight}">
    <!-- 地址列表 -->
    <view v-if="addressList && addressList.length > 0" class="address-list">
      <view 
        v-for="(item, index) in addressList" 
        :key="index" 
        class="address-card"
        :class="{'default-card': item.isDefault === 1}"
      >
        <view class="card-content" @click="choseAddress(index, item)">
          <view class="card-header">
            <view class="address-tag" :class="`tag-${trans(item.label as string)}`">
              {{ getLableVal(item.label as string) }}
            </view>
            <view class="user-info">
              <text class="user-name">{{ item.consignee }}</text>
              <text class="user-gender">{{ item.gender === 1 ? '先生' : '女士' }}</text>
              <text class="user-phone">{{ item.phone }}</text>
            </view>
          </view>
          
          <view class="address-detail">
            <text class="address-text">
              {{ item.provinceName }}{{ item.cityName }}{{ item.districtName }}{{ item.detail }}
            </text>
          </view>
          
          <view class="card-footer">
            <view class="default-label" v-if="item.isDefault === 1">
              <text>默认地址</text>
            </view>
          </view>
        </view>
        
        <view class="card-actions">
          <view class="set-default" @click.stop="getRadio(index, item)">
            <radio 
              class="default-radio"
              color="#FF9C10"
              :value="String(item.id)"
              :checked="item.isDefault === 1"
            />
            <text>设为默认</text>
          </view>
          
          <view class="edit-btn" @click.stop="addOrEdit('编辑', item)">
            <image class="edit-icon" src="../../static/icon/edit2.png"></image>
            <text>编辑</text>
          </view>
        </view>
      </view>
    </view>
    
    <Empty v-else boxHeight="100%" textLabel="暂无地址" />
    
    <!-- 添加地址按钮 -->
    <view class="add-address-btn">
      <button class="add-btn" @click="addOrEdit('新增', 0)">
        添加收货地址
      </button>
    </view>
  </view>
</template>


<script lang="ts" setup>
import {ref, onMounted, computed} from 'vue'
import {getAddressListAPI, updateDefaultAddressAPI} from '@/api/address'
import type {Address} from '@/types/address'
import {useAddressStore} from '@/stores/modules/address'
import Empty from '@/components/empty/Empty.vue'
import Navbar from '@/components/navbar/Navbar.vue';

const store = useAddressStore()

const testValue = ref(true)
const addressList = ref<Address[]>([])
const addressBackUrl = store.addressBackUrl
const statusBarHeight = computed(() => uni.getSystemInfoSync().statusBarHeight + 'px')

onMounted(() => {
  getAddressList()
})

const getAddressList = async () => {
  testValue.value = false
  const res = await getAddressListAPI()
  if (res.code === 0) {
    testValue.value = true
    addressList.value = res.data
  }
}

// 标签文字转数字
const trans = (item: string) => {
  if (item === '公司') {
    return '1'
  } else if (item === '家') {
    return '2'
  } else if (item === '学校') {
    return '3'
  } else {
    return '4'
  }
}

// 未选择标签时，默认展示其他
const getLableVal = (item: string) => {
  if (item === null) {
    return '其他'
  }
  return item
}

// 编辑与新增，根据情况跳转不同页面
const addOrEdit = (type: string, item: any) => {
  if (type === '新增') {
    uni.navigateTo({
      url: '/pages/addOrEditAddress/addOrEditAddress',
    })
  } else {
    console.log('我要去编辑地址页面！！！  item', item)
    uni.navigateTo({
      url: '/pages/addOrEditAddress/addOrEditAddress?type=' + '编辑' + '&' + 'id=' + item.id,
    })
  }
}

// 选择地址，并跳转回订单submit页面
const choseAddress = (e: any, item: any) => {
  console.log('addressBackUrl', addressBackUrl)
  // 1、当前是从 个人中心-地址管理 页面跳转过来的，点击不用跳回订单页面
  if (addressBackUrl !== '/pages/submit/submit') {
    return false
  }
  // 2、有记录addressBackUrl，要跳回订单页面
  uni.redirectTo({
    url: '/pages/submit/submit?address=' + JSON.stringify(item),
  })
}

// 设置默认地址
const getRadio = async (e: any, item: any) => {
  // 提供默认接口
  const res = await updateDefaultAddressAPI({id: item.id})
  if (res.code === 0) {
    uni.showToast({
      title: '默认地址设置成功',
      duration: 2000,
      icon: 'none',
    })
    getAddressList()
  }
}
</script>

<style lang="less" scoped>
.address-container {
  min-height: 100vh;
  padding: 20rpx 30rpx 120rpx;
}

.address-list {
  margin-top: 20rpx;
}

.address-card {
  background: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 20rpx rgba(255, 156, 16, 0.08);
  margin-bottom: 30rpx;
  overflow: hidden;
  transition: all 0.3s;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 12rpx 30rpx rgba(255, 156, 16, 0.15);
  }
  
  &.default-card {
    border: 2rpx solid #FF9C10;
    position: relative;
    overflow: visible;
    
    &::before {
      content: '默认';
      position: absolute;
      top: -10rpx;
      right: 20rpx;
      background: #FF9C10;
      color: #fff;
      font-size: 22rpx;
      padding: 4rpx 16rpx;
      border-radius: 0 0 10rpx 10rpx;
      z-index: 2;
    }
  }
}

.card-content {
  padding: 30rpx 30rpx 20rpx;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.address-tag {
  height: 40rpx;
  padding: 0 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20rpx;
  
  &.tag-1 { background: #22A7FF; } /* 公司 - 蓝色 */
  &.tag-2 { background: #FF6B6B; } /* 家 - 红色 */
  &.tag-3 { background: #6BCB77; } /* 学校 - 绿色 */
  &.tag-4 { background: #FF9C10; } /* 其他 - 橙色 */
}

.user-info {
  display: flex;
  align-items: center;
  
  .user-name {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-right: 10rpx;
  }
  
  .user-gender {
    font-size: 26rpx;
    color: #999;
    margin-right: 20rpx;
  }
  
  .user-phone {
    font-size: 28rpx;
    color: #666;
  }
}

.address-detail {
  padding-left: 10rpx;
  margin-bottom: 24rpx;
  
  .address-text {
    font-size: 28rpx;
    color: #666;
    line-height: 1.6;
  }
}

.card-footer {
  padding-top: 20rpx;
  border-top: 1rpx dashed #f0f0f0;
  
  .default-label {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    color: #FF9C10;
    
    .default-icon {
      width: 28rpx;
      height: 28rpx;
      margin-right: 8rpx;
    }
  }
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #FFFCF5;
  border-top: 1rpx solid #f8f8f8;
  
  .set-default {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    color: #666;
    
    .default-radio {
      transform: scale(0.8);
      margin-right: 6rpx;
    }
  }
  
  .edit-btn {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    color: #FF9C10;
    
    .edit-icon {
      color: #FF9C10;
      width: 30rpx;
      height: 30rpx;
      margin-right: 8rpx;
    }
  }
}

.add-address-btn {
  position: fixed;
  bottom: 40rpx;
  left: 30rpx;
  right: 30rpx;
  
  .add-btn {
    height: 88rpx;
    line-height: 88rpx;
    border-radius: 88rpx;
    background: linear-gradient(to right, #FFB74D, #FF9C10);
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    box-shadow: 0 8rpx 20rpx rgba(255, 156, 16, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
    transition: all 0.3s;
    
    &:active {
      transform: scale(0.98);
      opacity: 0.9;
    }
    
    .add-icon {
      width: 36rpx;
      height: 36rpx;
      margin-right: 12rpx;
    }
  }
}
</style>

<style>
page {
  background: linear-gradient(180deg, #FFF9E6 30%, #f8f9fa 100%);
}
</style>

