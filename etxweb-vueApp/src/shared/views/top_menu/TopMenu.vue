<template>
  <header class="header-main">
    <div class="container-fluid" @dragover.prevent @drop.stop.prevent>
      <div class="row">
        <div class="col-md-12">
          <div class="header-main-wrapper" v-if="authenticated">
            <div class="header-main-logos">
              <img v-if="logoGroupConfiguration.stringValue" alt="" class="logo-ec"
                   :src="logoGroupConfiguration.stringValue">
              <img v-else alt="" class="logo-ec" src="@/assets/img/logo-ec-header-main.svg">
              <span v-if="isAdminRoute">
                <router-link :to="adminRoot" aria-label="European Commission eTrustEx Web administration"
                             class="branding">eTrustEx Web<br>Administration</router-link>
              </span>
              <span v-else>
                <a :href="baseUrl()" aria-label="European Commission eTrustEx Web" class="branding">eTrustEx<br>Web</a>
              </span>
            </div>

            <nav v-if="showEntitySelector" aria-label="primary" class="nav-primary">
              <entity-selector></entity-selector>
            </nav>

            <nav aria-label="parameters" class="nav-parameter">
              <ul>
                <li v-if="showCertificates">
                  <certificates-dropdown></certificates-dropdown>
                </li>
                <li v-if="username">
                  <status-dropdown></status-dropdown>
                </li>
                <li>
                  <guides-dropdown></guides-dropdown>
                </li>
                <li v-if="showEntitySelector">
                  <div class="nav-parameter-sidebar-alert">
                    <a class="position-relative ico-standalone" href="javascript:void(0);" @click="toggleExpandAlert">
                      <i class="ico-notification" aria-label="open notifications" title="open notifications"></i>
                      <img alt="" v-if="unreadNotification" src="@/assets/ico/rounded-error-danger.svg">
                    </a>
                  </div>
                </li>
              </ul>
            </nav>
          </div>

          <div v-else aria-label="European Commission eTrustEx Web" class="branding">
            eTrustEx Web
          </div>
        </div>
      </div>
    </div>

    <alerts v-if="showAlerts"></alerts>
    <alert-list v-if="displayNotifications"></alert-list>
  </header>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { AlertUserStatusType } from '@/model/entities'
import { baseUrl } from '@/shared/store/rootLinks'
import useOpenIdStore from '../../store/openId'
import useAuthenticationStore from '@/shared/store/authentication'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import useAlertUserStatusStore from '@/shared/store/alertUserStatus'
import { routeNames } from '@/router'
import EntitySelector from '@/shared/views/top_menu/EntitySelector.vue'
import CertificatesDropdown from '@/shared/views/top_menu/CertificatesDropdown.vue'
import StatusDropdown from '@/shared/views/top_menu/StatusDropdown.vue'
import GuidesDropdown from '@/shared/views/top_menu/GuidesDropdown.vue'
import useCertificateStore from '@/shared/store/certificate'
import AlertList from '@/shared/views/top_menu/AlertList.vue'
import Alerts from '@/shared/views/top_menu/Alerts.vue'
import { storeToRefs } from 'pinia'

const route = useRoute()
const alertUserStatusStore = useAlertUserStatusStore()
const authenticationStore = useAuthenticationStore()

const authenticated = computed(() => useOpenIdStore().authenticated)
const adminRoot = computed(() => useAuthenticationStore().getAdminRoot())
const { logoGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
const showCertificates = computed(() => useCertificateStore().identities?.length > 0)
const isAdminRoute = computed(() => authenticationStore.isAdminRoute(route))
const username = computed(() => authenticationStore.username)

const showAlerts = computed(() => authenticated.value && !(routeNames.FORBIDDEN === route.name || routeNames.NOT_FOUND === route.name))
const showEntitySelector = computed(() =>
  authenticationStore.userEntities.length &&
  !(isAdminRoute.value ||
    routeNames.FORBIDDEN === route.name ||
    routeNames.NOT_FOUND === route.name ||
    routeNames.USER_REGISTRATION_REQUEST === route.name ||
    routeNames.UPDATE_CERTIFICATE_REQUEST === route.name
  ))
const unreadNotification = computed(() => alertUserStatusStore.alertUserStatuses.filter(value => value.status === AlertUserStatusType.UNREAD).length > 0)
const displayNotifications = computed(() => alertUserStatusStore.displayNotifications && authenticated.value && alertUserStatusStore.alertUserStatuses.length > 0)

const toggleExpandAlert = () => {
  alertUserStatusStore.setDisplayNotifications(!alertUserStatusStore.displayNotifications)
}

</script>
