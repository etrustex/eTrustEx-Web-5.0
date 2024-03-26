<template>
  <div class="container-fluid">
    <div class="row justify-content-between">
      <div class="col-lg-8">
        <welcome-configuration id="welcome_notification" :business="business"/>
        <hr>
      </div>

      <div class="col-lg-3">
        <div class="sticky-top">
          <h3 class="h6 mb-3 pb-3 border-bottom">On this page</h3>
          <nav aria-label="Table of contents" class="nav-table-of-contents">
            <ul class="list-unstyled">
              <li>
                <router-link :to="templatesRoute" @click="scrollPageTo('welcome_notification')">Welcome notification
                </router-link>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { PropType } from 'vue'
import { scrollPageTo } from '@/utils/scroll'
import useAuthenticationStore from '@/shared/store/authentication'
import { Group } from '@/model/entities'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import WelcomeConfiguration from '@/admin/views/businesses/business/configurations/WelcomeConfiguration.vue'

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const authenticationStore = useAuthenticationStore()
const templatesRoute = { name: AdminRouteNames.BUSINESS_TEMPLATES }

</script>
