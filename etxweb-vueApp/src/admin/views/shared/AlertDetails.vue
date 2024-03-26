<template>
  <configuration-container :title="'Notification banner'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="createOrSave"
                           @reset="reset">
    <form class="form-admin" v-bind:class=" editMode ? 'form-admin-edit-mode' : 'form-admin-view-mode'">
      <div class="row">
        <label class="col-lg-4 col-form-label">
          Type of notification<span class="label-required">*</span>
        </label>
        <div class="col-lg-8 align-self-center">
          <div v-if="editMode">
            <select class="form-select" v-model="euAlertConfiguration.type">
              <option v-for="option in alertTypeOptions" :key="option.value" :value="option.value">
                {{ option.text }}
              </option>
            </select>
          </div>
          <strong v-else>{{ AlertTypeFormatter.toAlertTypeTitle(euAlertConfiguration.type) }}</strong>
        </div>
      </div>

      <div class="row">
        <label for="alert_title" class="col-lg-4 col-form-label">
          Title<span class="label-required">*</span>
        </label>
        <div class="col-lg-8 align-self-center">
          <div v-if="editMode">
            <input id="alert_title" class="form-control" v-model="euAlertConfiguration.title"/>
            <span v-if="v$.title.$error" class="error"> {{ v$.title.$errors[0].$message }} </span>
          </div>
          <strong v-else>{{ euAlertConfiguration.title }}</strong>
        </div>
      </div>

      <div class="row align-items-start">
        <label class="col-lg-4 col-form-label">
          Content<span class="label-required">*</span>
        </label>
        <div class="col-lg-8 align-self-center">
          <div v-if="editMode">
            <quillEditor :options="editorOptions" v-model:value="euAlertConfiguration.content"/>
            <span v-if="v$.content.$error" class="error"> {{ v$.content.$errors[0].$message }} </span>
          </div>
          <div v-else>
            <div class="mb-0 pt-1" v-html="euAlertConfiguration.content"></div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-lg-4 col-form-label">
          Status<span class="label-required">*</span>
        </div>
        <div class="col-lg-8 d-flex align-items-center">
          <div v-if="editMode" class="form-check form-switch">
            <input id="alertDetailsCheckbox" type="checkbox" role="switch" class="form-check-input"
                   name="'check-button'" v-model="euAlertConfiguration.active">
            <label class="form-check-label" for="alertDetailsCheckbox">
              <strong>{{ euAlertConfiguration.active ? 'Active' : 'Inactive' }}</strong>
            </label>
          </div>
          <div v-else>
            <label class="form-check-label" for="alertDetailsCheckbox">
              <strong>{{ euAlertConfiguration.active ? 'Active' : 'Inactive' }}</strong>
            </label>
          </div>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-4 col-form-label">
          Banner activation start date<span class="label-required">*</span>
        </label>
        <div class="col-lg-8 align-self-center">
          <vue-date-picker v-if="editMode" :disabled="!euAlertConfiguration.active"
                           v-model="euAlertConfiguration.startDate"
                           format="dd/MM/yyyy HH:mm"></vue-date-picker>
          <strong v-else>{{ toDateTime(euAlertConfiguration.startDate) }}</strong>
          <span v-if="v$.startDate.$error" class="error"> {{ v$.startDate.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-4 col-form-label">
          Banner activation end date
          <more-information content="Leave the end date blank to keep the banner indefinitely." placement="right"/>
        </label>
        <div class="col-lg-8 align-self-center">
          <vue-date-picker v-if="editMode" :disabled="!euAlertConfiguration.active"
                           v-model="euAlertConfiguration.endDate"
                           format="dd/MM/yyyy HH:mm"></vue-date-picker>
          <strong v-else>{{ toDateTime(euAlertConfiguration.endDate) }}</strong>
          <span v-if="v$.endDate.$error" class="error"> {{ v$.endDate.$errors[0].$message }} </span>
        </div>
      </div>
    </form>
  </configuration-container>
</template>

<script setup lang="ts">
import { computed, ComputedRef, PropType, readonly, ref } from 'vue'
import { Alert, AlertType, Group } from '@/model/entities'
import { AlertApi } from '@/admin/api/alertApi'
import { AlertTypeFormatter } from '@/utils/AlertTypeFormatter'
import useVuelidate from '@vuelidate/core'
import { alertRules } from '@/admin/views/shared/validation/AlertValidator'
import { editorOptions } from '@/shared/views/bootstrap_vue/EditorOptions'
import QuillEditor from 'vue3-quill/src/editor.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'
import { toDateTime } from '@/utils/formatters'

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const editMode = ref(false)

const { euAlertConfiguration } = storeToRefs(useBusinessConfigurationStore())
let euAlertConfigurationCopy = readonly(deepCopy(euAlertConfiguration.value))

const alertTypeOptions: ComputedRef = computed(() => [
  { value: AlertType.INFO, text: AlertTypeFormatter.toAlertTypeTitle(AlertType.INFO) },
  { value: AlertType.WARNING, text: AlertTypeFormatter.toAlertTypeTitle(AlertType.WARNING) },
  { value: AlertType.SUCCESS, text: AlertTypeFormatter.toAlertTypeTitle(AlertType.SUCCESS) },
  { value: AlertType.DANGER, text: AlertTypeFormatter.toAlertTypeTitle(AlertType.DANGER) }
])

const v$ = useVuelidate(alertRules, euAlertConfiguration)

async function createOrSave() {
  await v$.value.$validate()
  if (v$.value.$error) {
    return
  }

  // Needed to avoid create a new paragraph for each line until https://github.com/quilljs/quill/issues/2872 is fixed
  euAlertConfiguration.value.content = euAlertConfiguration.value.content
    .replaceAll('<p><br></p>', '<br>')
    .replaceAll('<p>', '')
    .replaceAll('</p>', '<br>')

  if (euAlertConfiguration.value.id) {
    euAlertConfiguration.value = await AlertApi.update(euAlertConfiguration.value)
  } else {
    euAlertConfiguration.value.group = props.business
    euAlertConfiguration.value = await AlertApi.create(euAlertConfiguration.value)
  }
  euAlertConfigurationCopy = readonly(deepCopy(euAlertConfiguration.value))

  editMode.value = false
}

function reset() {
  euAlertConfiguration.value = deepCopy(euAlertConfigurationCopy) as Alert

  editMode.value = false
  v$.value.$reset()
}
</script>
