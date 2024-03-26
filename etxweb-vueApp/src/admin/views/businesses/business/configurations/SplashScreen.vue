<template>
  <configuration-container v-if="splashScreenGroupConfigurationCopy.dtype"
                           :title="'Splash screen'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div class="row align-items-start">
      <div class="col-lg-4 text-lg-end">
        Content<span class="label-required">*</span>
      </div>
      <div class="col-lg-8">
        <div v-if="editMode">
          <quill-editor v-model:value="splashScreenGroupConfiguration.stringValue"
                        :options="editorOptions"
                        :disabled="false"/>
          <span v-if="v$.stringValue.$error" class="error"> {{ v$.stringValue.$errors[0].$message }} </span>
        </div>
        <div v-else v-html="splashScreenGroupConfiguration.stringValue"></div>
      </div>
    </div>

    <div class="row">
      <div class="col col-lg-4 text-lg-end">
        Status<span class="label-required">*</span>
      </div>
      <div class="col col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <input id="splashScreenCheckbox" type="checkbox" role="switch" class="form-check-input"
                 name="'check-button'"
                 v-model="splashScreenGroupConfiguration.active">
          <label class="form-check-label" for="splashScreenCheckbox">
            <strong>
              {{ splashScreenGroupConfiguration.active ? 'Active' : 'Inactive' }}
            </strong>
          </label>
        </div>
        <strong v-else class="form-check-label">
          {{ splashScreenGroupConfiguration.active ? 'Active' : 'Inactive' }}
        </strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, SplashScreenGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useVuelidate from '@vuelidate/core'
import { requiredHtmlContentBasedOnActiveParam } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import { editorOptions } from '@/shared/views/bootstrap_vue/EditorOptions'
import QuillEditor from 'vue3-quill/src/editor.vue'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const editMode: Ref<boolean> = ref(false)

const { splashScreenGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let splashScreenGroupConfigurationCopy = readonly(deepCopy(splashScreenGroupConfiguration.value))

const splashScreenRules = {
  stringValue: {
    requiredHtmlContent: requiredHtmlContentBasedOnActiveParam
  }
}
const v$ = useVuelidate(splashScreenRules, splashScreenGroupConfiguration)

async function save() {
  await v$.value.$validate()
  if (v$.value.$error) {
    return
  }

  // Needed to avoid create a new paragraph for each line until https://github.com/quilljs/quill/issues/2872 is fixed
  splashScreenGroupConfiguration.value.stringValue = splashScreenGroupConfiguration.value.stringValue
    .replaceAll('<p><br></p>', '<br>')
    .replaceAll('<p>', '')
    .replaceAll('</p>', '<br>')

  const spec = new GroupConfigurationSpec<string>()
  spec.active = splashScreenGroupConfiguration.value.active
  spec.value = splashScreenGroupConfiguration.value.stringValue

  splashScreenGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<string, SplashScreenGroupConfiguration>(
    splashScreenGroupConfiguration.value, spec
  )
  splashScreenGroupConfigurationCopy = readonly(deepCopy(splashScreenGroupConfiguration.value))

  editMode.value = false
}

function reset() {
  console.log('before splashScreenGroupConfiguration.value:', splashScreenGroupConfiguration.value)
  splashScreenGroupConfiguration.value = deepCopy(splashScreenGroupConfigurationCopy) as SplashScreenGroupConfiguration
  console.log('after splashScreenGroupConfiguration.value:', splashScreenGroupConfiguration.value)
  editMode.value = false
  v$.value.$reset()
}

</script>
