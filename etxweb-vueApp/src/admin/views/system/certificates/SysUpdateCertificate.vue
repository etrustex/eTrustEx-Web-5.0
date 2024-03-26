<template>
  <configuration-container :title="'Certificate update'"
                           :confirm-text="'Create link'"
                           :confirm-class="'ico-link-white'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="confirmAndSave"
                           @reset="reset">
    <form class="form-admin" v-bind:class=" editMode ? 'form-admin-edit-mode' : 'form-admin-view-mode'">
      <div class="row">
        <label class="col-lg-4 col-form-label">User EU Login ID<span class="label-required">*</span></label>
        <div v-if="editMode" class="col-lg-8 align-self-center">
          <input id="eu_login_id" class="form-control" type="text"
                 v-model="updateCertificateSpec.euLoginId"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.euLoginId.$error" class="error"> {{ v$.euLoginId.$errors[0].$message }} </span>
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
        <div v-if="updateCertificateSpec.businessId" class="col-lg-8 align-self-center">
          <group-search ref="entitySearch" :businessId="updateCertificateSpec.businessId"
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
import useDialogStore, { buttonWithCallback, DialogType } from '@/shared/store/dialog'
import { GroupSearchItem, UpdateCertificateSpec } from '@/model/entities'
import GroupSearch from '@/admin/views/shared/group/GroupSearch.vue'
import useVuelidate from '@vuelidate/core'
import { updateCertificateRules } from '@/admin/views/shared/validation/CertificateValidator'
import { CertificateUpdateApi } from '@/admin/api/certificateUpdateApi'
import { UserProfileApi } from '@/admin/api/userProfileApi'

const dialogStore = useDialogStore()

const updateCertificateSpec: Ref<Partial<UpdateCertificateSpec>> = ref(new UpdateCertificateSpec())

const editMode: Ref<boolean> = ref(false)
const entityIdentifier: Ref<string> = ref('')
const businessIdentifier: Ref<string> = ref('')
const generatedLink: Ref<string> = ref('')

const v$ = useVuelidate(updateCertificateRules, updateCertificateSpec as Ref<UpdateCertificateSpec>)

function updateBusiness(business: GroupSearchItem) {
  businessIdentifier.value = business.identifier
  updateCertificateSpec.value.businessId = business.id
  updateCertificateSpec.value.entityId = undefined
}

function updateEntity(entity: GroupSearchItem) {
  entityIdentifier.value = entity.identifier
  updateCertificateSpec.value.entityId = entity.id
}

function confirmAndSave() {
  v$.value.$validate()
  if (v$.value.$error) {
    return
  }

  CertificateUpdateApi.createLink(updateCertificateSpec.value as UpdateCertificateSpec)
    .then(link => {
      generatedLink.value = link
      if (updateCertificateSpec.value.entityId && updateCertificateSpec.value.euLoginId) {
        UserProfileApi.fetchUserProfileInfo(updateCertificateSpec.value.entityId, updateCertificateSpec.value.euLoginId)
          .then(user => {
            dialogStore.show(
              'Link to update a certificate',
              `<p>The link below can only be used by <strong>${user.name}</strong> to update the certificate used for encypting messages within the business domain
                       <strong>${businessIdentifier.value}</strong> and entity <strong>${entityIdentifier.value}</strong></p>
                       <p class="mt-3">Be aware that the new certificate should be already configured in the entity!</p><p class="mt-3"><a>${link}</a></p>
                       <p><div class="col-lg-1">
                        <button class="ico-standalone btn btn-outline-primary" type="button"
                                v-tooltip.tooltip="'Copy to clipboard'"
                                @click="${clipboard()}">
                            <span aria-hidden="true" class="ico-standalone"><i class="ico-inbox"></i>
                            </span>
                        </button>
                      </div></p>`,
              DialogType.INFO,
              buttonWithCallback('Ok', () => reset())
            )
          })
      }
    })
    .then(() => reset())
}

function clipboard() {
  navigator.clipboard.writeText(generatedLink.value)
}

function reset() {
  updateCertificateSpec.value = {} as UpdateCertificateSpec
  v$.value.$reset()

  editMode.value = false
}
</script>
