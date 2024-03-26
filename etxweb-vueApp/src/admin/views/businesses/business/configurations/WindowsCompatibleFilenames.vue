<template>
  <configuration-container v-if="windowsCompatibleFilenamesGroupConfiguration.dtype"
                           :title="'Accept only Windows compatible filenames'"
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
          <input id="windowsCompatibleFilenamesCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="windowsCompatibleFilenamesGroupConfiguration.active">
          <label class="form-check-label" for="windowsCompatibleFilenamesCheckbox">
            <strong> {{ windowsCompatibleFilenamesGroupConfiguration.active ? 'Active' : 'Inactive' }} </strong>
          </label>
        </div>
        <div v-else>
          <label class="form-check-label" for="alertDetailsCheckbox">
            <strong> {{ windowsCompatibleFilenamesGroupConfiguration.active ? 'Active' : 'Inactive' }} </strong>
          </label>
        </div>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, WindowsCompatibleFilenamesGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode: Ref<boolean> = ref(false)

const { windowsCompatibleFilenamesGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let windowsCompatibleFilenamesGroupConfigurationCopy = readonly(deepCopy(windowsCompatibleFilenamesGroupConfiguration.value))

async function save() {
  const spec = new GroupConfigurationSpec<boolean>()
  spec.active = windowsCompatibleFilenamesGroupConfiguration.value.active

  windowsCompatibleFilenamesGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<boolean, WindowsCompatibleFilenamesGroupConfiguration>(
    windowsCompatibleFilenamesGroupConfiguration.value, spec
  )
  windowsCompatibleFilenamesGroupConfigurationCopy = readonly(deepCopy(windowsCompatibleFilenamesGroupConfiguration.value))

  editMode.value = false
}

function reset() {
  windowsCompatibleFilenamesGroupConfiguration.value = deepCopy(windowsCompatibleFilenamesGroupConfigurationCopy) as WindowsCompatibleFilenamesGroupConfiguration
  editMode.value = false
}

</script>
