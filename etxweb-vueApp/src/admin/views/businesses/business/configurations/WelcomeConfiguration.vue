<template>
  <configuration-container v-if="template?.type"
                           :title="'Welcome notification'"
                           :edit-mode="editMode"
                           :disable-confirm="!content || content.length === 0"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">
    <div v-if="editMode" class="row">
      <div class="col-lg-4 text-lg-end"></div>
      <div class="col-lg-8 ps-5">
        <span>For the template you can use the following variables:</span>

        <ul class="mt-3">
          <li>
            <strong>$userName$</strong> - Name of the user that was added to the entity.
          </li>
          <li>
            <strong>$entityName$</strong> - Name of the entity to which the user was added.
          </li>
          <li>
            <strong>$applicationURL$</strong> - Link to the webpage.
          </li>
          <li>
            <strong>$functionalMailbox$</strong> - Support email.
          </li>
        </ul>

        <span>The variables can be inserted into the message and they will be replaced by the correct value inside the notification.</span>
        <br>
        <span>They need to be added exactly how it is displayed above.</span>

        <div class="form-check form-switch mt-3">
          <input id="use_default" type="checkbox" role="switch" class="form-check-input"
                 v-model="useDefault">
          <label class="form-check-label" for="use_default"><strong>Use default template</strong></label>
        </div>
      </div>
    </div>

    <div class="row mt-0">
      <div class="col-lg-4 text-lg-end">Content<span class="label-required">*</span></div>
      <div class="col-lg-8">
        <div v-if="editMode" class="form-check form-switch">
          <quill-editor v-model:value="content"
                        :options="editorOptions"
                        :disabled="useDefault"/>
          <span v-if="!content || content.length === 0" class="error">The content is required</span>
        </div>
        <div v-else v-html="content" class="mt-3"></div>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { computed, onBeforeMount, PropType, readonly, ref, Ref } from 'vue'
import { Group, Template, TemplateType } from '@/model/entities'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'
import { TemplateApi } from '@/admin/api/templateApi'
import { editorOptions } from '@/shared/views/bootstrap_vue/EditorOptions'
import QuillEditor from 'vue3-quill/src/editor.vue'

const editMode: Ref<boolean> = ref(false)

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const template: Ref<Template> = ref({} as Template)
const useDefault = ref(false)

let defaultTemplate: Template
let templateCopy: Template

const content = computed({
  get: () => {
    if (useDefault.value) {
      return swapVariables(defaultTemplate.content.replace('\n', '')
        .split('<body>')[1].split('</body>')[0])
    }
    return swapVariables(template.value.content.replace('\n', '')
      .split('<body>')[1].split('</body>')[0])
  },
  set: (value: string) => {
    if (!useDefault.value) {
      template.value.content = `<!DOCTYPE html><!-- ${props.business?.identifier} ${TemplateType.USER_CONFIGURED_NOTIFICATION} --><html xmlns:th="http://www.thymeleaf.org" lang="en"><head><title th:remove="all">Template for HTML GENERIC email</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/></head><body>${swapVariables(value, true)}</body></html>`
    }
  }
})

const varMap = {
  $userName$: '<span th:text="${userName}"></span>',
  $entityName$: '<span th:text="${groupName}"></span>',
  $applicationURL$: '<a th:href="${applicationURL}" th:text="${applicationURL}"/>',
  $functionalMailbox$: '<a th:href="${functionalMailbox}" th:text="${functionalMailbox}"/>'
}

onBeforeMount(async () => {
  template.value = await TemplateApi.getTemplate(props.business?.id, TemplateType.USER_CONFIGURED_NOTIFICATION)
  defaultTemplate = await TemplateApi.getDefaultTemplate(TemplateType.USER_CONFIGURED_NOTIFICATION)
  useDefault.value = template.value.id === defaultTemplate.id
  templateCopy = readonly(deepCopy(template.value))
})

function swapVariables(content: string, toServer = false): string {
  if (toServer) {
    for (const value of Object.keys(varMap)) {
      content = content.replace(value, varMap[value as keyof typeof varMap])
    }
  } else {
    for (const value of Object.values(varMap)) {
      const rep = Object.keys(varMap)
        .find(k => varMap[k as keyof typeof varMap] === value)!
      content = content.replace(value, rep)
    }
  }
  return content
}

async function save() {
  if (template.value.content !== defaultTemplate.content) {
    const templateSpec = {
      type: template.value.type,
      groupId: props.business?.id,
      content: useDefault.value ? '' : template.value.content,
      useDefault: useDefault.value
    }

    template.value = await TemplateApi.saveOrUpdate(templateSpec)
    templateCopy = readonly(deepCopy(template.value))
  }

  useDefault.value = template.value.id === defaultTemplate.id
  editMode.value = false
}

function reset() {
  template.value = deepCopy(templateCopy) as Template
  useDefault.value = template.value.id === defaultTemplate.id
  editMode.value = false
}

</script>
