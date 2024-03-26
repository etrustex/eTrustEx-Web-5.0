<template>
  <configuration-container v-if="retentionPolicyGroupConfiguration?.dtype"
                           :title="'Retention policy'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row">
      <label for="retentionPolicy" class="col-lg-4 col-form-label">
        Number of days
        <more-information content="Maximum value allowed for the retention policy is 7300 days (~20 years)"
                          placement="right"/>
      </label>

      <div class="col-lg-8 align-self-center">
        <div v-if="editMode">
          <input id="retentionPolicy" class="form-control" type="number" min="1" step="1"
                 v-model="retentionPolicyGroupConfiguration.integerValue"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.integerValue.$error" class="error">
            {{ v$.integerValue.$errors[0].$message }}
          </span>
        </div>
        <strong v-else>
          {{
            retentionPolicyGroupConfiguration.integerValue > 0 ? retentionPolicyGroupConfiguration.integerValue : ''
          }}
        </strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, RetentionPolicyGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useVuelidate from '@vuelidate/core'
import { retentionPolicyRules } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'
import MoreInformation from '@/shared/views/MoreInformation.vue'

const editMode: Ref<boolean> = ref(false)

const { retentionPolicyGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let retentionPolicyGroupConfigurationCopy = readonly(deepCopy(retentionPolicyGroupConfiguration.value))

const v$ = useVuelidate(retentionPolicyRules, retentionPolicyGroupConfiguration)

async function save() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    const spec = new GroupConfigurationSpec<number>()
    spec.active = true
    spec.value = retentionPolicyGroupConfiguration.value.integerValue

    retentionPolicyGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, RetentionPolicyGroupConfiguration>(
      retentionPolicyGroupConfiguration.value, spec
    )
    retentionPolicyGroupConfigurationCopy = readonly(deepCopy(retentionPolicyGroupConfiguration.value))

    editMode.value = false
  }
}

function reset() {
  retentionPolicyGroupConfiguration.value = deepCopy(retentionPolicyGroupConfigurationCopy) as RetentionPolicyGroupConfiguration
  editMode.value = false
  v$.value.$reset()
}

</script>
