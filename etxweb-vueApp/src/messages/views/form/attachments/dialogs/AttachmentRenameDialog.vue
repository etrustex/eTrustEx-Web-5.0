<template>
  <div>
    <Teleport to="#modals">
      <modal id="rename-dialog" ref="renameDialog" body-classes="bg-info" footer-classes="bg-info">
        <template #body>
          <div class="ico-pins-container">
            <span class="ico-pins"></span>
            <i aria-hidden="true" class="ico-edit-grey"></i>
          </div>
          <h2 class="h5 text-break">Rename file</h2>
          <input class="form-control" id="reference" type="text" v-model="attachmentName">
        </template>

        <template #footer>
          <div class="btns">
            <button type="button" class="btn btn-outline-secondary btn-ico" data-bs-dismiss="modal">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
              <span>Cancel</span>
            </button>
            <button type="button" class="btn btn-primary btn-ico" v-on:click="renameAttachment(); $event.preventDefault()">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
              <span>Save</span>
            </button>
          </div>
        </template>
      </modal>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { PropType, ref, Ref } from 'vue'
import Modal from '@/shared/views/modal/Modal.vue'
import { AttachmentSpecWrapper } from '@/model/attachmentDto'
import useMessageFormStore from '@/messages/store/messageForm'

const messageFormStore = useMessageFormStore()

const props = defineProps({
  attachmentSpecWrapper: { type: Object as PropType<AttachmentSpecWrapper>, required: true }
})

const attachmentName: Ref<string> = ref(props.attachmentSpecWrapper.name)
const renameDialog: Ref<typeof Modal | null> = ref(null)

function show() {
  renameDialog.value?.show()
}

function renameAttachment() {
  messageFormStore.renameAttachment({
    attachmentSpecWrapper: props.attachmentSpecWrapper,
    name: attachmentName.value.trim()
  })
  renameDialog.value?.hide()
}

defineExpose({ show })

</script>
