/*
 * Copyright (c) 2024. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

import fs, { PathLike } from 'fs'
import { createDigester } from '@/messages/api/streamTransformers'
import { expect } from 'vitest'
import { Transform } from 'stream'
import { once } from 'events'

export const deleteFile = (path: PathLike) => {
  fs.unlink(path, (err) => {
    if (err) throw err
    console.log(path + ' deleted')
  })
}

export const compareChecksumFiles = (path1: PathLike, path2: PathLike) => {
  const file1Digester = createDigester()
  const file2Digester = createDigester()

  file1Digester.update(new Uint8Array(fs.readFileSync(path1)))
  const file1Md = file1Digester.digest('hex')
  file2Digester.update(new Uint8Array(fs.readFileSync(path2)))
  const file2Md = file2Digester.digest('hex')

  expect(file2Md).toBe(file1Md)
}

export const encryptFile = async (originalFilePath: PathLike, encryptedPath: PathLike, cipher: Transform) => {
  const originalFileStream: fs.ReadStream = fs.createReadStream(originalFilePath)
  const encryptedStream: fs.WriteStream = fs.createWriteStream(encryptedPath)
  encryptedStream.on('error', (e) => console.error('Error:', e))

  originalFileStream
    .pipe(cipher)
    .pipe(encryptedStream)

  await once(encryptedStream, 'finish')
}

export const decryptFile = async (encryptedPath: PathLike, decryptedPath: PathLike, decipher: Transform) => {
  const readEncrypted: fs.ReadStream = fs.createReadStream(encryptedPath)
  const writeDecrypted: fs.WriteStream = fs.createWriteStream(decryptedPath)
  writeDecrypted.on('error', (e) => console.error('Error:', e))

  readEncrypted
    .pipe(decipher)
    .pipe(writeDecrypted)

  await once(writeDecrypted, 'finish')
}
