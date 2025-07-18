<script setup lang="ts">
import {onLaunch, onShow, onHide} from '@dcloudio/uni-app'
onLaunch(() => {
  console.log('App Launch')
})
onShow(() => {
  console.log('App Show')
})
onHide(() => {
  console.log('App Hide')
})
</script>

<style>
@import '@/static/styles/iconfont.css';
/* page {
  background-color: #f7e293;
} */
</style>
