<template>
  <div>
    <div class="pt-3 pb-3 container-fluid">
      <div class="row">
        <div class="text-lg-end col-12 col-lg-3">
          Business Name
        </div>
        <div class="col-12 col-lg-6">
          <strong class="text-break">{{ entity.businessName }}</strong>
        </div>
      </div>

      <div class="row">

        <div class="mt-2 text-lg-end col-12 col-lg-3">
          Entity Identifier
        </div>
        <div class="mt-lg-2 col-12 col-lg-6">
          <strong class="text-break">{{ entity.entityIdentifier }}</strong>
        </div>
      </div>

      <div class="row">
        <div class="mt-2 text-lg-end col-12 col-lg-3">
          Entity Name
        </div>
        <div class="mt-lg-2 col-12 col-lg-6">
          <strong class="text-break">{{ entity.entityName }}</strong>
        </div>
      </div>

      <div class="row">
        <div class="mt-2 text-lg-end col-12 col-lg-3">
          Channel Name
        </div>
        <div class="mt-lg-2 col-12 col-lg-6">
          <strong class="text-break">{{ entity.channelName }}</strong>
        </div>
      </div>

      <div class="row">
        <div class="mt-2 text-lg-end col-12 col-lg-3">
          Role
        </div>
        <div class="mt-lg-2 col-12 col-lg-6">
          <strong class="text-break">{{ entity.exchangeMode }}</strong>
        </div>
      </div>

      <div class="row">
        <div class="mt-2 text-lg-end col-12 col-lg-3">
          Status
        </div>
        <div class="mt-lg-2 col-12 col-lg-6">
          <strong>{{ entity.active ? 'Active' : 'Inactive' }}</strong>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { EntityItem } from '@/model/entities'

const props = defineProps({
  entity: { type: Object as PropType<EntityItem> }
})
</script>
