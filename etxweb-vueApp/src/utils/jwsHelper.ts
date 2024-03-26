import { asn1, pki, util } from 'node-forge'
import { AttachmentSpec, Confidentiality, Integrity, MessageSummary, MessageSummarySpec, SendMessageRequestSpec } from '@/model/entities'
import { Identity } from '@/utils/crypto/pkcsReader'
import { RSA } from '@/utils/crypto/rsa'
import { ConverterUtils } from '@/utils/converterUtils'

export class JwsHelper {
  static async signMessage(identity: Identity, sendMessageRequestSpec: SendMessageRequestSpec) {
    for (const messageSummarySpec of sendMessageRequestSpec.recipients) {
      const payload = this.toSignedFields(sendMessageRequestSpec, messageSummarySpec)
      const signature = await this.sign(identity, JSON.stringify(payload))
      messageSummarySpec.signature = JSON.stringify(signature)
    }
  }

  static async verifyMessage(messageSummary: MessageSummary) {
    try {
      const payload = this.toVerifiableFields(messageSummary)
      const signature = JSON.parse(messageSummary.signature)
      return await this.verify(JSON.stringify(payload), signature)
    } catch (e) {
      console.error(e)
      return false
    }
  }

  static async sign(identity: Identity, payload: string) {
    const dataBuffer = new TextEncoder().encode(payload)

    const encodedSignature = await window.crypto.subtle.sign(
      { name: 'RSA-PSS', saltLength: 64 },
      await RSA.importSignaturePrivateKey(identity.privateKey),
      dataBuffer,
    )

    return {
      header: { alg: 'PS512', x5c: [JwsHelper.toDer(identity.certificatePem)] },
      signature: ConverterUtils.bufferToBase64String(encodedSignature)
    }
  }

  static async verify(payload: string, signature: { header: { x5c: string[] }, signature: string }) {
    const publicKeyPem = JwsHelper.getPublicKeyPem(signature.header.x5c)
    const dataBuffer = new TextEncoder().encode(payload)

    return await window.crypto.subtle.verify(
      { name: 'RSA-PSS', saltLength: 64 },
      await RSA.importSignaturePublicKey(publicKeyPem),
      ConverterUtils.base64StringToBuffer(signature.signature),
      dataBuffer,
    )
  }

  static toDer(certificatePem: string) {
    const certificate = pki.certificateFromPem(certificatePem)
    const certAsn1 = pki.certificateToAsn1(certificate)
    const der = asn1.toDer(certAsn1)
    return util.encode64(der.getBytes())
  }

  static getPublicKeyPem(certificatePems: Array<string>) {
    const certAsn1 = asn1.fromDer(util.decode64(certificatePems[0]))
    const certificate = pki.certificateFromAsn1(certAsn1)
    return pki.publicKeyToPem(certificate.publicKey)
  }

  static toSignedFields(sendMessageRequestSpec: SendMessageRequestSpec, messageSummarySpec: MessageSummarySpec) {
    const signedFields: SignedFields = {
      attachmentSpecs: sendMessageRequestSpec.attachmentSpecs.map(this.sortAttachmentSpecFields),
      attachmentsTotalByteLength: sendMessageRequestSpec.attachmentsTotalByteLength,
      attachmentTotalNumber: sendMessageRequestSpec.attachmentTotalNumber,
      subject: sendMessageRequestSpec.subject,
      confidentiality: messageSummarySpec.confidentiality,
      integrity: messageSummarySpec.integrity,
    }

    if (sendMessageRequestSpec.text) {
      signedFields.text = sendMessageRequestSpec.text
    }
    if (sendMessageRequestSpec.templateVariables && Object.keys(sendMessageRequestSpec.templateVariables).length) {
      signedFields.templateVariables = sendMessageRequestSpec.templateVariables
    }

    return signedFields
  }

  static toVerifiableFields(messageSummary: MessageSummary) {
    const { message } = messageSummary
    const signedFields: SignedFields = {
      attachmentSpecs: message.attachmentSpecs.map(this.sortAttachmentSpecFields),
      attachmentsTotalByteLength: message.attachmentsTotalByteLength,
      attachmentTotalNumber: message.attachmentTotalNumber,
      subject: message.subject,
      confidentiality: messageSummary.confidentiality,
      integrity: messageSummary.integrity,
    }

    if (message.text) {
      signedFields.text = message.text
    }
    if (message.templateVariables) {
      signedFields.templateVariables = message.templateVariables
    }

    return signedFields
  }

  static sortAttachmentSpecFields(spec: AttachmentSpec): AttachmentSpec {
    return {
      originalByteLength: spec.originalByteLength,
      id: spec.id,
      byteLength: spec.byteLength,
      name: spec.name,
      type: spec.type,
      path: spec.path,
      originalCheckSum: spec.originalCheckSum,
      checkSum: spec.checkSum,
    } as AttachmentSpec
  }
}

interface SignedFields {
  attachmentSpecs: AttachmentSpec[]
  attachmentsTotalByteLength: number
  attachmentTotalNumber: number
  subject: string
  templateVariables?: any
  text?: string
  confidentiality: Confidentiality
  integrity: Integrity
}
