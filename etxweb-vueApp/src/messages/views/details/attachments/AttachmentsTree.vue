<template>
  <div class="tree-view mt-3">
    <tree-node v-for="item in treeData" :key="item.id" :item="item" :canDownload="canDownload"
               @set-selected="setSelected" @download="download($event)">
    </tree-node>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, Ref, ref, watch } from 'vue'
import { AttachmentSpec } from '@/model/entities'
import TreeNode from './node/TreeNode.vue'
import { NodeItem } from './node/Node'

export default defineComponent({
  name: 'AttachmentsTree',
  components: {
    TreeNode
  },
  props: {
    attachments: { type: Array as PropType<Array<AttachmentSpec>>, required: true },
    canDownload: { type: Boolean },
    showAdditionalAttachmentActions: { type: Boolean }
  },
  emits: ['selected', 'download'],
  setup(props, { emit }) {
    const treeData: Ref<Array<NodeItem>> = ref(treeDisplayData())
    const checkedFilesIds: Ref<Array<number>> = ref([])

    watch(() => props.attachments, () => {
      treeData.value = treeDisplayData()
      checkedFilesIds.value = []
    }, { deep: true })

    function treeDisplayData() {
      const roots: Array<NodeItem> = []
      let key = 0

      props.attachments.forEach(attachmentSpec => {
        attachmentSpec.name.split('/')
          .reduce((parent, name) => {
            let child = parent.children.find(treeNode => treeNode.name === name)

            if (!child) {
              child = {
                id: attachmentSpec.id,
                name,
                selected: false,
                children: [],
                item: attachmentSpec,
                showAdditionalActions: props.showAdditionalAttachmentActions
              }

              parent.children.push(child)

              // The parent should have a generated ID and no item
              parent.id = key++
              parent.item = null
            }

            return child
          }, { children: roots } as NodeItem)
      })

      return roots
    }

    function flatten(item?: NodeItem) {
      const result: Array<NodeItem> = item ? [item] : [...treeData.value]
      const data = item ? item.children : treeData.value

      data.forEach(i => {
        if (i.children.length === 0) {
          result.push(i)
        } else {
          result.push(...flatten(i))
        }
      })

      return result
    }

    function setSelected(item: NodeItem, selected: boolean) {
      item.selected = selected

      if (item.children.length === 0) {
        checkedFilesIds.value = selected ? addLeafItems(item) : removeLeafItems(item)
      }

      const parent = flatten().find(x => x.children.find(y => y.id === item.id))

      if (parent) {
        setSelected(parent, allChildrenSelected(parent))
      }

      emit('selected', checkedFilesIds.value)
    }

    function download(item: NodeItem) {
      emit('download', item.item.id)
    }

    function addLeafItems(item: NodeItem) {
      return [...checkedFilesIds.value, item.item.id]
    }

    function removeLeafItems(item: NodeItem) {
      return checkedFilesIds.value.filter(x => x !== item.item.id)
    }

    function allChildrenSelected(parent: NodeItem): boolean {
      return parent.children.every(x => x.selected)
    }

    return {
      treeData, setSelected, download, emit
    }
  }
})
</script>
