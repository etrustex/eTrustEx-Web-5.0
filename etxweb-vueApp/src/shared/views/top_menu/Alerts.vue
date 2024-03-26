<template>
  <div class="container">
    <div class="row" v-for="alert in visibleAlerts" :key="alert.group.identifier">
      <div class="col">
        <div role="alert" class="alert alert-dismissible show" :class="`alert-${alert.type.toLowerCase()}`">
          <h3 class="h4">{{ alert.title }}</h3>
          <p class="mb-0 trix-content" v-html="alert.content"></p>
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"
                  v-on:click="close(alert)">
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Alert, AlertUserStatusSpec, AlertUserStatusType } from '@/model/entities'
import useAlertStore, { CERTIFICATE_EXPIRATION } from '@/shared/store/alert'
import useAlertUserStatusStore from '@/shared/store/alertUserStatus'
import { AlertUserStatusApi } from '@/shared/api/alertUserStatusApi'
import useAuthenticationStore from '@/shared/store/authentication'
import { storeToRefs } from 'pinia'

const route = useRoute()
const authenticationStore = useAuthenticationStore()
const alertUserStatusStore = useAlertUserStatusStore()

const { alerts } = storeToRefs(useAlertStore())
const { alertUserStatuses } = storeToRefs(alertUserStatusStore)

const certificateExpirationAlert = computed(() => alerts.value.find(a => a.title === CERTIFICATE_EXPIRATION))
const visibleAlerts = computed(() => {
  if (authenticationStore.isAdminRoute(route)) {
    return certificateExpirationAlert.value ? [certificateExpirationAlert.value] : []
  } else {
    return alertUserStatuses.value.length !== 0
      ? alerts.value.filter(alert => !alertUserStatuses.value.find(alertUserStatus => alertUserStatus.alert.id === alert.id) && alert.active)
      : alerts.value
  }
})

function close(alert: Alert) {
  const alertUserStatusSpec: AlertUserStatusSpec = new AlertUserStatusSpec()
  alertUserStatusSpec.alertId = alert.id
  alertUserStatusSpec.groupId = alert.group.id
  alertUserStatusSpec.status = AlertUserStatusType.UNREAD
  AlertUserStatusApi.create(alertUserStatusSpec).then(
    value => alertUserStatusStore.add(value)
  )
}
</script>
