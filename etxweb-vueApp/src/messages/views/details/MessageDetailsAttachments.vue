<template>
  <div class="message-detail-attachments">
    <div class="text-center">
      <h3 class="variant">List of files</h3>
      <ul class="list-unstyled">
        <li>
          <span aria-hidden="true" class="ico-conjunction">
            <i class="ico-attach message-detail-attachment-ico"></i>
            {{ message.attachmentTotalNumber + ' files ' }} - {{ formatBytes(message.attachmentsTotalByteLength) }}
          </span>
        </li>
        <li v-if="isEncrypted">
          <span aria-hidden="true" class="ico-conjunction">
            <i class="ico-encrypted-mandatory"></i>
            Encrypted
          </span>
        </li>
      </ul>
    </div>

    <div class="d-flex justify-content-end align-items-md-center btns">

      <button class="btn btn-primary" type="submit"
              @click="showCertificateSelectDialogIfEncryptedOrDownload(downloadFiles)">
        <span class="ico-conjunction">
          <i aria-hidden="true" class="ico-file-download-white"></i>
          Download
        </span>
      </button>

      <button class="ico-standalone" type="button"
              :title="showListView ? 'List view': 'Tree view'"
              :aria-label="showListView ? 'See list view': 'See tree view'"
              @click="toggleAttachmentsView()">
        <span aria-hidden="true" class="ico-standalone">
          <i :class="showListView ? 'ico-tree-view-primary': 'ico-list-view-primary'"></i>
        </span>
      </button>
    </div>

  </div>

  <div v-if="attachments.length > 0">
    <attachments-list v-if="showListView" :canDownload="true"
                      :attachments="attachmentSpecs"
                      :showAdditionalAttachmentActions="showAdditionalAttachmentActions"
                      @selected="selectAttachments($event)"
                      @download="showCertificateSelectDialogIfEncryptedOrDownload(() => downloadFile(downloadableAttachmentSpecByAttachmentId($event)))">
    </attachments-list>
    <attachments-tree v-else :canDownload="true"
                      :attachments="attachmentSpecs"
                      :showAdditionalAttachmentActions="showAdditionalAttachmentActions"
                      @selected="selectAttachments($event)"
                      @download="showCertificateSelectDialogIfEncryptedOrDownload(() => downloadFile(downloadableAttachmentSpecByAttachmentId($event)))">
    </attachments-tree>
  </div>

  <certificate-view v-if="showCertificate" ref="certificateView"></certificate-view>
</template>

