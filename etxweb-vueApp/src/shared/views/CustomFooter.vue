<template>
  <footer class="footer-main">
    <div class="container-fluid" @dragover.prevent @drop.stop.prevent>
      <div class="col">
        <ul>
          <li>Contact support: <strong>placeholder@example.com</strong></li>
          <li>European Commission - eTrustEx Web {{ version }} - {{ timeStamp }}</li>
        </ul>
      </div>
    </div>
  </footer>
</template>
<script lang="ts">
import { defineComponent } from 'vue'
import application from '@/application.json'
import { Environment } from '@/model/environment'
import useSettingsStore from '@/shared/store/settings'
import { formatDate } from '@/utils/formatters'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { storeToRefs } from 'pinia'

export default defineComponent({
  name: 'CustomFooter',
  computed: {
    supportEmail: () => {
      const { supportEmailGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
      return supportEmailGroupConfiguration.value.stringValue
        ? supportEmailGroupConfiguration.value.stringValue
        : application.supportEmail.toString()
    }
  },
  setup() {
    const settingsStore = useSettingsStore()
    const version = application.build.info.replace('release-', '') + ' - ' + settingsStore.env.toUpperCase()
    const timeStamp = formatDate(settingsStore.env === Environment.DEV ? new Date() : new Date(application.build.timestamp))


    return {
      version, timeStamp
    }
  }
})
</script>
