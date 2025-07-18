<template>
  <Navbar :title="showDel ? '编辑地址' : '新增地址'" :show-back="true" />
  
  <view class="add-edit-container" :style="{paddingTop: statusBarHeight()}">
    <view class="form-container">
      <form class="address-form">
        <!-- 联系人 -->
        <view class="form-item">
          <text class="form-label">联系人</text>
          <input
            class="form-input"
            placeholder-class="placeholder"
            v-model="form.consignee"
            placeholder="请输入联系人"
            :maxlength="5"
          />
          <view class="gender-radio">
            <view 
              v-for="(item, index) in items" 
              :key="index" 
              class="radio-item"
              @click="sexChangeHandle(item.value)"
            >
              <radio 
                class="radio-input"
                color="#FF9C10"
                :value="String(item.value)"
                :checked="item.value === form.gender"
              />
              <text class="radio-label">{{ item.name }}</text>
            </view>
          </view>
        </view>
        
        <!-- 手机号 -->
        <view class="form-item">
          <text class="form-label">手机号</text>
          <input
            class="form-input"
            v-model="form.phone"
            type="number"
            placeholder-class="placeholder"
            placeholder="请输入手机号"
            :maxlength="11"
          />
        </view>
        
        <!-- 所在地区 -->
        <view class="form-item">
          <text class="form-label">所在地区</text>
          <!-- 只有微信小程序端内置了省市区数据 -->
          <!-- #ifdef MP-WEIXIN -->
          <picker 
            @change="pickerChange" 
            mode="region" 
            class="region-picker"
            :value="address?.split(' ')"
          >
            <view v-if="address" class="picker-value">{{ address }}</view>
            <view class="placeholder" v-else>请选择城市</view>
          </picker>
          <!-- #endif -->
        </view>
        
        <!-- 详细地址 -->
        <view class="form-item">
          <text class="form-label">详细地址</text>
          <input
            class="form-input"
            :class="{'detail-ios': platform == 'ios'}"
            placeholder-class="placeholder"
            v-model="form.detail"
            placeholder="精确到门牌号"
          />
        </view>
        
        <!-- 标签 -->
        <view class="form-item tag-item">
          <text class="form-label">地址标签</text>
          <view class="tag-options">
            <view
              v-for="(item, idx) in options"
              :key="idx"
              class="tag-option"
              :class="{
                'tag-company': item.name === '公司' && form.label === item.name,
                'tag-home': item.name === '家' && form.label === item.name,
                'tag-school': item.name === '学校' && form.label === item.name,
                'active': form.label === item.name
              }"
              @click="getTextOption(item)"
            >
              {{ item.name }}
            </view>
          </view>
        </view>
      </form>
      
      <!-- 操作按钮 -->
      <view class="action-buttons">
        <button 
          class="save-btn" 
          @click="addAddress()"
        >
          保存地址
        </button>
        <button 
          v-if="showDel" 
          class="delete-btn" 
          type="button" 
          plain 
          @click="deleteAddress()"
        >
          删除地址
        </button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {addAddressAPI, deleteAddressAPI, getAddressByIdAPI, updateAddressAPI} from '@/api/address'
import {onLoad, onShow, onUnload} from '@dcloudio/uni-app'
import {reactive} from 'vue'
import type {Address} from '@/types/address'
import Navbar from '@/components/navbar/Navbar.vue'

// 自己实现的省市区选择器
let fullLocationCode: [string, string, string] = ['', '', '']
const pickerChange: UniHelper.RegionPickerOnChange = (ev) => {
  console.log(ev)
  // 修改前端界面
  address.value = ev.detail.value.join(' ')
  console.log(address.value)
  // 提交后端更新
  fullLocationCode = ev.detail.code!
  console.log(fullLocationCode)
  // 拆分省市区编码给三个变量，后端需要
  form.provinceCode = fullLocationCode[0]
  form.cityCode = fullLocationCode[1]
  form.districtCode = fullLocationCode[2]
}

const platform = ref('ios')
const showDel = ref(false)
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
const options = [
  {
    name: '公司',
  },
  {
    name: '家',
  },
  {
    name: '学校',
  },
]
const form = reactive({
  id: 0,
  consignee: '',
  phone: '',
  label: '',
  gender: 1,
  provinceCode: '110000',
  provinceName: '',
  cityCode: '110100',
  cityName: '',
  districtCode: '110102',
  districtName: '',
  detail: '',
})
// 联动省市县
// 弹框的初始值
const cityPickerValueDefault = [0, 0, 1]
const pickerText = ref('')
// 初始值
const address = ref('北京市 市辖区 西城区')
// 保存将要删除的
const delId = ref<number>()

