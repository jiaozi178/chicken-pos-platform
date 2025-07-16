<!-- 空组件 -->
<template>
  <div class="empty-box">
    <div class="img-box">
<<<<<<< HEAD
      <img src="../static/images/table_empty.png" alt="" />
=======
      <img class="the-higher-one" v-if="!props.isSearch" src="../assets/image/table_empty.png" alt="">
      <img class="the-shorter-one" v-else src="../assets/image/search_table_empty.png">
>>>>>>> origin/main
      <p>{{ !props.isSearch ? '这里空空如也~' : 'Sorry，木有找到您搜索的内容哦~' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  isSearch: {
    type: Boolean,
    default: false,
  },
})
</script>

<style scoped lang="less">
.empty-box {
  text-align: center;
  margin: 20px 0;

  img.the-shorter-one {
    margin: 0 auto;
    width: 238px;
    height: 184px;
    margin-top: 20px;
    margin-bottom: 26px;
  }

  img.the-higher-one {
    margin: 0 auto;
    width: 215px;
    height: 235px;
    margin-top: 20px;
    margin-bottom: 26px;
  }

  p {
    color: #818693;
  }
}
</style>
