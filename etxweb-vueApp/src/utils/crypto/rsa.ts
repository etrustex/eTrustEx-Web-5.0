/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

import { EncryptionMethod, Message, SymmetricKey } from '@/model/entities'
import isUtf8 from '@/utils/isUTF8'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import { Buffer } from 'buffer'
import { asn1, pki } from 'node-forge'
import { ConverterUtils } from '@/utils/converterUtils'
import { constants, publicEncrypt } from 'crypto'

/**
 * WebCrypto does not support exporting in PKCS#12.
 * We use forge third party library
 *
 * encrypt: getBytes, encrypt, encode, toString
 * decrypt: getBytes, decode, decrypt, toString
 */

export class RSA {
  static async parsePublicKey(bytes: Uint8Array): Promise<string> {
    return !isUtf8(bytes) ? this.getPublicKeyFromDerFile(bytes) : this.getPublicKeyFromPem(bytes)
  }

  static parseRawPublicKey(bytes: Uint8Array) {
    const parsed = !isUtf8(bytes)
      ? '-----BEGIN PUBLIC KEY-----\n' + ConverterUtils.bufferToBase64String(bytes) + '\n-----END PUBLIC KEY-----\n'
      : new TextDecoder().decode(bytes)
    return parsed.replace(/\n/g, '\r\n')
  }

  static async getPublicKeyFromDerFile(bytes: Uint8Array) {
    const content = Buffer.from(bytes)
      .toString('base64')
    const wrappedPubKey = '-----BEGIN PUBLIC KEY-----\n' + content + '\n' + '-----END PUBLIC KEY-----'

    return this.validateParsingPubKey(wrappedPubKey)
  }

  static async getPublicKeyFromPem(bytes: Uint8Array) {
    const content = new TextDecoder().decode(bytes)

    if (this.isOpenSshFormat(content)) {
      useDialogStore()
        .show('OpenSSH format (ssh-rsa ... Not supported!', '<p><strong>The file contains a public key in OpenSSH format</strong></p><p>Please, select a public key file in PEM format</p>', DialogType.ERROR)

      throw Error('OpenSSH format (ssh-rsa ... Not supported!')
    }

    return this.validateParsingPubKey(content)
  }

  static async validateParsingPubKey(publicKeyStr: string): Promise<string> {
    try {
      const publicKey: CryptoKey = await RSA.importPublicKey(publicKeyStr)

      return RSA.exportPublicKey(publicKey)
    } catch (e) {
      useDialogStore()
        .show('Unrecognized public key file!', '<p><strong>The selected file is not recognized as a valid public key</strong></p>', DialogType.ERROR)

      throw Error('Unrecognized public key file!')
    }
  }

  static isOpenSshFormat(content: string): boolean {
    // eslint-disable-next-line
    const rsaRegex = /^(?:ssh-rsa AAAAB3NzaC1yc2|ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNT|ecdsa-sha2-nistp384 AAAAE2VjZHNhLXNoYTItbmlzdHAzODQAAAAIbmlzdHAzOD|ecdsa-sha2-nistp521 AAAAE2VjZHNhLXNoYTItbmlzdHA1MjEAAAAIbmlzdHA1Mj|ssh-ed25519 AAAAC3NzaC1lZDI1NTE5|ssh-dss AAAAB3NzaC1kc3)[0-9A-Za-z+/].*\n?$/

    return rsaRegex.test(content)
  }

  /************************************************************************/
  /** WebCrypto RSA-OAEP **************************************************/

  /************************************************************************/
  static async generateKey(): Promise<CryptoKeyPair> {
    return await crypto.subtle.generateKey(
      {
        name: 'RSA-OAEP',
        modulusLength: 2048, // can be 1024, 2048, or 4096
        publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
        hash: { name: 'SHA-1' }, // can be "SHA-1", "SHA-256", "SHA-384", or "SHA-512"
      },
      true, // whether the key is extractable (i.e. can be used in exportKey)
      ['encrypt', 'decrypt'], // must be ["encrypt", "decrypt"] or ["wrapKey", "unwrapKey"]
    )
      .then((key) => {
        return key
      })
      .catch((err) => {
        throw err
      })
  }

