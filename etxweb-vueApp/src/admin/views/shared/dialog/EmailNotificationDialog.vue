<template>
  <modal ref="modalElement" :id="id" modal-classes="modal-xl">
    <template #body>
      <div class="ico-pins-container"><span class="ico-pins"></span><i aria-hidden="true" class="ico-send"></i></div>
      <h2 class="text-center">Send message</h2>

      <div class="form-admin-edit-mode">

        <div class="row">
          <label class="col-lg-4 col-form-label" for="tags-basic-cc">{{ displayBcc ? 'CC' : 'To' }}</label>
          <div class="col-lg-8">
            <b-form-tags ref="formTag" separator=",;" placeholder="Enter new email separated by comma or semicolon"
                         tag-variant="primary"
                         input-id="tags-basic-cc" v-model="savedCc"></b-form-tags>
            <span v-if="v$.savedCc.$error" class="error"> {{ v$.savedCc.$errors[0].$message }}</span>
          </div>
        </div>

        <div v-if="displayBcc" class="row">
          <label class="col-lg-4 col-form-label" for="bccEmail">Bcc</label>

          <div class="col-lg-6">
            <strong v-if="!visible" :class="`d-block ${truncateBcc}`">{{ savedBcc.join(',') }}</strong>
            <div v-if="visible">
              <b-form-tags separator=",;" placeholder="Enter new email separated by comma or semicolon"
                           tag-variant="primary"
                           input-id="tags-basic-bcc" id="bccEmail" v-model="savedBcc"></b-form-tags>
              <span v-if="v$.savedBcc.$error" class="error"> {{ v$.savedBcc.$errors[0].$message }} </span>
            </div>
          </div>

          <div class="col-lg-1">
            <a
              :class="['collapse-arrow','ico-standalone','trix-content', 'form-rich-editor', {active: visible}]"
              @click="visible = !visible"
              aria-controls="bcc-list"
              :aria-expanded="visible ? 'true' : 'false'"
            >
              <i v-if="visible" class="ico-expand-more"></i>
              <i v-else class="ico-expand-less"></i>
            </a>
          </div>

          <div class="col-lg-1">
            <button class="ico-standalone btn btn-outline-primary" type="button"
                    v-tooltip.tooltip="'Copy to clipboard'"
                    @click="clipboard()">
                <span aria-hidden="true" class="ico-standalone"><i class="ico-inbox"></i>
                </span>
            </button>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-4 col-form-label">Subject</label>
          <div class="col-lg-8">
            <input type="text" v-model.trim="savedNotificationEmail.subject" class="form-control">
            <span v-if="v$.savedNotificationEmail.subject.$error"
                  class="error"> {{ v$.savedNotificationEmail.subject.$errors[0].$message }} </span>
          </div>
        </div>

        <div class="row">
          <div class="col-12">
            <quill-editor v-model:value="savedNotificationEmail.body" :options="editorOptions"/>
            <span v-if="v$.savedNotificationEmail.body.$error"
                  class="error"> {{ v$.savedNotificationEmail.body.$errors[0].$message }} </span>
          </div>
        </div>

      </div>

      <div class="d-flex justify-content-end btns mt-3">
        <button type="button" class="btn btn-outline-secondary btn-ico" @click="cancel()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
          <span>Cancel</span>
        </button>
        <button type="button" class="btn btn-primary btn-ico" @click="save">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-send-white"></i></span>
          <span>Send</span>
        </button>
      </div>

    </template>
  </modal>
</template>

<script setup lang="ts">
import { computed, PropType, Ref, ref } from 'vue'
import { NotificationEmailSpec } from '@/model/entities'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import BFormTags from '@/shared/views/bootstrap_vue/BFormTags/BFormTags.vue'
import Modal from '@/shared/views/modal/Modal.vue'
import useVuelidate from '@vuelidate/core'
import { emailNotificationRules } from '@/admin/views/shared/validation/EmailNotificationDialogValidator'
import QuillEditor from 'vue3-quill/src/editor.vue'
import { editorOptions } from '@/shared/views/bootstrap_vue/EditorOptions'

const props = defineProps({
  id: { type: String },
  notificationEmail: { type: Object as PropType<NotificationEmailSpec>, required: true },
  displayBcc: { type: Boolean }
})

const modalElement: Ref<typeof Modal | null> = ref(null)

const savedBcc = ref(props.notificationEmail.bcc ? [...props.notificationEmail.bcc] : [])
const savedCc: Ref<Array<string>> = ref([])
const savedNotificationEmail: Ref<NotificationEmailSpec> = ref({
  ...props.notificationEmail,
  body: props.notificationEmail.body ? props.notificationEmail.body : ''
} as NotificationEmailSpec)
const visible = ref(true)
const truncateBcc = computed(() => savedBcc.value.join(',').length < 100 ? '' : 'text-truncate')

const v$ = useVuelidate(emailNotificationRules, { savedNotificationEmail, savedBcc, savedCc })

const formTag = ref()
const emit = defineEmits(['close-send-request'])

const save = () => {
  v$.value.$validate()
  if (!v$.value.$error) {
    savedNotificationEmail.value.cc = savedCc.value
    savedNotificationEmail.value.bcc = savedBcc.value
    UserProfileApi.sendNotificationEmails(savedNotificationEmail.value)
      .then(() => {
        modalElement.value?.hide()
        emit('close-send-request', true)
      })
      .then(() => reset())
      .catch(reason => {
        console.log('Problem during email sending: ' + reason)
      })
  }
}

const cancel = () => {
  modalElement.value?.hide()
  reset()
}

const reset = () => {
  formTag.value.clearInput()
  savedBcc.value = props.notificationEmail.bcc ? [...props.notificationEmail.bcc] : []
  savedCc.value = []
  savedNotificationEmail.value.subject = props.notificationEmail.subject ? props.notificationEmail.subject : ''
  savedNotificationEmail.value.body = props.notificationEmail.body ? props.notificationEmail.body : ''
  v$.value.$reset()
}

const clipboard = () => {
  navigator.clipboard.writeText(savedBcc.value.join(','))
}

const show = () => {
  modalElement.value?.show()
}

const hide = () => {
  modalElement.value?.hide()
}

defineExpose({ show, hide })

</script>
