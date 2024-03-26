<template>
  <div class="main-wrapper">
    <div :class="['folder-menu', {sidebarActive: sidebarActive}]" @dragover.prevent @drop.stop.prevent>
      <span class="ico-conjunction">
        <i aria-hidden="true" class="ico-back"></i>
        <a @click="leaveForm()" class="ms-2">Back to inbox</a>
      </span>
    </div>
    <div class="container-fluid" @dragover.prevent @drop.stop.prevent>
      <div class="row">
        <div class="col-md-5 message-form">

          <div class="d-flex align-items-md-center">
            <hamburger/>
            <h1 class="d-inline mb-0">Sending files</h1>
          </div>

          <form v-on:submit.prevent="$event.preventDefault()">
            <message-info :e2eEnabled="e2eEnabled" :e2eReadOnly="e2eReadOnly" :sign="sign" :v$="v$"
                          @toggle-e2e="e2eEnabled = !e2eEnabled" @toggle-sign="sign = !sign"/>

            <div class="row mt-3">
              <div class="col">
                <div class="text-end btns">
                  <button v-if="canShowSaveAsDraft && !sign" class="btn btn-outline-primary btn-ico" type="button"
                          v-on:click="saveDraft()">
                    <span class="ico-standalone"><i aria-hidden="true" class="ico-draft-primary"></i></span>
                    <span>Save as draft</span>
                  </button>

                  <button class="btn btn-outline-secondary btn-ico" type="button" @click="leaveForm()">
                    <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
                    <span>Cancel</span>
                  </button>

                  <button class="btn btn-primary btn-ico" type="button" :disabled="isSending" @click="sendMessage()">
                    <span class="ico-standalone"><i aria-hidden="true" class="ico-send-white"></i></span>
                    <span>Send</span>
                  </button>

                </div>
              </div>
            </div>
          </form>
        </div>
        <message-attachments :e2eEnabled="e2eEnabled" :v$="v$"/>
      </div>
    </div>
  </div>

</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref, Ref } from 'vue'
import Hamburger from '@/messages/views/Hamburger.vue'
import { EncryptionMethod, Group, Message, UpdateMessageRequestSpec } from '@/model/entities'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import MessageFormUtils from '@/messages/utils/messageFormUtils'
import { EntityE2EEncryption } from '@/utils/groupHelper'
import { useRoute, useRouter } from 'vue-router'
import { MessageApi } from '@/messages/api/messageApi'
import { RSA } from '@/utils/crypto/rsa'
import useSettingsStore from '@/shared/store/settings'
import useMessageFormStore from '@/messages/store/messageForm'
import { storeToRefs } from 'pinia'
import { MessageBoxRouteNames } from '@/messages/router'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import useViewConfigStore from '@/messages/store/messagesView'
import MessageAttachments from '@/messages/views/form/MessageAttachments.vue'
import useVuelidate from '@vuelidate/core'
import { messageFormRules } from '@/messages/validation/MessageFormValidator'
import MessageInfo from '@/messages/views/form/MessageInfo.vue'
import useAuthenticationStore from '@/shared/store/authentication'
import { InboxApi } from '@/messages/api/inboxApi'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import { AES } from '@/utils/crypto/aes'
import { AttachmentStatus } from '@/model/attachmentDto'
import { ConverterUtils } from '@/utils/converterUtils'

