<template>
  <button :id="id" ref="target" type="button" class="btn btn-primary ico-more-info" data-bs-trigger="click"
          data-bs-container="body" data-bs-toggle="popover" :data-bs-placement="placement"
          :data-bs-content="content" data-bs-html="true" title="More information"
  >
    <span>i</span>
  </button>
</template>

<script setup lang="ts">
import { onMounted, Ref, ref } from 'vue'
import { onClickOutside } from '@vueuse/core'
import { Popover } from 'bootstrap'

const props = defineProps({
  content: String,
  placement: String
})

const id = ref(`more_info_${Math.random()
  .toString()
  .slice(2, 8)}`)
const popover: Ref<Popover | null> = ref(null)
const target = ref(null)

onMounted(() => {
  const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
  popover.value = popoverTriggerList
    .filter((b: HTMLButtonElement) => b.id === id.value)
    .map(popoverTriggerEl => new Popover(popoverTriggerEl))[0]
})

onClickOutside(target, () => {
  popover.value?.hide()
})
</script>
