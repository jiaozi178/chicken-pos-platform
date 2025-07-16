<template>
  <div class="chicken-shop">
    <!-- 头部 -->
    <header class="header">
      <h1>正新鸡排</h1>
      <p>新鲜美味，现做现卖</p>
    </header>

    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索商品名称..."
          class="search-input"
        >
        <button class="search-btn" @click="searchProducts">搜索</button>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="category-section">
      <h3>商品分类</h3>
      <div class="category-list">
        <button
          v-for="category in categories"
          :key="category.id"
          :class="['category-btn', { active: selectedCategory === category.id }]"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-section">
      <h3>商品列表</h3>
      <div v-if="filteredProducts.length === 0" class="no-products">
        没有找到相关商品
      </div>
      <div v-else class="products-grid">
        <div
          v-for="product in filteredProducts"
          :key="product.id"
          class="product-card"
        >
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-desc">{{ product.description }}</p>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-actions">
              <div class="quantity-control">
                <button
                  @click="decreaseQuantity(product.id)"
                  :disabled="getQuantity(product.id) === 0"
                  class="quantity-btn"
                >
                  -
                </button>
                <span class="quantity">{{ getQuantity(product.id) }}</span>
                <button
                  @click="increaseQuantity(product.id)"
                  class="quantity-btn"
                >
                  +
                </button>
              </div>
              <button
                @click="addToCart(product)"
                class="add-cart-btn"
                :disabled="getQuantity(product.id) === 0"
              >
                加入购物车
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车 -->
    <div v-if="cart.length > 0" class="cart-section">
      <h3>购物车 ({{ getTotalItems() }})</h3>
      <div class="cart-items">
        <div v-for="item in cart" :key="item.id" class="cart-item">
          <span class="item-name">{{ item.name }}</span>
          <span class="item-quantity">x{{ item.quantity }}</span>
          <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          <button @click="removeFromCart(item.id)" class="remove-btn">删除</button>
        </div>
      </div>
      <div class="cart-total">
        <strong>总计: ¥{{ getTotalPrice() }}</strong>
        <button @click="checkout" class="checkout-btn">结账</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, type Ref } from 'vue'

// 定义接口类型
interface Category {
  id: string
  name: string
  description: string
}

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
}

// 响应式数据
const searchQuery: Ref<string> = ref('')
const selectedCategory: Ref<string> = ref('all')
const quantities: Ref<Record<number, number>> = ref({})
const cart: Ref<CartItem[]> = ref([])

// 商品分类数据
const categories: Ref<Category[]> = ref([
  { id: 'all', name: '全部商品' },
  { id: 'chicken', name: '鸡排类' },
  { id: 'drink', name: '饮品类' },
  { id: 'side', name: '小食类' },
  { id: 'combo', name: '套餐类' },
  { id: 'combo1', name: '111套餐类' }
])

// 商品数据
const products: Ref<Product[]> = ref([
  {
    id: 1,
    name: '经典鸡排',
    description: '酥脆外皮，鲜嫩内里，经典口味',
    price: 15.00,
    originalPrice: 18.00,
    category: 'chicken',
    image: 'https://images.unsplash.com/photo-1569058242252-92a9c755a0ec?w=300&h=200&fit=crop'
  },
  {
    id: 2,
    name: '香辣鸡排',
    description: '特调香辣料，口感层次丰富',
    price: 16.00,
    category: 'chicken',
    image: 'https://images.unsplash.com/photo-1562967914-608f82629710?w=300&h=200&fit=crop'
  },
  {
    id: 3,
    name: '蜂蜜鸡排',
    description: '蜂蜜调味，甜而不腻',
    price: 17.00,
    category: 'chicken',
    image: 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=300&h=200&fit=crop'
  },
  {
    id: 4,
    name: '珍珠奶茶',
    description: '经典台式奶茶，Q弹珍珠',
    price: 12.00,
    category: 'drink',
    image: 'https://images.unsplash.com/photo-1581006852262-e17d0b4a7e4d?w=300&h=200&fit=crop'
  },
  {
    id: 5,
    name: '柠檬汽水',
    description: '清爽柠檬味，解腻好选择',
    price: 8.00,
    category: 'drink',
    image: 'https://images.unsplash.com/photo-1622597467836-f3285f2131b8?w=300&h=200&fit=crop'
  },
  {
    id: 6,
    name: '薯条',
    description: '金黄酥脆，外酥内软',
    price: 10.00,
    category: 'side',
    image: 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=300&h=200&fit=crop'
  },
  {
    id: 7,
    name: '鸡米花',
    description: '一口一个，香脆可口',
    price: 13.00,
    category: 'side',
    image: 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=300&h=200&fit=crop'
  },
  {
    id: 8,
    name: '鸡排套餐',
    description: '经典鸡排+薯条+饮料',
    price: 25.00,
    originalPrice: 30.00,
    category: 'combo',
    image: 'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=300&h=200&fit=crop'
  },
  {
    id: 9,
    name: '香辣鸡腿堡',
    description: '香辣鸡腿肉配新鲜蔬菜',
    price: 18.00,
    category: 'chicken',
    image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300&h=200&fit=crop'
  },
  {
    id: 10,
    name: '红茶',
    description: '经典红茶，清香回甘',
    price: 6.00,
    category: 'drink',
    image: 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=300&h=200&fit=crop'
  }
])