<script lang="ts">
import { AttachmentSpec, EncryptionMethod, Message } from '@/model/entities'
import { formatBytes, formatZipFileName } from '@/utils/formatters'
import { DownloadableAttachmentSpec } from '@/model/attachmentDto'
import { computed, ComputedRef, defineComponent, onMounted, PropType, ref, Ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import useCertificateStore from '@/shared/store/certificate'
import useProgressTrackerStore from '@/messages/store/progress'
import { MessageBoxRouteNames } from '@/messages/router'
import AttachmentsList from '@/messages/views/details/attachments/AttachmentsList.vue'
import AttachmentsTree from '@/messages/views/details/attachments/AttachmentsTree.vue'
import CertificateView from '@/messages/views/CertificateView.vue'
import ProgressBar from '@/shared/views/ProgressBar.vue'
import useMessageStore from '@/messages/store/message'
import useDialogStore from '@/shared/store/dialog'
import useSettingsStore from '@/shared/store/settings'
import { RSA } from '@/utils/crypto/rsa'
import { AES } from '@/utils/crypto/aes'
import { AttachmentDownloadApi } from '@/messages/api/attachmentDownloadApi'

export default defineComponent({
  name: 'MessageDetailsAttachments',
  components: {
    CertificateView,
    ProgressBar,
    AttachmentsList,
    AttachmentsTree
  },
  props: {
    message: { type: Object as PropType<Message>, required: true },
    attachments: { type: Array as PropType<Array<DownloadableAttachmentSpec>>, required: true },
    isEncrypted: { type: Boolean },
    showCertificate: { type: Boolean },
    showAdditionalAttachmentActions: { type: Boolean, default: false }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    const messageStore = useMessageStore()
    const dialogStore = useDialogStore()
    const certificateStore = useCertificateStore()
    const progressTrackerStore = useProgressTrackerStore()
    const attachmentDownloadApi: AttachmentDownloadApi = new AttachmentDownloadApi()

    const checkedFilesIds: Ref<Array<number>> = ref([])
    const attachmentSpecs: ComputedRef<Array<AttachmentSpec>> = computed(() => (props.attachments.map(x => x.attachmentSpec)))

    const attachmentsView: Ref<'LIST' | 'TREE'> = ref('LIST')
    const showListView: ComputedRef<boolean> = computed(() => (attachmentsView.value === 'LIST' && !!attachmentSpecs.value.length))

    const progressTitle: ComputedRef<string> = computed(() => (progressTrackerStore.status === 'cancel' ? 'Cancelling...' : 'Downloading...'))
    const progressMessage: ComputedRef<string> = computed(() => (progressTrackerStore.status === 'cancel'
      ? 'Cancelling download in progress. <br>This dialog will close automatically at the end of the process.'
      : 'Download in progress. <br>This dialog will close automatically at the end of the download.'))

    const certificateView: Ref<typeof CertificateView | null> = ref(null)

    onMounted(() => downloadRouteSwitch())
    watch(() => props.message, () => {
      selectAttachments()
      downloadRouteSwitch()
    })

    function selectAttachments(attachmentIds: Array<number> = []) {
      checkedFilesIds.value = attachmentIds
    }

    function downloadableAttachmentSpecByAttachmentId(id: number): DownloadableAttachmentSpec {
      return props.attachments.find(x => x.attachmentSpec.id === id)!
    }

    function progressCancelFunction() {
      return new Promise<void>((resolve) => {
        attachmentDownloadApi.abort()
        setTimeout(resolve, 500)
      })
    }

    function downloadRouteSwitch() {
      const nextRoute = { name: MessageBoxRouteNames.INBOX_MSG_DETAILS, params: route.params }

      switch (route.name) {
        case MessageBoxRouteNames.INBOX_DOWNLOAD_ALL_ATTACHMENTS:
        case MessageBoxRouteNames.SENT_DOWNLOAD_ALL_ATTACHMENTS:
          showCertificateSelectDialogIfEncryptedOrDownload(
            () => {
              setupProgressTracker()
              getAesKey()
                .then(key => {
                  attachmentDownloadApi.downloadAll(props.attachments, formatZipFileName(props.message), key)
                    .finally(() => router.push(nextRoute))
                })
            })

          break
        case MessageBoxRouteNames.INBOX_DOWNLOAD_ATTACHMENT:
        case MessageBoxRouteNames.SENT_DOWNLOAD_ATTACHMENT:
          const attachmentToDownload = props.attachments.find(attachment => attachment.attachmentSpec.id.toString() === route.params.attachmentId)
          if (!attachmentToDownload) {
            return
          }
          showCertificateSelectDialogIfEncryptedOrDownload(
            () => downloadFile(attachmentToDownload)
              .finally(() => router.push(nextRoute)))
          break
        default:
          break
      }
    }

    function setupProgressTracker() {
      progressTrackerStore.config = {
        title: progressTitle.value,
        message: progressMessage.value,
        isUpload: false,
        progressCancelFunction
      }
    }

    async function downloadFile(attachment: DownloadableAttachmentSpec) {
      setupProgressTracker()
      const key = await getAesKey()
      return attachmentDownloadApi.download(attachment, key)
    }

    async function downloadFiles() {
      if (checkedFilesIds.value.length === 0) {
        dialogStore.show(
          'No file selected!',
          'Please select at least one file to download.'
        )
        return
      }

      setupProgressTracker()

      const selectedAttachmentsSet = new Set(checkedFilesIds.value.values())
      const selectedAttachments = props.attachments.filter(value => selectedAttachmentsSet.has(value.attachmentSpec.id))
      const key = await getAesKey()
      return attachmentDownloadApi.downloadAll(selectedAttachments, formatZipFileName(props.message), key)
    }

    async function getAesKey(): Promise<CryptoKey> {
      const messageSummary = messageStore.selectedMessageSummary ||
        messageStore.selectedMessage?.messageSummaries.find(ms => ms.symmetricKey && ms.symmetricKey.encryptionMethod !== EncryptionMethod.RSA_OAEP_E2E)

      if (!messageSummary) {
        throw new Error('No message summary selected')
      }

      let privateKey: CryptoKey
      if (messageSummary.symmetricKey.encryptionMethod === EncryptionMethod.RSA_OAEP_E2E) {
        privateKey = await RSA.importPrivateKey(certificateStore.selectedIdentity.privateKey)
      } else {
        privateKey = useSettingsStore().privateKey
      }

      let decryptedKey = ''
      try {
        decryptedKey = await RSA.decrypt(privateKey, messageSummary.symmetricKey.randomBits)
      } catch (error: any) {
        dialogStore.show(
          'Decryption failed!',
          'The identity selected does not contain the correct decryption information, please select a different certificate/identity!'
        )
        throw new Error(error.toString())
      }

      return AES.importGcmKey(decryptedKey)
    }

    function showCertificateSelectDialogIfEncryptedOrDownload(downloadFn: Function) {
      certificateView.value?.validate()
      if (props.isEncrypted && certificateStore.p12FileSize <= 0) {
        dialogStore.show(
          'Select a certificate',
          'Please select a certificate to download the files'
        )
        return
      }
      downloadFn()
    }

    function toggleAttachmentsView() {
      attachmentsView.value = attachmentsView.value === 'LIST' ? 'TREE' : 'LIST'
      checkedFilesIds.value = []
    }

    return {
      attachmentSpecs,
      showListView,
      progressTitle,
      progressMessage,
      certificateView,
      downloadableAttachmentSpecByAttachmentId,
      progressCancelFunction,
      toggleAttachmentsView,
      selectAttachments,
      showCertificateSelectDialogIfEncryptedOrDownload,
      downloadFile,
      downloadFiles,
      formatBytes
    }
  }
})
</script>
