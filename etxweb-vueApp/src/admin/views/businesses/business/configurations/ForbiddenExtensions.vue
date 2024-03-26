<template>
  <configuration-container v-if="forbiddenExtensionsGroupConfiguration.dtype"
                           :title="'Forbidden file extensions'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div v-if="editMode" class="row">
      <div class="col col-lg-4 text-lg-end">Add default archive extensions</div>
      <div class="col col-lg-8">
        <div>
          <input id="selectAllCheckbox" type="checkbox" class="form-check-input"
                 :checked="allDefaultSelected"
                 :indeterminate="someDefaultSelected"
                 @change="toggleAll(!allDefaultSelected)">
          <label for="selectAllCheckbox" class="form-check-label ms-2">All</label>
        </div>
        <div class="d-block">
          <div v-for="option in defaultForbiddenExtensionsOptions" class="form-check form-check-inline">
            <input id="selectAllCheckbox" type="checkbox" class="form-check-input"
                   :checked="isSelected(option.value)"
                   @change="toggleSelection(option.value, !isSelected(option.value))">
            <label for="selectAllCheckbox" class="form-check-label">{{ option.text }}</label>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <label for="forbidden_file_extensions" class="col-lg-4 col-form-label">File extensions list</label>
      <div v-if="editMode" class="col-lg-8">
        <textarea id="forbidden_file_extensions" class="form-control"
                  v-model.trim="selectedExtensions"></textarea>
      </div>
      <div v-else class="col-lg-8 align-self-center">
        <strong>{{ formatListForDisplay(forbiddenExtensionsGroupConfiguration.stringValue) }}</strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { computed, readonly, ref, Ref } from 'vue'
import { DefaultForbiddenExtensions, ForbiddenExtensionsGroupConfiguration } from '@/model/entities'
import { formatListForDisplay, formatSetForInput, toArray } from '@/utils/stringListHelper'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import useValidationStore from '@/shared/store/validation'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode: Ref<boolean> = ref(false)

const { forbiddenExtensionsGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let forbiddenExtensionsGroupConfigurationCopy = readonly(deepCopy(forbiddenExtensionsGroupConfiguration.value))

const selectedExtensions = ref(forbiddenExtensionsGroupConfiguration.value.stringValue?.toUpperCase() ?? '')
const allDefaultSelected = computed(() => !defaultForbiddenExtensionsOptions.some(x => !isSelected(x.value)))
const someDefaultSelected = computed(() => !allDefaultSelected.value && defaultForbiddenExtensionsOptions.some(x => isSelected(x.value)))
const defaultForbiddenExtensions: Array<string> = Object.values(DefaultForbiddenExtensions)
const defaultForbiddenExtensionsOptions: Array<{
  text: string;
  value: string
}> = defaultForbiddenExtensions.map(value => ({ text: value, value }))

function toggleAll(selected: boolean) {
  defaultForbiddenExtensionsOptions.forEach(x => toggleSelection(x.value, selected))
}

function isSelected(option: string) {
  return toArray(selectedExtensions.value)
    .includes(option)
}

function toggleSelection(option: string, selected: boolean) {
  const tempSet = new Set(toArray(selectedExtensions.value))
  if (selected) {
    tempSet.add(option)
  } else {
    tempSet.delete(option)
  }
  selectedExtensions.value = formatSetForInput(tempSet)
    .toUpperCase()
}

async function save() {
  const spec = new ForbiddenExtensionsGroupConfiguration()
  spec.active = true
  spec.value = formatSetForInput(new Set(toArray(selectedExtensions.value.toUpperCase())))

  forbiddenExtensionsGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<string, ForbiddenExtensionsGroupConfiguration>(
    forbiddenExtensionsGroupConfiguration.value, spec
  )
  forbiddenExtensionsGroupConfigurationCopy = readonly(deepCopy(forbiddenExtensionsGroupConfiguration.value))

  selectedExtensions.value = forbiddenExtensionsGroupConfiguration.value.stringValue?.toUpperCase() ?? ''
  editMode.value = false
}

async function reset() {
  useValidationStore()
    .setServerSideValidationErrors([])
  forbiddenExtensionsGroupConfiguration.value = deepCopy(forbiddenExtensionsGroupConfigurationCopy) as ForbiddenExtensionsGroupConfiguration
  selectedExtensions.value = forbiddenExtensionsGroupConfiguration.value.stringValue?.toUpperCase() ?? ''
  editMode.value = false
}

</script>
