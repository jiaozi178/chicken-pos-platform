<template>
  <div class="cart-page">
    <!-- 头部 -->
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="back-btn">← 返回商品页</button>
        <h1>购物车管理</h1>
        <div class="cart-summary">
          <span class="item-count">{{ getTotalItems() }}件商品</span>
        </div>
      </div>
    </header>

    <!-- 购物车内容 -->
    <div class="cart-content">
      <!-- 空购物车状态 -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-icon">🛒</div>
        <h3>购物车是空的</h3>
        <p>快去选购一些美味的正新鸡排吧！</p>
        <button @click="goBack" class="shop-now-btn">立即购买</button>
      </div>

      <!-- 购物车商品列表 -->
      <div v-else class="cart-items-section">
        <!-- 操作栏 -->
        <div class="cart-actions">
          <div class="select-all">
            <input
              type="checkbox"
              id="selectAll"
              v-model="selectAll"
              @change="toggleSelectAll"
            >
            <label for="selectAll">全选</label>
          </div>
          <div class="bulk-actions">
            <button @click="clearCart" class="clear-btn">清空购物车</button>
            <button @click="removeSelected" class="remove-selected-btn">删除选中</button>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="cart-items">
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="cart-item"
            :class="{ 'selected': item.selected }"
          >
            <div class="item-select">
              <input
                type="checkbox"
                v-model="item.selected"
                @change="updateSelectAll"
              >
            </div>

            <div class="item-image">
              <img :src="item.image" :alt="item.name">
            </div>

            <div class="item-info">
              <h4 class="item-name">{{ item.name }}</h4>
              <p class="item-desc">{{ item.description }}</p>
              <div class="item-price">
                <span class="current-price">¥{{ item.price }}</span>
                <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
              </div>
            </div>

            <div class="item-quantity">
              <div class="quantity-control">
                <button
                  @click="decreaseQuantity(item.id)"
                  :disabled="item.quantity <= 1"
                  class="quantity-btn"
                >
                  -
                </button>
                <input
                  type="number"
                  v-model.number="item.quantity"
                  @change="updateQuantity(item.id, item.quantity)"
                  class="quantity-input"
                  min="1"
                >
                <button
                  @click="increaseQuantity(item.id)"
                  class="quantity-btn"
                >
                  +
                </button>
              </div>
            </div>

            <div class="item-total">
              <span class="total-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>

            <div class="item-actions">
              <button @click="removeItem(item.id)" class="remove-btn">删除</button>
              <button @click="addToFavorites(item)" class="favorite-btn">收藏</button>
            </div>
          </div>
        </div>

        <!-- 底部结算栏 -->
        <div class="checkout-section">
          <div class="checkout-summary">
            <div class="summary-info">
              <span class="selected-count">已选择{{ getSelectedItems().length }}件商品</span>
              <div class="price-breakdown">
                <div class="subtotal">
                  <span>商品总价：</span>
                  <span class="amount">¥{{ getSubtotal() }}</span>
                </div>
                <div class="discount" v-if="getDiscount() > 0">
                  <span>优惠减免：</span>
                  <span class="amount discount-amount">-¥{{ getDiscount() }}</span>
                </div>
                <div class="shipping">
                  <span>配送费：</span>
                  <span class="amount">¥{{ getShippingFee() }}</span>
                </div>
                <div class="total">
                  <span>应付总额：</span>
                  <span class="total-amount">¥{{ getFinalTotal() }}</span>
                </div>
              </div>
            </div>
            <div class="checkout-actions">
              <button
                @click="checkout"
                :disabled="getSelectedItems().length === 0"
                class="checkout-btn"
              >
                结算({{ getSelectedItems().length }})
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 优惠券/促销信息 -->
    <div v-if="cartItems.length > 0" class="promotions">
      <h3>优惠活动</h3>
      <div class="promotion-list">
        <div class="promotion-item">
          <span class="promotion-text">满30元免配送费</span>
          <span class="promotion-status" :class="{ 'active': getSubtotal() >= 30 }">
            {{ getSubtotal() >= 30 ? '已满足' : `还差¥${(30 - parseFloat(getSubtotal())).toFixed(2)}` }}
          </span>
        </div>
        <div class="promotion-item">
          <span class="promotion-text">满50元享9折优惠</span>
          <span class="promotion-status" :class="{ 'active': getSubtotal() >= 50 }">
            {{ getSubtotal() >= 50 ? '已满足' : `还差¥${(50 - parseFloat(getSubtotal())).toFixed(2)}` }}
          </span>
        </div>
      </div>
    </div>

    <!-- 收藏夹 -->
    <div v-if="favorites.length > 0" class="favorites-section">
      <h3>我的收藏</h3>
      <div class="favorites-grid">
        <div
          v-for="item in favorites"
          :key="'fav-' + item.id"
          class="favorite-item"
        >
          <img :src="item.image" :alt="item.name">
          <div class="favorite-info">
            <h5>{{ item.name }}</h5>
            <span class="favorite-price">¥{{ item.price }}</span>
          </div>
          <div class="favorite-actions">
            <button @click="addToCartFromFavorites(item)" class="add-to-cart-btn">加入购物车</button>
            <button @click="removeFromFavorites(item.id)" class="remove-favorite-btn">移除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { inject, type Ref } from 'vue'