onLoad(async (options) => {
  init()
  if (options && options.type === '编辑') {
    delId.value = -1 // 初始时没有要删除的地址，其id为-1
    showDel.value = true
    uni.setNavigationBarTitle({
      title: '编辑收货地址',
    })
    // 保存将要删除的id
    delId.value = options.id
    // 查询详情，用于回显原状态信息
    await queryAddressBookById(options.id)
  } else {
    showDel.value = false
  }
})
onUnload(() => {
  uni.removeStorage({
    key: 'edit',
  })
})
const statusBarHeight = () => {
  return uni.getSystemInfoSync().statusBarHeight + 'px'
}
const init = () => {
  const res = uni.getSystemInfoSync()
  platform.value = res.platform
}
const goBack = () => {
  uni.redirectTo({
    url: '/pages/address/address',
  })
}
// 查询地址详情接口
const queryAddressBookById = async (id: number) => {
  const res = await getAddressByIdAPI(id)
  if (res.code === 0) {
    const newForm = {
      provinceCode: res.data.provinceCode,
      cityCode: res.data.cityCode,
      districtCode: res.data.districtCode,
      phone: res.data.phone,
      consignee: res.data.consignee,
      gender: res.data.gender,
      label: res.data.label,
      detail: res.data.detail,
      id: res.data.id,
    }
    Object.assign(form, newForm)
    if (res.data.provinceName && res.data.cityName && res.data.districtName) {
      address.value = res.data.provinceName + '-' + res.data.cityName + '-' + res.data.districtName
    }
  }
}
// 标签的事件
const getTextOption = (item: any) => {
  console.log('点击了标签', item)
  form.label = item.name
}
// const bindTextAreaBlur = (e: any) => {
//   console.log(e.detail.value)
// }
// const radioChange = (e: any) => {
//   if (e.detail.value === 'man') {
//     form.radio = 0
//   } else {
//     form.radio = 1
//   }
// }
const sexChangeHandle = (val: number) => {
  form.gender = val
  console.log(form.gender)
}
// 新增地址
const addAddress = async () => {
  // 1、先校验
  if (form.consignee === '') {
    return uni.showToast({
      title: '联系人不能为空',
      duration: 1000,
      icon: 'none',
    })
  } else if (form.phone === '') {
    return uni.showToast({
      title: '手机号不能为空',
      duration: 1000,
      icon: 'none',
    })
  } else if (form.label === '') {
    return uni.showToast({
      title: '所属标签不能为空',
      duration: 1000,
      icon: 'none',
    })
  } else if (address.value === '') {
    return uni.showToast({
      title: '所在地区不能为空',
      duration: 1000,
      icon: 'none',
    })
  }
  if (form.phone) {
    const reg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
    if (!reg.test(form.phone)) {
      return uni.showToast({
        title: '手机号输入有误',
        duration: 1000,
        icon: 'none',
      })
    }
  }
  // 2、再拼接参数params
  const params = {
    ...(form as {id?: number}),
    provinceName: address.value.split(' ')[0],
    cityName: address.value.split(' ')[1],
    districtName: address.value.split(' ')[2],
  }
  // 3、编辑 or 新增 地址
  if (showDel.value) {
    console.log('update params !!!', params)
    const res = await updateAddressAPI(params)
    if (res.code === 0) {
      uni.redirectTo({
        url: '/pages/address/address',
      })
    }
  } else {
    delete params.id
    console.log('add params with label!', params)
    const res = await addAddressAPI(params)
    if (res.code === 0) {
      uni.redirectTo({
        url: '/pages/address/address',
      })
    }
  }
}
// 删除地址
const deleteAddress = async () => {
  if (delId.value === -1 || !delId.value) {
    return uni.showToast({
      title: '删除失败',
      duration: 1000,
      icon: 'none',
    })
  }
  const res = await deleteAddressAPI(delId.value)
  if (res.code === 0) {
    uni.redirectTo({
      url: '/pages/address/address',
    })
    uni.showToast({
      title: '地址删除成功',
      duration: 1000,
      icon: 'none',
    })
    form.consignee = ''
    form.phone = ''
    // form.address = ''
    form.label = ''
    // form.radio = 0
    form.provinceCode = '110000'
    form.cityCode = '110100'
    form.districtCode = '110102'
  }
}
</script>

<style lang="less" scoped>
.add-edit-container {
  min-height: 100vh;
  padding: 20rpx 30rpx;
  background: linear-gradient(180deg, #FFF9E6 30%, #f8f9fa 100%);
}

.form-container {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(255, 156, 16, 0.08);
}

.form-item {
  margin-bottom: 40rpx;
  padding-bottom: 30rpx;
  border-bottom: 1rpx dashed #f0f0f0;
  
  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
}

.form-label {
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
  color: #151515;
  padding: 0 10rpx;
  border-radius: 8rpx;
  background: #f5f5f4eb;
}

.placeholder {
  font-size: 26rpx;
  color: #999;
}

.gender-radio {
  display: flex;
  margin-top: 20rpx;
  
  .radio-item {
    display: flex;
    align-items: center;
    margin-right: 40rpx;
    
    .radio-input {
      transform: scale(0.8);
      margin-right: 8rpx;
    }
    
    .radio-label {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.region-picker {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  color: #151515;
  padding: 0 10rpx;
  border-radius: 8rpx;
  background: #f5f5f4eb;
  
  .picker-value {
    color: #333;
  }
}

.tag-item {
  .tag-options {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
    margin-top: 20rpx;
  }
  
  .tag-option {
    padding: 10rpx 24rpx;
    border-radius: 8rpx;
    font-size: 26rpx;
    color: #666;
    background: #f5f5f5;
    transition: all 0.3s;
    
    &.active {
      color: #fff;
    }
    
    &.tag-company {
      background: #22A7FF;
    }
    
    &.tag-home {
      background: #FF6B6B;
    }
    
    &.tag-school {
      background: #6BCB77;
    }
  }
}

.action-buttons {
  margin-top: 60rpx;
  
  .save-btn {
    height: 88rpx;
    line-height: 88rpx;
    border-radius: 88rpx;
    background: linear-gradient(to right, #FFB74D, #FF9C10);
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    box-shadow: 0 8rpx 20rpx rgba(255, 156, 16, 0.3);
    transition: all 0.3s;
    
    &:active {
      transform: scale(0.98);
      opacity: 0.9;
    }
  }
  
  .delete-btn {
    height: 88rpx;
    line-height: 88rpx;
    border-radius: 88rpx;
    margin-top: 30rpx;
    font-size: 32rpx;
    font-weight: bold;
    color: #FF6B6B;
    border: 2rpx solid #FF6B6B;
    background: #fff;
    
    &:active {
      transform: scale(0.98);
      opacity: 0.9;
    }
  }
}

.detail-ios {
  padding: 20rpx 14rpx;
}
</style>