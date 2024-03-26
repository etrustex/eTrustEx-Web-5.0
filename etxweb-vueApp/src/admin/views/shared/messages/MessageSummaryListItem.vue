<template>
  <div>
    <search searchBy="Subject or MessageId" searchTooltip="Search a Message"
            :filterFn="filter"
            :prefilterFn="prefilterFn"/>
    <div class="d-flex justify-content-end">
      <div class="dropdown mt-3 btn-group" v-if="selectedMessages.length > 0">
        <button class="btn btn-outline-primary dropdown-toggle" type="button" data-bs-toggle="dropdown"
                aria-expanded="false">
          Actions
        </button>
        <ul class="dropdown-menu">
          <li role="presentation">
            <button role="menuitem" class="dropdown-item" v-on:click="activateOrInactivateMessages(true)">
              <span aria-hidden="true" class="ico-standalone">
                <a href="javascript:void(0);" class="badge rounded-pill bg-success">Activate messages</a>
              </span>
            </button>
          </li>
          <li role="presentation">
            <button role="menuitem" class="dropdown-item" v-on:click="activateOrInactivateMessages(false)">
              <span aria-hidden="true" class="ico-standalone">
                <a href="javascript:void(0);" class="badge rounded-pill bg-danger">Inactivate messages</a>
              </span>
            </button>
          </li>
        </ul>
      </div>
    </div>

    <b-table
      :fields="[
        { key: 'index', thClass: 'index-width', tdClass: 'index-width' },
        { key: 'messageId',  label: 'Message ID',sortable: true },
        { key: 'subject', label: 'Subject', sortable: true },
        { key: 'recipientEntity', label: 'Recipient entity', sortable: true },
        { key: 'date', label: 'Date', sortable: true },
        { key: 'active', label: 'Display status'},
        { key:'actions' },
        'more'
      ]"
      :items="messageSummaryListItems"
      :no-local-sorting="true"
      @sort-changed="sortingChanged"
      sort-icon-left
      class="mt-3"
    >
      {{ messageSummaryListItems.length }}
      <template #head(index)>
        <div class="d-flex align-items-md-center">
          <input class="form-check-input" type="checkbox" :checked="allSelected" v-on:change="toggleAllSelected()"/>
        </div>
      </template>
      <template #cell(index)="data">
        <div class="d-flex align-items-md-center">
          <input class="form-check-input" type="checkbox" :id="data.item.name + data.index + 1"
                 :checked="isMessageSelected(data.item)" v-on:change="toggleMessageSelected(data.item)"/>
        </div>
      </template>
      <template #cell(messageId)="data">
        <span class="text-break">{{ data.item.messageId }}</span>
      </template>
      <template #cell(subject)="data">
        <span class="text-break">{{ data.item.subject }}</span>
      </template>
      <template #cell(recipientEntity)="data">
        <span class="text-break">{{ data.item.recipientEntity }}</span>
      </template>
      <template #cell(date)="data">
        <span class="text-break">{{ toDateTime(data.item.date) }}</span>
      </template>

      <template #cell(active)="data">
        <span v-if="data.item.active"
              class="badge rounded-pill bg-success">Active</span>
        <span v-else
              class="badge rounded-pill bg-danger">Inactive</span>
      </template>
      <template #cell(actions)="data">
        <a v-tooltip.tooltip="'Edit'"
           class="ico-standalone" type="button" v-on:click="!data.item.isEditMode && edit(data)">
          <i class="ico-edit"></i>
          <span>Edit</span>
        </a>
      </template>
      <template #cell(more)="data">
        <a :class="`ico-standalone has-tooltip ${data.item._showDetails ? 'not-collapsed': 'collapsed'}`"
           type="button" v-on:click="!data.item.isEditMode && data.toggleDetails()">
          <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
        </a>
      </template>
      <template #row-details="data">
        <edit-message-summary-list-item-details :row="data" :businessId="businessId"
                                                :messageSummaryListItem="data.item" @updated="refresh(0)"/>

      </template>

    </b-table>
    <div class="container-fluid">
      <div class="row">
        <div class="col-12 mt-3">
          <pagination :page="messageSummaryListItemPage" :paginationCommand="paginationCommand"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Search from '@/shared/views/Search.vue'
