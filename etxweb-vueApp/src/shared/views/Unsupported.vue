<template>
  <div class="container mt-4">
    <h3 class="font-weight-bold">The browser you are using is not supported by the application.</h3>
    <h4>Please use one of the following browsers with the minimum version specified as in the table below:</h4>
    <table class="w-50 mx-auto mt-4 border">
      <tr class="border">
        <td class="py-3 px-5 border text-center"><img alt="" src="@/assets/ico/chrome_ico.png" style="width: 24px;"></td>
        <td class="px-5 border">Google Chrome</td>
        <td class="px-5 border">{{ chromiumVersion }}</td>
      </tr>
      <tr class="border">
        <td class="py-3 px-5 border text-center"><img alt="" src="@/assets/ico/firefox_ico.png" style="width: 24px;"></td>
        <td class="px-5 border">Mozilla Firefox</td>
        <td class="px-5 border">{{ firefoxVersion }}</td>
      </tr>
      <tr class="border">
        <td class="py-3 px-5 border text-center"><img alt="" src="@/assets/ico/edge_ico.png" style="width: 24px;"></td>
        <td class="px-5 border">Microsoft Edge</td>
        <td class="px-5 border">{{ chromiumVersion }}</td>
      </tr>
    </table>
  </div>
  <custom-footer/>
</template>

<script setup lang="ts">
import CustomFooter from '@/shared/views/CustomFooter.vue'
import { firefoxVersion, SUPPORTED_CHROMIUM, SUPPORTED_FIREFOX } from '@/utils/browserUtils'

const chromiumVersion = SUPPORTED_CHROMIUM
const firefoxVersion = SUPPORTED_FIREFOX
</script>
