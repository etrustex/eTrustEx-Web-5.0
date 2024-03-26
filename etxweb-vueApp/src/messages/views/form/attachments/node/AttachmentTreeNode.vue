<template>
  <ul class="list-unstyled">
    <li :class="node.children.length === 0 ? 'file-type' : 'folder-type'">
      <div class="d-flex justify-content-between align-items-center">

        <div class="d-flex justify-content-between align-items-center">
          <span class="ico-conjunction">
            <i :class="node.children.length === 0? 'ico-file' : 'ico-folder'"></i>
          </span>
          <span class="text-break">{{ node.name }}</span>
        </div>

        <div class="d-flex justify-content-between align-items-center actions">
          <a v-if="node.children.length !== 0"
             href="javascript:void(0);"
             class="ico-standalone delete"
             type="button"
             v-tooltip.tooltip="'Delete'"
             @click.prevent="removeFolder(node)"
             @keydown.enter.prevent="onEnter">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
          <span v-if="node.children.length === 0" v-tooltip.tooltip="node.attachmentSpecWrapper.errorTooltip"
                :class="`badge rounded-pill ${getStatusClass(node.attachmentSpecWrapper.status)}`">
            {{ node.attachmentSpecWrapper.status }}
          </span>
          <attachment-actions v-if="node.children.length === 0" :attachmentSpecWrapper="node.attachmentSpecWrapper"/>
        </div>
      </div>
    </li>
    <attachment-tree-node v-for="item in node.children" :node="item" :key="item.id"/>
  </ul>
</template>

<script setup lang="ts">
import { AttachmentStatus } from '@/model/attachmentDto'
import { TreeNode } from '@/messages/views/form/attachments/node/TreeNode'
import { PropType } from 'vue'
import AttachmentStatusHelper from '@/utils/attachments/attachmentStatusHelper'
import AttachmentActions from '@/messages/views/form/attachments/AttachmentActions.vue'
import useMessageFormStore from '@/messages/store/messageForm'

const messageFormStore = useMessageFormStore()

const props = defineProps({
  node: { type: Object as PropType<TreeNode> }
})

function getStatusClass(status: AttachmentStatus) {
  return AttachmentStatusHelper.getStatusClass(status)
}

function removeFolder(treeNode: TreeNode) {
  treeNode.children.forEach(node => {
    if (node.children.length === 0 && node.attachmentSpecWrapper) {
      messageFormStore.removeAttachment(node.attachmentSpecWrapper)
    } else {
      removeFolder(node)
    }
  })
}
</script>
