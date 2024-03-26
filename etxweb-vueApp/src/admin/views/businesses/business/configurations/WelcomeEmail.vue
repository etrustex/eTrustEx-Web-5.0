<template>
  <configuration-container v-if="welcomeEmailGroupConfiguration.dtype"
                           :title="'Welcome email'"
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
          <input id="welcomeEmailCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="welcomeEmailGroupConfiguration.active">
          <strong>{{ welcomeEmailGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
        </div>
        <div v-else>
          <strong>{{ welcomeEmailGroupConfiguration.active ? 'Active' : 'Inactive' }}</strong>
        </div>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, WelcomeEmailGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode: Ref<boolean> = ref(false)

const { welcomeEmailGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let welcomeEmailGroupConfigurationCopy = readonly(deepCopy(welcomeEmailGroupConfiguration.value))

async function save() {
  const spec = new GroupConfigurationSpec<boolean>()
  spec.active = welcomeEmailGroupConfiguration.value.active

  welcomeEmailGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<boolean, WelcomeEmailGroupConfiguration>(
    welcomeEmailGroupConfiguration.value, spec
  )
  welcomeEmailGroupConfigurationCopy = readonly(deepCopy(welcomeEmailGroupConfiguration.value))

  editMode.value = false
}

function reset() {
  welcomeEmailGroupConfiguration.value = deepCopy(welcomeEmailGroupConfigurationCopy) as WelcomeEmailGroupConfiguration
  editMode.value = false
}

</script>
