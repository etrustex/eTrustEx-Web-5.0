<template>
  <div v-if="messagesPage">
    <search searchBy="Subject or MessageId" searchTooltip="Search a Message" :filterFn="filter"/>
    <b-table
      :fields="[
        { key: 'messageId', label: 'Message ID', sortable: true },
        { key: 'subject', thClass: 'w-25', tdClass: 'w-25', sortable: true },
        { key: 'name', sortable: true },
        { key: 'ecasId', label: 'EU Login ID', sortable: true },
        { key: 'modifiedDate', label: 'Read on', sortable: true },
        'more'
      ]"
      :items="messages"
      :no-local-sorting="true"
      @sort-changed="sortingChanged"
      sort-icon-left
      class="mt-3"
    >
      <template #cell(messageId)="data">
        <span class="text-break">{{ data.item.messageId }}</span>
      </template>
      <template #cell(subject)="data">
        <span class="text-break ellipsis" style="max-width: 30rem">{{ data.item.subject }}</span>
      </template>
      <template #cell(name)="data">
        <span class="text-break">{{ data.item.name }}</span>
      </template>
      <template #cell(ecasId)="data">
        <span class="text-break">{{ data.item.ecasId }}</span>
      </template>
      <template #cell(modifiedDate)="data">
        <span class="text-break">{{ formatDate(data.item.modifiedDate) }}</span>
      </template>
      <template #cell(more)="data">
        <a :class="`ico-standalone has-tooltip ${data.item._showDetails ? 'not-collapsed': 'collapsed'}`"
           type="button" v-on:click="!data.item.isEditMode && data.toggleDetails()">
          <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
        </a>
      </template>
      <template #row-details="data">
        <div class="container-fluid">
          <div class="row mt-3">
            <div class="col col-lg-3 text-lg-end">Message ID</div>
            <div class="col col-lg-6"><strong class="text-break">{{ data.item.messageId }}</strong></div>
          </div>

          <div class="row mt-3">
            <div class="col col-lg-3 text-lg-end">Subject</div>
            <div class="col col-lg-6"><strong class="text-break">{{ data.item.subject }}</strong></div>
          </div>

          <div class="row mt-3">
            <div class="col col-lg-3 text-lg-end">Name</div>
            <div class="col col-lg-6"><strong class="text-break">{{ data.item.name }}</strong></div>
          </div>

          <div class="row mt-3">
            <div class="col col-lg-3 text-lg-end">
              <span>EU Login ID</span>
              <more-information :content="moreInfo" placement="right"/>
            </div>
            <div class="col col-lg-6"><strong class="text-break">{{ data.item.ecasId }}</strong></div>
          </div>

          <div class="row mt-3">
            <div class="col col-lg-3 text-lg-end">Read on</div>
            <div class="col col-lg-6"><strong class="text-break">{{ formatDate(data.item.modifiedDate) }}</strong></div>
          </div>
        </div>
      </template>
    </b-table>

    <div class="container-fluid">
      <div class="row">
        <div class="col-12 mt-3">
          <pagination :page="messagesPage" :paginationCommand="paginationCommand"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { computed, ComputedRef, onMounted, ref, Ref } from 'vue'
import { Direction, MessageSummaryUserStatusItem, Page } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import Pagination from '@/shared/views/Pagination.vue'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import { formatDate } from '@/utils/formatters'
import Search from '@/shared/views/Search.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { MessageMonitoringApi } from '@/admin/api/messageMonitoringApi'

const props = defineProps({
  groupId: { type: Number, required: true }
})

const messages: ComputedRef<Array<MessageSummaryUserStatusItem>> = computed(() => messagesPage.value ? messagesPage.value?.content : [])
const messagesPage: Ref<Page<MessageSummaryUserStatusItem> | undefined> = ref(undefined)
const moreInfo = 'EU Login ID is the unique identifier at the Commission (uid). To find it, connect to EU Login -> My account -> My account details.\n\n' +
  '<br/><a href="https://your.auth.service.host/cas/userdata/ShowDetails.cgi" class="underline-on-hover"> EU Login ID</a>'

const paginationCommand = computed(() => props.groupId
  ? new PaginationCommand(
    (paginationOptions: PaginationOptions) => MessageMonitoringApi.get(props.groupId, paginationOptions)
      .then(page => messagesPage.value = page),
    { sortBy: 'auditingEntity.modifiedDate' })
  : undefined)

onMounted(() => {
  paginationCommand.value?.fetch()
})

function filter(filterValue: string) {
  if (paginationCommand.value) {
    paginationCommand.value.options.filterBy = 'subject_or_message_id'
    paginationCommand.value.options.filterValue = filterValue
    paginationCommand.value.fetch({ page: 0 })
  }
}

function sortingChanged(key: string, isDesc: boolean) {
  if (paginationCommand.value) {
    paginationCommand.value.options.sortBy = getSortBy(key)
    paginationCommand.value.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
    paginationCommand.value.fetch({ page: 0 })
  }
}

function getSortBy(key: string) {
  let sortBy: string
  switch (key) {
    case 'name':
      sortBy = 'user.name'
      break
    case 'ecasId':
      sortBy = 'user.ecasId'
      break
    case 'messageId':
      sortBy = 'm.id'
      break
    case 'subject':
      sortBy = 'm.subject'
      break
    default:
      sortBy = 'auditingEntity.modifiedDate'
      break
  }

  return sortBy
}
</script>
