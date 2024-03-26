<template>
  <configuration-container v-if="enforceEncryptionGroupConfiguration.dtype"
                           :title="'Enforce end-to-end encryption'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="confirmEnforceEncryption"
                           @reset="reset">
    <div class="row">
      <div class="col-lg-4 text-lg-end">
        Status
      </div>
      <div class="col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <input id="enableEncryptionCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="enforceEncryptionGroupConfiguration.active">
          <label class="form-check-label" for="enableEncryptionCheckbox">
            <strong>{{ enforceEncryptionGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
        <div v-else>
          <label class="form-check-label" for="alertDetailsCheckbox">
            <strong>{{ enforceEncryptionGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { EnforceEncryptionGroupConfiguration, GroupConfigurationSpec } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useDialogStore, { buttonWithCallback, DialogType } from '@/shared/store/dialog'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const dialogStore = useDialogStore()
const editMode: Ref<boolean> = ref(false)

const { enforceEncryptionGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let enforceEncryptionGroupConfigurationCopy = readonly(deepCopy(enforceEncryptionGroupConfiguration.value))

async function save() {
  const spec = new GroupConfigurationSpec<boolean>()
  spec.active = enforceEncryptionGroupConfiguration.value.active

  enforceEncryptionGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<boolean, EnforceEncryptionGroupConfiguration>(
    enforceEncryptionGroupConfiguration.value, spec
  )
  enforceEncryptionGroupConfigurationCopy = readonly(deepCopy(enforceEncryptionGroupConfiguration.value))

  editMode.value = false
}

function confirmEnforceEncryption() {
  if (enforceEncryptionGroupConfiguration.value.active) {
    dialogStore.show(
      'Enforce end-to-end encryption',
      `<p class="mt-3">End-to-end encyption will be enforced for all entities in the business <strong class="text-break">${enforceEncryptionGroupConfiguration.value.group.name}</strong>.</p><p class="mt-3"> Would you like to proceeed?</p>`,
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
  enforceEncryptionGroupConfiguration.value = deepCopy(enforceEncryptionGroupConfigurationCopy) as EnforceEncryptionGroupConfiguration
  editMode.value = false
  dialogStore.hide()
}

</script>