// 接收菜单页面注入的数据
const cartItems = inject<Ref<CartItem[]>>('cartItems')!
const favorites = inject<Ref<Product[]>>('favorites')!
const selectAll = inject<Ref<boolean>>('selectAll')!

interface Product {
  id: number
  name: string
  description: string
  price: number
  originalPrice?: number
  category: string
  image: string
}

interface CartItem extends Product {
  quantity: number
  selected: boolean
}

// --- 方法定义 ---
const getTotalItems = (): number =>
  cartItems.value.reduce((total, item) => total + item.quantity, 0)

const getSelectedItems = (): CartItem[] =>
  cartItems.value.filter(item => item.selected)

const getSubtotal = (): string =>
  getSelectedItems().reduce((t, i) => t + i.price * i.quantity, 0).toFixed(2)

const getDiscount = (): string => {
  const subtotal = parseFloat(getSubtotal())
  const discount = subtotal >= 50 ? subtotal * 0.1 : 0
  return discount.toFixed(2)
}

const getShippingFee = (): string =>
  parseFloat(getSubtotal()) >= 30 ? '0.00' : '5.00'

const getFinalTotal = (): string => {
  const subtotal = parseFloat(getSubtotal())
  const discount = parseFloat(getDiscount())
  const shipping = parseFloat(getShippingFee())
  return (subtotal - discount + shipping).toFixed(2)
}

const goBack = () => {
  window.history.back()
}

const toggleSelectAll = () => {
  cartItems.value.forEach(item => (item.selected = selectAll.value))
}

const updateSelectAll = () => {
  const selectedCount = getSelectedItems().length
  selectAll.value = selectedCount > 0 && selectedCount === cartItems.value.length
}

const increaseQuantity = (id: number) => {
  const item = cartItems.value.find(i => i.id === id)
  if (item) item.quantity++
}

const decreaseQuantity = (id: number) => {
  const item = cartItems.value.find(i => i.id === id)
  if (item && item.quantity > 1) item.quantity--
}

const updateQuantity = (id: number, quantity: number) => {
  const item = cartItems.value.find(i => i.id === id)
  if (item) item.quantity = Math.max(1, quantity)
}

const removeItem = (id: number) => {
  cartItems.value = cartItems.value.filter(item => item.id !== id)
  updateSelectAll()
}

const removeSelected = () => {
  if (confirm('确定删除选中商品？')) {
    cartItems.value = cartItems.value.filter(item => !item.selected)
    selectAll.value = false
  }
}

const clearCart = () => {
  if (confirm('确定清空购物车？')) {
    cartItems.value = []
    selectAll.value = false
  }
}

const addToFavorites = (item: CartItem) => {
  if (!favorites.value.find(f => f.id === item.id)) {
    const { quantity, selected, ...product } = item
    favorites.value.push(product)
    alert('已加入收藏夹')
  } else {
    alert('商品已存在收藏夹中')
  }
}

const removeFromFavorites = (id: number) => {
  favorites.value = favorites.value.filter(item => item.id !== id)
}

const addToCartFromFavorites = (item: Product) => {
  const exist = cartItems.value.find(i => i.id === item.id)
  if (exist) {
    exist.quantity++
  } else {
    cartItems.value.push({ ...item, quantity: 1, selected: false })
  }
  alert('已加入购物车')
}

