<template>
  <div>
    <button id="clean-up" type="button" class="btn btn-primary" v-on:click="scan">Run UV Scan</button>
    <ul>
      <li v-for="l in result" :key="l" id="result">{{ l }}</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'
import { Rels } from '@/model/entities'
import { ref } from 'vue'

const result = ref<Array<string>>([])

function scan(): void {
  HttpRequest.post(useRootLinksStore()
    .link(Rels.UVSCAN_TEST))
    .then((result) => result.value = result)
    .catch(reason => result.value[0] = 'failed. ' + reason)
}
</script>
