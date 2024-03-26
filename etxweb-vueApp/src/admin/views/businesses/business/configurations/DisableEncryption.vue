<template>
  <configuration-container v-if="disableEncryptionGroupConfiguration.dtype"
                           :title="'Disable end-to-end encryption'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="confirmDisableEncryption"
                           @reset="reset">
    <div class="row">
      <div class="col-lg-4 text-lg-end">
        Status
      </div>
      <div class="col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <input id="disableEncryptionCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="disableEncryptionGroupConfiguration.active">
          <label class="form-check-label" for="disableEncryptionCheckbox">
            <strong>{{ disableEncryptionGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
        <div v-else>
          <label class="form-check-label" for="alertDetailsCheckbox">
            <strong>{{ disableEncryptionGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { DisableEncryptionGroupConfiguration, GroupConfigurationSpec } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useDialogStore, { buttonWithCallback, DialogType } from '@/shared/store/dialog'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const dialogStore = useDialogStore()
const editMode: Ref<boolean> = ref(false)

const { disableEncryptionGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let disableEncryptionGroupConfigurationCopy = readonly(deepCopy(disableEncryptionGroupConfiguration.value))

async function save() {
  const spec = new GroupConfigurationSpec<boolean>()
  spec.active = disableEncryptionGroupConfiguration.value.active

  disableEncryptionGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<boolean, DisableEncryptionGroupConfiguration>(
    disableEncryptionGroupConfiguration.value, spec
  )
  disableEncryptionGroupConfigurationCopy = readonly(deepCopy(disableEncryptionGroupConfiguration.value))

  editMode.value = false
}

function confirmDisableEncryption() {
  if (disableEncryptionGroupConfiguration.value.active) {
    dialogStore.show(
      'Disable end-to-end encryption',
      `<p class="mt-3">End-to-end encyption will be disabled for all entities in the business <strong class="text-break">${disableEncryptionGroupConfiguration.value.group.name}</strong>. All public keys will be deleted.</p><p class="mt-3"> Would you like to proceeed?</p>`,
      DialogType.ERROR,
      buttonWithCallback('Ok', () => save()),
      buttonWithCallback('Cancel', () => reset())
    )
  } else {
    save()
      .then()
  }
}

function reset() {
  disableEncryptionGroupConfiguration.value = deepCopy(disableEncryptionGroupConfigurationCopy) as DisableEncryptionGroupConfiguration
  editMode.value = false
  dialogStore.hide()
}

</script>
