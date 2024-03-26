<template>
  <div>
    <button id="clean-up" type="button" class="btn btn-primary" v-on:click="cleanUp">Clean up after tests</button>
    <span id="result">{{ result }}</span>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import useRootLinksStore from '@/shared/store/rootLinks'
import { Rels } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import { ref } from 'vue'

const route = useRoute()
const result = ref('')

function cleanUp(): void {
  const { parentIdentifier, groupIdentifier } = route.params
  const link = useRootLinksStore()
    .link(Rels.TESTS_CLEAN_UP_DELETE)
  HttpRequest.delete(link, { params: { parentIdentifier, groupIdentifier } })
    .then(() => result.value = 'done')
    .catch(reason => result.value = 'failed. ' + reason)
}
</script>
