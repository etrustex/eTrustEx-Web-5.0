<template>
  <div class="modal fade" :class="{'show d-block': loaderVisible}" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-body">
          <div class="d-flex flex-column align-items-center text-center">
            <div class="loader"/>
            <p class="mt-3 mb-0">Loading...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">

import { defineComponent } from 'vue'
import useLoaderStore from '@/shared/store/loader'

export default defineComponent({
  name: 'Loader',
  computed: {
    loaderVisible: () => useLoaderStore().loaderVisible
  }
})
</script>

<style>
.loader {
  margin:0 auto;
  width: 40px;
  height: 40px;
  border: 8px solid #F2F5F9;
  border-top: 8px solid rgb(243, 243, 243);
  border-radius: 50%;
  -webkit-animation: spin 1s linear infinite; /* Safari */
  animation: spin 1s linear infinite;
}

/* Safari */
@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
