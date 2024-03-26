<template>
  <div v-if="logged">
    <h1>User not registered</h1>
    You are not authorized to use eTrustEx Web application.
  </div>
  <custom-footer/>
</template>

<script setup lang="ts">
import application from '@/application.json'
import { computed } from 'vue'
import useOpenIdStore from '@/shared/store/openId'
import CustomFooter from '@/shared/views/CustomFooter.vue'

const supportEmail = computed(() => application.supportEmail)
const logged = computed(() => useOpenIdStore().idToken.length > 0)
</script>