const checkout = () => {
  const selected = getSelectedItems()
  if (!selected.length) return alert('请选择商品')

  alert(`结算成功！共 ${selected.length} 件商品，总计 ¥${getFinalTotal()}`)

  cartItems.value = cartItems.value.filter(item => !item.selected)
  selectAll.value = false
}
</script>


<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Arial', sans-serif;
  background-color: #f8f9fa;
}

.header {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-btn {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.back-btn:hover {
  background: #5a6268;
}

.header h1 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 15px;
}

.item-count {
  padding: 5px 15px;
  background: #ff6b6b;
  color: white;
  border-radius: 15px;
  font-size: 14px;
}

.cart-content {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.empty-cart {
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-cart h3 {
  margin-bottom: 10px;
  color: #333;
}

.shop-now-btn {
  padding: 12px 30px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
}

.shop-now-btn:hover {
  background: #ff5252;
}

.cart-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 10px;
}

.select-all input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

.bulk-actions {
  display: flex;
  gap: 10px;
}

.clear-btn, .remove-selected-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
}

.clear-btn:hover, .remove-selected-btn:hover {
  background: #f8f9fa;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s;
}

.cart-item:hover {
  background-color: #f8f9fa;
}

.cart-item.selected {
  background-color: #fff3cd;
}

.item-select {
  margin-right: 15px;
}

.item-select input[type="checkbox"] {
  width: 18px;
  height: 18px;
}

.item-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  overflow: hidden;
  border-radius: 8px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  margin-right: 15px;
}

.item-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.item-desc {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.item-price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-price {
  font-weight: bold;
  color: #ff6b6b;
  font-size: 16px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 14px;
}

.item-quantity {
  margin-right: 15px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 5px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn:hover:not(:disabled) {
  background: #f8f9fa;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 60px;
  height: 30px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.item-total {
  margin-right: 15px;
  min-width: 80px;
  text-align: right;
}

.total-price {
  font-weight: bold;
  color: #ff6b6b;
  font-size: 16px;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.remove-btn, .favorite-btn {
  padding: 5px 10px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 12px;
}

.remove-btn:hover {
  background: #f8f9fa;
  border-color: #dc3545;
  color: #dc3545;
}

.favorite-btn:hover {
  background: #f8f9fa;
  border-color: #ffc107;
  color: #ffc107;
}

.checkout-section {
  background: #f8f9fa;
  padding: 20px;
  border-top: 2px solid #ddd;
}

.checkout-summary {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  max-width: 800px;
  margin: 0 auto;
}

.summary-info {
  flex: 1;
}

.selected-count {
  display: block;
  margin-bottom: 10px;
  color: #666;
}

.price-breakdown {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.price-breakdown > div {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subtotal, .discount, .shipping {
  font-size: 14px;
  color: #666;
}

.total {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  border-top: 1px solid #ddd;
  padding-top: 10px;
  margin-top: 10px;
}

.discount-amount {
  color: #28a745;
}

.total-amount {
  color: #ff6b6b;
  font-size: 20px;
}

.checkout-actions {
  margin-left: 30px;
}

.checkout-btn {
  padding: 15px 30px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  transition: background-color 0.2s;
}

.checkout-btn:hover:not(:disabled) {
  background: #ff5252;
}

.checkout-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.promotions {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-top: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.promotions h3 {
  margin-bottom: 15px;
  color: #333;
}

.promotion-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.promotion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 5px;
}

.promotion-text {
  color: #333;
}

.promotion-status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  background: #ffc107;
  color: white;
}

.promotion-status.active {
  background: #28a745;
}

.favorites-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-top: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.favorites-section h3 {
  margin-bottom: 15px;
  color: #333;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.favorite-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
}

.favorite-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 5px;
  margin-bottom: 10px;
}

.favorite-info h5 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.favorite-price {
  color: #ff6b6b;
  font-weight: bold;
}

.favorite-actions {
  display: flex;
  gap: 5px;
  margin-top: 10px;
}

.add-to-cart-btn, .remove-favorite-btn {
  flex: 1;
  padding: 5px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 12px;
}

.add-to-cart-btn:hover {
  background: #28a745;
  color: white;
}

.remove-favorite-btn:hover {
  background: #dc3545;
  color: white;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }

  .cart-actions {
    flex-direction: column;
    gap: 15px;
  }

  .cart-item {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .checkout-summary {
    flex-direction: column;
    gap: 20px;
  }

  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>