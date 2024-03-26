<template>
  <div class="container" v-if="logged">
    <div class="row">
      <div class="col-12 mt-5 mb-5 text-center">
        <div class="ico-pins-container">
          <span class="ico-pins"></span>
          <i aria-hidden="true" title="Access Denied / Forbidden" class="ico-https"></i>
        </div>
        <h2 class="mt-3">Access Denied / Forbidden</h2>
        <p class="mt-3">Sorry, you do not have permission to access the requested page.</p>
      </div>
    </div>
  </div>
  <custom-footer/>
</template>

<script setup lang="ts">
import application from '@/application.json'
import { computed } from 'vue'
import CustomFooter from '@/shared/views/CustomFooter.vue'
import useAuthenticationStore from '@/shared/store/authentication'

const supportEmail = computed(() => application.supportEmail)
const logged = computed(() => !!useAuthenticationStore().username)
</script>
