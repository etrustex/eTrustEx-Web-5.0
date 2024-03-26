import { AttachmentSpec, Link, Message, Rels } from '@/model/entities'
import { AttachmentStatus, DownloadableAttachmentSpec } from '@/model/attachmentDto'
import { extractRelUrl } from '@/utils/linksHandler'
import useProgressTrackerStore from '@/messages/store/progress'

export function compareByPathAndName(a: { name: string, path: string }, b: { name: string, path: string }) {
  return (a.path + a.name).localeCompare(b.path + b.name)
}

export class DownloadHelper {
  static toDownloadableAttachmentSpecArray(message: Message, links: Array<Link>): Array<DownloadableAttachmentSpec> {
    message.attachmentSpecs.sort(compareByPathAndName)
    const downloadableAttachmentSpecs: Array<DownloadableAttachmentSpec> = []
    message.attachmentSpecs.forEach(attachmentSpec => downloadableAttachmentSpecs.push(this.toDownloadableAttachmentSpec(links, attachmentSpec, message.senderGroup.id)))

    if (message.metadataAttachmentSpec) {
      downloadableAttachmentSpecs.push(this.toDownloadableAttachmentSpec(links, JSON.parse(message.metadataAttachmentSpec), message.senderGroup.id))
    }

    return downloadableAttachmentSpecs
  }

  static toDownloadableAttachmentSpec(links: Array<Link>, attachmentSpec: AttachmentSpec, entityId: number): DownloadableAttachmentSpec {
    return {
      status: AttachmentStatus.Pending,
      attachmentSpec,
      downloadUrl: extractRelUrl(links, Rels.ATTACHMENT_DOWNLOAD, { attachmentId: attachmentSpec.id, entityId })
    }
  }

  static initProgressTracker(attachments: Array<DownloadableAttachmentSpec>) {
    const totalBytes = attachments
      .filter(attachment => attachment.status === AttachmentStatus.Pending || attachment.status === AttachmentStatus.Error)
      .map(value => value.attachmentSpec.byteLength)
      .reduce((previousValue, currentValue) => previousValue + currentValue, 0)

    useProgressTrackerStore().init(totalBytes)
  }
}
