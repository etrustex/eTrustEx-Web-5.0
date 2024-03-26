import forge, { pki } from 'node-forge'

export interface Identity {
  id: string
  publicKeyPem: string
  privateKeyPem: string
  privateKey: pki.PrivateKey
  certificatePem: string
  notBefore: Date
  notAfter: Date
  friendlyName: string
  subject: { shortName: string, value: string }[]
}

export async function getIdentitiesFromP12(p12FileContent: Uint8Array, password: string) {
  const b64 = forge.util.binary.base64.encode(p12FileContent)
  const pkcs12Der = forge.util.decode64(b64)

  const pkcs12Asn1 = forge.asn1.fromDer(pkcs12Der)
  const pkcs12 = forge.pkcs12.pkcs12FromAsn1(pkcs12Asn1, false, password)

  const identitiesMap = new Map<string, Identity>()

  for (const safeContents of pkcs12.safeContents) {
    for (const safeBag of safeContents.safeBags.filter(safeBag => safeBag.attributes.localKeyId)) {
      const localKeyId = forge.util.bytesToHex(safeBag.attributes.localKeyId[0])

      if (!identitiesMap.has(localKeyId)) {
        identitiesMap.set(localKeyId, { id: localKeyId } as Identity)
      }

      const identity = identitiesMap.get(localKeyId) as Identity

      if (safeBag.type === pki.oids.pkcs8ShroudedKeyBag && safeBag.key) {
        identity.privateKey = safeBag.key
        identity.privateKeyPem = pki.privateKeyToPem(safeBag.key)
      } else if (safeBag.type === pki.oids.certBag && safeBag.cert) {
        identity.certificatePem = pki.certificateToPem(safeBag.cert)
        identity.notBefore = safeBag.cert.validity.notBefore
        identity.notAfter = safeBag.cert.validity.notAfter

        identity.friendlyName = safeBag.attributes.friendlyName?.[0] ?? 'not defined'

        identity.subject = safeBag.cert.subject.attributes.map(item => ({
          shortName: item.shortName!,
          value: item.value as string,
        }))

        identity.publicKeyPem = pki.publicKeyToPem(safeBag.cert.publicKey)
      }
    }
  }

  return Array.from(identitiesMap.values())
}
