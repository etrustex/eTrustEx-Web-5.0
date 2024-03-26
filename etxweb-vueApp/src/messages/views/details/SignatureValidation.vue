<template>
  <div v-if="_rawMessageSummary?.signature">
    <button v-if="isTampered" class="btn btn-link text-danger" @click="downloadSignedContent()">
      <span class="ico-conjunction">
        <i class="ico-error"></i>
        Untrusted message!
      </span>
    </button>
    <button v-else class="btn btn-link text-success" @click="downloadSignedContent()">
      <span class="ico-conjunction">
        <i class="ico-check-circle-success"></i>
        Signed
      </span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { formatDateTime } from '@/utils/formatters'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import { JwsHelper } from '@/utils/jwsHelper'
import { storeToRefs } from 'pinia'
import useMessageStore from '@/messages/store/message'
import { cloneDeep } from '@/shared/views/bootstrap_vue/utils/object'
import { HttpRequest } from '@/utils/httpRequest'
import set = Reflect.set

const dialogStore = useDialogStore()
const { _rawMessageSummary, selectedMessage } = storeToRefs(useMessageStore())

const isTampered = ref(false)

onMounted(() => displayTamperedModalIfTampered())
onUnmounted(() => dialogStore.$reset())

async function displayTamperedModalIfTampered() {
  isTampered.value = !!_rawMessageSummary?.value?.signature && !(await JwsHelper.verifyMessage(_rawMessageSummary.value))
  if (isTampered.value) {
    displayTamperedModal()
  }
}

function displayTamperedModal() {
  const message = '<p><strong>The signature failed validation!</strong></p>' +
    '<p>The content of this message might have been tampered. ' +
    'It is <strong>strongly suggested not to download the attachments</strong> of this message as they could have been altered.</p>' +
    '<p>Proceed at your own risk.</p>'
  dialogStore.show('Signature validation failed!', message, DialogType.ERROR)
}

function downloadSignedContent() {
  if (!_rawMessageSummary?.value || !selectedMessage?.value) {
    return
  }

  const copyRawMessage = cloneDeep(_rawMessageSummary.value)
  const { message } = copyRawMessage
  message.text = selectedMessage.value.text // unencrypted text

  const signedContent = JSON.parse(copyRawMessage.signature)
  set(signedContent, 'payload', JwsHelper.toVerifiableFields(copyRawMessage))

  const fileName = formatDateTime(message.sentOn) + '_' + message.senderGroup.name + '.json'
  HttpRequest.downloadJson(signedContent, fileName)
}
</script>
