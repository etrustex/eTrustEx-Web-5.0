import { AttachmentSpec, Link } from '@/model/entities'

export class AttachmentSpecWrapper extends File {
  attachmentSpec: AttachmentSpec
  attachmentLinks: Array<Link>
  status?: AttachmentStatus
  errorTooltip?: string
  selected: boolean
  byteLength: number
  cipherTextChecksum: string
  cipherTextByteLength: number
  active: boolean

  get file() {
    return this as File
  }
}

export class DownloadableAttachmentSpec {
  attachmentSpec: AttachmentSpec
  status: AttachmentStatus
  downloadUrl: string
}

export enum AttachmentStatus {
  Pending = 'Pending upload',
  Uploading = 'Uploading',
  Error = 'Error',
  Success = 'Success',
  Forbidden = 'Forbidden'
}
