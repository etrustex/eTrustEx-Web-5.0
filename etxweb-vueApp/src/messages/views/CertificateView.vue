<template>
  <div v-bind:class=" asFormSection ? '' : 'message-detail-certificate'">
    <div v-if="!asFormSection" class="text-center">
      <h3 class="variant">Certificate </h3>
    </div>

    <div class="row mt-2">
      <label class="col-lg-3 col-form-label" for="certificateFile">Certificate<span class="label-required">*</span></label>
      <div class="col-lg-9 message-detail-select-certificate">
        <label class="btn btn-outline-primary btn-ico" for="certificateFile">
          <span aria-hidden="true" class="ico-standalone"><i class="ico-add-primary"></i></span>
          <span class="ms-0">Add certificate</span>
          <span aria-hidden="true" class="ico-standalone"><i class="ico-card-membership"></i></span>
        </label>
        <input id="certificateFile" class="form-control-file" style="visibility: hidden;"
               type="file" @change="setP12File($event.target.files[0])"/>
        <p class="mb-0 text-truncate">
          {{ p12FileName }}
        </p>
        <span v-if="v$.p12FileName.$error" class="error"> {{ v$.p12FileName.$errors[0].$message }} </span>
        <span v-if="v$.fileSize.$error" class="error"> {{ v$.fileSize.$errors[0].$message }} </span>
      </div>
    </div>

    <div class="row mt-3 align-items-center">
      <label class="col-lg-3 col-form-label" for="certificatePassword">Certificate Password<span class="label-required">*</span></label>
      <div class="col-lg-9">
        <div class="input-group">
          <input id="certificatePassword" v-model="p12Password" class="form-control" type="password"
                 v-on:change="passwordChanged()">
          <button class="btn btn-outline-primary btn-ico" type="submit" @click="loadIdentities()">
            <span aria-hidden="true" class="ico-standalone"><i class="ico-sync-primary"></i></span>
            <span>Load Identities</span>
          </button>
        </div>
        <span v-if="v$.p12Password.$error" class="error"> {{ v$.p12Password.$errors[0].$message }} </span>
      </div>
    </div>

    <table v-if="identities.length" aria-label="Certificate identities"
           class="mt-3 table align-middle">
      <thead>
      <tr>
        <th scope="col" class="col-1"></th>
        <th scope="col" class="col-7">Id</th>
        <th scope="col" class="col-4">Period</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="identity in identities" :key="identity.id"
          v-bind:class="{ invalid: isIdentityInvalid(identity) }"
          v-on:click="() => selectedIdentity = identity"
      >
        <td v-tooltip.tooltip="isIdentityExpired(identity) ? 'This identity is expired' : (isIdentityNotYetValid(identity) ? 'This identity is not yet valid' : '')"
            class="col-1">
          <img v-if="!isIdentityInvalid(identity)" alt="valid certificate" class="ico-valid-certificate"
               src="@/assets/ico/rounded-check-circle-success.svg" style="width: 24px;">
          <img v-else alt="invalid certificate" class="ico-invalid-certificate"
               src="@/assets/ico/rounded-error-danger.svg" style="width: 24px;">
        </td>
        <td class="col-7">
          <div class="form-check">
            <input
              :id="identity.id"
              :checked="selectedIdentity && selectedIdentity.id === identity.id"
              :value="identity"
              class="form-check-input"
              type="radio"
            >
            <label class="form-check-label text-start">
              <span class="text-break">{{ identity.friendlyName }}</span>
            </label>
          </div>
          <span class="text-break">{{ formatSubjects(identity) }}</span>
        </td>
        <td class="col-4">
          <small>valid from</small><br> {{ formatDate(identity.notBefore) }}<br>
          <small>valid until</small><br> {{ formatDate(identity.notAfter) }}
        </td>
      </tr>
      </tbody>
    </table>
    <span v-if="v$.isIdentitySelected.$error" class="error"> {{ v$.isIdentitySelected.$errors[0].$message }} </span>
    <span v-if="v$.selectedIdentity.$error" class="error"> {{ getSelectedIdentityError() }} </span>
  </div>
</template>
<script setup lang="ts">
import { computed, Ref, ref } from 'vue'
import { formatDate } from '@/utils/formatters'
import useDialogStore from '@/shared/store/dialog'
import useCertificateStore from '@/shared/store/certificate'
import { getIdentitiesFromP12, Identity } from '@/utils/crypto/pkcsReader'
import useVuelidate from '@vuelidate/core'
import { certificateRules } from '@/admin/views/shared/validation/CertificateValidator'
import { storeToRefs } from 'pinia'

const CERTIFICATE_MAX_FILE_SIZE = 102400

const props = defineProps({
  allowExpired: { type: Boolean, default: true },
  asFormSection: { type: Boolean, default: false }
})

const dialogStore = useDialogStore()
const certificateStore = useCertificateStore()
const { p12Password, selectedIdentity, p12FileName, identities, fileSize } = storeToRefs(certificateStore)

const isIdentitySelected = computed(() => !!selectedIdentity?.value)
const allowExpired = ref(props.allowExpired)
const p12File: Ref<File | undefined> = ref()

const v$ = useVuelidate(certificateRules, { p12FileName, fileSize, p12Password, isIdentitySelected, selectedIdentity, allowExpired })

function isIdentityInvalid(identity: Identity) {
  return isIdentityExpired(identity) || isIdentityNotYetValid(identity)
}

function isIdentityExpired(identity: Identity) {
  return Date.now() > identity.notAfter.getTime()
}

function isIdentityNotYetValid(identity: Identity) {
  return Date.now() < identity.notBefore.getTime()
}

function passwordChanged() {
  certificateStore.loadIdentities([])
}

function formatSubjects(identity: Identity) {
  return identity.subject.map((subject) => `${subject.shortName}: ${subject.value}`).join('; ')
}

function setP12File(file: File) {
  if (file) {
    certificateStore.clear()
    certificateStore.setSP12FileMetadata(file)
    p12File.value = file
  }
}

function getSelectedIdentityError() {
  return props.allowExpired
    ? 'It is required to select a valid or expired identity'
    : 'It is required to select a valid identity'
}

function validate() {
  v$.value.$validate()
}

function loadIdentities() {
  validate()
  if (v$.value.p12FileName.$error || v$.value.p12Password.$error || !p12File.value) {
    return
  }
  if (CERTIFICATE_MAX_FILE_SIZE < certificateStore.fileSize) {
    return
  }

  const fileReader = new FileReader()

  fileReader.onload = () => {
    getIdentitiesFromP12(new Uint8Array(fileReader.result as ArrayBuffer), p12Password.value)
      .then((p12identities) => certificateStore.loadIdentities(p12identities))
      .catch(() => {
        dialogStore.show('Cannot load identities!', 'The certificate cannot be opened, please check the file and the password!')
      })
  }

  fileReader.readAsArrayBuffer(p12File.value)
}

defineExpose({
  validate
})

</script>
