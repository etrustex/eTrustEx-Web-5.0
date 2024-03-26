<template>
  <div class="modal fade" :class="{'show d-block': config.show}" tabindex="-1">
    <div class="modal-dialog" :class="{'modal-lg': config.isSplash}">
      <div class="modal-content">
        <div class="modal-body" :class="typeClasses">
          <div v-if="config.type === DialogType.INFO">
            <h2 v-if="config.title" class="h5 mb-0 ico-pins-container justify-content-start p-0">
              <i v-if="!config.hideIcon" aria-hidden="true" class="ico-info-primary me-1"></i>
              <span>{{ config.title }}</span>
            </h2>
            <p class="mb-0"><span v-html="config.message"/></p>
          </div>
          <div v-if="config.type === DialogType.ERROR">
            <div v-if="!config.hideIcon" class="ico-pins-container">
              <span class="ico-pins"></span>
              <i aria-hidden="true" class="ico-delete"></i>
            </div>

            <h2 class="h5 mb-0">{{ config.title }}</h2>
            <p class="mt-3 mb-0"><span class="w-100 d-inline-block" v-html="config.message"/></p>
          </div>
        </div>
        <div v-if="primaryButton.show || secondaryButton.show" class="modal-footer" :class="typeClasses">
          <div class="btns">
            <button v-if="secondaryButton.show" type="button" :class="secondaryClasses"
                    v-on:click="secondaryButtonClick(); $event.preventDefault();">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
              <span>{{ secondaryButton.title }}</span>
            </button>
            <button v-if="primaryButton.show" type="button" :class="primaryClasses"
                    v-on:click="primaryButtonClick(); $event.preventDefault();">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-done-white"></i></span>
              <span>{{ primaryButton.title }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import { storeToRefs } from 'pinia'

export default defineComponent({
  name: 'CustomDialog',
  setup() {
    const dialogStore = useDialogStore()
    const { config, primaryButton, secondaryButton } = storeToRefs(dialogStore)
    const typeClasses = computed(() => {
      switch (config.value.type) {
      case DialogType.INFO:
        return 'bg-info'
      case DialogType.ERROR:
        return 'bg-error'
      }
    })
    const primaryClasses = computed(() => {
      switch (config.value.type) {
      case DialogType.INFO:
        return 'btn btn-primary btn-ico'
      case DialogType.ERROR:
        return 'btn btn-danger'
      }
    })
    const secondaryClasses = computed(() => {
      switch (config.value.type) {
      case DialogType.INFO:
        return 'btn btn-outline-secondary btn-ico'
      case DialogType.ERROR:
        return 'btn btn-outline-secondary'
      }
    })

    function primaryButtonClick() {
      dialogStore.hide()
      if (primaryButton.value.callback) primaryButton.value.callback()
    }

    function secondaryButtonClick() {
      dialogStore.hide()
      if (secondaryButton.value.callback) secondaryButton.value.callback()
    }

    return {
      config,
      primaryButton,
      secondaryButton,
      typeClasses,
      primaryClasses,
      secondaryClasses,
      primaryButtonClick,
      secondaryButtonClick,
      DialogType,
    }
  },
})
</script>