export default defineComponent({
  name: 'MessageForm',
  components: { MessageInfo, MessageAttachments, Hamburger },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const businessConfigurationStore = useBusinessConfigurationStore()
    const dialogStore = useDialogStore()
    const viewConfigStore = useViewConfigStore()

    const messageFormStore = useMessageFormStore()
    const {
      message,
      messageRecipients,
      attachmentsSymmetricKey,
      attachmentSpecDtos,
      templateVariables,
    } = storeToRefs(messageFormStore)
    const canShowSaveAsDraft = computed(() => messageFormStore.attachmentSpecDtos.length === 0)

    const currentGroupStore = useCurrentGroupStore()
    const { currentGroup } = storeToRefs(currentGroupStore)
    const businessId = computed(() => currentGroup.value.businessId)
    const groupIds = computed(() => currentGroupStore.recipients.map(value => value.id))

    const isSending: Ref<boolean> = ref(false)
    const sign: Ref<boolean> = ref(false)
    const e2eInfo: Ref<boolean> = ref(false)
    const e2eEnabled: Ref<boolean> = ref(false)
    const e2eReadOnly: Ref<boolean> = ref(false)
    const isSignatureEnabled = computed(() => businessConfigurationStore.signatureGroupConfiguration.active)
    const sidebarActive = computed(() => viewConfigStore.sidebarActive)

    const v$ = useVuelidate(messageFormRules, {
      message,
      messageRecipients,
      e2eEnabled,
      attachmentSpecDtos,
      templateVariables,
    })

    onMounted(() => {
      ExchangeRuleApi.getValidRecipients(+route.params.currentGroupId)
        .then(recipients => {
          if (route.query.messageId) {
            loadDraft(+route.query.messageId, recipients)
          } else if (route.query.replyId) {
            loadReply(+route.params.currentGroupId, +route.query.replyId, recipients)
          } else {
            createMessage(+route.params.currentGroupId)
              .then(message => {
                messageRecipients.value = (recipients.length === 1) ? recipients : []
                return message
              })
          }

          if (MessageFormUtils.areAllRecipientsWithSameEncryption(recipients, EntityE2EEncryption.MANDATORY) || businessConfigurationStore.enforceEncryptionGroupConfiguration.active) {
            e2eEnabled.value = true
            e2eReadOnly.value = true
          } else if (MessageFormUtils.areAllRecipientsWithSameEncryption(recipients, EntityE2EEncryption.NOT_ENABLED)) {
            e2eEnabled.value = false
            e2eReadOnly.value = true
          }
        })
    })

    async function loadDraft(messageId: number, recipients: Array<Group>) {
      v$.value.$reset()

      const savedMessage = await MessageApi.getMessageDetails(messageId, currentGroup.value.id)
      message.value = savedMessage
      messageRecipients.value = recipients.filter(r => savedMessage.draftRecipients.find(dr => dr.id === r.id))

      if (message.value.symmetricKey && message.value.iv) {
        message.value.symmetricKey = await RSA.getDecryptedSymmetricKeyFromMessage(message.value, useSettingsStore().privateKey)
        if (message.value.templateVariables) {
          message.value.templateVariables = await AES.decrypt(message.value.symmetricKey.randomBits, message.value.iv, message.value.templateVariables, true)
          templateVariables.value = JSON.parse(message.value.templateVariables)
        }
        if (message.value.text) {
          message.value.text = await AES.decrypt(message.value.symmetricKey.randomBits, message.value.iv, message.value.text)
        }
      }

      messageFormStore.generateAttachmentsAesKey().then()

      return savedMessage
    }

    function loadReply(senderEntityId: number, messageId: number, recipients: Array<Group>) {
      v$.value.$reset()

      InboxApi.fetchMessageSummaryByMessageId(messageId, currentGroup.value.id)
        .then(messageSummary => createMessage(senderEntityId)
          .then(() => {
            messageRecipients.value = recipients.filter(r => r.id === messageSummary.message.senderGroup.id)
            message.value.subject = 'RE: ' + messageSummary.message.subject
          }))
    }

    async function createMessage(senderEntityId: number): Promise<Message> {
      const newMessage = await MessageApi.createMessage(senderEntityId)
      message.value = newMessage
      templateVariables.value = initTemplateVariables()

      const messageAesKey = await AES.generateGcmKey()
      const randomBits = await AES.exportKey(messageAesKey)
      const iv = AES.generateIv()
      const ivB64 = ConverterUtils.bufferToBase64String(iv)
      message.value.symmetricKey = {
        randomBits,
        encryptionMethod: EncryptionMethod.ENCRYPTED,
      }

      message.value.iv = ivB64

      messageFormStore.generateAttachmentsAesKey().then()

      return newMessage
    }

    async function saveDraft() {
      v$.value.$validate()
      v$.value.attachmentSpecDtos.$reset()
      if (v$.value.$errors.length !== 0) {
        return
      }
      const messageRequestSpec = new UpdateMessageRequestSpec()
      messageRequestSpec.recipients = await MessageFormUtils.getMessageSummarySpecs(messageRecipients.value, attachmentsSymmetricKey.value, e2eEnabled.value)
      messageRequestSpec.subject = message.value.subject
      messageRequestSpec.highImportance = message.value.highImportance

      message.value.templateVariables = JSON.stringify(templateVariables.value)
      await MessageFormUtils.setTextAndTemplateVariables(message.value, messageRequestSpec)

      return MessageApi.draftMessage(message.value.links, messageRequestSpec)
        .then(() => dialogStore.show('Message saved!', 'The message was successfully saved as draft'))
        .catch(reason => console.log(reason))
    }

    function initTemplateVariables() {
      const { userDetails } = useAuthenticationStore()

      return {
        senderInfo_email: userDetails.email,
        senderInfo_firstName: userDetails.firstName,
        senderInfo_lastName: userDetails.lastName,
        senderInfo_ecasId: userDetails.username,
        emailRecipients: '',
        reference: '',
        isTemplatePresent: !!currentGroupStore.customFormTemplate,
      }
    }

    function sendMessage() {
      v$.value.$validate()
      if (v$.value.$error) {
        return
      }
      isSending.value = true
      delete templateVariables.value.isTemplatePresent
      message.value.templateVariables = JSON.stringify({ ...templateVariables.value })
      MessageApi.isReadyToSend(messageFormStore.message.id).then(isReady => {
        if (isReady) {
          messageFormStore.sendMessage(messageRecipients.value, e2eEnabled.value, sign.value)
            .then(() => {
              dialogStore.show(
                'Message sent!',
                'The message was successfully sent!',
                DialogType.INFO,
                buttonWithCallback('Ok', () => {
                  messageFormStore.$reset()
                  isSending.value = false
                  router.push({ name: MessageBoxRouteNames.INBOX })
                })
              )
            })
            .catch(reason => {
              console.log(reason)
              dialogStore.show(
                'Message not sent!',
                'The message was unsuccessfully sent!, please try again',
                DialogType.ERROR,
                buttonWithCallback('Ok', () => {
                  messageFormStore.$reset()
                  isSending.value = false
                  router.push({ name: MessageBoxRouteNames.INBOX })
                })
              )
            })
        } else {
          useDialogStore()
            .show('Error when uploading attachments', 'Please remove all attachments and retry again', DialogType.ERROR)
          messageFormStore.attachmentSpecDtos.forEach(attachmentSpecDto => attachmentSpecDto.status = AttachmentStatus.Error)
          isSending.value = false
        }
      })
    }

    function leaveForm() {
      dialogStore.show(
        'Confirm',
        'Are you sure you want to leave this message? <br/>All unsaved changes will be lost.',
        DialogType.INFO,
        buttonWithCallback('Confirm', () => {
          messageFormStore.$reset()
          v$.value.$reset()
          router.push({ name: MessageBoxRouteNames.INBOX })
        }),
        CANCEL_BUTTON,
      )
    }

    return {
      isSending,
      sign,
      e2eInfo,
      e2eEnabled,
      e2eReadOnly,
      isSignatureEnabled,
      canShowSaveAsDraft,
      currentGroup,
      message,
      groupIds,
      businessId,
      sidebarActive,
      v$,
      saveDraft,
      leaveForm,
      sendMessage,
    }
  },
})
</script>
