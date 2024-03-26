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

import { expect } from 'vitest'

import crypto from 'crypto'
import { Buffer } from 'buffer'
import { RSA } from '@/utils/crypto/rsa'
import path from 'path'
import fs from 'fs'
import { getIdentitiesFromP12, Identity } from '@/utils/crypto/pkcsReader'
import { JwsHelper } from '@/utils/jwsHelper'

Object.defineProperty(global, 'crypto', {
  value: {
    subtle: crypto.webcrypto.subtle
  }
})

describe('RSA Utils', () => {
  test('WebCrypto decrypt & encrypt', async () => {
    const keyPair: CryptoKeyPair = await RSA.generateKey()
    const privateKey = keyPair.privateKey!
    const publicKey = keyPair.publicKey!
    const text = 'OqLGNXLeW63tX8f0zCab+wzKwnIdi13mCqKWmwTz4co='

    const encrypted = await RSA.encrypt(publicKey, text)
    console.log(encrypted)
    const decrypted = await RSA.decrypt(privateKey, encrypted)
    console.log(decrypted)

    expect(decrypted).toBe(text)
  })

  test('WebCrypto decrypt & encrypt using certificate', async () => {
    const certificatePath: string = path.resolve(__dirname + '/files/TestConfidential.p12')
    const p12: Buffer = fs.readFileSync(certificatePath)
    const p12identities: Identity[] = await getIdentitiesFromP12(p12, 'test123')
    const selectedIdentity: Identity = p12identities[0]

    const pem = selectedIdentity.publicKeyPem

    const publicKey = await RSA.importPublicKey(pem)

    const privateKey = await RSA.importPrivateKey(selectedIdentity.privateKey)

    const text = 'OqLGNXLeW63tX8f0zCab+wzKwnIdi13mCqKWmwTz4co='

    const encrypted = await RSA.encrypt(publicKey, text)
    console.log(encrypted)
    const decrypted = await RSA.decrypt(privateKey, encrypted)
    console.log(decrypted)

    expect(decrypted).toBe(text)
  })

  test('WebCrypto sign & verify', async () => {
    const certificatePath: string = path.resolve(__dirname + '/files/TestConfidential.p12')
    const p12: Buffer = fs.readFileSync(certificatePath)
    const p12identities: Identity[] = await getIdentitiesFromP12(p12, 'test123')
    const selectedIdentity: Identity = p12identities[0]

    const payload = 'Loren ipsum'
    const signature = await JwsHelper.sign(selectedIdentity, payload)

    expect(await JwsHelper.verify(payload, signature)).toBeTruthy()
    expect(await JwsHelper.verify(payload+'foo', signature)).toBeFalsy()
  })



  test('Parse public key', async () => {
    const keyPair: CryptoKeyPair = await RSA.generateKey()
    const publicKey = keyPair.publicKey!
    const exportedPubKey = await RSA.exportPublicKey(publicKey)
    const validatedPublicKey = await RSA.validateParsingPubKey(exportedPubKey)

    expect(validatedPublicKey).toBe(exportedPubKey)
  })
})