  static async encrypt(publicKey: CryptoKey, text: string): Promise<string> {
    const data = ConverterUtils.base64StringToBuffer(text)

    return crypto.subtle.encrypt(
      {
        name: 'RSA-OAEP',
        // label: Uint8Array([...]) //optional
      },
      publicKey, // from generateKey or importKey above
      data, // ArrayBuffer of data you want to encrypt
    )
      .then((encrypted) => {
        // returns an ArrayBuffer containing the encrypted data
        return ConverterUtils.bufferToBase64String(encrypted)
      })
      .catch((err) => {
        throw err
      })
  }

  static async encryptLegacy(keyPem: string, randomBits: string): Promise<string> {
    const LEGACY_PADDING = constants.RSA_PKCS1_PADDING

    const data = Buffer.from(randomBits)

    const encryptedRandomBits = RSA.publicCipher(keyPem, data, LEGACY_PADDING)
    return encryptedRandomBits.toString('base64')
  }

  static publicCipher(publicKeyPem: string, clearText: Buffer, padding: number | undefined): Buffer {

    // ETRUSTEX-5841 publicCipher(keyPem, randomBits) might return less than 256 bytes.
    // The leftmost bytes with all 0s are omitted from the returned Buffer.
    // as a workaround we add them back when the buffer is too short
    let encrypted: Buffer = publicEncrypt({ key: publicKeyPem, padding }, clearText)
    if (encrypted.length < 256) {
      console.log(`padding encrypted key: length was ${ encrypted.length }`)
      const diff = 256 - encrypted.length
      const zeroPadding = Buffer.alloc(diff)
      for (let i = 0; i < diff; i++) {
        zeroPadding.writeUInt8(0, i)
      }
      encrypted = Buffer.concat([zeroPadding, encrypted])
    }

    return encrypted
  }

  static async decrypt(privateKey: CryptoKey, text: string): Promise<string> {
    const data = ConverterUtils.base64StringToBuffer(text)

    return crypto.subtle.decrypt(
      {
        name: 'RSA-OAEP',
        // label: Uint8Array([...]) //optional
      },
      privateKey, // from generateKey or importKey above
      data, // ArrayBuffer of the data
    )
      .then((decryptedData) => {
        // returns an ArrayBuffer containing the decrypted data
        return ConverterUtils.bufferToBase64String(decryptedData)
      })
      .catch((err) => {
        throw err
      })
  }

  static async exportPublicKey(publicKey: CryptoKey): Promise<string> {
    return crypto.subtle.exportKey(
      'spki', // can be "jwk" (public or private), "spki" (public only), or "pkcs8" (private only)
      publicKey, // can be a publicKey or privateKey, as long as extractable was true
    )
      .then(function (keydata) {
        // returns the exported key data
        return ConverterUtils.bufferToBase64String(keydata)
      })
      .catch((err) => {
        throw err
      })
  }

  private static removePemHeaders(pem: string): string {
    const pemHeader = '-----BEGIN PUBLIC KEY-----'
    const pemFooter = '-----END PUBLIC KEY-----'
    return pem
      .replace(pemHeader, '')
      .replace(pemFooter, '')
      .replace(/[\r\n]+/gm, '')
      .trim()
  }

