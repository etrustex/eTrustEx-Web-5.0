<template>
  <configuration-container :title="'Compromised certificate'"
                           :confirm-text="'Deactivate'"
                           :confirm-class="'ico-visibility-off-white'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="confirmDeactivation"
                           @reset="reset">
    <form class="form-admin" v-bind:class=" editMode ? 'form-admin-edit-mode' : 'form-admin-view-mode'">
      <div class="row">
        <label class="col-lg-4 col-form-label">Public Key<span class="label-required">*</span></label>
        <div v-if="editMode" class="col-lg-8 align-self-center">
          <public-key ref="publicKey"
                      :publicKey="publicKeyContent"
                      :publicKeyFileName="publicKeyFileName"
                      :editMode="editMode"
                      :ignore-store="true"
                      @updated="publicKeyContent = $event.rawKey; publicKeyFileName = $event.name"/>
          <span v-if="v$.publicKey.$error" class="error"> {{ v$.publicKey.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-4 col-form-label" for="businessSearch">Business identifier</label>
        <div v-if="editMode" class="col-lg-8 align-self-center">
          <group-search ref="businessSearch" :showIcons="false" :showIdentifier="true" :singleSelection="true"
                        @selected="updateBusiness($event[0])"/>
          <span v-if="v$.businessId.$error" class="error"> {{ v$.businessId.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-4 col-form-label" for="entitySearch">Entity identifier</label>
        <div v-if="compromisedCertificate.businessId" class="col-lg-8 align-self-center">
          <group-search ref="entitySearch" :businessId="compromisedCertificate.businessId"
                        :showIcons="false" :showIdentifier="true" :singleSelection="true"
                        @selected="updateEntity($event[0])"/>
          <span v-if="v$.entityId.$error" class="error"> {{ v$.entityId.$errors[0].$message }} </span>
        </div>
      </div>
    </form>
  </configuration-container>
</template>

<script setup lang="ts">
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { ref, Ref } from 'vue'
import useVuelidate from '@vuelidate/core'
import { compromisedCertificateRules } from '@/admin/views/shared/validation/CertificateValidator'
import PublicKey from '@/admin/views/shared/recipient_preferences/PublicKey.vue'
import useDialogStore, { buttonWithCallback, DialogType } from '@/shared/store/dialog'
import { CertificateSpec, GroupSearchItem } from '@/model/entities'
import GroupSearch from '@/admin/views/shared/group/GroupSearch.vue'
import { InboxApi } from '@/messages/api/inboxApi'

const dialogStore = useDialogStore()

const compromisedCertificate: Ref<Partial<CertificateSpec>> = ref(new CertificateSpec())

const publicKeyContent: Ref<string> = ref('')
const publicKeyFileName: Ref<string> = ref('')
const editMode: Ref<boolean> = ref(false)

const v$ = useVuelidate(compromisedCertificateRules, compromisedCertificate as Ref<CertificateSpec>)

function updateBusiness(business: GroupSearchItem | undefined) {
  compromisedCertificate.value.businessId = business?.id
  compromisedCertificate.value.entityId = undefined
}

function updateEntity(entity: GroupSearchItem | undefined) {
  compromisedCertificate.value.entityId = entity?.id
}

async function confirmDeactivation() {
  compromisedCertificate.value.publicKey = publicKeyContent.value
  await v$.value.$validate()
  if (v$.value.$error) {
    return
  }
  dialogStore.show(
    'Deactivate Messages',
    '<p>All messages sent with the specified public key will be deactivated.</p><p class="mt-3">This action cannot be undone</p><p class="mt-3">Are you sure you want to continue?</p>',
    DialogType.ERROR,
    buttonWithCallback('Deactivate', () => save()),
    buttonWithCallback('Cancel', () => reset()),
    true
  )
}

function save() {
  InboxApi.countMessageSummariesToDisable(compromisedCertificate.value as CertificateSpec)
    .then(numberOfMessagesToDeactivated => {
      if (numberOfMessagesToDeactivated === 0) {
        dialogStore.show(
          'No message to deactivate',
          '<p>No message with the specified public key to deactivate.</p>',
          DialogType.INFO,
          buttonWithCallback('Ok', () => reset())
        )
      } else {
        InboxApi.disable(compromisedCertificate.value as CertificateSpec)
          .then(() => {
            dialogStore.show(
              'Job Started',
              '<p>' + numberOfMessagesToDeactivated + ' messages sent with the specified public key will be deactivated.</p>',
              DialogType.INFO,
              buttonWithCallback('Ok', () => reset())
            )
          })
      }
    })
}

function reset() {
  compromisedCertificate.value = {} as CertificateSpec
  publicKeyFileName.value = ''
  publicKeyContent.value = ''

  editMode.value = false
  v$.value.$reset()
}
</script>
