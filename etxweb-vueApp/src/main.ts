import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'
import useAuthenticationStore from '@/shared/store/authentication'
import useSettingsStore from '@/shared/store/settings'
import useOpenIdStore from '@/shared/store/openId'
import useRootLinksStore from '@/shared/store/rootLinks'
import { register } from 'fetch-intercept'
import { CustomFetchInterceptor } from '@/utils/customFetchInterceptor'
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import { Buffer } from 'buffer'

window.Buffer = Buffer

const pinia = createPinia()
const app = createApp(App)
  .use(pinia)
app.directive('tooltip', (el, binding) => {
  if (binding.value) {
    const position = binding.arg || 'top'
    const tooltipText = binding.value
    el.setAttribute('position', position)
    el.setAttribute('tooltip', tooltipText)
  }
})

app.component('VueDatePicker', VueDatePicker)

// Global JS errors
window.onerror = (msg, url, line, col, error) => {
  console.error()
  console.error(url + '\n' + msg + '\n' + line + '\n' + col + '\n', error)
}

// Global VUE errors
app.config.errorHandler = (err, vm, info) => {
  // handle error
  // `info` is a Vue-specific error info, e.g. which lifecycle hook
  console.error('[Global Error Handler]: Error in ' + info + ': ' + err)
}
// Vue only warns in development mode
app.config.warnHandler = (msg, vm, trace) => {
  // `trace` is the component hierarchy trace
  console.warn(msg + ' trace: ' + trace)
}

/*
* This code chunk need to be executed before the router is mounted as it does some weird href stuff
* */
const settings = useSettingsStore()
settings.fetchEnvironment().then(envName => {
  const openIdStore = useOpenIdStore()
  const authenticationStore = useAuthenticationStore()
  openIdStore.fetchSettings(envName)
  openIdStore.loginOrRestore()
    .then(() => register(new CustomFetchInterceptor()))
    .then(() => {
      const rootLinksStore = useRootLinksStore()
      return rootLinksStore.fetchRootLinks().then(() => {
        if (authenticationStore.isSysAdmin()) {
          rootLinksStore.fetchAdminLinks().then()
        }
      })
    })
    .then(() => authenticationStore.loadUserDetails())
    .then(() => settings.generateKeyPair())
    .then(() => settings.fetchServerPublicKey())
    .finally(() => {
      app.use(router)
      app.mount('#app')
    })
})