  /**
   * Converts exported Public Key string to CryptoKey (public)
   * @param pem The Public Key as exported. That is, in pem format without headers, CR/LF and trimmed
   */
  static async importPublicKey(pem: string): Promise<CryptoKey> {
    const pemContents = this.removePemHeaders(pem)

    return crypto.subtle.importKey(
      'spki', // can be "jwk" (public or private), "spki" (public only), or "pkcs8" (private only)
      ConverterUtils.base64StringToBuffer(pemContents),
      { name: 'RSA-OAEP', hash: { name: 'SHA-1' } }, // can be "SHA-1", "SHA-256", "SHA-384", or "SHA-512"
      true, // whether the key is extractable (i.e. can be used in exportKey)
      ['encrypt'], // "encrypt" or "wrapKey" for public key import or "decrypt" or "unwrapKey" for private key imports
    )
  }

  /**
   * Converts exported Public Key string to CryptoKey (public)
   * @param pem The Public Key as exported. That is, in pem format without headers, CR/LF and trimmed
   */
  static async importSignaturePublicKey(pem: string): Promise<CryptoKey> {
    const pemContents = this.removePemHeaders(pem)

    return window.crypto.subtle.importKey(
      'spki',
      ConverterUtils.base64StringToBuffer(pemContents),
      { name: 'RSA-PSS', hash: { name: 'SHA-512' } },
      false,
      ['verify'],
    )
  }

  /**
   * Converts forge.pki.PrivateKey to CryptoKey (private)
   * 1. Remove pem header & footer
   * 2. Remove CR/LF & trim
   * 3. Trim
   * 4. Base64 decode the string to get the binary data
   * 5. Convert from a binary string to an ArrayBuffer
   * 6. import key
   * @param privateKey
   */
  static async importPrivateKey(privateKey: pki.PrivateKey): Promise<CryptoKey> {
    const privateKeyInfoDerBuff = RSA.privateKeyToPkcs8Der(privateKey)

    return crypto.subtle.importKey(
      'pkcs8', // can be "jwk" (public or private), "spki" (public only), or "pkcs8" (private only)
      privateKeyInfoDerBuff,
      { name: 'RSA-OAEP', hash: { name: 'SHA-1' } }, // can be "SHA-1", "SHA-256", "SHA-384", or "SHA-512"
      false, // whether the key is extractable (i.e. can be used in exportKey)
      ['decrypt'], // "encrypt" or "wrapKey" for public key import or "decrypt" or "unwrapKey" for private key imports
    )
  }


  /**
   * Converts forge.pki.PrivateKey to CryptoKey (private)
   * @param privateKey
   */
  static async importSignaturePrivateKey(privateKey: pki.PrivateKey): Promise<CryptoKey> {
    const privateKeyInfoDerBuff = RSA.privateKeyToPkcs8Der(privateKey)

    // import the PKCS#8 DER-formatted key
    return window.crypto.subtle.importKey(
      'pkcs8',
      privateKeyInfoDerBuff,
      { name: 'RSA-PSS', hash: { name: 'SHA-512' } },
      false,
      ['sign'])
  }

  static async getDecryptedSymmetricKeyFromMessage(message: Message, privateKey: CryptoKey): Promise<SymmetricKey> {
    const { symmetricKey } = message

    if (symmetricKey && message.iv && symmetricKey.encryptionMethod === EncryptionMethod.RSA_OAEP_SERVER) {
      symmetricKey.randomBits = await RSA.decrypt(privateKey, symmetricKey.randomBits)
    }

    return symmetricKey
  }

  static privateKeyToPkcs8Der(privateKey: pki.PrivateKey) {
    const rsaPrivateKey = pki.privateKeyToAsn1(privateKey)
    const privateKeyInfo = pki.wrapRsaPrivateKey(rsaPrivateKey)
    const privateKeyInfoDer = asn1.toDer(privateKeyInfo).getBytes()

    return ConverterUtils.stringToArrayBuffer(privateKeyInfoDer)
  }

  static privateKeyToPkcs8Pem(privateKey: pki.PrivateKey) {
    const rsaPrivateKey = pki.privateKeyToAsn1(privateKey)
    const privateKeyInfo = pki.wrapRsaPrivateKey(rsaPrivateKey)
    return pki.privateKeyInfoToPem(privateKeyInfo)
  }
}
