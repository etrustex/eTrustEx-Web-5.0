
/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

/**
 * @jest-environment jsdom
 */

import crypto from 'crypto'
import fs from 'fs'
import { AES } from '@/utils/crypto/aes'
import path from 'path'
import { AesValues } from '@/model/entities'
import { compareChecksumFiles, deleteFile } from './crypto.test.utils'

const { once } = require('events')

Object.defineProperty(global, 'crypto', {
  value: {
    subtle: crypto.webcrypto.subtle,
    getRandomValues: (arr: string | any[]) => crypto.randomBytes(arr.length)
  }
})


describe('AES GCM Subtle Crypto', async () => {
  test('encrypt & decrypt file in chunks appending auth tag to each encrypted chunk', async () => {
    const key = await AES.generateGcmKey()

    const originalFilePath: string = path.resolve(__dirname + '/files/30Mb.zip')
    const encryptedPath: string = path.resolve(__dirname + '/files/30Mb.zip.encrypted')
    const decryptedPath: string = path.resolve(__dirname + '/files/30Mb.zip.decrypted')

    fs.readFileSync(originalFilePath)

    const originalFileStreamReader = fs.createReadStream(originalFilePath, { highWaterMark: AesValues.CHUNK_SIZE })

    const cipher = AES.gcmCipher(key)

    const encryptedStreamWriter = fs.createWriteStream(encryptedPath)

    originalFileStreamReader
      .pipe(cipher)
      .pipe(encryptedStreamWriter)

    await once(encryptedStreamWriter, 'finish')

    fs.readFileSync(encryptedPath)

    const decipher = AES.gcmDecipher(key)

    const encryptedStreamReader = fs.createReadStream(encryptedPath, { highWaterMark: AesValues.CHUNK_SIZE + AesValues.TAG_LENGTH_BYTES + AesValues.IV_LENGTH_BYTES })
    const decryptedStreamWriter = fs.createWriteStream(decryptedPath)

    encryptedStreamReader
      .pipe(decipher)
      .pipe(decryptedStreamWriter)

    await once(decryptedStreamWriter, 'finish')

    compareChecksumFiles(originalFilePath, decryptedPath)

    deleteFile(encryptedPath)
    deleteFile(decryptedPath)

    console.log('rawKeyB64: ' + await AES.exportKey(key))
  })

})
