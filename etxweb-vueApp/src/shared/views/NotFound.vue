<template>
  <div class="container">
    <div class="row">
      <div class="col-12 mt-5 mb-5 text-center">
        <div class="ico-pins-container">
          <span class="ico-pins"></span>
          <i aria-hidden="true" title="Page Not Found / 404" class="ico-https"></i>
        </div>
        <h2 class="mt-3">Page not found!</h2>
        <p class="mt-3">Sorry, the requested page <b>{{ fullPath }}</b> could not be found.</p>
      </div>
    </div>
  </div>
  <custom-footer/>
</template>

<script setup lang="ts">
import application from '@/application.json'
import { computed, ref } from 'vue'
import CustomFooter from '@/shared/views/CustomFooter.vue'
import { useRoute } from 'vue-router'

const supportEmail = computed(() => application.supportEmail)

const route = useRoute()
const fullPath = ref(route.fullPath)
</script>
