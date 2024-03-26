<template>
  <configuration-container v-if="unreadMessageReminderConfiguration.dtype"
                           :title="'Unread message reminder'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row">
      <div class="col-lg-4 text-lg-end">Status</div>
      <div class="col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <input id="unreadMessageCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="unreadMessageReminderConfiguration.active" v-on:change="activeChanged">
          <label class="form-check-label" for="unreadMessageCheckbox">
            <strong>{{ unreadMessageReminderConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
        <div v-else>
          <label class="form-check-label" for="alertDetailsCheckbox">
            <strong>{{ unreadMessageReminderConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
      </div>
    </div>
    <div class="row">
      <label for="retention_policy_notification" class="col-lg-4 col-form-label">
        Number of days
        <more-information
          content="Set up the number of days after a message is not read that a notification should be sent."
          placement="right"/>
      </label>
      <div class="col-lg-8">
        <div v-if="editMode && unreadMessageReminderConfiguration.active">
          <input id="retention_policy_notification" class="form-control" type="number" min="1" step="1"
                 v-model="unreadMessageReminderConfiguration.integerValue"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.integerValue.$error" class="error"> {{ v$.integerValue.$errors[0].$message }} </span>
        </div>
        <strong v-else>
          {{
            unreadMessageReminderConfiguration.integerValue > 0 ? unreadMessageReminderConfiguration.integerValue : ''
          }}
        </strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, UnreadMessageReminderConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useVuelidate from '@vuelidate/core'
import { unreadMessageReminderRules } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode: Ref<boolean> = ref(false)

const { unreadMessageReminderConfiguration } = storeToRefs(useBusinessConfigurationStore())
let unreadMessageReminderConfigurationCopy = readonly(deepCopy(unreadMessageReminderConfiguration.value))

const v$ = useVuelidate(unreadMessageReminderRules, unreadMessageReminderConfiguration)

async function save() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    const spec = new GroupConfigurationSpec<number>()
    spec.active = unreadMessageReminderConfiguration.value.active
    spec.value = unreadMessageReminderConfiguration.value.integerValue

    unreadMessageReminderConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, UnreadMessageReminderConfiguration>(
      unreadMessageReminderConfiguration.value, spec
    )
    unreadMessageReminderConfigurationCopy = readonly(deepCopy(unreadMessageReminderConfiguration.value))

    editMode.value = false
  }
}

function activeChanged() {
  if (!unreadMessageReminderConfiguration.value.active) {
    unreadMessageReminderConfiguration.value.integerValue = 0
  }
}

function reset() {
  unreadMessageReminderConfiguration.value = deepCopy(unreadMessageReminderConfigurationCopy) as UnreadMessageReminderConfiguration
  editMode.value = false
  v$.value.$reset()
}

</script>
