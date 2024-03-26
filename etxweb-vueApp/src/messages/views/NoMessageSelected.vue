<template>
  <div class="choose-message d-flex justify-content-center">
    <div class="align-self-center">
      <div class="ico-pins-container">
        <span class="ico-pins"></span>
        <i title="Please choose a message..." class="ico-mail"></i>
      </div>
      <p class="lead">Please choose a message...</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'NoMessageSelected',
  setup() {
    return { }
  }
})
</script>