import { Direction, MessageSummaryListItem } from '@/model/entities'
import { computed, onMounted, ref, Ref, watch } from 'vue'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { storeToRefs } from 'pinia'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import { toDateTime } from '@/utils/formatters'
import Pagination from '@/shared/views/Pagination.vue'
import EditMessageSummaryListItemDetails from '@/admin/views/shared/messages/EditMessageSummaryListItemDetails.vue'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import useMessageSummaryListItemStore from '@/admin/store/messageSummaryListItem'
import { MessageApi } from '@/admin/api/messageApi'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'

const props = defineProps({
  businessId: { type: Number, required: true },
})

const dialogStore = useDialogStore()
const messageSummaryListItemStore = useMessageSummaryListItemStore()
const selectedMessages: Ref<Array<string>> = ref([])
const allSelected = computed(() => selectedMessages.value.length > 0 && selectedMessages.value.length === messageSummaryListItems.value.length)
const paginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => useMessageSummaryListItemStore()
    .fetchMessageDisplay(paginationOptions),
  { sortBy: 'auditingEntity.createdDate' },
  { businessId: props.businessId },
)
const { messageSummaryListItemPage, messageSummaryListItems } = storeToRefs(messageSummaryListItemStore)

onMounted(() => paginationCommand.fetch())
watch(props, () => paginationCommand.fetch(), { deep: true })

function toggleAllSelected() {
  selectedMessages.value = allSelected.value ? [] : messageSummaryListItems.value.map(u => `${ u.messageId }|${ u.recipientEntity }`)
}

function isMessageSelected(messageSummaryListItem: MessageSummaryListItem) {
  return selectedMessages.value.includes(`${ messageSummaryListItem.messageId }|${ messageSummaryListItem.recipientEntity }`)
}

function toggleMessageSelected(messageSummaryListItem: MessageSummaryListItem) {
  if (isMessageSelected(messageSummaryListItem)) {
    selectedMessages.value = selectedMessages.value.filter(key => key !== `${ messageSummaryListItem.messageId }|${ messageSummaryListItem.recipientEntity }`)
  } else {
    selectedMessages.value = [...selectedMessages.value, `${ messageSummaryListItem.messageId }|${ messageSummaryListItem.recipientEntity }`]
  }
}

function prefilterFn(searchBy: string) {
  return MessageApi.search(props.businessId, searchBy)
}

function filter(filterValue: string) {
  paginationCommand.options.filterBy = 'messageId,subject'
  paginationCommand.options.filterValue = filterValue
  paginationCommand.fetch({ page: 0 })
    .then()
}

function edit(row: any) {
  if (row.detailsShowing) {
    row.toggleDetails()
  }
  row.item.isEditMode = !row.item.isEditMode
  row.toggleDetails()
}

function refresh(removedCount = 1) {
  selectedMessages.value = []
  return paginationCommand.fetch({
    page: paginationCommand.currentOrLast(
      messageSummaryListItemStore.messageSummaryListItemPage.totalElements - removedCount,
      messageSummaryListItemStore.messageSummaryListItemPage.number),
  })
}

function activateOrInactivateMessages(activate: boolean) {
  const selectedMessageItems = messageSummaryListItems.value.filter(m => isMessageSelected(m))

  if (activate) {
    dialogStore.show(
      'Activate message',
      'Are you sure you want to activate all selected messages?',
      DialogType.INFO,
      buttonWithCallback('Activate', () => updateMessageSummariesActiveStatus(selectedMessageItems, activate)),
      CANCEL_BUTTON,
    )
  } else {
    dialogStore.show(
      'Inactivate message',
      'Are you sure you want to inactivate all selected messages?',
      DialogType.INFO,
      buttonWithCallback('Inactivate', () => updateMessageSummariesActiveStatus(selectedMessageItems, activate)),
      CANCEL_BUTTON,
    )
  }
}

function updateMessageSummariesActiveStatus(selectedMessageItems: Array<MessageSummaryListItem>, activate: boolean) {
  MessageApi.updateMessageSummariesActiveStatus(props.businessId, selectedMessageItems, activate)
    .then(() => refresh(0),
    )
}

function sortingChanged(key: string, isDesc: boolean) {
  paginationCommand.options.sortBy = getSortBy(key)
  paginationCommand.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
  paginationCommand.fetch({ page: 0 })
    .then()
}

function getSortBy(key: string) {
  let sortBy: string
  switch (key) {
  case 'messageId':
    sortBy = 'message.id'
    break
  case 'subject':
    sortBy = 'message.subject'
    break
  case 'recipientEntity':
    sortBy = 'recipient.identifier'
    break
  default:
    sortBy = 'auditingEntity.createdDate'
    break
  }

  return sortBy
}

</script>
