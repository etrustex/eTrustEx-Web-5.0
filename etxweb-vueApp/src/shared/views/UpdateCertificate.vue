<template>
  <div class="container-fluid my-5">
    <div class="row mt-3">
      <div class="col text-center">
        <h1>Certificate Update</h1>
        <p>Please add the certificate that needs to be updated and it's password. The system will update the messages
          with the new certificate that is configured for the entity.</p>
        <p>This process may take a few minutes. Please do not close the page.</p>
      </div>
    </div>

    <div class="row mt-3">
      <div class="col col-lg-5 text-lg-end">
        Entity
      </div>
      <div class="col col-lg-4">
        <strong>{{ groupIdentifier }}</strong>
      </div>
    </div>

    <div class="row mt-3 align-content-center mx-auto" style="width:60%!important; padding-left: 7rem">
      <CertificateView :allow-expired="true" :as-form-section="true"/>
    </div>

    <div v-if="sent" class="row mt-3">
      <div class="col col-lg-4"></div>
      <div class="col col-lg-5">
        <p class="alert alert-success">
          <strong>
            The messages have been successfully updated with the new certificate.
          </strong></p>
      </div>
    </div>

    <div class="row mt-3">
      <div class="col col-lg-5"></div>
      <div class="col col-lg-4 text-end">
        <button type="button" class="btn btn-primary" :disabled="sent || !isIdentitySelected" v-on:click="send()">
          Update
        </button>
      </div>
    </div>


  </div>
</template>

<script setup lang="ts">
import { computed, PropType, ref, Ref } from 'vue'
import { User } from '@/model/entities'
import CertificateView from '@/messages/views/CertificateView.vue'
import useCertificateStore from '@/shared/store/certificate'
import { storeToRefs } from 'pinia'
import { RSA } from '@/utils/crypto/rsa'
import useSettingsStore from '@/shared/store/settings'
import forge from 'node-forge'
import { AES } from '@/utils/crypto/aes'
import { CertificateUpdateApi } from '@/admin/api/certificateUpdateApi'
import { ConverterUtils } from '@/utils/converterUtils'

const props = defineProps({
  groupId: { type: Number, required: true },
  groupIdentifier: { type: String, required: true },
  user: { type: Object as PropType<User>, required: true },
})

const certificateStore = useCertificateStore()
const { selectedIdentity } = storeToRefs(certificateStore)
const isIdentitySelected = computed(() => !!selectedIdentity?.value)
const sent: Ref<boolean> = ref(false)

async function send() {
  const settingsStore = useSettingsStore()
  if (selectedIdentity?.value?.publicKeyPem) {
    const hashedContent = await createHashStr(selectedIdentity?.value?.publicKeyPem)

    const rsaPrivateKey = forge.pki.privateKeyFromPem(selectedIdentity.value.privateKeyPem)
    const pkcs8PrivateKey = RSA.privateKeyToPkcs8Pem(rsaPrivateKey)

    const messageAesKey = await AES.generateGcmKey()
    const randomBits = await AES.exportKey(messageAesKey)
    const iv = AES.generateIv()
    const ivB64 = ConverterUtils.bufferToBase64String(iv)

    const encryptedPrivateKey = await AES.encrypt(randomBits, ivB64, pkcs8PrivateKey)
    const encryptedRandomBits = await RSA.encrypt(settingsStore.serverPublicKey, randomBits)

    CertificateUpdateApi.updateCompromisedMessages({
      recipientEntityId: props.groupId,
      encryptedPrivateKey,
      hashedPublicKey: hashedContent,
      randomBits: encryptedRandomBits,
      iv: ivB64,
    }).then(() => sent.value = true)
  }
}

async function createHashStr(content: string) {
  const hashBuffer = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(content))

  // Convert the hash buffer to a hexadecimal string
  return Array.from(new Uint8Array(hashBuffer)).map(byte => byte.toString(16).padStart(2, '0')).join('')
}

</script>
