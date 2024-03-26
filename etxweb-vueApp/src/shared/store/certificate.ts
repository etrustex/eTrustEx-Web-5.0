import { Identity } from '@/utils/crypto/pkcsReader'
import { defineStore } from 'pinia'

const noFileChosen = 'No file chosen'
const nameSpace = 'certificate'

interface State {
  identities: Array<Identity>
  selectedIdentity: Identity
  p12FileName: string
  p12FileSize: number
  p12Password: string
}

const useCertificateStore = defineStore(nameSpace, {
  state: (): State => ({
    identities: [],
    selectedIdentity: {} as Identity,
    p12FileName: noFileChosen,
    p12FileSize: -1,
    p12Password: '',
  }),
  getters: {
    fileSize: (state) => state.p12FileSize,
  },
  actions: {
    clear() {
      this.$reset()
    },
    loadIdentities(ids: Array<Identity>) {
      this.identities = ids

      if (ids.length === 1) {
        this.selectedIdentity = this.identities[0]
      }

      return ids
    },
    setSP12FileMetadata(p12File: File) {
      this.p12FileName = p12File.name
      this.p12FileSize = p12File.size
    },
    removeIdentity(identity: Identity) {
      this.identities = this.identities.filter(i => i.id !== identity.id)
      if (this.selectedIdentity.id === identity.id) {
        this.selectedIdentity = this.identities.length === 1 ? this.identities[0] : {} as Identity
      }
    },
  },
})

export default useCertificateStore
