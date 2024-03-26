<template>
  <configuration-container v-if="signatureGroupConfiguration?.dtype"
                           :title="'Signature'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row">
      <div class="col-lg-4 text-lg-end">
        Status
      </div>
      <div class="col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <input id="signatureGroupCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="signatureGroupConfiguration.active">
          <label class="form-check-label" for="signatureGroupCheckbox">
            <strong>{{ signatureGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
        <strong v-else class="form-check-label">
          <strong>{{ signatureGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
        </strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref } from 'vue'
import { GroupConfigurationSpec, SignatureGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode = ref(false)

const { signatureGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let signatureGroupConfigurationCopy = readonly(deepCopy(signatureGroupConfiguration.value))

async function save() {
  const spec = new GroupConfigurationSpec<boolean>()
  spec.active = signatureGroupConfiguration.value.active

  signatureGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<boolean, SignatureGroupConfiguration>(
    signatureGroupConfiguration.value, spec
  )
  signatureGroupConfigurationCopy = readonly(deepCopy(signatureGroupConfiguration.value))

  editMode.value = false
}

function reset() {
  signatureGroupConfiguration.value = deepCopy(signatureGroupConfigurationCopy) as SignatureGroupConfiguration
  editMode.value = false
}
</script>
