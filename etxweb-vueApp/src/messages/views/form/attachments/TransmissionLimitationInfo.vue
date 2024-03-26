<template>
  <div class="mt-3">
    <div v-if="configuredLimitations" class="alert alert-info">
      <h2 class="h5">Limitations per transmission:</h2>
      <div>
        <ul>
          <li v-for="limit in limitations" :key="limit">
            <strong>{{ limit }}</strong>
          </li>
        </ul>
      </div>
    </div>
    <div v-else class="alert alert-info">
      <div v-if="isNodeConfigured">
        <h2 class="h5">Limitations per transmission:</h2>
        <div>
          <ul>
            <li><strong>Up to 1GB per file</strong></li>
            <li><strong>Up to 5GB total size</strong></li>
            <li><strong>Up to 1500 files</strong></li>
          </ul>
        </div>
      </div>
      <div v-else>
        <h2 class="h5">Limitations per transmission:</h2>
        <div>
          <ul>
            <li><strong>Up to 1GB per file</strong></li>
            <li><strong>Up to 5GB total size</strong></li>
            <li><strong>Up to 1500 files</strong></li>
          </ul>
          <p class="mb-0 trix-content">These limitations are a guideline and any value above the ones listed will have a
            direct impact on the performance of the application. </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { computed } from 'vue'
import { formatBytes } from '@/utils/formatters'

const props = defineProps({
  isNodeConfigured: { type: Boolean }
})

const businessConfigurationStore = useBusinessConfigurationStore()
const fileSizeLimitation = computed(() => (businessConfigurationStore.fileSizeLimitationGroupConfiguration))
const totalFileSizeLimitation = computed(() => (businessConfigurationStore.totalFileSizeLimitationGroupConfiguration))
const numberOfFilesLimitation = computed(() => (businessConfigurationStore.numberOfFilesLimitationGroupConfiguration))
const configuredLimitations = computed(() => (fileSizeLimitation.value.active || totalFileSizeLimitation.value.active ||
  numberOfFilesLimitation.value.active))
const limitations = computed(() => {
  const limit: Array<string> = []
  if (fileSizeLimitation.value.active) {
    limit.push(`Up to ${formatBytes(toBytes(fileSizeLimitation.value.integerValue), false)} per file`)
  }
  if (totalFileSizeLimitation.value.active) {
    limit.push(`Up to ${formatBytes(toBytes(totalFileSizeLimitation.value.integerValue), false)} total size`)
  }
  if (numberOfFilesLimitation.value.active) {
    limit.push(`Up to ${numberOfFilesLimitation.value.integerValue} files`)
  }
  return limit
})

function toBytes(value: number) {
  return value * 1024 * 1024
}
</script>
