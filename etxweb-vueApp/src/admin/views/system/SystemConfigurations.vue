<template>
  <div>
    <div class="container-fluid mt-3">
      <div class="row justify-content-between">
        <div class="col-lg-8">
          <alert-details id="notification_banner" :business="rootGroup"></alert-details>
          <hr>
          <user-guides id="user_guides" :business="rootGroup"></user-guides>
        </div>
        <div class="col-lg-3">
          <div class="sticky-top">
            <h3 class="h6 mb-3 pb-3 border-bottom">On this page</h3>
            <nav aria-label="Table of contents" class="nav-table-of-contents">
              <ul class="list-unstyled">
                <li>
                  <router-link :to="CONFIGURATIONS_ROUTE" @click="scrollPageTo('notification_banner')">Notification
                    banner
                  </router-link>
                </li>
                <li>
                  <router-link :to="CONFIGURATIONS_ROUTE" @click="scrollPageTo('user_guides')">User guides</router-link>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { scrollPageTo } from '@/utils/scroll'
import AlertDetails from '@/admin/views/shared/AlertDetails.vue'
import UserGuides from '@/admin/views/shared/configurations/UserGuides.vue'
import { PropType } from 'vue'
import { Group } from '@/model/entities'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'

const props = defineProps({
  rootGroup: { type: Object as PropType<Group>, required: true }
})

const CONFIGURATIONS_ROUTE = AdminRouteNames.ROOT_CONFIGURATIONS

</script>
