<template>
  <div v-if="message?.id" class="inbox-sticky-part">
    <message-details-header :message="message" :messageSummary="messageSummary"
                            :key="'header' + message.id" :includeReply="includeReply"
                            :date="formatDate(isDraft ? message.modifiedDate : message.sentOn)"
                            @reply="replyMessage()">
      <div v-if="isInbox">
        <p class="text-break message-detail-sender">
          From: <strong>{{ message.senderGroup.name }}</strong>
        </p>
      </div>
      <div v-if="isSent">
        <p class="text-break message-detail-sender">
          Sent by: <strong>{{ message.senderUserName }}</strong>
        </p>
      </div>
      <div v-if="isDraft">
        <p class="mb-2 text-break message-detail-receiver">
          To: <strong>{{ recipients }}</strong>
        </p>
        <p class="text-break message-detail-sender">
          Saved by: <strong>{{ message.senderUserName }}</strong>
        </p>
      </div>
    </message-details-header>

    <recipient-list-view v-if="isSent" :recipients="recipientList"></recipient-list-view>
    <message-details-body :messageBody="message.text" :key="'body' + message.id"></message-details-body>

    <div v-if="isSent && isE2EEncrypted" class="message-detail-attachments">
      <div class="text-center">
        <h3 class="variant">List of files</h3>
        <ul class="list-unstyled">
          <li>
          <span aria-hidden="true" class="ico-conjunction">
            <i class="ico-attach message-detail-attachment-ico"></i>
            {{ message.attachmentTotalNumber + ' files ' }} - {{ formatBytes(message.attachmentsTotalByteLength) }}
          </span>
          </li>
          <li>
          <span aria-hidden="true" class="ico-conjunction">
            <i class="ico-encrypted-mandatory"></i>
            Encrypted
          </span>
          </li>
        </ul>
      </div>

      <div class="d-flex justify-content-end align-items-md-center">
        <button class="ico-standalone"
                type="button"
                :title="showListView ? 'List view': 'Tree view'"
                :aria-label="showListView ? 'See list view': 'See tree view'"
                @click="toggleAttachmentsView()">
        <span aria-hidden="true" class="ico-standalone">
          <i :class="showListView ? 'ico-tree-view-primary': 'ico-list-view-primary'"></i>
        </span>
        </button>
      </div>
    </div>

    <div v-if="isSent && isE2EEncrypted">
      <attachments-list v-if="showListView" :attachments="attachments" :canDownload="false"
                        :showAdditionalAttachmentActions="showAdditionalAttachmentActions">
      </attachments-list>
      <attachments-tree v-else :canDownload="false"
                        :attachments="attachments"
                        :showAdditionalAttachmentActions="showAdditionalAttachmentActions">
      </attachments-tree>
    </div>

    <message-details-attachments v-if="(isInbox || (isSent && !isE2EEncrypted)) && downloadableAttachments.length > 0"
                                 :message="message"
                                 :attachments="downloadableAttachments"
                                 :isEncrypted="isE2EEncrypted"
                                 :showCertificate="showCertificate"
                                 :showAdditionalAttachmentActions="showAdditionalAttachmentActions">
    </message-details-attachments>

    <custom-template :template="customTemplate" :templateVariables="templateVariables"></custom-template>

    <div v-if="isDraft" class="text-end">
      <button class="btn btn-outline-primary btn-ico" type="button" @click="reopenDraft">
        <span class="ico-standalone"><i aria-hidden="true" class="ico-draft-primary"></i></span>
        <span>Reopen message</span>
      </button>
    </div>
  </div>
  <no-message-selected v-else/>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, onMounted, Ref, ref, watch } from 'vue'

import { AttachmentSpec, Confidentiality, Message, MessageSummary, TemplateType } from '@/model/entities'

import { isEncrypted } from '@/utils/crypto/cryptoHelper'
import FileUpload from 'vue-upload-component'
import { useRoute, useRouter } from 'vue-router'
import useDialogStore from '@/shared/store/dialog'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { isDraftRoute, isInboxRoute, isSentRoute, MessageBoxRouteNames, MessageFormRouteNames } from '@/messages/router'
import NoMessageSelected from '@/messages/views/NoMessageSelected.vue'
import CustomTemplate from '@/messages/views/CustomTemplate.vue'
import MessageDetailsHeader from '@/messages/views/details/MessageDetailsHeader.vue'
import useMessageStore from '@/messages/store/message'
import RecipientDto from '@/model/recipientDto'
import { formatBytes, formatDate } from '@/utils/formatters'
import MessageDetailsBody from '@/messages/views/details/MessageDetailsBody.vue'
import RecipientListView from '@/messages/views/details/RecipientListView.vue'
import MessageDetailsAttachments from '@/messages/views/details/MessageDetailsAttachments.vue'
import { compareByPathAndName, DownloadHelper } from '@/messages/utils/downloadHelper'
import { DownloadableAttachmentSpec } from '@/model/attachmentDto'
import AttachmentsList from '@/messages/views/details/attachments/AttachmentsList.vue'
import AttachmentsTree from '@/messages/views/details/attachments/AttachmentsTree.vue'

