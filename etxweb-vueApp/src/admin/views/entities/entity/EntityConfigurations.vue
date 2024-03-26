<template>
  <div class="container-fluid">
    <entity-encryption-configuration v-if="showEncryptionConfiguration"/>
    <hr>
    <entity-message-retention-configuration/>
  </div>
</template>

<script setup lang="ts">

import EntityEncryptionConfiguration
  from '@/admin/views/entities/entity/configurations/EntityEncryptionConfiguration.vue'
import { computed } from 'vue'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import EntityMessageRetentionConfiguration
  from '@/admin/views/entities/entity/configurations/EntityMessageRetentionConfiguration.vue'

const showEncryptionConfiguration = computed(() => !useBusinessConfigurationStore().disableEncryptionGroupConfiguration.active)

const props = defineProps({
  entityId: { type: Number, required: true }
})
</script>
