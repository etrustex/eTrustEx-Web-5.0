<template>

  <div>
    <div class="card">
      <nav class="card-header">
        <ul class="nav nav-tabs card-header-tabs justify-content-start">
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: CONFIGURATIONS_ROUTE }">Configurations</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: ADMINISTRATORS_ROUTE }">Administrators</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: ENTITIES_ROUTE }">Entities</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: USERS_ROUTE }">Users</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: SYS_OFFICIALS_IN_CHARGE }">Officials in charge</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: CERTIFICATES_ROUTE }">Certificates</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: SYSTEM_LOGS_ROUTE }">System Logs</router-link>
          </li>
        </ul>
      </nav>
      <div class="card-body">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import useAuthenticationStore from '@/shared/store/authentication'
import useGroupStore from '@/admin/store/group'

const authenticationStore = useAuthenticationStore()
const groupStore = useGroupStore()

const ADMINISTRATORS_ROUTE = AdminRouteNames.SYS_ADMINS
const ENTITIES_ROUTE = AdminRouteNames.SYS_ENTITIES
const USERS_ROUTE = AdminRouteNames.SYS_USERS
const CERTIFICATES_ROUTE = AdminRouteNames.SYS_CERTIFICATES
const SYSTEM_LOGS_ROUTE = AdminRouteNames.SYS_LOGS
const CONFIGURATIONS_ROUTE = AdminRouteNames.ROOT_CONFIGURATIONS
const SYS_OFFICIALS_IN_CHARGE = AdminRouteNames.SYS_OFFICIALS_IN_CHARGE
</script>
