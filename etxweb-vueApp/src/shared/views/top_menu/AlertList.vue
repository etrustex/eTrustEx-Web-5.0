<template>
  <div class="sidebar-alert">
    <h3>Notifications</h3>
    <ul v-for="alertUsersStatus in visibleAlerts" :key="alertUsersStatus.alert.group.identifier">
      <li>
        <div role="alert" class="alert alert-dismissible show"
             :class="`alert-${alertUsersStatus.alert.type.toLowerCase()}`">
          <div class="alert-header">
            <h4 class="alert-title">{{ alertUsersStatus.alert.title }}</h4>
            <button class="btn btn-primary alert-btn" @click="close(alertUsersStatus)">
              {{ alertUsersStatus.status }}
            </button>
          </div>
          <p v-html="alertUsersStatus.alert.content"></p>
        </div>
      </li>
    </ul>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { AlertUserStatus, AlertUserStatusType } from '@/model/entities'
import { useRoute } from 'vue-router'
import useAlertStore, { CERTIFICATE_EXPIRATION } from '@/shared/store/alert'
import useAlertUserStatusStore from '@/shared/store/alertUserStatus'
import useAuthenticationStore from '@/shared/store/authentication'
import { AlertUserStatusApi } from '@/shared/api/alertUserStatusApi'
import { storeToRefs } from 'pinia'

const route = useRoute()
const authenticationStore = useAuthenticationStore()

const { alerts } = storeToRefs(useAlertStore())
const { alertUserStatuses } = storeToRefs(useAlertUserStatusStore())

const certificateExpirationAlert = computed(() => alerts.value.find(a => a.title === CERTIFICATE_EXPIRATION))
const visibleAlerts = computed(() => authenticationStore.isAdminRoute(route)
  ? (certificateExpirationAlert.value ? [certificateExpirationAlert.value] : [])
  : alertUserStatuses.value.filter(a => a.alert.active))

function close(alertUsersStatus: AlertUserStatus) {
  if (alertUsersStatus.status === AlertUserStatusType.UNREAD) {
    alertUsersStatus.status = AlertUserStatusType.READ
    AlertUserStatusApi.update(alertUsersStatus).then()
  }
}
</script>
