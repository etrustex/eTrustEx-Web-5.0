<template>
  <div class="dropdown">
    <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
      <span class="ico-standalone"><i class="ico-card-membership"></i></span>
    </button>
    <ul class="dropdown-menu">
      <li v-for="certificate in certificates" :key="certificate.id" :id="certificate.id">
        <button class="dropdown-item" v-on:click="removeCertificate(certificate)">
          <span class="dropdown-item-ico">
            <img aria-hidden="true" alt="" src="@/assets/ico/rounded-delete.svg">
            <span>{{ certificate.friendlyName }}</span>
          </span>
        </button>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import useCertificateStore from '@/shared/store/certificate'
import { Identity } from '@/utils/crypto/pkcsReader'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'

export default defineComponent({
  name: 'CertificatesDropdown',
  setup() {
    const dialogStore = useDialogStore()
    const certificateStore = useCertificateStore()
    const certificates = computed(() => certificateStore.identities)

    function removeCertificate(certificate: Identity) {
      const modalMessage = `
        <p class="mt-3">Are you sure you want to remove the certificate?</p>
        <h4 class="text-break">${certificate.friendlyName}</h4>
        <p class="mt-3 mb-0">This action cannot be undone.</p>
        <p>The page might be refreshed for this action to take place.</p>`
      const primary = buttonWithCallback('Delete', () => {
        certificateStore.removeIdentity(certificate)
        if (certificateStore.identities.length === 0) {
          window.location.reload()
        }
        return Promise.resolve()
      })

      dialogStore.show('Remove certificate', modalMessage, DialogType.ERROR, primary, CANCEL_BUTTON)
    }

    return {
      certificates, removeCertificate
    }
  }
})
</script>
