export class ConverterUtils {
  static bufferToBase64String(arrayBuffer: ArrayBufferLike): string {
    return Buffer.from(arrayBuffer).toString('base64')
  }

  static base64StringToBuffer(b64str: string): Buffer {
    return Buffer.from(b64str, 'base64')
  }

  static stringToArrayBuffer(str:string): ArrayBuffer {
    const buf = new ArrayBuffer(str.length)
    const bufView = new Uint8Array(buf)
    for (let i = 0, strLen = str.length; i < strLen; i++) {
      bufView[i] = str.charCodeAt(i)
    }
    return buf
  }
}
