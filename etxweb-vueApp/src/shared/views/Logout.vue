<template>
  <div class="container">
    <div class="row">
      <div class="col-12 mt-5 mb-5 text-center">
        <div class="ico-pins-container">
          <span class="ico-pins"></span>
          <i aria-hidden="true" title="Successfully logged out" class="ico-check"></i>
        </div>
        <h2 class="mt-3">Successfully logged out
          <span class="d-block">from eTrustEx Web.</span></h2>
        <p class="mt-3">You may consider to:</p>
        <ul class="list-unstyled">
          <li>
            <a v-on:click="toLogOutUrl()" class="link-external">Logout from EU Login</a>
          </li>
          <li>
            <a v-on:click="toBaseUrl()" class="link-external">Log back into eTrustEx Web</a>
          </li>
        </ul>
      </div>
    </div>
  </div>
  <custom-footer/>
</template>

<script setup lang="ts">
import { baseUrl } from '@/shared/store/rootLinks'
import CustomFooter from '@/shared/views/CustomFooter.vue'

import useSettingsStore from '@/shared/store/settings'

function toLogOutUrl() {
  location.href = 'https://cas/logout.cgi'
}

function toBaseUrl() {
  location.href = baseUrl()
}

</script>
