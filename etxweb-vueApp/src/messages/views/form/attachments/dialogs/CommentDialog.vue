<template>
  <div>
    <Teleport to="#modals">
      <modal id="comment-dialog" ref="commentDialog" body-classes="bg-info" footer-classes="bg-info">
        <template #body>
          <div class="ico-pins-container">
            <span class="ico-pins"></span>
            <i aria-hidden="true" class="ico-add-comment"></i>
          </div>
          <h2 class="h5 text-break">{{ props.attachmentSpecWrapper.name }}</h2>
          <label>Add comment</label>
          <textarea v-model.trim="comment" rows="3" class="form-control" ref="commentTextArea"></textarea>
        </template>

        <template #footer>
          <div class="btns">
            <button type="button" class="btn btn-outline-secondary btn-ico" data-bs-dismiss="modal">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
              <span>Cancel</span>
            </button>
            <button type="button" class="btn btn-primary btn-ico" v-on:click="save(); $event.preventDefault()">
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
import { AttachmentSpecWrapper } from '@/model/attachmentDto'
import Modal from '@/shared/views/modal/Modal.vue'
import useMessageFormStore from '@/messages/store/messageForm'

const messageFormStore = useMessageFormStore()

const props = defineProps({
  attachmentSpecWrapper: { type: Object as PropType<AttachmentSpecWrapper>, required: true }
})

const comment: Ref<string> = ref('')
const commentDialog: Ref<typeof Modal | null> = ref(null)
const commentTextArea: Ref = ref(null)

const save = () => {
  messageFormStore.updateComment({ attachmentSpecWrapper: props.attachmentSpecWrapper, text: comment.value })
  commentDialog.value?.hide()
}

const show = () => {
  comment.value = props.attachmentSpecWrapper.attachmentSpec.comment
  commentTextArea.value.focus()
  commentDialog.value?.show()
}

defineExpose({ show })

</script>
