<template>
  <div class="container-fluid">
    <div class="row justify-content-between">
      <div class="col-lg-8">
        <sys-compromised-certificate id="compromised_certificate"/>
        <sys-update-certificate id="certificate_update"/>
      </div>

      <div class="col-lg-3">
        <div class="sticky-top">
          <h3 class="h6 mb-3 pb-3 border-bottom">On this page</h3>
          <nav aria-label="Table of contents" class="nav-table-of-contents">
            <ul class="list-unstyled">
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('compromised_certificate')">
                  Compromised Certificate
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('certificate_update')">
                  Certificate Update
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
import { scrollPageTo } from '@/utils/scroll'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import SysCompromisedCertificate from '@/admin/views/system/certificates/SysCompromisedCertificate.vue'
import SysUpdateCertificate from '@/admin/views/system/certificates/SysUpdateCertificate.vue'

const configurationRoute = { name: AdminRouteNames.SYS_CERTIFICATES }
</script>
