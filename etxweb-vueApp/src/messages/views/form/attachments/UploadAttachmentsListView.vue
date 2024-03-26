<template>
  <div class="mt-3 table-responsive message-form-table-files">
    <table class="table align-middle">
      <thead class="thead-fixed">
      <tr>
        <th scope="col" class="col-1">
          <div class="d-flex align-items-center">
            <input type="checkbox" class="form-check-input" v-model="allSelected"/>
          </div>
        </th>
        <th scope="col" class="col-5" v-on:click="toggleSorting('name')">
          <span class="ico-conjunction">
            <i class="ico-sort-default me-0" :class="{'ico-sort-default': sortBy !== 'name',
               [`ico-sort-${sortDirectionDesc ? 'descending' : 'ascending'}`]: sortBy === 'name'}"></i>
            <span>Name</span>
        </span>
        </th>
        <th scope="col" class="col-2" v-on:click="toggleSorting('size')">
          <span class="ico-conjunction">
            <i class="ico-sort-default me-0" :class="{'ico-sort-default': sortBy !== 'size',
               [`ico-sort-${sortDirectionDesc ? 'descending' : 'ascending'}`]: sortBy === 'size'}"></i>
            <span>Size</span>
          </span>
        </th>
        <th scope="col" class="col-2" v-on:click="toggleSorting('status')">
          <span class="ico-conjunction">
            <i class="ico-sort-default me-0" :class="{'ico-sort-default': sortBy !== 'status',
               [`ico-sort-${sortDirectionDesc ? 'descending' : 'ascending'}`]: sortBy === 'status'}"></i>
            <span>Status</span>
          </span>
        </th>
        <th scope="col" class="col-2">Action</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(attachmentSpecDto, index) in sortedAttachments" :key="attachmentSpecDto.name">
        <td>
          <div class="d-flex align-items-center">
            <input :id="attachmentSpecDto.attachmentSpec.name" type="checkbox" class="form-check-input"
                   v-model="attachmentSpecDto.selected"/>
            <span class="ms-2">{{ index + 1 }}</span>
          </div>
        </td>
        <td class="tooltip-text-no-space">
          <div class="overflow" v-tooltip.tooltip="attachmentSpecDto.name">
            <a href="javascript:void(0);">{{ attachmentSpecDto.name }}</a>
          </div>
        </td>
        <td>{{ formatBytes(attachmentSpecDto.size) }}</td>
        <td>
          <div v-if="attachmentSpecDto.errorTooltip" v-tooltip.tooltip="attachmentSpecDto.errorTooltip">
            <span :class="`badge rounded-pill ${getStatusClass(attachmentSpecDto.status)}`">
              {{ attachmentSpecDto.status }}
            </span>
          </div>
          <span v-else :class="`badge rounded-pill ${getStatusClass(attachmentSpecDto.status)}`">
            {{ attachmentSpecDto.status }}
          </span>
        </td>
        <td>
          <attachment-actions :attachmentSpecWrapper="attachmentSpecDto"/>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">

import { computed, ComputedRef, PropType, ref, Ref } from 'vue'
import { AttachmentSpecWrapper, AttachmentStatus } from '@/model/attachmentDto'
import AttachmentStatusHelper from '@/utils/attachments/attachmentStatusHelper'
import { formatBytes } from '@/utils/formatters'
import AttachmentActions from '@/messages/views/form/attachments/AttachmentActions.vue'

const props = defineProps({
  attachmentSpecDtos: { type: Array as PropType<Array<AttachmentSpecWrapper>>, required: true }
})

const sortBy: Ref<'name' | 'size' | 'status' | undefined> = ref()
const sortDirectionDesc: Ref<boolean> = ref(false)

const allSelected = computed({
  get: () => props.attachmentSpecDtos.filter(attachment => attachment.selected).length === props.attachmentSpecDtos.length,
  set: (checked) => props.attachmentSpecDtos.forEach(attachment => attachment.selected = checked)
})
const sortedAttachments: ComputedRef<Array<AttachmentSpecWrapper>> = computed(() => {
  const attachments = props.attachmentSpecDtos
  if (sortBy.value) {
    attachments.sort((one, two) => compareAttachments(one, two))
  }
  return attachments
})

function compareAttachments(one: AttachmentSpecWrapper, two: AttachmentSpecWrapper): number {
  {
    switch (sortBy.value!) {
      case 'name':
        if (sortDirectionDesc.value) {
          return one.name.toUpperCase() > two.name.toUpperCase() ? -1 : 1
        }
        return one.name.toUpperCase() < two.name.toUpperCase() ? -1 : 1
      case 'size':
        if (sortDirectionDesc.value) {
          return one.size > two.size ? -1 : 1
        }
        return one.size < two.size ? -1 : 1
      case 'status':
        if (sortDirectionDesc.value) {
          return one.status! > two.status! ? -1 : 1
        }
        return one.status! < two.status! ? -1 : 1
    }
  }
}

function toggleSorting(key: 'name' | 'size' | 'status') {
  if (sortBy.value === key) {
    sortDirectionDesc.value = !sortDirectionDesc.value
    return
  }
  sortBy.value = key
  sortDirectionDesc.value = false
}

function getStatusClass(status: AttachmentStatus) {
  return AttachmentStatusHelper.getStatusClass(status)
}

</script>
