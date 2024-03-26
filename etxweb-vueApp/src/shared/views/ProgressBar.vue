<template>
  <div class="modal fade" :class="{'show d-block': showProgress}" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-body">

          <div class="mt-3">
            <div>
              <div v-if="status === 'failed'" class="alert alert-danger pe-3 pb-3" role="alert">
                <p v-html="errorMessage"></p>
              </div>
            </div>
            <div class="ico-pins-container">
              <span class="ico-pins"></span>
              <i aria-hidden="true" :class="config.isUpload ? 'ico-upload' : 'ico-download'"></i>
            </div>
            <h2 class="h5 text-center">{{ config.title }}</h2>
            <p v-html="config.message"></p>
          </div>

          <div class="progress">
            <div class="progress-bar bg-primary" role="progressbar" aria-valuemin="0" aria-valuemax="100"
                 v-bind:aria-valuenow="progress * 100"
                 v-bind:style="{ width: (progress * 100)+'%'}">
            </div>
          </div>

          <div class="d-flex justify-content-end align-items-center mt-3">

            <div v-if="status === 'failed'">
              <button type="button" class="btn btn-primary btn-ico"
                      @click.exact="close">
                <span class="ico-standalone"><i aria-hidden="true" class="ico-done-white"></i></span>
                <span>Ok</span>
              </button>
            </div>

            <div v-else-if="status !== 'cancel'">
              <button type="button" class="btn btn-outline-secondary btn-ico"
                      @click.exact="cancel">
                <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
                <span>Cancel</span>
              </button>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import useProgressTrackerStore from '@/messages/store/progress'
import { storeToRefs } from 'pinia'

const progressStore = useProgressTrackerStore()
const { status, progress, showProgress, config } = storeToRefs(progressStore)

const errorMessage = 'An error has occurred. Please try again.<br> ' +
  'If the error persists please contact the support team: <a href=\'mailto:DIGIT-EU-SEND@ec.europa.eu\'>DIGIT-EU-SEND@ec.europa.eu</a>'

const cancel = () => {
  progressStore.status = 'cancel'
  if (config.value.progressCancelFunction) {
    config.value.progressCancelFunction().then(() => close())
  }
}

const close = () => {
  progressStore.status = 'idle'
}
</script>
