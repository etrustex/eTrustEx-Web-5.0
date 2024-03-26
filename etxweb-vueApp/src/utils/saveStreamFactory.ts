import { isSupportedBrowser, matchesFirefox } from '@/utils/browserUtils'
import streamSaver from 'streamsaver'
import { isLocalhost } from '@/utils/openIdHelper'
import { baseUrl } from '@/shared/store/rootLinks'

// remove this file once we verify native streams are working
const supportFileAccessAPI = !matchesFirefox && ('showSaveFilePicker' in window)
if (isSupportedBrowser()) {
  if (!supportFileAccessAPI) {
    if (typeof (WritableStream) === 'undefined') {
      streamSaver.WritableStream = WritableStream
    }
    if (typeof (TransformStream) === 'undefined') {
      streamSaver.TransformStream = TransformStream
    }

    // the mitm and the sw need be provided by a server with a valid certificate
    if (!isLocalhost(window.location.href)) {
      streamSaver.mitm = baseUrl() + 'stream-saver/mitm.html'
    }
  }
}

export async function getSaveStream(fileName: string, fileSize?: number) {
  const options: streamSaver.CreateWriteStreamOptions = fileSize ? { size: fileSize } : {}
  return streamSaver.createWriteStream(fileName, options)
}
