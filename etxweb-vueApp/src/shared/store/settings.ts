import { defineStore } from 'pinia'
import { SettingsApi } from '@/shared/api/settingsApi'
import { Environment } from '@/model/environment'
import { RSA } from '@/utils/crypto/rsa'

const nameSpace = 'settings'

interface State {
  env: Environment
  serverPublicKey: CryptoKey
  privateKey: CryptoKey
  publicKey: CryptoKey
  publicKeyDerBase64: string
}

const useSettingsStore = defineStore(nameSpace, {
  state: (): State => ({
    env: Environment.TEST,
    serverPublicKey: {} as CryptoKey,
    privateKey: {} as CryptoKey,
    publicKey: {} as CryptoKey,
    publicKeyDerBase64: ''
  }),
  actions: {
    async fetchEnvironment(): Promise<Environment> {
      const env = await SettingsApi.getEnvironment()
      this.env = env
      return env
    },
    async fetchServerPublicKey(): Promise<CryptoKey> {
      const rsaPubKeyDto = await SettingsApi.getPublicKey()
      this.serverPublicKey = await RSA.importPublicKey(rsaPubKeyDto.pem)

      return this.serverPublicKey
    },
    async generateKeyPair() {
      const keyPair: CryptoKeyPair = await RSA.generateKey()
      this.privateKey = keyPair.privateKey!
      this.publicKey = keyPair.publicKey!
      this.publicKeyDerBase64 = await RSA.exportPublicKey(this.publicKey!)
    }
  }
})

export default useSettingsStore
