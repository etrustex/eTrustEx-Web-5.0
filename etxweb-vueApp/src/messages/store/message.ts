import { Message, MessagePage, MessageSummary, MessageSummaryPage, MessageSummaryUserStatus, MessageUserStatus, Status } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { defineStore } from 'pinia'
import { MessageApi } from '../api/messageApi'
import { RSA } from '@/utils/crypto/rsa'
import useSettingsStore from '@/shared/store/settings'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { isMessageRead, isMessageSummaryRead } from '@/messages/utils/userStatusHelper'
import { InboxApi } from '@/messages/api/inboxApi'
import { cloneDeep } from '@/shared/views/bootstrap_vue/utils/object'
import { AES } from '@/utils/crypto/aes'

const nameSpace = 'message'

export interface MessageConfig {
  id: number
  signature?: string
  message: Message
  messageSummary?: MessageSummary
  sentFrom?: string
  sentTo?: string
}

interface State {
  _rawMessage?: Message
  _rawMessageSummary?: MessageSummary
  selectedMessage?: Message
  selectedMessageSummary?: MessageSummary
  messagesPage?: MessagePage,
  messageSummariesPage?: MessageSummaryPage,
  numberOfUnreadMessages: number,
  numberOfUnreadMessageSummaries: number
}

const useMessageStore = defineStore(nameSpace, {
  state: (): State => ({
    _rawMessage: undefined,
    _rawMessageSummary: undefined,
    selectedMessage: undefined,
    selectedMessageSummary: undefined,
    messagesPage: undefined,
    messageSummariesPage: undefined,
    numberOfUnreadMessages: 0,
    numberOfUnreadMessageSummaries: 0,
  }),
  getters: {
    messages(): Array<Message> {
      return this.messagesPage?.content || []
    },
    messageSummaries(): Array<MessageSummary> {
      return this.messageSummariesPage?.content || []
    },
    messageConfigs(): Array<MessageConfig> {
      return this.messages.map(m => ({
        id: m.id,
        signature: m.messageSummaries.length > 0 ? m.messageSummaries[0].signature : undefined,
        message: m,
        sentTo: (m.status === 'DRAFT')
          ? m.draftRecipients.map(recipient => recipient.name).join(', ')
          : m.messageSummaries.map(messageSummary => messageSummary.recipient.name).join(', '),
      }))
    },
    messageSummaryConfigs(): Array<MessageConfig> {
      return this.messageSummaries.map(m => ({
        id: m.message.id,
        signature: m.signature,
        message: m.message,
        messageSummary: m,
        sentFrom: m.message.senderGroup.name,
      }))
    },
  },
  actions: {
    clear() {
      this.$reset()
    },
    clear_list() {
      this.messagesPage = undefined
      this.messageSummariesPage = undefined
      this.numberOfUnreadMessages = 0
      this.numberOfUnreadMessageSummaries = 0
    },
    clear_details() {
      this._rawMessage = undefined
      this._rawMessageSummary = undefined
      this.selectedMessage = undefined
      this.selectedMessageSummary = undefined
    },
    async fetchMessages(paginationOptions: PaginationOptions, isDraft = false): Promise<MessagePage> {
      let messagePage = await MessageApi.getMessages(paginationOptions)
      this.messagesPage = messagePage

      if (isDraft) {
        this.fetchNumberOfUnreadMessages(paginationOptions.senderEntityId).then()
        this.fetchNumberOfUnreadMessageSummaries(paginationOptions.senderEntityId).then()
      } else {
        this.numberOfUnreadMessages = messagePage.unreadMessages
        this.fetchNumberOfUnreadMessageSummaries(paginationOptions.senderEntityId).then()
      }

      return messagePage
    },
    async fetchMessageSummaries(paginationOptions: PaginationOptions) {
      try {
        let messageSummariesPage = await InboxApi.fetchMessageSummaries(paginationOptions)
        this.messageSummariesPage = messageSummariesPage

        this.numberOfUnreadMessageSummaries = messageSummariesPage.unreadMessages
        this.fetchNumberOfUnreadMessages(paginationOptions.recipientEntityId).then()

        return messageSummariesPage
      } catch (error) {
        throw new Error(`Error retrieving messageSummaries. ${ error }`)
      }
    },
    async fetchMessage(messageId: number): Promise<Message> {
      const message: Message = await MessageApi.getMessageDetails(messageId, useCurrentGroupStore().group.id)
      this._rawMessage = cloneDeep(message)

      const { text, templateVariables, symmetricKey, iv } = message

      if ((text || templateVariables) && symmetricKey && iv) {
        const decryptedSymmetricKey = await RSA.getDecryptedSymmetricKeyFromMessage(message, useSettingsStore().privateKey)
        message.symmetricKey = decryptedSymmetricKey
        message.text = text ? await AES.decrypt(decryptedSymmetricKey.randomBits, message.iv, text) : text
        message.templateVariables = templateVariables ? await AES.decrypt(decryptedSymmetricKey.randomBits, message.iv, templateVariables, true) : templateVariables
      }

      if (!isMessageRead(message)) {
        const index = this.messages.findIndex(obj => obj.id === message.id)
        message.messageUserStatuses = [{ status: 'READ' } as MessageUserStatus]

        this.messagesPage!.content[index] = message
        if (message.status !== Status.DRAFT) {
          this.numberOfUnreadMessages -= 1
        }
      }
      this.selectedMessage = message

      return message
    },

    async fetchMessageSummary(messageId: number | string, recipientEntityId: number) {
      const messageSummary: MessageSummary = await InboxApi.fetchMessageSummaryByMessageId(messageId, recipientEntityId)

      this._rawMessageSummary = cloneDeep(messageSummary)
      this._rawMessage = cloneDeep(messageSummary.message)

      const { text, templateVariables, symmetricKey, iv } = messageSummary.message
      if ((text || templateVariables) && symmetricKey && iv) {
        const decryptedSymmetricKey = await RSA.getDecryptedSymmetricKeyFromMessage(messageSummary.message, useSettingsStore().privateKey)
        messageSummary.message.symmetricKey = decryptedSymmetricKey
        messageSummary.message.text = text ? await AES.decrypt(decryptedSymmetricKey.randomBits, iv, text) : text
        messageSummary.message.templateVariables = templateVariables ? await AES.decrypt(decryptedSymmetricKey.randomBits, iv, templateVariables, true) : templateVariables
      }
      if (!isMessageSummaryRead(messageSummary)) {
        const index = this.messageSummaries.findIndex(m => m.message.id === messageSummary.message.id)
        messageSummary.messageSummaryUserStatuses = [{ status: 'READ' } as MessageSummaryUserStatus]

        this.messageSummariesPage!.content[index] = messageSummary
        this.numberOfUnreadMessageSummaries -= 1
      }
      this.selectedMessage = messageSummary.message
      this.selectedMessageSummary = messageSummary

      return messageSummary
    },
    async fetchNumberOfUnreadMessages(senderEntityId: number) {
      let numberOfUnread = await MessageApi.countUnreadSent(senderEntityId)
      this.numberOfUnreadMessages = numberOfUnread
      return numberOfUnread
    },
    async fetchNumberOfUnreadMessageSummaries(recipientEntityId: number) {
      let unreadMessageSummaries = await InboxApi.countUnread(recipientEntityId)
      this.numberOfUnreadMessageSummaries = unreadMessageSummaries
      return unreadMessageSummaries
    },
  },
})
export default useMessageStore
