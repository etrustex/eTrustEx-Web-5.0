<template>
  <configuration-container v-if="retentionPolicyNotificationGroupConfiguration.dtype"
                           :title="'Retention policy warning notification'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row">
      <label for="retentionPolicyNotification" class="col-lg-4 col-form-label">
        Number of days
        <more-information
          content="Set up notification period before the messages are permanently removed due to the retention policy."
          placement="right"/>
      </label>

      <div class="col-lg-8">
        <div v-if="editMode">
          <input id="retentionPolicyNotification" class="form-control" type="number" min="1" step="1"
                 v-model="retentionPolicyNotificationGroupConfiguration.integerValue"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.integerValue.$error" class="error">
            {{ v$.integerValue.$errors[0].$message }}
          </span>
        </div>
        <strong v-else>
          {{
            retentionPolicyNotificationGroupConfiguration.integerValue === -1 ? '' : retentionPolicyNotificationGroupConfiguration.integerValue
          }}
        </strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, RetentionPolicyNotificationGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useVuelidate from '@vuelidate/core'
import { retentionPolicyNotificationRule } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode: Ref<boolean> = ref(false)

const { retentionPolicyNotificationGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let retentionPolicyNotificationGroupConfigurationCopy = readonly(deepCopy(retentionPolicyNotificationGroupConfiguration.value))

const v$ = useVuelidate(retentionPolicyNotificationRule, retentionPolicyNotificationGroupConfiguration)

async function save() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    const spec = new GroupConfigurationSpec<number>()
    spec.active = retentionPolicyNotificationGroupConfiguration.value.active
    spec.value = retentionPolicyNotificationGroupConfiguration.value.integerValue

    retentionPolicyNotificationGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, RetentionPolicyNotificationGroupConfiguration>(
      retentionPolicyNotificationGroupConfiguration.value, spec
    )
    retentionPolicyNotificationGroupConfigurationCopy = readonly(deepCopy(retentionPolicyNotificationGroupConfiguration.value))

    editMode.value = false
  }
}

function reset() {
  retentionPolicyNotificationGroupConfiguration.value = deepCopy(retentionPolicyNotificationGroupConfigurationCopy) as RetentionPolicyNotificationGroupConfiguration
  editMode.value = false
  v$.value.$reset()
}

</script>
