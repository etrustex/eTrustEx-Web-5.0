<template>
  <ul class="list-unstyled">
    <li :class="item.children.length === 0 ? 'file-type' : 'folder-type'">

        <div class="d-flex align-items-center" v-if="canDownload">

          <div class="col-input">
            <input type="checkbox" class="form-check-input"
                   :id="item.id" :value="item.id"
                   v-model="item.selected"
                   v-on:change="toggleSelection()">
          </div>

          <div class="col-content">

            <div v-if="item.item" class="d-flex justify-content-between align-items-center">

              <div class="d-flex align-items-center">
                <span class="ico-conjunction">
                  <i :class="item.children.length === 0 ? 'ico-file' : 'ico-folder'"></i>
                </span>

                <a href="javascript:void(0);" v-on:click="download(item)">
                  <span class="text-break">{{ item.name }}</span>
                </a>
              </div>

              <div v-if="item.showAdditionalActions" class="d-flex align-items-center information">
                <a v-if="item.item.comment" class="ico-standalone comment" v-tooltip.tooltip="item.item.comment">
                  <i class="ico-comment-primary"></i>
                  <span>Comment</span>
                </a>
                <a class="ico-standalone comment"
                   v-tooltip.tooltip="item.item.confidential ? 'Confidential' : 'Non confidential'">
                  <i
                    :class="item.item.confidential ? 'ico-confidential-primary' : 'ico-not-confidential-primary'"></i>
                  <span>Confidential</span>
                </a>
              </div>

            </div>

            <div v-else class="d-flex align-items-center">
              <span class="ico-conjunction">
                <i :class="item.children.length === 0 ? 'ico-file' : 'ico-folder'"></i>
              </span>
              <span class="ellipsis">{{ item.name }}</span>
            </div>

          </div>

        </div>

      <div v-else class="d-flex align-items-center">
        <span class="ico-conjunction">
          <i :class="item.children.length === 0 ? 'ico-file' : 'ico-folder'"></i>
        </span>
        <span class="ellipsis">{{ item.name }}</span>
      </div>

    </li>
    <tree-node v-for="item in item.children" :key="item.id"
               :item="item"
               :canDownload="canDownload"
               @setSelected="setSelected"
               @download="download($event)">
    </tree-node>
  </ul>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { NodeItem } from './Node'

export default defineComponent({
  name: 'TreeNode',
  props: {
    item: { type: Object as PropType<NodeItem>, required: true },
    canDownload: { type: Boolean }
  },
  emits: ['set-selected', 'download'],
  setup(props, { emit }) {
    function toggleSelection() {
      setSelected(props.item, props.item.selected)
    }

    function setSelected(item: NodeItem, selected: boolean) {
      item.selected = selected

      if (item.children.length !== 0) {
        item.children.forEach(x => setSelected(x, selected))
      } else {
        emit('set-selected', item, selected)
      }
    }

    function download(item: NodeItem) {
      emit('download', item)
    }

    return {
      toggleSelection, setSelected, download, emit
    }
  }
})
</script>