// 计算属性 - 过滤后的商品
const filteredProducts = computed((): Product[] => {
  let filtered: Product[] = products.value

  // 按分类过滤
  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter((product: Product) => product.category === selectedCategory.value)
  }

  // 按搜索关键词过滤
  if (searchQuery.value.trim()) {
    filtered = filtered.filter((product: Product) =>
      product.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      product.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  return filtered
})

// 方法
const selectCategory = (categoryId: string): void => {
  selectedCategory.value = categoryId
  searchQuery.value = '' // 清空搜索
}

const searchProducts = (): void => {
  selectedCategory.value = 'all' // 搜索时显示所有分类
}

const getQuantity = (productId: number): number => {
  return quantities.value[productId] || 0
}

const increaseQuantity = (productId: number): void => {
  quantities.value[productId] = (quantities.value[productId] || 0) + 1
}

const decreaseQuantity = (productId: number): void => {
  if (quantities.value[productId] > 0) {
    quantities.value[productId]--
  }
}

const addToCart = (product: Product): void => {
  const quantity: number = getQuantity(product.id)
  if (quantity === 0) return

  const existingItem: CartItem | undefined = cart.value.find((item: CartItem) => item.id === product.id)
  if (existingItem) {
    existingItem.quantity += quantity
  } else {
    cart.value.push({
      ...product,
      quantity: quantity
    } as CartItem)
  }

  // 重置数量
  quantities.value[product.id] = 0
}

const removeFromCart = (productId: number): void => {
  const index: number = cart.value.findIndex((item: CartItem) => item.id === productId)
  if (index > -1) {
    cart.value.splice(index, 1)
  }
}

const getTotalItems = (): number => {
  return cart.value.reduce((total: number, item: CartItem) => total + item.quantity, 0)
}

const getTotalPrice = (): string => {
  return cart.value.reduce((total: number, item: CartItem) => total + (item.price * item.quantity), 0).toFixed(2)
}

const checkout = (): void => {
  alert(`下单成功！总金额: ¥${getTotalPrice()}`)
  cart.value = []
}
</script>

<style scoped>
.chicken-shop {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Arial', sans-serif;
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  color: white;
  border-radius: 10px;
}

.header h1 {
  margin: 0;
  font-size: 2.5em;
}

.header p {
  margin: 10px 0 0 0;
  font-size: 1.2em;
}

.search-section {
  margin-bottom: 30px;
}

.search-box {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.search-input {
  flex: 1;
  max-width: 400px;
  padding: 12px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 25px;
  outline: none;
}

.search-input:focus {
  border-color: #ff6b6b;
}

.search-btn {
  padding: 12px 24px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s;
}

.search-btn:hover {
  background: #ff5252;
}

.category-section {
  margin-bottom: 30px;
}

.category-section h3 {
  margin-bottom: 15px;
  color: #333;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-btn {
  padding: 10px 20px;
  background: #f8f9fa;
  border: 2px solid #ddd;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-btn:hover {
  background: #e9ecef;
}

.category-btn.active {
  background: #ff6b6b;
  color: white;
  border-color: #ff6b6b;
}

.products-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.no-products {
  text-align: center;
  color: #666;
  font-size: 18px;
  margin: 50px 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 20px;
}

.product-name {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.3em;
}

.product-desc {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 0.9em;
}

.product-price {
  margin-bottom: 15px;
}

.price {
  font-size: 1.5em;
  font-weight: bold;
  color: #ff6b6b;
}

.original-price {
  margin-left: 10px;
  text-decoration: line-through;
  color: #999;
}

.product-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 35px;
  height: 35px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 5px;
  font-size: 18px;
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

.quantity {
  font-weight: bold;
  min-width: 20px;
  text-align: center;
}

.add-cart-btn {
  padding: 10px 20px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.add-cart-btn:hover:not(:disabled) {
  background: #218838;
}

.add-cart-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.cart-section {
  margin-top: 40px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
}

.cart-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.cart-items {
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: white;
  border-radius: 5px;
  margin-bottom: 10px;
}

.item-name {
  flex: 1;
  font-weight: bold;
}

.item-quantity {
  margin: 0 20px;
  color: #666;
}

.item-price {
  font-weight: bold;
  color: #ff6b6b;
  margin-right: 20px;
}

.remove-btn {
  padding: 5px 10px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

.remove-btn:hover {
  background: #c82333;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 2px solid #ddd;
}

.checkout-btn {
  padding: 12px 24px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.checkout-btn:hover {
  background: #ff5252;
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: 1fr;
  }

  .category-list {
    justify-content: center;
  }

  .product-actions {
    flex-direction: column;
    gap: 15px;
  }

  .cart-item {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }

  .cart-total {
    flex-direction: column;
    gap: 15px;
  }
}
</style>