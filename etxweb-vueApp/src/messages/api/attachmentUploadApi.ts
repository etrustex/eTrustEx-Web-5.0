import { AbortableFetch } from '@/messages/api/utils/abortableFetch'
import { AttachmentSpecWrapper, AttachmentStatus } from '@/model/attachmentDto'
import { extractLink, linkToUrl } from '@/utils/linksHandler'
import { AesValues, Link, Rels } from '@/model/entities'
import { createDigester, digesterTransformStream, FixedChunkSizeTransformStream, trackingTransformStream } from './streamTransformers'
import useOpenIdStore from '@/shared/store/openId'
import { addTicket } from '@/utils/openIdHelper'
import { AES } from '@/utils/crypto/aes'
import { Buffer } from 'buffer'

export interface AttachmentUploadResult {
  contentMD?: string
  clearTextMD?: string
  uploadedSize: number
  status: AttachmentStatus
  errorTooltip?: string
}

export class AttachmentUploadApi extends AbortableFetch {
  async uploadAttachment(senderEntityId: number, attachmentSpecDto: AttachmentSpecWrapper, attachmentsSymmetricKey: CryptoKey, isEncrypted: boolean) {
    const uploadLink: Link = extractLink(attachmentSpecDto.attachmentLinks, Rels.ATTACHMENT_UPLOAD)
    const clearDigester = createDigester()
    const processedDigester = createDigester()

    try {
      await attachmentSpecDto.file.stream()
        .pipeThrough(new FixedChunkSizeTransformStream(new Uint8Array(AesValues.CHUNK_SIZE)), { signal: this.signal })
        .pipeThrough(digesterTransformStream(clearDigester), { signal: this.signal })
        .pipeThrough(AES.gcmCipherTransformStream(attachmentsSymmetricKey), { signal: this.signal })
        .pipeThrough(digesterTransformStream(processedDigester), { signal: this.signal })
        .pipeThrough(trackingTransformStream(), { signal: this.signal })
        .pipeTo(new WritableStream({
          write: async (chunk) => await this.uploadChunk([chunk], uploadLink, senderEntityId, this.signal),
        }))

      const clearTextMD = clearDigester.digest('hex')
      const contentMD = processedDigester.digest('hex')
      attachmentSpecDto.attachmentSpec.originalCheckSum = clearTextMD.toUpperCase()

      return {
        contentMD: contentMD.toUpperCase(),
        clearTextMD: clearTextMD.toUpperCase(),
        uploadedSize: attachmentSpecDto.size,
        status: AttachmentStatus.Success,
      }
    } catch (err) {
      return {
        uploadedSize: attachmentSpecDto.size,
        status: AttachmentStatus.Error,
        errorTooltip: (err as { message: string }).message,
      }
    }
  }

  private async getCipherTransformer(attachmentSpecDto: AttachmentSpecWrapper, attachmentsSymmetricKey: CryptoKey, isEncrypted: boolean) {
    return AES.gcmCipherTransformStream(attachmentsSymmetricKey)
  }

  private async uploadChunk(chunkToUpload: Array<Buffer>, link: Link, senderEntityId: number, signal: AbortSignal) {
    const requestURL = linkToUrl(link, { senderEntityId })
    const digester = createDigester()
    chunkToUpload.forEach(piece => digester.update(piece))

    const openIdStore = useOpenIdStore()

    const ticket = await openIdStore.getAccessToken({ signal })

    const requestInit: RequestInit = {
      method: 'PUT',
      redirect: 'follow',
      credentials: 'omit',
      body: new Blob(chunkToUpload),
      headers: {
        'Content-Type': 'application/json',
        Authorization: `${ ticket.token_type } ${ openIdStore.signAccessToken(ticket.access_token) }`,
      },
      signal,
    }

    const response = await fetch(new Request(addTicket(requestURL, ticket.access_token), requestInit))
    const uploadResponse = await response.json()
    const serverSideSegmentChecksum = uploadResponse.checksum
    const clientSideSegmentChecksum = digester.digest('hex').toUpperCase()

    if (serverSideSegmentChecksum !== clientSideSegmentChecksum) {
      console.error('Checksum mismatch!')
      throw new Error(`Server digest: ${ serverSideSegmentChecksum } - client digest: ${ clientSideSegmentChecksum }`)
    }
  }
}
