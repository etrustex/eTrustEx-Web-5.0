<template>
  <div>
    <progress-bar/>
    <custom-dialog/>
    <top-menu/>
    <main>
      <router-view/>
    </main>
    <custom-footer/>
    <loader/>
    <div id="modals"></div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, watchEffect } from 'vue'
import TopMenu from '@/shared/views/top_menu/TopMenu.vue'
import Loader from '@/shared/views/loader/Loader.vue'
import CustomDialog from '@/shared/views/dialog/CustomDialog.vue'
import useValidationStore from '@/shared/store/validation'
import useDialogStore, { buttonWithCallback, DialogType } from '@/shared/store/dialog'
import CustomFooter from '@/shared/views/CustomFooter.vue'
import ProgressBar from '@/shared/views/ProgressBar.vue'

export default defineComponent({
  name: 'DefaultLayout',
  components: { ProgressBar, CustomFooter, TopMenu, Loader, CustomDialog },
  setup() {
    const dialogStore = useDialogStore()
    const serverSideValidationErrors = computed(() => useValidationStore().serverValidationErrors)

    watchEffect(() => {
      if (serverSideValidationErrors.value.length > 0) {
        let message = '<ul class="mb-0">'
        useValidationStore().serverValidationErrors.forEach(value => {
          message += '<li>' + value + '</li>'
        })
        message += '</ul>'
        dialogStore.show(
          useValidationStore().title,
          message,
          DialogType.ERROR,
          buttonWithCallback('Ok', () => clearServerErrors()),
        )
      }
    })

    function clearServerErrors() {
      useValidationStore().setServerSideValidationErrors([])
    }
  },
})
</script>