export default defineComponent({
  name: 'MessageDetails',
  components: {
    AttachmentsList,
    AttachmentsTree,
    FileUpload,
    MessageDetailsHeader,
    MessageDetailsBody,
    MessageDetailsAttachments,
    NoMessageSelected,
    CustomTemplate,
    RecipientListView
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const dialogStore = useDialogStore()
    const messageStore = useMessageStore()
    const currentGroupStore = useCurrentGroupStore()

    const message: ComputedRef<Message | undefined> = computed(() => messageStore.selectedMessage)
    const messageSummary: ComputedRef<MessageSummary | undefined> = computed(() => messageStore.selectedMessageSummary)
    const recipientList: Ref<Array<RecipientDto>> = ref([])
    const recipients: ComputedRef<string> = computed(() => {
      if (!message.value) return ''
      return (message.value.status === 'DRAFT')
        ? message.value.draftRecipients.map(recipient => recipient.name).join(', ')
        : message.value.messageSummaries.map(summary => summary.recipient.name).join(', ')
    })

    const isE2EEncrypted: Ref<boolean> = ref(false)
    const showCertificate: ComputedRef<boolean> = computed(
      () => !!messageSummary.value && isEncrypted(messageSummary.value))
    const showAdditionalAttachmentActions: ComputedRef<boolean> = computed(() => (currentGroupStore.group.parent.additionalAttachmentActions))
    const includeReply: ComputedRef<boolean> = computed(() => isInbox.value && currentGroupStore.recipients.length > 0)

    const attachments: Ref<Array<AttachmentSpec>> = ref([])
    const downloadableAttachments: Ref<Array<DownloadableAttachmentSpec>> = ref([])

    const customTemplate: ComputedRef<string> = computed(
      () => currentGroupStore.group.parent?.templates
        ? currentGroupStore.group.parent.templates.find(t => t.type === TemplateType.SENT_MESSAGE_DETAILS_VIEW)?.content || ''
        : ''
    )
    const templateVariables: ComputedRef<string> = computed(
      () => (message.value && message.value.templateVariables
        ? JSON.parse(message.value.templateVariables)
        : {})
    )

    const isInbox: ComputedRef<boolean> = computed(() => isInboxRoute(route.name as MessageBoxRouteNames))
    const isSent: ComputedRef<boolean> = computed(() => isSentRoute(route.name as MessageBoxRouteNames))
    const isDraft: ComputedRef<boolean> = computed(() => isDraftRoute(route.name as MessageBoxRouteNames))

    function setupAndLoad() {
      messageStore.clear_details()

      if (isInbox.value) {
        messageStore.fetchMessageSummary(+route.params.messageId, currentGroupStore.group.id).then((loadedSummary) => {
          isE2EEncrypted.value = isEncrypted(loadedSummary)
          downloadableAttachments.value = DownloadHelper.toDownloadableAttachmentSpecArray(loadedSummary.message, loadedSummary.links)
        })
      } else if (isSent.value) {
        messageStore.fetchMessage(+route.params.messageId).then(loadedMessage => {
          recipientList.value = loadedMessage.messageSummaries
            .map(ms => new RecipientDto(ms.recipient.name, ms.status, ms.statusModifiedDate, ms.confidentiality === Confidentiality.LIMITED_HIGH))
            .sort((n1, n2) => RecipientDto.compare(n1, n2))

          setupMessageAttachments(loadedMessage)
        })
      } else if (isDraft.value) {
        messageStore.fetchMessage(+route.params.messageId).then()
      } else {
        throw new Error(`${String(route.name)}`)
      }
    }

    function setupMessageAttachments(message: Message) {
      isE2EEncrypted.value = message.messageSummaries.every(ms => isEncrypted(ms))
      if (isE2EEncrypted.value) {
        attachments.value = message.attachmentSpecs
        attachments.value.sort((a, b) => compareByPathAndName(a, b))
      } else {
        downloadableAttachments.value = DownloadHelper.toDownloadableAttachmentSpecArray(message, message.links)
      }
    }

    onMounted(() => setupAndLoad())
    watch(() => route.params.messageId, (messageId, previousMessageId) => {
      if (!messageId || messageId === previousMessageId) return
      setupAndLoad()
    }
    )

    function replyMessage() {
      if (!message.value) return

      if (!currentGroupStore.recipients.find(g => g.id === message.value!.senderGroup.id)) {
        dialogStore.show('Message Reply', `The ${message.value.senderGroup.identifier} entity is not configured as a recipient of ${currentGroupStore.group.identifier} entity!`)
        return
      }
      router.push({
        name: MessageFormRouteNames.MESSAGE_FORM,
        params: { currentGroupId: `${currentGroupStore.group.id}` },
        query: { replyId: `${message.value.id}` }
      }).then()
    }

    function reopenDraft() {
      if (!message.value) return

      router.push({
        name: MessageFormRouteNames.MESSAGE_FORM,
        params: { currentGroupId: `${currentGroupStore.group.id}` },
        query: { messageId: `${message.value.id}` }
      }).then()
    }

    const attachmentsView: Ref<'LIST' | 'TREE'> = ref('LIST')
    const showListView = computed(() => (attachmentsView.value === 'LIST'))

    function toggleAttachmentsView() {
      attachmentsView.value = attachmentsView.value === 'LIST' ? 'TREE' : 'LIST'
    }

    return {
      messageSummary,
      message,
      isInbox,
      isSent,
      isDraft,
      recipients,
      recipientList,
      showListView,
      attachments,
      downloadableAttachments,
      isE2EEncrypted,
      showCertificate,
      showAdditionalAttachmentActions,
      includeReply,
      customTemplate,
      templateVariables,
      toggleAttachmentsView,
      replyMessage,
      reopenDraft,
      formatDate,
      formatBytes
    }
  }

})

</script>
