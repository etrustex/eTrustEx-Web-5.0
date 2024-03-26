<template>
  <div class="container-fluid mt-3">
    <div class="row">
      <div class="col col-lg-9">
        <div class="container-fluid my-5">
          <div class="row mt-3">
            <div class="col d-flex justify-content-between align-items-md-center">
              <div class="d-flex justify-content-between align-items-md-center">
                <h2 class="h3 variant">Generate Access Token</h2>
              </div>
            </div>
          </div>
          <textarea v-model.trim="accessToken" disabled rows="10" cols="100" id="access_token"/>
          <button type="button" class="btn btn-primary" v-on:click="newAccessToken">New Access Token</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import useOpenIdStore from '@/shared/store/openId'

const openIdStore = useOpenIdStore()
const accessToken = ref('')
onMounted(() => newAccessToken() )

async function newAccessToken(): Promise<void> {
  const at = await openIdStore.getAccessToken({})
  accessToken.value = at.token_type + ' ' + openIdStore.signAccessToken(at.access_token)
}

</script>
