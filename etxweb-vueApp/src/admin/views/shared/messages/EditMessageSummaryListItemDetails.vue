<template>
  <div class="container form-admin form-admin-edit-mode">
    <div class="row">
      <label class="col-lg-6 col-form-label">Message ID</label>
      <div class="col-lg-6"><strong class="text-break">{{ messageSummaryListItem.messageId }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">
        <span>Subject</span>
      </label>
      <div class="col col-lg-6"><strong class="text-break">{{ messageSummaryListItem.subject }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">Recipient entity</label>
      <div class="col-lg-6"><strong class="text-break">{{ messageSummaryListItem.recipientEntity }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">Date</label>
      <div class="col-lg-6"><strong class="text-break">{{ toDateTime(messageSummaryListItem.date) }}</strong></div>
    </div>
    <div class="row">
      <label class="col-lg-6 col-form-label">Display status</label>
      <div class="col-lg-6">
        <div v-if="props.row.item.isEditMode" class="mb-0 form-check form-switch">
          <input id="status" type="checkbox" role="switch" class="form-check-input"
                 v-model="editMessageSummaryListItem.active">
          <strong>{{ editMessageSummaryListItem.active ? 'Active' : 'Inactive' }}</strong>
        </div>
        <div v-else>
        <span v-if="editMessageSummaryListItem.active"
              class="badge rounded-pill bg-success">Active</span>
          <span v-else
                class="badge rounded-pill bg-danger">Inactive</span>
        </div>
      </div>
    </div>

    <div class="row mb-3" v-if="props.row.item.isEditMode">
      <div class="col-lg-12 text-end btns">
        <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="reset()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
          <span>Cancel</span>
        </button>
        <button type="button" class="btn btn-primary btn-ico" v-on:click="save(props.row.item)">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
          <span>Save</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, PropType, ref } from 'vue'
import { MessageSummaryListItem } from '@/model/entities'
import useDialogStore from '@/shared/store/dialog'
import { toDateTime } from '@/utils/formatters'
import { MessageApi } from '@/admin/api/messageApi'

const props = defineProps({
  businessId: { type: Number, required: true },
  row: { type: Object, required: true },
  messageSummaryListItem: { type: Object as PropType<MessageSummaryListItem>, required: true }
})
const emit = defineEmits(['updated'])

const dialogStore = useDialogStore()

const editMessageSummaryListItem = ref(props.messageSummaryListItem)

onMounted(() => editMessageSummaryListItem.value = { ...props.messageSummaryListItem })

async function save(messageSummaryListItem: MessageSummaryListItem) {
  MessageApi.updateActiveStatus(props.businessId, messageSummaryListItem.messageId, messageSummaryListItem.recipientEntity, editMessageSummaryListItem.value.active)
    .then(value => {
      emit('updated', messageSummaryListItem)
      props.row.item.isEditMode = false
      props.row.toggleDetails()
    })
}

function reset() {
  props.row.item.isEditMode = false
  props.row.toggleDetails()
}

</script>
