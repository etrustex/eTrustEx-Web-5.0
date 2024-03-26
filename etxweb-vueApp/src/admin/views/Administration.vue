<template>
  <div class="container-fluid">
    <nav v-if="isSystemAdmin" aria-label="Primary" class="admin-nav-primary">
      <ul class="nav nav-tabs justify-content-center pt-3">
        <li class="nav-item">
          <router-link class="nav-link" :to="{ name: BUSINESSES_ROUTE }">Businesses</router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :to="{ name: SYSTEM_ROUTE }">System</router-link>
        </li>
      </ul>
    </nav>
    <div class="row">
      <div class="col-12">
        <breadcrumbs/>
      </div>
    </div>
    <div class="row">
      <div class="col-12">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ComputedRef, onBeforeMount } from 'vue'
import useAuthenticationStore from '@/shared/store/authentication'
import useRootLinksStore from '@/shared/store/rootLinks'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import Breadcrumbs from '@/shared/views/breadcrumbs/Breadcrumbs.vue'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'

const BUSINESSES_ROUTE = AdminRouteNames.BUSINESSES
const SYSTEM_ROUTE = AdminRouteNames.SYSTEM
const authenticationStore = useAuthenticationStore()
const isSystemAdmin: ComputedRef<boolean> = computed(() => authenticationStore.isSysAdmin())
const currentGroupStore = useCurrentGroupStore()

onBeforeMount(() => {
  if (authenticationStore.isSysAdmin()) {
    const rootLinksStore = useRootLinksStore()

    rootLinksStore.fetchAdminLinks()
      .then(() => currentGroupStore.systemAdmin())
  }
})
</script>
