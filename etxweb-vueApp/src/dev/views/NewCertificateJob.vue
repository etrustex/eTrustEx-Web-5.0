<template>
  <div class="container-fluid mt-3">
    <div v-if="showUpdatedMessagesFlag" class="row">
      <div class="col">
        <div class="alert alert-warning alert-dismissible">
          <h3 class="h4">Reset Successfully updated messages flag</h3>
          <p class="mb-0">Remember to reset UPDATED_WITH_NEW_CERTIFICATE flag once all messages symmetric keys have been
            successfully encrypted with the new
            certificate.</p>
          <p class="mb-0">You can do it by clicking on <strong>'Reset Successfully updated messages flag'</strong>
            button</p>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col col-lg-9">
        <div class="container-fluid my-5">
          <div class="row mt-3">
            <div class="col d-flex justify-content-between align-items-md-center">
              <div class="d-flex justify-content-between align-items-md-center">
                <h2 class="h3 variant">{{ message }}</h2>
              </div>
            </div>
          </div>
          <form>
            <div class="row mt-3">
              <div class="col col-lg-4 text-lg-right">
                New Certificate Ready?
              </div>
              <div class="col col-lg-8">
                <strong>{{ newServerCertificate.ready ? 'Yes' : 'No' }}</strong>
              </div>
            </div>

            <div class="row mt-3">
              <div class="col col-lg-4 text-lg-right">
                First notification sent?
              </div>
              <div class="col col-lg-8">
                <strong>{{ newServerCertificate.firstNotificationSent ? 'Yes' : 'No' }}</strong>
              </div>
            </div>

            <div class="row mt-3">
              <div class="col col-lg-4 text-lg-right">
                Second notification sent?
              </div>
              <div class="col col-lg-8">
                <strong>{{ newServerCertificate.secondNotificationSent ? 'Yes' : 'No' }}</strong>
              </div>
            </div>

            <div class="row mt-3">
              <div class="col col-lg-4 text-lg-right">
                Expiration date
              </div>
              <div class="col col-lg-8">
                <strong>{{ parseLocalDateTime(newServerCertificate.expirationDate) }}</strong>
              </div>
            </div>

            <div class="row mt-3">
              <div class="col col-lg-4 text-lg-right">
                Update certificate job status
              </div>
              <div class="col col-lg-8">
                <strong>{{ newServerCertificate.status }}</strong>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

  <button type="button" class="btn btn-primary" v-on:click="newCertificateJob">Launch New Certificate Job</button>
  <button type="button" class="btn btn-primary" v-on:click="updateStatus">Status</button>
  <button type="button" class="btn btn-primary" v-on:click="resetUpdatedFlag">Reset Successfully updated messages flag
  </button>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { toDateTime } from '@/utils/formatters'
import { NewServerCertificate, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

const rootLinkStore = useRootLinksStore()

const message = ref('Press Launch New Certificate Job button to launch the job or Status button to update its status ')
const newServerCertificate = ref(new NewServerCertificate())
const showUpdatedMessagesFlag = ref(true)

onMounted(() => updateStatus()
  .then())

function newCertificateJob(): void {
  updateStatus()
    .then(status => {
      if (!isRunning(status)) {
        HttpRequest.post(rootLinkStore.link(Rels.NEW_CERTIFICATE_JOB))
          .then(() => console.log('New Certificate launched'))
        showUpdatedMessagesFlag.value = true
        newServerCertificate.value.status = BatchStatus.STARTED
        message.value = 'Job launched. Press the Status button to refresh'
      } else {
        message.value = 'Job cannot be launched because it is running'
      }
    })
}

async function updateStatus(): Promise<BatchStatus> {
  newServerCertificate.value = await HttpRequest.get(rootLinkStore.link(Rels.NEW_CERTIFICATE))
  const status: BatchStatus = parseStatus(newServerCertificate.value.status)
  newServerCertificate.value.status = status
  message.value = 'Job is in status: ' + status

  return status
}

function resetUpdatedFlag() {
  HttpRequest.post(rootLinkStore.link(Rels.NEW_CERTIFICATE_RESET_UPDATED_FLAG))
    .then(() => showUpdatedMessagesFlag.value = false)
}

function parseLocalDateTime(date: Date) {
  if (date) {
    return toDateTime(date)
  } else {
    return ''
  }
}

enum BatchStatus {
  COMPLETED = 'COMPLETED',
  STARTING = 'STARTING',
  STARTED = 'STARTED',
  STOPPING = 'STOPPING',
  STOPPED = 'STOPPED',
  FAILED = 'FAILED',
  ABANDONED = 'ABANDONED',
  UNKNOWN = 'UNKNOWN'
}

function isRunning(status: BatchStatus): boolean {
  return status === BatchStatus.STARTING || status === BatchStatus.STARTED
}

function parseStatus(status: string): BatchStatus {
  return BatchStatus[status as keyof typeof BatchStatus]
}

</script>
