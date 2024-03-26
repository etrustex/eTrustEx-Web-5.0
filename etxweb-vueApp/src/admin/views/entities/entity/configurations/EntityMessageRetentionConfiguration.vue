<template>
  <entity-configuration-container v-if="retentionPolicyEntityConfiguration.dtype"
                                  :title="'Message Retention'"
                                  :edit-mode="editMode"
                                  :hide-edit-button="!canEdit"
                                  :show-info-button="!canEdit"
                                  :info-content="'This functionality can only be configured by a Business or System Administrator'"
                                  @edit="editMode = true"
                                  @confirm="validateAndSave"
                                  @reset="reset">
    <div class="row">
      <div class="col-lg-4 text-lg-end">Status</div>
      <div class="col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <input id="unreadMessageCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="retentionPolicyEntityConfiguration.active" v-on:change="activeChanged">
          <label class="form-check-label" for="unreadMessageCheckbox">
            <strong>{{ retentionPolicyEntityConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
        <div v-else>
          <label class="form-check-label" for="alertDetailsCheckbox">
            <strong>{{ retentionPolicyEntityConfiguration.active ? 'Active' : 'Inactive' }}</strong>
          </label>
        </div>
      </div>
    </div>

    <div class="row">
      <label for="retentionPolicy" class="col-lg-4 col-form-label">
        Number of days
        <more-information content="Maximum value allowed for the retention policy is 7300 days (~20 years)"
                          placement="right"/>
      </label>

      <div class="col-lg-8 align-self-center">
        <div v-if="editMode && retentionPolicyEntityConfiguration.active">
          <input id="retentionPolicy" class="form-control" type="number" min="1" step="1"
                 v-model="retentionPolicyEntityConfiguration.integerValue"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.integerValue.$error" class="error">
            {{ v$.integerValue.$errors[0].$message }}
          </span>
        </div>
        <strong v-else>
          {{
            retentionPolicyEntityConfiguration.integerValue > 0 ? retentionPolicyEntityConfiguration.integerValue : ''
          }}
        </strong>
      </div>
    </div>
  </entity-configuration-container>
</template>

<script setup lang="ts">

import EntityConfigurationContainer
  from '@/admin/views/entities/entity/configurations/shared/EntityConfigurationContainer.vue'
import { computed, readonly, ref, Ref } from 'vue'
import {
  GroupConfigurationSpec,
  RetentionPolicyEntityConfiguration,
  RetentionPolicyGroupConfiguration,
} from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useVuelidate from '@vuelidate/core'
import { retentionPolicyRules } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import { storeToRefs } from 'pinia'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import useEntityConfigurationStore from '@/shared/store/entityConfiguration'
import useAuthenticationStore from '@/shared/store/authentication'
import { useRoute } from 'vue-router'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import useDialogStore from '@/shared/store/dialog'

const route = useRoute()
const dialogStore = useDialogStore()
const authenticationStore = useAuthenticationStore()

const editMode: Ref<boolean> = ref(false)
const canEdit = computed(() => authenticationStore.isSysAdmin() || authenticationStore.isBusinessAdmin(+route.params.businessId))

const { retentionPolicyEntityConfiguration } = storeToRefs(useEntityConfigurationStore())
let retentionPolicyEntityConfigurationCopy = readonly(deepCopy(retentionPolicyEntityConfiguration.value))

const { retentionPolicyGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())

const v$ = useVuelidate(retentionPolicyRules, retentionPolicyEntityConfiguration)

function activeChanged() {
  if (!retentionPolicyEntityConfiguration.value.active) {
    retentionPolicyEntityConfiguration.value.integerValue = 0
  }
}

async function validateAndSave() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    if (retentionPolicyEntityConfiguration.value.active && retentionPolicyEntityConfiguration.value.integerValue >= retentionPolicyGroupConfiguration.value.integerValue) {
      dialogStore.show('Message Retention', `Message retention shouldn't be larger than the configured business retention: ${retentionPolicyGroupConfiguration.value.integerValue}`)
      return
    }

    await save()
  }
}

async function save() {
  const spec = new GroupConfigurationSpec<number>()
  spec.active = retentionPolicyEntityConfiguration.value.active
  spec.value = retentionPolicyEntityConfiguration.value.integerValue

  retentionPolicyEntityConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, RetentionPolicyEntityConfiguration>(
    retentionPolicyEntityConfiguration.value, spec
  )
  retentionPolicyEntityConfigurationCopy = readonly(deepCopy(retentionPolicyEntityConfiguration.value))

  editMode.value = false
}

function reset() {
  retentionPolicyEntityConfiguration.value = deepCopy(retentionPolicyEntityConfigurationCopy) as RetentionPolicyGroupConfiguration
  editMode.value = false
  v$.value.$reset()
}

</script>
