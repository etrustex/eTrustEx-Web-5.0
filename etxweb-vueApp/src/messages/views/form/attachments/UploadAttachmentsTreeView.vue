<template>
  <div class="tree-view mt-3">
    <attachment-tree-node :attachmentSpecDtos="attachmentSpecDtos" v-for="item in treeDisplayData" :node="item" :key="item.id"/>
  </div>
</template>

<script setup lang="ts">

import { computed, ComputedRef, PropType } from 'vue'
import { TreeNode } from '@/messages/views/form/attachments/node/TreeNode'
import AttachmentTreeNode from '@/messages/views/form/attachments/node/AttachmentTreeNode'
import { AttachmentSpecWrapper } from '@/model/attachmentDto'

const props = defineProps({
  attachmentSpecDtos: { type: Object as PropType<Array<AttachmentSpecWrapper>>, required: true }
})

const treeDisplayData: ComputedRef<Array<TreeNode>> = computed(() => {
  const roots: Array<TreeNode> = []
  let key = 0
  props.attachmentSpecDtos.forEach(attachmentSpecDto => {
    const leaf = attachmentSpecDto.name.split('/')
      .reduce((parent, name) => {
        let child = parent.children.find(treeNode => treeNode.name === name)

        if (!child) {
          child = {
            id: key++,
            name,
            children: []
          }
          parent.children.push(child)
        }
        return child
      }, { children: roots } as TreeNode)
    leaf.attachmentSpecWrapper = attachmentSpecDto
  })
  return roots
})

</script>
