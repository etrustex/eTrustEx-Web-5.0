<template>
  <configuration-container v-if="supportEmailGroupConfiguration.dtype"
                           :title="'Support email address'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row">
      <label for="supportEmail" class="col-lg-4 col-form-label">
        Support email address
        <more-information
          content="Add an email address for support to be displayed on the footer and help section of the application."
          placement="right"/>
      </label>
      <div class="col-lg-8 align-self-center">
        <div v-if="editMode">
          <input id="supportEmail" class="form-control"
                 v-model.trim="supportEmailGroupConfiguration.stringValue">
          <span v-if="v$.stringValue.$error" class="error"> {{ v$.stringValue.$errors[0].$message }} </span>
        </div>
        <strong v-else>{{ supportEmailGroupConfiguration.stringValue }}</strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, SupportEmailGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { validEmails } from '@/admin/views/shared/validation/EmailValidator'
import useVuelidate from '@vuelidate/core'

const editMode: Ref<boolean> = ref(false)

const { supportEmailGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let supportEmailGroupConfigurationCopy = readonly(deepCopy(supportEmailGroupConfiguration.value))

const supportEmailFormRule = {
  stringValue: {
    validEmail: validEmails
  }
}

const v$ = useVuelidate(supportEmailFormRule, supportEmailGroupConfiguration)

async function save() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    const spec = new GroupConfigurationSpec<string>()
    spec.active = true
    spec.value = supportEmailGroupConfiguration.value.stringValue

    supportEmailGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<string, SupportEmailGroupConfiguration>(
      supportEmailGroupConfiguration.value, spec
    )
    supportEmailGroupConfigurationCopy = readonly(deepCopy(supportEmailGroupConfiguration.value))

    editMode.value = false
  }
}

function reset() {
  supportEmailGroupConfiguration.value = deepCopy(supportEmailGroupConfigurationCopy) as SupportEmailGroupConfiguration
  editMode.value = false
  v$.value.$reset()
}

</script>
