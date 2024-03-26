<template>
  <configuration-container :title="'Transmission limitations'"
                           :info-content="'For businesses connected to the node, transmissions are only configurable by a system administrator'"
                           :edit-mode="editMode"
                           :hide-edit-button="isReadonly"
                           :show-info-button="isReadonly"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row">
      <label for="file_size_limit" class="col-lg-4 col-form-label">
        File size limit (MB)
        <more-information content="Leave 0 for no limit" placement="right"/>
      </label>
      <div class="col-lg-8 align-self-center">
        <div v-if="editMode">
          <input id="file_size_limit" class="form-control" v-model="fileSizeLimitationGroupConfiguration.integerValue"
                 :type="'number'" min="0" step="1"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.fileSizeLimitationGroupConfiguration.integerValue.$error" class="error">
            {{ v$.fileSizeLimitationGroupConfiguration.integerValue.$errors[0].$message }}
          </span>
        </div>
        <strong v-else>
          {{
            fileSizeLimitationGroupConfiguration.integerValue === -1 ? '' : fileSizeLimitationGroupConfiguration.integerValue
          }}
        </strong>
      </div>
    </div>

    <div class="row">
      <label for="file_size_limit" class="col-lg-4 col-form-label">
        Transmission size limit (MB)
        <more-information content="Leave 0 for no limit" placement="right"/>
      </label>
      <div class="col-lg-8 align-self-center">
        <div v-if="editMode">
          <input id="file_size_limit" class="form-control"
                 v-model="totalFileSizeLimitationGroupConfiguration.integerValue"
                 :type="'number'" min="0" step="1"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.totalFileSizeLimitationGroupConfiguration.integerValue.$error" class="error">
            {{ v$.totalFileSizeLimitationGroupConfiguration.integerValue.$errors[0].$message }}
          </span>
        </div>
        <strong v-else>
          {{
            totalFileSizeLimitationGroupConfiguration.integerValue === -1 ? '' : totalFileSizeLimitationGroupConfiguration.integerValue
          }}
        </strong>
      </div>
    </div>

    <div class="row">
      <label for="file_size_limit" class="col-lg-4 col-form-label">
        Number of files limit
        <more-information content="Leave 0 for no limit" placement="right"/>
      </label>
      <div class="col-lg-8 align-self-center">
        <div v-if="editMode">
          <input id="file_size_limit" class="form-control"
                 v-model="numberOfFilesLimitationGroupConfiguration.integerValue"
                 :type="'number'" min="0" step="1"
                 @keydown="($event.key === '.' || $event.key === ',') && $event.preventDefault()"
                 @keydown.enter.prevent="$event.preventDefault()">
          <span v-if="v$.numberOfFilesLimitationGroupConfiguration.integerValue.$error" class="error">
            {{ v$.numberOfFilesLimitationGroupConfiguration.integerValue.$errors[0].$message }}
          </span>
        </div>
        <strong v-else>
          {{
            numberOfFilesLimitationGroupConfiguration.integerValue === -1 ? '' : numberOfFilesLimitationGroupConfiguration.integerValue
          }}
        </strong>
      </div>
    </div>

  </configuration-container>
</template>

<script setup lang="ts">
import { computed, PropType, readonly, ref, Ref } from 'vue'
import {
  FileSizeLimitationGroupConfiguration,
  Group,
  GroupConfigurationSpec,
  NumberOfFilesLimitationGroupConfiguration,
  TotalFileSizeLimitationGroupConfiguration,
} from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useAuthenticationStore from '@/shared/store/authentication'
import useVuelidate from '@vuelidate/core'
import { helpers, minValue } from '@vuelidate/validators'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const editMode: Ref<boolean> = ref(false)

const {
  fileSizeLimitationGroupConfiguration,
  totalFileSizeLimitationGroupConfiguration,
  numberOfFilesLimitationGroupConfiguration
} = storeToRefs(useBusinessConfigurationStore())
let fileSizeLimitationGroupConfigurationCopy = readonly(deepCopy(fileSizeLimitationGroupConfiguration.value))
let totalFileSizeLimitationGroupConfigurationCopy = readonly(deepCopy(totalFileSizeLimitationGroupConfiguration.value))
let numberOfFilesLimitationGroupConfigurationCopy = readonly(deepCopy(numberOfFilesLimitationGroupConfiguration.value))

const authenticationStore = useAuthenticationStore()
const isReadonly: Ref<boolean> = computed(() => !authenticationStore.isSysAdmin)
const minNumber = helpers.withMessage('The limit should be at least one', minValue(0))

const sizeLimitRules = {
  fileSizeLimitationGroupConfiguration: {
    integerValue: { minNumber }
  },
  totalFileSizeLimitationGroupConfiguration: {
    integerValue: { minNumber }
  },
  numberOfFilesLimitationGroupConfiguration: {
    integerValue: { minNumber }
  }
}

const v$ = useVuelidate(sizeLimitRules, {
  fileSizeLimitationGroupConfiguration,
  totalFileSizeLimitationGroupConfiguration,
  numberOfFilesLimitationGroupConfiguration
})

async function save() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    return Promise.all([
      updateFileSize(),
      updateTotalFileSize(),
      updateNumberOfFiles()
    ])
      .then(() => editMode.value = false)
  }
}

function reset() {
  fileSizeLimitationGroupConfiguration.value = deepCopy(fileSizeLimitationGroupConfigurationCopy) as FileSizeLimitationGroupConfiguration
  totalFileSizeLimitationGroupConfiguration.value = deepCopy(totalFileSizeLimitationGroupConfigurationCopy) as TotalFileSizeLimitationGroupConfiguration
  numberOfFilesLimitationGroupConfiguration.value = deepCopy(numberOfFilesLimitationGroupConfigurationCopy) as NumberOfFilesLimitationGroupConfiguration
  editMode.value = false
}

async function updateFileSize() {
  const spec = new GroupConfigurationSpec<number>()
  spec.active = fileSizeLimitationGroupConfiguration.value.integerValue > 0
  spec.value = fileSizeLimitationGroupConfiguration.value.integerValue

  fileSizeLimitationGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, FileSizeLimitationGroupConfiguration>(
    fileSizeLimitationGroupConfiguration.value, spec
  )
  fileSizeLimitationGroupConfigurationCopy = readonly(deepCopy(fileSizeLimitationGroupConfiguration.value))
}

async function updateTotalFileSize() {
  const spec = new GroupConfigurationSpec<number>()
  spec.active = totalFileSizeLimitationGroupConfiguration.value.integerValue > 0
  spec.value = totalFileSizeLimitationGroupConfiguration.value.integerValue

  totalFileSizeLimitationGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, TotalFileSizeLimitationGroupConfiguration>(
    totalFileSizeLimitationGroupConfiguration.value, spec
  )
  totalFileSizeLimitationGroupConfigurationCopy = readonly(deepCopy(totalFileSizeLimitationGroupConfiguration.value))
}

async function updateNumberOfFiles() {
  const spec = new GroupConfigurationSpec<number>()
  spec.active = numberOfFilesLimitationGroupConfiguration.value.integerValue > 0
  spec.value = numberOfFilesLimitationGroupConfiguration.value.integerValue

  numberOfFilesLimitationGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<number, NumberOfFilesLimitationGroupConfiguration>(
    numberOfFilesLimitationGroupConfiguration.value, spec
  )
  numberOfFilesLimitationGroupConfigurationCopy = readonly(deepCopy(numberOfFilesLimitationGroupConfiguration.value))
}

</script>
