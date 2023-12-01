<script setup lang="ts">

import {onMounted, ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

const router = useRouter();

const title = ref("");
const content = ref("");

const post = ref({
  id: 0,
  title: "",
  content: ""
});

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  }
})

axios.get(`/my-backend-api/posts/${props.postId}`).then((res) => {
  post.value = res.data;
})

const edit = () => {
  axios.patch(`/my-backend-api/posts/${props.postId}`,  post.value)
      .then(() => {
        router.replace({name: "home"})
      })
}
</script>

<template>
  <main>
    <div>
      <el-input v-model="post.title" type="text" placeholder="제목"/>
    </div>

    <div class="mt-2">
      <el-input v-model="post.content" type="textarea" rows="15"/>
    </div>

    <div class="mt-2">
      <el-button type="primary" @click="edit"> 수정완료</el-button>
    </div>

  </main>
</template>
