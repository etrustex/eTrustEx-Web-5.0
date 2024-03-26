<template>
  <div class="table-responsive message-detail-attachments-table">
    <table class="table align-middle">
      <thead class="thead-fixed">
      <tr>
        <th v-if="canDownload" scope="col" class="col-1">
          <div class="d-flex align-items-md-center">
            <input class="form-check-input" type="checkbox" id="checkboxNoLabel"
                   :checked="allSelected" @change="selectAllToggle(!allSelected)">
          </div>
        </th>
        <th scope="col" :class="{'col-5': showAdditionalAttachmentActions, 'col-8': !showAdditionalAttachmentActions}"
            v-on:click="toggleSorting('name')">
          <span class="ico-conjunction">
            <i class="ico-sort-default me-0" :class="{'ico-sort-default': sortBy !== 'name',
               [`ico-sort-${sortDirectionDesc ? 'descending' : 'ascending'}`]: sortBy === 'name'}"></i>
            <span>Name</span>
          </span>
        </th>
        <th scope="col" class="col-3" v-on:click="toggleSorting('size')">
          <span class="ico-conjunction">
            <i class="ico-sort-default me-0" :class="{'ico-sort-default': sortBy !== 'size',
               [`ico-sort-${sortDirectionDesc ? 'descending' : 'ascending'}`]: sortBy === 'size'}"></i>
            <span>Size</span>
          </span>
        </th>
        <th v-if="showAdditionalAttachmentActions" class="col-3">
          <span>Attributes</span>
        </th>
      </tr>
      </thead>
      <tbody class="table-striped">
      <tr v-for="attachment in sortedAttachments" :key="attachment.id">
        <th v-if="canDownload" scope="row">
          <div class="d-flex align-items-md-center">
            <input :id="attachment.id"
                   class="form-check-input" type="checkbox" id="checkboxNoLabel"
                   :checked="isAttachmentSelected(attachment)" @change="selectFile(attachment)">
          </div>
        </th>
        <td class="tooltip-text-no-space">
          <div class="overflow" v-tooltip.tooltip="attachment.name">
            <a v-if="canDownload" href="javascript:void(0);" v-on:click="downloadFile(attachment.id)">
              {{ attachment.name }}
            </a>
            <span v-else>{{ attachment.name }}</span>
          </div>
        </td>
        <td>
          {{ formatBytes(attachment.byteLength) }}
        </td>
        <td v-if="showAdditionalAttachmentActions">
          <ul class="ico-list">
            <li>
              <button type="button" class="ico-standalone comment"
                      v-tooltip.tooltip="attachment.confidential ? 'Confidential' : 'Non confidential'"
                      :data-bs-title="attachment.confidential ? 'Confidential' : 'Non confidential'">
                <i :class="attachment.confidential ? 'ico-confidential-primary' : 'ico-not-confidential-primary'"></i>
                <span>Confidential</span>
              </button>
            </li>
            <li v-if="attachment.comment" v-tooltip.tooltip="attachment.comment">
              <button type="button" class="ico-standalone comment">
                <i class="ico-comment-primary"></i>
                <span>Comment</span>
              </button>
            </li>
          </ul>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, PropType, ref, Ref, watch } from 'vue'
import { formatBytes } from '@/utils/formatters'
import { AttachmentSpec } from '@/model/entities'

export default defineComponent({
  name: 'AttachmentsList',
  props: {
    attachments: { type: Array as PropType<Array<AttachmentSpec>>, required: true },
    canDownload: { type: Boolean },
    showAdditionalAttachmentActions: { type: Boolean }
  },
  emits: ['selected', 'download'],
  setup(props, { emit }) {
    const checkedFilesIds: Ref<Array<number>> = ref([])

    const sortBy: Ref<'name' | 'size' | undefined> = ref()
    const sortDirectionDesc: Ref<boolean> = ref(false)

    const allSelected: ComputedRef<boolean> = computed(
      () => (props.attachments.length > 0 && checkedFilesIds.value.length === props.attachments.length))
    const sortedAttachments: ComputedRef<Array<AttachmentSpec>> = computed(() =>
      sortBy.value
        ? props.attachments.sort((one, two) => compareAttachments(one, two))
        : props.attachments
    )

    function compareAttachments(one: AttachmentSpec, two: AttachmentSpec): number {
      switch (sortBy.value!) {
        case 'name':
          if (sortDirectionDesc.value) {
            return one.name.toUpperCase() > two.name.toUpperCase() ? -1 : 1
          }
          return one.name.toUpperCase() < two.name.toUpperCase() ? -1 : 1
        case 'size':
          if (sortDirectionDesc.value) {
            return one.byteLength > two.byteLength ? -1 : 1
          }
          return one.byteLength < two.byteLength ? -1 : 1
      }
    }

    watch(() => props.attachments,
      () => {
        checkedFilesIds.value = []
      }, { deep: true })

    function toggleSorting(key: 'name' | 'size') {
      if (sortBy.value === key) {
        sortDirectionDesc.value = !sortDirectionDesc.value
        return
      }
      sortBy.value = key
      sortDirectionDesc.value = false
    }

    function isAttachmentSelected(attachment: AttachmentSpec) {
      return checkedFilesIds.value.includes(attachment.id)
    }

    function selectFile(attachment: AttachmentSpec) {
      if (isAttachmentSelected(attachment)) {
        checkedFilesIds.value = checkedFilesIds.value.filter(x => x !== attachment.id)
      } else {
        checkedFilesIds.value.push(attachment.id)
      }
      changeSelection()
    }

    function selectAllToggle(checked: boolean) {
      checkedFilesIds.value = checked ? props.attachments.map(x => x.id) : []
      changeSelection()
    }

    function downloadFile(file: number) {
      emit('download', file)
    }

    function changeSelection() {
      emit('selected', checkedFilesIds.value)
    }

    return {
      sortBy,
      sortDirectionDesc,
      toggleSorting,
      sortedAttachments,
      checkedFilesIds,
      allSelected,
      isAttachmentSelected,
      selectFile,
      selectAllToggle,
      formatBytes,
      downloadFile
    }
  }
})
</script>
