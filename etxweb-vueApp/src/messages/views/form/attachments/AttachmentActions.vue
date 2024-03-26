<template>
  <ul class="ico-list">
    <template v-if="showAdditionalAttachmentActions()">
      <li>
        <button type="button" class="ico-standalone comment"
                v-tooltip.tooltip="props.attachmentSpecWrapper.attachmentSpec.confidential ? 'Unmark confidential' : 'Mark confidential'"
                v-on:click="messageFormStore.updateConfidential({attachmentSpecWrapper: props.attachmentSpecWrapper})">
          <i
            :class="props.attachmentSpecWrapper.attachmentSpec.confidential ? 'ico-confidential-primary' : 'ico-not-confidential-primary'"></i>
          <span>Mark Unmark confidential</span>
        </button>
      </li>
      <li>
        <button type="button" class="ico-standalone comment"
                v-tooltip.tooltip="comment ? comment : 'Comments'" v-on:click="commentDialog.show()">
          <i :class="comment ? 'ico-comment-primary' : 'ico-add-comment-primary'"></i>
          <span>Comment</span>
        </button>
        <comment-dialog ref="commentDialog"
                        :attachmentSpecWrapper="props.attachmentSpecWrapper"
                        @update-comment="messageFormStore.updateComment"/>
      </li>
    </template>

    <li v-if="props.attachmentSpecWrapper.status === AttachmentStatus.Pending || props.attachmentSpecWrapper.status === AttachmentStatus.Forbidden">
      <button type="button" class="ico-standalone edit" v-tooltip.tooltip="'Edit'"
              @click="renameDialog.show()">
        <i class="ico-edit"></i>
        <span>Edit</span>
      </button>
    </li>
    <li>
      <button type="button" class="ico-standalone delete" v-tooltip.tooltip="'Delete'"
              @click.prevent="messageFormStore.removeAttachment(props.attachmentSpecWrapper)">
        <i class="ico-delete"></i>
        <span>Remove</span>
      </button>
    </li>
  </ul>

  <attachment-rename-dialog ref="renameDialog" :attachmentSpecWrapper="props.attachmentSpecWrapper"
                            @rename-attachment="messageFormStore.renameAttachment"/>
</template>

<script setup lang="ts">
import { computed, PropType, ref, Ref } from 'vue'
import { AttachmentSpecWrapper, AttachmentStatus } from '@/model/attachmentDto'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import Modal from '@/shared/views/modal/Modal.vue'
import AttachmentRenameDialog from '@/messages/views/form/attachments/dialogs/AttachmentRenameDialog.vue'
import CommentDialog from '@/messages/views/form/attachments/dialogs/CommentDialog.vue'
import useMessageFormStore from '@/messages/store/messageForm'

const props = defineProps({
  attachmentSpecWrapper: { type: Object as PropType<AttachmentSpecWrapper>, required: true }
})

const currentGroupStore = useCurrentGroupStore()
const messageFormStore = useMessageFormStore()
const renameDialog: Ref<typeof AttachmentRenameDialog | null> = ref(null)
const commentDialog: Ref<typeof Modal | null> = ref(null)
const comment = computed(() => props.attachmentSpecWrapper.attachmentSpec.comment)

function showAdditionalAttachmentActions(): boolean {
  return currentGroupStore.group.parent?.additionalAttachmentActions
}

</script>
