<template>
  <div>
    <entity-actions :group="entity"/>
    <entity-overview :group="entity"/>
    <hr class="my-4">
    <h3 class="variant">Settings</h3>
    <ul class="nav nav-tabs mt-3">
      <li class="nav-item">
        <router-link class="nav-link" :to="{ name: configurationsRoute }">Configurations</router-link>
      </li>
      <li class="nav-item">
        <router-link class="nav-link" :to="{ name: usersRoute }">Users</router-link>
      </li>
      <li class="nav-item">
        <router-link class="nav-link" :to="{ name: registrationRequestsRoute }">Registration requests</router-link>
      </li>
      <li class="nav-item">
        <router-link class="nav-link" :to="{ name: messagesMonitoringRoute }">Messages monitoring</router-link>
      </li>
    </ul>
    <div class="pt-4">
      <router-view/>
    </div>
  </div>
</template>

<script setup lang="ts">

import { computed, watch } from 'vue'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import useValidationStore from '@/shared/store/validation'
import EntityOverview from '@/admin/views/entities/entity/EntityOverview.vue'
import EntityActions from '@/admin/views/entities/entity/EntityActions.vue'
import { GroupApi } from '@/shared/api/groupApi'
import useGroupStore from '@/admin/store/group'

const props = defineProps({
  configurationsRoute: { type: String },
  usersRoute: { type: String },
  registrationRequestsRoute: { type: String },
  messagesMonitoringRoute: { type: String }
})

const route = useRoute()
const validationStore = useValidationStore()
const groupStore = useGroupStore()
const entity = computed({
  get: () => groupStore.entity,
  set: (value) => groupStore.setGroup(value)
})

watch(
  () => route.params.entityId,
  async (newId, previousId) => {
    if (newId && newId !== previousId) {
      entity.value = await GroupApi.get(+newId)
      route.meta.displayName = () => entity.value.name
    }
  }, { immediate: true, deep: true }
)

onBeforeRouteUpdate((to, from, next) => {
  validationStore.setServerSideValidationErrors([])
  next()
})
</script>
