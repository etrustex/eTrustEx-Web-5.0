import { AttachmentSpec, Confidentiality, EncryptionMethod, Group, Integrity, Message, MessageSummarySpec, SendMessageRequestSpec, SymmetricKey, UpdateMessageRequestSpec } from '@/model/entities'
import useSettingsStore from '@/shared/store/settings'
import { RSA } from '@/utils/crypto/rsa'
import { AttachmentSpecWrapper } from '@/model/attachmentDto'
import { EntityE2EEncryption, GroupHelper } from '@/utils/groupHelper'
import { AES } from '@/utils/crypto/aes'
import { Buffer } from 'buffer'

export default class MessageFormUtils {
  static async setTextAndTemplateVariables(message: Message, updateMessageRequestSpec: UpdateMessageRequestSpec) {
    const settingsStore = useSettingsStore()

    if ((message.text || message.templateVariables) && message.symmetricKey && message.iv) {
      updateMessageRequestSpec.symmetricKey = {
        randomBits: await RSA.encrypt(settingsStore.serverPublicKey, message.symmetricKey.randomBits),
        encryptionMethod: EncryptionMethod.RSA_OAEP_SERVER,
      }
      updateMessageRequestSpec.iv = message.iv
    }

    if (message.text && message.symmetricKey && message.iv) {
      updateMessageRequestSpec.text = await AES.encrypt(message.symmetricKey.randomBits, message.iv, message.text)
    } else {
      updateMessageRequestSpec.text = message.text
    }

    if (message.templateVariables && message.symmetricKey && message.iv) {
      updateMessageRequestSpec.templateVariables = message.templateVariables ? await AES.encrypt(message.symmetricKey.randomBits, message.iv, message.templateVariables) : ''
    } else {
      updateMessageRequestSpec.templateVariables = message.templateVariables ? message.templateVariables : ''
    }
  }

  static async getMessageSummarySpecs(recipients: Array<Group>, attachmentsSymmetricKey: CryptoKey, e2eEnabled: boolean): Promise<Array<MessageSummarySpec>> {
    return await Promise.all(
      recipients.map(async recipient => {
        const aesKey = await MessageFormUtils.getAttachmentsSymmetricKeyInfo(recipient, attachmentsSymmetricKey, e2eEnabled)

        return ({
          recipientId: recipient.id,
          entitySpec: { entityIdentifier: recipient.identifier, businessIdentifier: recipient.businessIdentifier },
          symmetricKey: aesKey,
          integrity: recipient.recipientPreferences && recipient.recipientPreferences.integrity ? recipient.recipientPreferences.integrity : Integrity.MODERATE,
          confidentiality: recipient.recipientPreferences && recipient.recipientPreferences.confidentiality ? recipient.recipientPreferences.confidentiality : Confidentiality.PUBLIC,
          clientReference: '',
          signature: '',
        })
      }),
    )
    // .then(messageSummarySpecs)
  }

  static async getAttachmentsSymmetricKeyInfo(recipient: Group, aesKey: CryptoKey, e2eEnabled: boolean): Promise<SymmetricKey> {
    const rawKey = await AES.exportKey(aesKey)
    const attachmentsSymmetricKey = Buffer.from(rawKey).toString()

    const settingsStore = useSettingsStore()
    let encryptionMethod: EncryptionMethod
    let publicKey: CryptoKey

    if (recipient.recipientPreferences && recipient.recipientPreferences.publicKey && (e2eEnabled || recipient.recipientPreferences.confidentiality === Confidentiality.LIMITED_HIGH)) {
      encryptionMethod = EncryptionMethod.RSA_OAEP_E2E
      publicKey = await RSA.importPublicKey(recipient.recipientPreferences.publicKey)
    } else {
      encryptionMethod = EncryptionMethod.RSA_OAEP_SERVER
      publicKey = settingsStore.serverPublicKey
    }

    const encryptedRandomBits = await RSA.encrypt(publicKey, attachmentsSymmetricKey)

    return { randomBits: encryptedRandomBits, encryptionMethod }
  }

  static async buildMessageRequestSpec(message: Message, recipients: Array<Group>, attachmentsSymmetricKey: CryptoKey, attachmentSpecWrappers: Array<AttachmentSpecWrapper>, e2eEnabled: boolean): Promise<SendMessageRequestSpec> {
    const prepareMessageRequestSpecDataFunction = MessageFormUtils.prepareMessageRequestSpecData
    return prepareMessageRequestSpecDataFunction(message, recipients, attachmentsSymmetricKey, attachmentSpecWrappers, e2eEnabled)
  }


  static async prepareMessageRequestSpecData(message: Message, recipients: Array<Group>, attachmentsSymmetricKey: CryptoKey, attachmentSpecWrappers: Array<AttachmentSpecWrapper>, e2eEnabled: boolean): Promise<SendMessageRequestSpec> {
    const settingsStore = useSettingsStore()
    const attachments: Array<AttachmentSpec> =
      attachmentSpecWrappers.map(attachmentSpecDto => attachmentSpecDto.attachmentSpec)
    const attachmentsTotalByteLength: number = attachments.reduce((accumulator, attachmentSpecDto) => {
      return accumulator + attachmentSpecDto.byteLength
    }, 0)
    const attachmentTotalNumber: number = attachmentSpecWrappers.length
    const { ...messageCopyLessProps } = message
    const messageSummarySpecs = await MessageFormUtils.getMessageSummarySpecs(recipients, attachmentsSymmetricKey, e2eEnabled)

    const sendMessageRequestSpec: SendMessageRequestSpec = {
      ...messageCopyLessProps,
      recipients: messageSummarySpecs,
      attachmentsTotalByteLength,
      attachmentTotalNumber,
      attachmentSpecs: attachments,
      highImportance: message.highImportance,
    }

    if (messageCopyLessProps.symmetricKey) {
      sendMessageRequestSpec.symmetricKey = {
        randomBits: await RSA.encrypt(settingsStore.serverPublicKey, messageCopyLessProps.symmetricKey.randomBits),
        encryptionMethod: EncryptionMethod.RSA_OAEP_SERVER,
      }
    }

    await MessageFormUtils.setTextAndTemplateVariables(message, sendMessageRequestSpec)

    return sendMessageRequestSpec
  }

  static areAllRecipientsWithSameEncryption(recipients: Array<Group>, encryption: EntityE2EEncryption) {
    let count = 0
    recipients.forEach(group => {
      if (GroupHelper.getEncryptionConfig(group) === encryption) {
        count++
      }
    })
    return count === recipients.length
  }

}
