<template>
  <div>
    <h1 v-if="displayBusinessCardTitle" class="title-primary text-break"><span>Business</span>{{ businessName }}</h1>
    <div class="card">
      <nav v-if="displayBusinessCardTitle" class="card-header">
        <ul class="nav nav-tabs card-header-tabs justify-content-start">
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessOverview">Overview</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessConfigurations">Configurations</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessTemplates">Templates</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessAdministrators">Administrators</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessQuickStart">Initial Setup</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessChannels">Exchange Configurations</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="businessEntities">Entities</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessUsers">Users</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessRegistrationRequests">Registration requests</router-link>
          </li>
          <li class="nav-item" v-if="isBusinessAdmin">
            <router-link class="nav-link" :to="businessMessagesDisplay">Messages display</router-link>
          </li>
        </ul>
      </nav>
      <div class="card-body">
        <router-view :key="route.fullPath"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ComputedRef } from 'vue'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import useAuthenticationStore, { AdminType } from '@/shared/store/authentication'
import useGroupStore from '@/admin/store/group'

const businessOverview = { name: AdminRouteNames.BUSINESS_OVERVIEW }
const businessConfigurations = { name: AdminRouteNames.BUSINESS_CONFIGURATIONS }
const businessTemplates = { name: AdminRouteNames.BUSINESS_TEMPLATES }
const businessAdministrators = { name: AdminRouteNames.BUSINESS_ADMINISTRATORS }
const businessChannels = { name: AdminRouteNames.BUSINESS_CHANNELS }
const businessEntities = { name: AdminRouteNames.ENTITIES }
const businessQuickStart = { name: AdminRouteNames.BUSINESS_QUICK_START }
const businessUsers = { name: AdminRouteNames.BUSINESS_USERS }
const businessRegistrationRequests = { name: AdminRouteNames.BUSINESS_USER_REGISTRATION_REQUESTS }
const businessMessagesDisplay = { name: AdminRouteNames.BUSINESS_MESSAGE_SUMMARY_LIST_ITEMS }

const route = useRoute()
const authenticationStore = useAuthenticationStore()
const groupStore = useGroupStore()
const isBusinessAdmin: ComputedRef<boolean> = computed(() => authenticationStore.isSysAdmin() || authenticationStore.isBusinessAdmin())
const businessName = computed(() => groupStore.business.name)
const displayBusinessCardTitle = computed(() => authenticationStore.adminType() === AdminType.SYSTEM || authenticationStore.adminType() === AdminType.MULTIPLE_BUSINESS || authenticationStore.adminType() === AdminType.SINGLE_BUSINESS)

onBeforeRouteUpdate((to, from, next) => {
  if (to.params.businessId !== from.params.businessId) {
    groupStore.fetchCurrentBusiness(+to.params.businessId)
      .then(() => {
        to.meta.displayName = () => groupStore.business.name
      })
      .then(() => next())
  } else {
    next()
  }
})

</script>
