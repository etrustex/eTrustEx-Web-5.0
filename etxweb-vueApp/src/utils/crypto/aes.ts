import { Buffer } from 'buffer'
import { Transform } from 'stream'
import { AesValues } from '@/model/entities'
import { ConverterUtils } from '@/utils/converterUtils'


const AES_GCM = 'AES-GCM'

export class AES {
  /************************************************************************/
  /** WebCrypto / AES GCM Encryption in chunks ****************************/

  /************************************************************************/
  static generateIv(): Uint8Array {
    return crypto.getRandomValues(new Uint8Array(AesValues.IV_LENGTH_BYTES))
  }

  static async generateGcmKey(): Promise<CryptoKey> {
    return await crypto.subtle.generateKey(
      { name: AES_GCM, length: AesValues.AES_KEY_LENGTH_BITS },
      true, // whether the key is extractable (i.e. can be used in exportKey)
      ['encrypt', 'decrypt'] // can "encrypt", "decrypt", "wrapKey", or "unwrapKey"
    )
      .then((key: CryptoKey) => {
        // returns a key object
        return key
      })
      .catch((err) => {
        throw err
      })
  }

  static async exportKey(key: CryptoKey): Promise<string> {
    return crypto.subtle.exportKey(
      'raw', // can be "jwk" or "raw"
      key // extractable must be true
    )
      .then((keydata) => {
        // returns the exported key data
        return Buffer.from(keydata)
          .toString('base64')
      })
      .catch((err) => {
        throw err
      })
  }

  static async exportKeyBytes(key: CryptoKey): Promise<Buffer> {
    return crypto.subtle.exportKey(
      'raw', // can be "jwk" or "raw"
      key // extractable must be true
    )
      .then((keydata) => {
        // returns the exported key data
        return Buffer.from(keydata)
      })
      .catch((err) => {
        throw err
      })
  }

  static async importGcmKey(key: string): Promise<CryptoKey> {
    return crypto.subtle.importKey(
      'raw', // can be "jwk" or "raw"
      ConverterUtils.base64StringToBuffer(key),
      { // this is the algorithm options
        name: AES_GCM,
        length: AesValues.AES_KEY_LENGTH_BITS
      },
      true, // whether the key is extractable (i.e. can be used in exportKey)
      ['encrypt', 'decrypt'] // can "encrypt", "decrypt", "wrapKey", or "unwrapKey"
    )
      .then((key) => {
        // returns the symmetric key
        return key
      })
      .catch((err) => {
        throw err
      })
  }

  static gcmCipherTransformStream(key: CryptoKey) {
    return new TransformStream({
      async transform(chunk, controller) {
        try {
          const iv: Uint8Array = AES.generateIv()
          const encryptedChunk = await AES.encryptData(key, iv, chunk)
          const encryptedAndIv = new Uint8Array(encryptedChunk.length + iv.length)
          encryptedAndIv.set(encryptedChunk)
          encryptedAndIv.set(iv, encryptedChunk.length)

          controller.enqueue(encryptedAndIv)
        } catch (error) {
          console.error('Error in gcmCipherTransformStream:', error)
          controller.error(error)
        }
      }
    })
  }

  static gcmCipher(key: CryptoKey): Transform {
    return new Transform({
      transform(chunk, encoding, callback) {
        const iv: Uint8Array = AES.generateIv()

        AES.encryptData(key, iv, chunk as Buffer)
          .then(encryptedChunk => {
            const encryptedAndIv = new Uint8Array(encryptedChunk.length + iv.length)
            encryptedAndIv.set(encryptedChunk)
            encryptedAndIv.set(iv, encryptedChunk.length)
            callback(null, encryptedAndIv)
          })
      }
    })
  }

  static gcmDecipherTransformStream(key: CryptoKey) {
    return new TransformStream({
      async transform(chunk, controller) {
        try {
          const encrypted: Uint8Array = chunk.subarray(0, chunk.length - AesValues.IV_LENGTH_BYTES)
          const iv: Uint8Array = chunk.subarray(chunk.length - AesValues.IV_LENGTH_BYTES)
          const decryptedChunk = await AES.decryptData(key, iv, encrypted)
          controller.enqueue(decryptedChunk)
        } catch (error) {
          console.error('Error in gcmDecipherTransformStream:', error)
          controller.error(error)
        }
      }
    })
  }

  static gcmDecipher(key: CryptoKey): Transform {
    return new Transform({
      transform(chunk, encoding, callback) {
        const encrypted: Uint8Array = chunk.subarray(0, chunk.length - AesValues.IV_LENGTH_BYTES)
        const iv: Uint8Array = chunk.subarray(chunk.length - AesValues.IV_LENGTH_BYTES)
        AES.decryptData(key, iv, encrypted)
          .then(decryptedChunk => {
            callback(null, decryptedChunk)
          })
      }
    })
  }

  static async encrypt(key: string, ivB64: string, plainText: string): Promise<string> {
    const cryptoKey = await AES.importGcmKey(key)
    const iv = new Uint8Array(ConverterUtils.base64StringToBuffer(ivB64))
    const data = new TextEncoder().encode(plainText)
    const encryptedData = await AES.encryptData(cryptoKey, iv, Buffer.from(data))

    return Buffer.from(encryptedData)
      .toString('base64')
  }

  static async encryptData(key: CryptoKey, iv: Uint8Array, data: Buffer): Promise<Uint8Array> {
    const encryptedBuffer = await crypto.subtle.encrypt(
      {
        name: AES_GCM,
        iv,
        tagLength: AesValues.TAG_LENGTH_BITS
      },
      key,
      data
    )
      .catch((err) => {
        throw err
      })

    return Buffer.from(encryptedBuffer)
  }

  static async decrypt(key: string, ivB64: string, encryptedText: string, allowPlain: boolean = false): Promise<string> {
    const cryptoKey = await AES.importGcmKey(key)
    const iv = new Uint8Array(ConverterUtils.base64StringToBuffer(ivB64))
    const encryptedData = new Uint8Array(ConverterUtils.base64StringToBuffer(encryptedText))
    const decryptedData = await AES.decryptData(cryptoKey, iv, Buffer.from(encryptedData))

    return new TextDecoder('utf-8').decode(decryptedData)
  }

  static async decryptData(key: CryptoKey, iv: Uint8Array, data: ArrayBuffer): Promise<Uint8Array> {
    const decryptedArrayBuffer: ArrayBuffer = await crypto.subtle.decrypt(
      {
        name: AES_GCM,
        iv,
        tagLength: AesValues.TAG_LENGTH_BITS
      },
      key,
      data
    )
      .then((decrypted) => {
        return new Uint8Array(decrypted)
      })
      .catch((err) => {
        console.error(err)
        throw err
      })

    return Buffer.from(decryptedArrayBuffer)
  }

}
