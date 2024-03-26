import { defineStore } from 'pinia'
import { Confidentiality, Group, Message } from '@/model/entities'
import { AttachmentSpecWrapper } from '@/model/attachmentDto'
import MessageFormUtils from '@/messages/utils/messageFormUtils'
import { JwsHelper } from '@/utils/jwsHelper'
import { Identity } from '@/utils/crypto/pkcsReader'
import { MessageApi } from '@/messages/api/messageApi'
import useCertificateStore from '@/shared/store/certificate'
import AttachmentStatusHelper from '@/utils/attachments/attachmentStatusHelper'
import FileNameHelper from '@/utils/attachments/fileNameHelper'
import { AES } from '@/utils/crypto/aes'
import { AttachmentApi } from '@/messages/api/attachmentApi'

const nameSpace = 'message-form'

interface State {
  message: Message
  messageRecipients: Array<Group>
  attachmentsSymmetricKey: CryptoKey
  attachmentSpecDtos: Array<AttachmentSpecWrapper>
  fileNames: Set<string>
  templateVariables: any
}

function prepareAttachmentName(fileNames: Set<string>, attachmentName: string) {
  let counter = 0
  const name = FileNameHelper.trimNameIfWindowsCompatibleFileNameConfigured(attachmentName)
  while (fileNames.has(FileNameHelper.addCounterToFileName(name, counter))) {
    counter++
  }

  return FileNameHelper.addCounterToFileName(name, counter)
}

const useMessageFormStore = defineStore(nameSpace, {
  state: (): State => ({
    message: {} as Message,
    messageRecipients: [],
    attachmentsSymmetricKey: {} as CryptoKey,
    attachmentSpecDtos: [],
    fileNames: new Set<string>(),
    templateVariables: {}
  }),
  getters: {},
  actions: {
    async generateAttachmentsAesKey() {
      this.attachmentsSymmetricKey = await AES.generateGcmKey()
    },
    async sendMessage(recipients: Array<Group>, e2eEnabled: boolean, sign: boolean) {
      //  the metadata change depending on whether we use encryption or not.
      //  as we are setting the metadata in the message attachmentSpecs we cannot deal with a mixed set of recipients
      //  some of which require encryption and some don't

      this.attachmentSpecDtos.forEach(attachmentSpecWrapper => {
        attachmentSpecWrapper.attachmentSpec.checkSum = attachmentSpecWrapper.cipherTextChecksum
        attachmentSpecWrapper.attachmentSpec.byteLength = attachmentSpecWrapper.byteLength
      })

      const messageRequestSpec = await MessageFormUtils.buildMessageRequestSpec(
        this.message, recipients, this.attachmentsSymmetricKey, this.attachmentSpecDtos, e2eEnabled
      )

      if (e2eEnabled) {
        messageRequestSpec.recipients.forEach(recipient => recipient.confidentiality = Confidentiality.LIMITED_HIGH)
      }
      if (sign) {
        await JwsHelper.signMessage(useCertificateStore().selectedIdentity as Identity, messageRequestSpec)
      }

      return MessageApi.sendMessage(this.message.links, messageRequestSpec)
    },
    updateComment(value: { attachmentSpecWrapper: AttachmentSpecWrapper, text: string }) {
      const index = this.attachmentSpecDtos.findIndex(a => a.name === value.attachmentSpecWrapper.name)
      this.attachmentSpecDtos[index].attachmentSpec.comment = value.text
    },
    updateConfidential(value: { attachmentSpecWrapper: AttachmentSpecWrapper }) {
      const index = this.attachmentSpecDtos.findIndex(a => a.name === value.attachmentSpecWrapper.name)
      this.attachmentSpecDtos[index].attachmentSpec.confidential = !this.attachmentSpecDtos[index].attachmentSpec.confidential
    },
    addAttachment(inputAttachmentSpecDto: AttachmentSpecWrapper) {
      const name = prepareAttachmentName(this.fileNames, inputAttachmentSpecDto.name)

      const inputAttachmentSpecDtoWithCustomFields: AttachmentSpecWrapper = {
        ...inputAttachmentSpecDto,
        name,
        selected: false,
        attachmentSpec: { originalByteLength: inputAttachmentSpecDto.size }
      } as AttachmentSpecWrapper

      AttachmentStatusHelper.setStatusAndErrorTooltip(inputAttachmentSpecDtoWithCustomFields, name)
      this.fileNames.add(name)
      this.attachmentSpecDtos.push(inputAttachmentSpecDtoWithCustomFields)
    },
    renameAttachment(value: { attachmentSpecWrapper: AttachmentSpecWrapper, name: string }) {
      if (value.name) {
        const previousName = value.attachmentSpecWrapper.name
        const index = this.attachmentSpecDtos.findIndex(a => a.name === previousName)
        const name = prepareAttachmentName(this.fileNames, value.name)

        const inputAttachmentSpecDtoWithCustomFields: AttachmentSpecWrapper = {
          ...this.attachmentSpecDtos[index],
          name,
          errorTooltip: undefined
        }
        AttachmentStatusHelper.setStatusAndErrorTooltip(inputAttachmentSpecDtoWithCustomFields, name)

        this.attachmentSpecDtos[index] = inputAttachmentSpecDtoWithCustomFields
        this.fileNames.delete(previousName)
        this.fileNames.add(name)
      }
    },
    removeAttachment(attachmentSpecWrapper: AttachmentSpecWrapper) {
      const index = this.attachmentSpecDtos.findIndex(a => a.name === attachmentSpecWrapper.name)
      this.deleteAttachmentByIndex(index)
    },
    deleteAttachmentByIndex(index: number) {
      const attachmentSpecDto = this.attachmentSpecDtos[index]

      if (attachmentSpecDto.attachmentLinks) {
        AttachmentApi.removeAttachment(attachmentSpecDto)
      }

      this.attachmentSpecDtos.splice(index, 1)
      this.fileNames.delete(attachmentSpecDto.name)
    },
    async bulkDeleteAttachments(attachmentSpecs: Array<AttachmentSpecWrapper>) {
      const attachmentIds = attachmentSpecs
        .filter(a => !!a.attachmentLinks)
        .map(a => a.attachmentSpec.id)

      if (attachmentIds) {
        await AttachmentApi.removeAttachments(this.message.id, attachmentIds)
      }

      attachmentSpecs.forEach(attachmentSpec => {
        this.attachmentSpecDtos.splice(this.attachmentSpecDtos.indexOf(attachmentSpec), 1)
        this.fileNames.delete(attachmentSpec.name)
      })
    }
  }
})
export default useMessageFormStore
