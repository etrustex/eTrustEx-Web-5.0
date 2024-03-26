<template>
  <div class="dropdown">
    <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown"
            aria-expanded="false">
      <span class="ico-standalone"><i class="ico-logged-in"></i></span>
      <span>{{ fullName }}</span>
    </button>
    <ul class="dropdown-menu">
      <li>
        <span class="dropdown-item">EU Login ID: <strong>{{ username }}</strong></span>
      </li>
      <li v-if="isSysAdmin">
        <span class="dropdown-item">BD: <strong>{{ currentGroup.businessIdentifier }}</strong></span>
      </li>
      <li v-if="isAdminRoute && entities.length">
        <a class="dropdown-item" v-on:click="toInbox()">Switch to inbox</a>
      </li>
      <li v-if="showAdminLink">
        <router-link class="dropdown-item" :to="adminRoot">Switch to administration</router-link>
      </li>
      <li>
        <router-link class="dropdown-item" :to="{ name: LOGOUT }">Logout</router-link>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import useAuthenticationStore from '@/shared/store/authentication'
import { useRoute } from 'vue-router'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { routeNames } from '@/router'

const LOGOUT = routeNames.LOGOUT

const route = useRoute()
const authenticationStore = useAuthenticationStore()
const currentGroupStore = useCurrentGroupStore()

const currentGroup = computed(() => currentGroupStore.group)
const username = computed(() => authenticationStore.username)
const fullName = computed(() => authenticationStore.fullName)
const entities = computed(() => authenticationStore.userEntities)
const adminRoot = computed(() => authenticationStore.getAdminRoot())
const isSysAdmin = computed(() => authenticationStore.isSysAdmin())
const isAdminRoute = computed(() => authenticationStore.isAdminRoute(route))

const showAdminLink = computed(() => !isAdminRoute.value && (authenticationStore.isAnAdmin() || authenticationStore.isOfficialInCharge()))

function toInbox() {
  location.href = window.location.pathname
}

</script>
