<template>
  <router-link :id="`message_${message.id}`" :to="{ name: routeName, params: { messageId: message.id } }"
               :class="`${(isRead ? 'read-message': 'unread-message')} ${(selected ? 'selected' : '')} item-link`">

    <div class="d-table message-summary-header">
      <div class="d-table-row">
        <div class="d-table-cell w-75">
          <span class="message-summary-subject">
            <span :class="`d-block ${ expandSubject ? '' : 'text-truncate'}`">
              {{ message.subject }}
            </span>
          </span>

          <div v-if="sentTo" class="d-table message-summary-body">
            <div class="d-table-row">
              <div class="d-table-cell w-75">
                <span class="message-summary-receiver">
                  <span class="d-block text-truncate"><span class="font-weight-normal">To:</span>
                    {{ sentTo }}
                  </span>
                </span>
              </div>
            </div>
            <div class="d-table-row">
              <div class="d-table-cell w-75">
                <span class="message-summary-sender">
                  <span class="d-block text-truncate">
                    <span class="font-weight-normal">
                      {{ message.status === 'DRAFT' ? 'Saved' : 'Sent' }} by:
                    </span> {{ message.senderUserName }}
                  </span>
                </span>
              </div>
            </div>
          </div>
          <div v-if="sentFrom" class="d-table message-summary-body">
            <span class="message-summary-sender">
              <span class="d-block text-truncate">
                <span class="font-weight-normal">From:</span> {{ sentFrom }}
              </span>
            </span>
          </div>
          <div class="d-flex message-summary-footer" v-if="message.status !== 'DRAFT'">
            <ul>
              <li v-if="showDownloadReceipt" v-on:click="downloadReceipt()">
                <span class="ico-conjunction" aria-hidden="true">
                  <i class="ico-pdf-primary"></i>
                  <span class="message-summary-pdf-label">Download</span>
                </span>
              </li>
              <li>
                <span class="ico-conjunction">
                  <i class="ico-attach message-summary-attachment-ico" aria-hidden="true"></i>
                  {{ message.attachmentTotalNumber + ' files ' }} - {{
                    formatBytes(message.attachmentsTotalByteLength)
                  }}
                </span>
              </li>
              <li v-if="isE2Eencrypted">
                <span class="ico-conjunction">
                  <i class="ico-encrypted-mandatory" aria-hidden="true"></i>Encrypted
                </span>
              </li>
              <li>
                <span v-if="signature" class="ico-conjunction">
                  <i class="ico-check-circle" aria-hidden="true"></i>
                  Signed
                </span>
              </li>
              <li v-if="messageSummary">
                <custom-template :template="template" :templateVariables="templateVariables"></custom-template>
              </li>
            </ul>
          </div>
        </div>
        <div class="d-table-cell w-25 text-end">
          <span class="d-block font-weight-normal" v-if="message.status !== 'DRAFT'">{{
              formatDate(message.sentOn)
            }}</span>
          <span class="d-block font-weight-normal" v-else>{{ formatDate(message.modifiedDate) }}</span>
          <span v-if="showStatusBadge" class="badge rounded-pill bg-primary">
            {{ message.status }}
          </span>
          <img v-if="message.highImportance" style="max-width: 1.5rem"
               class="d-block ms-auto me-1 mt-1 ico-invalid-certificate" src="@/assets/ico/rounded-error-danger.svg"
               alt="ERROR">
          <span v-if="message.status === 'DRAFT'" class="float-end">
              <a href="javascript:void(0);" class="ico-standalone delete" type="button"
                 v-on:click="confirmDeleteDraft(message)">
                <i class="ico-delete"></i>
                <span>Remove</span>
              </a>
            </span>
        </div>
      </div>
    </div>
  </router-link>
</template>
<script lang="ts">
import { computed, defineComponent, PropType } from 'vue'
import { Group, Message, MessageSummary, TemplateType } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import { MessageApi } from '@/messages/api/messageApi'
import { formatBytes, formatDate } from '@/utils/formatters'
import { isMessageRead, isMessageSummaryRead } from '@/messages/utils/userStatusHelper'
import { isEncrypted } from '@/utils/crypto/cryptoHelper'
import CustomTemplate from '@/messages/views/CustomTemplate.vue'

export default defineComponent({
  name: 'MessageSummaryView',
  components: { CustomTemplate },
  props: {
    group: { type: Object as PropType<Group>, required: true },
    message: { type: Object as PropType<Message>, required: true },
    messageSummary: { type: Object as PropType<MessageSummary> },
    expandSubject: { type: Boolean as PropType<boolean> },
    sentTo: { type: String as PropType<string> },
    sentFrom: { type: String as PropType<string> },
    signature: { type: String as PropType<string> },
    paginationCommand: { type: Object as PropType<PaginationCommand>, required: true },
    selected: { type: Boolean as PropType<boolean> },
    routeName: { type: String },
    showStatusBadge: { type: Boolean },
  },
  setup(props, { emit }) {
    const dialogStore = useDialogStore()
    const showDownloadReceipt = computed(() => !props.messageSummary)

    const isRead = computed(() => (
      props.message.status === 'DRAFT' ||
      isMessageRead(props.message) ||
      (props.messageSummary && isMessageSummaryRead(props.messageSummary))
    ))
    const isE2Eencrypted = computed(() => (props.message.messageSummaries?.every(isEncrypted) ||
      (props.messageSummary && isEncrypted(props.messageSummary))))

    // TODO: ETRUSTEX-8554 - templateVars can no longer be used in message summary as they are now encrypted
    const templateVariables = computed(() => {
      // const templateVars = props.messageSummary?.message.templateVariables ? props.messageSummary.message.templateVariables : '{}'
      const templateVars = { subsidiarityCheck: 'Under subsidiarity check' }
      return props.message.subject.toLowerCase().includes('subsidiarity') ? templateVars : {}
    })
    const template = computed(() => {
      const { templates } = props.group.parent
      return templates.find(t => t.type === TemplateType.MESSAGE_SUMMARY_VIEW)?.content ?? ''
    })

    function downloadReceipt() {
      return MessageApi.getReceipt(props.message)
        .catch(() => dialogStore.show(
          'Transmission PDF download failed!',
          'One or more characters present in the transmission is not supported.<br>eTrustEx Web only supports official EU languages.',
        ))
    }

    function confirmDeleteDraft(message: Message) {
      const modalMessage = '<p class="mt-3">Are you sure you want to delete the draft message <strong class="text-break"> ' +
        `${ message.subject }?</strong></p><p>The action cannot be undone.</p>`

      dialogStore.show('Delete Draft', modalMessage, DialogType.ERROR, buttonWithCallback('Delete', () => deleteDraft(message)), CANCEL_BUTTON)
    }

    function deleteDraft(message: Message) {
      return MessageApi.deleteDraftMessage(message)
        .then(() => emit('on-draft-deleted'))
        .catch(() => {
          confirmDeleteDraft(message)
        })
    }

    return {
      showDownloadReceipt,
      isRead,
      isE2Eencrypted,
      templateVariables,
      template,
      downloadReceipt,
      confirmDeleteDraft,
      formatDate,
      formatBytes,
    }
  },
})
</script>
