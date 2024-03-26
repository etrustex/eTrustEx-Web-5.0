<template>
  <button type="button" class="btn btn-primary" v-on:click="submitDocument">Submit Wrappers and Bundle</button>
</template>

<script setup lang="ts">
import useRootLinksStore from '@/shared/store/rootLinks'
import { Rels } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'

function submitDocument(): void {
  const link = useRootLinksStore()
    .link(Rels.INTEGRATION_TEST_SUBMIT_DOCUMENT)
  console.log('Document bundle submitted')
  console.log(link)
  HttpRequest.get(link)
    .then(() => console.log('Document bundle submitted'))
}
</script>
