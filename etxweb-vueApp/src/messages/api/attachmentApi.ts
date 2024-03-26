import { AttachmentSpecWrapper, AttachmentStatus } from '@/model/attachmentDto'
import { extractRelUrl } from '@/utils/linksHandler'
import { Attachment, Message, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import useProgressTrackerStore from '@/messages/store/progress'
import { AttachmentUploadApi, AttachmentUploadResult } from '@/messages/api/attachmentUploadApi'

export const attachmentUploadApi = new AttachmentUploadApi()

export class AttachmentApi {
  static async createAndUploadAttachments(message: Message, attachmentSpecDtos: Array<AttachmentSpecWrapper>, attachmentsSymmetricKey: CryptoKey, isEncrypted: boolean) {
    const progressTrackerStore = useProgressTrackerStore()

    if (progressTrackerStore.status === 'idle') {
      throw new Error('Upload Failure')
    }

    // Change attachment upload status
    const pendingAttachments = attachmentSpecDtos.filter(
      attachment => attachment.status === AttachmentStatus.Pending || attachment.status === AttachmentStatus.Error
    )

    pendingAttachments.forEach((attachment) => {
      attachment.active = true
      attachment.status = AttachmentStatus.Uploading
    })

    // Create server-side attachments
    await AttachmentApi.createAttachments(message, pendingAttachments.length)
      .then(attachments => attachments.forEach((attachment, index) => {
        const inputAttachmentSpecDto = AttachmentApi.updateMetadataAfterServerSideCreation(attachment, pendingAttachments[index])
        const attachmentSpecIndex = attachmentSpecDtos.findIndex(a => a.name === inputAttachmentSpecDto.name)
        attachmentSpecDtos[attachmentSpecIndex] = inputAttachmentSpecDto
      }))

    const poolPromiseSize = 10
    const validAttachmentSpecDtos = attachmentSpecDtos.filter(a => a.status !== AttachmentStatus.Success && a.status !== AttachmentStatus.Forbidden)

    attachmentUploadApi.resetAbortController()
    for (let i = 0; i < (validAttachmentSpecDtos.length + poolPromiseSize); i += poolPromiseSize) {
      const attachmentSpecWrappers = validAttachmentSpecDtos.slice(i, i + poolPromiseSize)
      await Promise.all(attachmentSpecWrappers.map(async attachmentSpecDto => {
        const uploadResult = await attachmentUploadApi.uploadAttachment(message.senderGroup.id, attachmentSpecDto, attachmentsSymmetricKey, isEncrypted)

        AttachmentApi.updateMetadataAfterUpload(uploadResult, attachmentSpecDto)
        const attachmentSpecIndex = attachmentSpecDtos.findIndex(a => a.name === attachmentSpecDto.name)
        attachmentSpecDtos[attachmentSpecIndex] = attachmentSpecDto
      }))
    }
    progressTrackerStore.close()
  }

  static removeAttachment(attachmentSpecDto: AttachmentSpecWrapper): void {
    fetch(extractRelUrl(attachmentSpecDto.attachmentLinks, Rels.ATTACHMENT_DELETE), { method: 'DELETE' })
      .catch(reason => console.log(reason))
  }

  static async removeFailedAttachments(attachmentSpecDtos: Array<AttachmentSpecWrapper>) {
    attachmentSpecDtos.forEach(attachment => {
      if (attachment.status === AttachmentStatus.Error) {
        AttachmentApi.removeAttachment(attachment)
      }
    })
  }

  static removeAttachments(messageId: number, attachmentIds: Array<number>) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ATTACHMENTS_DELETE)

    const promises = []
    for (let i = 0; i < attachmentIds.length; i += 500) {
      const chunk = attachmentIds.slice(i, i + 500)
      promises.push(HttpRequest.delete(link, { params: { messageId }, body: JSON.stringify(chunk) }) as Promise<void>)
    }

    return Promise.all(promises)
  }

  private static async createAttachments(message: Message, numberOfAttachments: number): Promise<Array<Attachment>> {
    const dialogStore = useDialogStore()

    const httpOptions = { method: 'POST' }
    const createAttachmentUrl = extractRelUrl(message.links, Rels.ATTACHMENT_CREATE, { numberOfAttachments })

    const response = await fetch(createAttachmentUrl, httpOptions)
    if (response.status === 403) {
      dialogStore.show('', 'The message is not available for edition anymore.', DialogType.ERROR)
      throw new Error(response.statusText)
    }
    return response.json()
  }

  private static updateMetadataAfterServerSideCreation(attachment: Attachment, attachmentSpecWrapper: AttachmentSpecWrapper): AttachmentSpecWrapper {
    attachmentSpecWrapper.attachmentSpec = {
      ...attachmentSpecWrapper.attachmentSpec,
      id: attachment.id,
      originalByteLength: attachmentSpecWrapper.size,
      name: attachmentSpecWrapper.name,
      type: attachmentSpecWrapper.type,
      path: attachmentSpecWrapper.name.substring(0, attachmentSpecWrapper.name.lastIndexOf('/'))
    }

    attachmentSpecWrapper.attachmentLinks = attachment.links

    return attachmentSpecWrapper
  }

  private static updateMetadataAfterUpload(attachmentUploadResult: AttachmentUploadResult, attachmentSpecWrapper: AttachmentSpecWrapper): void {
    attachmentSpecWrapper.active = false
    attachmentSpecWrapper.status = attachmentUploadResult.status
    attachmentSpecWrapper.byteLength = attachmentUploadResult.uploadedSize
    attachmentSpecWrapper.cipherTextChecksum = attachmentUploadResult.contentMD!
    attachmentSpecWrapper.errorTooltip = attachmentUploadResult.errorTooltip
  }
}
