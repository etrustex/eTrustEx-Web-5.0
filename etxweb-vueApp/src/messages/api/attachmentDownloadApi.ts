import { AbortableFetch } from '@/messages/api/utils/abortableFetch'
import { DownloadableAttachmentSpec } from '@/model/attachmentDto'
import { DownloadHelper } from '@/messages/utils/downloadHelper'
import useProgressTrackerStore from '@/messages/store/progress'
import JSZip from 'jszip'
import { AesValues } from '@/model/entities'
import { getSaveStream } from '@/utils/saveStreamFactory'
import { AES } from '@/utils/crypto/aes'
import { createDigester, digesterTransformStream, FixedChunkSizeTransformStream, trackingTransformStream } from './streamTransformers'

export class AttachmentDownloadApi extends AbortableFetch {
  async download(attachment: DownloadableAttachmentSpec, key: CryptoKey) {
    this.resetAbortController()
    DownloadHelper.initProgressTracker([attachment])

    const attachmentReadableStream = await this.fetchAttachment(attachment.downloadUrl)
    if (!attachmentReadableStream) {
      console.error(`Failed to load: ${ attachment.downloadUrl }`)
      return
    }

    const writeable = await getSaveStream(attachment.attachmentSpec.name, attachment.attachmentSpec.originalByteLength)
      .catch(() => useProgressTrackerStore().close())
    if (!writeable) {
      return
    }
    const readable = await this.processAttachment(attachment, attachmentReadableStream, key)
    readable
      .pipeTo(writeable)
      .finally(() => useProgressTrackerStore().close())
  }

  async downloadAll(attachments: Array<DownloadableAttachmentSpec>, zipFileName: string, key: CryptoKey) {
    this.resetAbortController()
    DownloadHelper.initProgressTracker(attachments)

    const zip = new JSZip()

    for (const attachment of attachments) {
      const attachmentReadableStream = await this.fetchAttachment(attachment.downloadUrl)
      if (!attachmentReadableStream) {
        console.error(`Failed to load: ${ attachment.downloadUrl }`)
        return
      }

      const readableStream = await this.processAttachment(attachment, attachmentReadableStream, key)
      await this.addFileToZip(zip, attachment.attachmentSpec.name, readableStream)
    }

    await this.downloadZip(zip, zipFileName)
      .finally(() => useProgressTrackerStore().close())
  }

  private async processAttachment(attachment: DownloadableAttachmentSpec, readableStream: ReadableStream, key: CryptoKey) {
    const isGcmEncrypted = !attachment.attachmentSpec.iv

    const chunkSize = isGcmEncrypted ? AesValues.CHUNK_SIZE + AesValues.TAG_LENGTH_BYTES + AesValues.IV_LENGTH_BYTES : AesValues.CHUNK_SIZE
    return readableStream
      .pipeThrough(new FixedChunkSizeTransformStream(new Uint8Array(chunkSize)), { signal: this.signal })
      .pipeThrough(trackingTransformStream(), { signal: this.signal })
      .pipeThrough(digesterTransformStream(createDigester(), attachment.attachmentSpec.checkSum), { signal: this.signal })
      .pipeThrough(AES.gcmDecipherTransformStream(key), { signal: this.signal })
      .pipeThrough(digesterTransformStream(createDigester(), attachment.attachmentSpec.originalCheckSum), { signal: this.signal })
  }


  private async addFileToZip(zip: JSZip, fileName: string, readableStream: ReadableStream) {
    const fileChunks: any = []
    const writableStream = new WritableStream({
      write(chunk) {
        fileChunks.push(chunk)
      },
    })

    await readableStream
      .pipeTo(writableStream)

    zip.file(fileName, new Blob(fileChunks))
  }

  private async downloadZip(zip: JSZip, zipFileName: string) {
    const content = await zip.generateAsync({ type: 'blob' })

    const downloadLink = document.createElement('a')
    const url = URL.createObjectURL(content)
    downloadLink.href = url
    downloadLink.download = zipFileName

    downloadLink.click()
    URL.revokeObjectURL(url)
  }

  private async fetchAttachment(url: string) {
    if (this.signal.aborted) {
      return Promise.reject('Download cancelled')
    }

    const response = await fetch(url, { method: 'GET', signal: this.signal })
    if (!response.ok || response.body == null) {
      this.handleError(new Error(`Response status: ${ response.status }`))
    }
    return response.body
  }

  private handleError(error: Error) {
    if (this.signal.aborted) {
      error.name = 'AbortError'
    }
    useProgressTrackerStore()
      .handleError(error)
  }
}
