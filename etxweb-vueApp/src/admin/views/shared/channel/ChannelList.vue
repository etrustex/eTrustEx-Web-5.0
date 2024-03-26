<template>
  <div class="d-flex justify-content-end align-items-center btns">
    <button type="button" class="btn btn-primary btn-ico" @click="exportChannelsAndParticipants()">
      <span class="ico-standalone"><i aria-hidden="true" class="ico-file-download-white"></i></span>
      <span>Export channels and participants</span>
    </button>
    <button data-bs-toggle="collapse" data-bs-target="#channelForm" class="btn btn-outline-primary btn-collapse-arrow"
            type="submit">
      <span class="ico-standalone"><i aria-hidden="true" class="ico-add-primary"></i></span>
      <span class="ms-0">Add channel</span>
      <span aria-hidden="true" class="collapse-arrow"></span>
    </button>
  </div>

  <div id="channelForm" class="collapse collapse-content-box" ref="collapseElement">
    <channel-form ref="formNewChannel" @on-save="saveChannel"/>
  </div>

  <div class="mt-3">
    <bulk-upload :paginationCommand="paginationCommand" :uploadFunction="uploadFunction"
                 :sampleFileName="'channels_bulk_creation'" :model="'channel'" :modelPlural="'channels'">
    </bulk-upload>
  </div>

  <search searchBy="Name" searchTooltip="Search a User"
          :filterFn="filter" :prefilterFn="prefilterFn"
  />

  <b-table
    :fields="[{ key: 'name', thClass: 'w-50', tdClass: 'w-50', sortable: true }, 'actions', 'more']"
    :items="channels"
    :no-local-sorting="true"
    @sort-changed="sortingChanged"
    sort-icon-left
    class="mt-3"
  >
    <template #cell(name)="data">
      <router-link
        :to="{ name: businessChannel.name, params: { ... businessChannel.params, channelId: data.item.id }}"><span
        class="text-break">{{ data.item.name }}</span></router-link>
    </template>
    <template #cell(status)="data">
      <div class="form-check form-switch form-control-lg py-0">
        <input type="checkbox" role="switch" class="form-check-input" name="'check-button'" disabled
               v-model="data.item.active">
      </div>
    </template>
    <template #cell(actions)="data">
      <ul class="ico-list">
        <li>
          <a v-tooltip.tooltip="'Delete'" class="ico-standalone delete" :disabled="data.item.active === false"
             href="javascript:void(0);" type="button" v-on:click="confirmDeleteChannel(data.item)">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
        </li>
      </ul>
    </template>
    <template #cell(more)="row">
      <a :class="`ico-standalone has-tooltip ${row.item._showDetails ? 'not-collapsed': 'collapsed'}`" type="button"
         @click="row.toggleDetails">
        <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
      </a>
    </template>
    <template #row-details="row">
      <channel-details :channel="row.item" :editable="false"/>
    </template>
  </b-table>

  <div class="container">
    <div class="row">
      <div class="col mt-3">
        <pagination :page="channelPage" :paginationCommand="paginationCommand"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { computed, onMounted, Ref, ref } from 'vue'
import { formatPopupName } from '@/utils/formatters'
import { Channel, ChannelSpec, Direction, ExchangeRule, SearchItem } from '@/model/entities'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import Pagination from '@/shared/views/Pagination.vue'
import useChannelStore from '@/admin/store/channel'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import { ChannelApi } from '@/admin/api/channelApi'
import ChannelForm from '@/admin/views/shared/channel/ChannelForm.vue'
import BulkUpload from '@/admin/views/shared/BulkUpload.vue'
import ChannelDetails from '@/admin/views/shared/channel/ChannelDetails.vue'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import { Collapse } from 'bootstrap'
import Search from '@/shared/views/Search.vue'

const props = defineProps({
  businessId: { type: Number, required: true }
})

const store = useChannelStore()
const dialogStore = useDialogStore()

const formNewChannel: Ref<typeof ChannelForm | null> = ref(null)
const collapseElement = ref<HTMLElement>()
let collapseInstance: Collapse = {} as Collapse

const paginationCommand: PaginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => store.fetchChannels(paginationOptions),
  { sortBy: 'auditingEntity.createdDate' },
  { businessId: props.businessId }
)
const uploadFunction = (file: File, groupId: number) => ExchangeRuleApi.uploadBulk(file, groupId)
const businessChannel = { name: AdminRouteNames.BUSINESS_CHANNEL }
const channelPage = computed(() => store.channelPage)
const channels = computed(() => store.channels)

onMounted(() => {
  collapseInstance = new Collapse(collapseElement.value as HTMLElement, { toggle: false })
  paginationCommand.options.businessId = props.businessId
  paginationCommand.fetch()
    .then()
})

function filter(filterValue: string) {
  paginationCommand.options.filterValue = filterValue
  paginationCommand.fetch({ page: 0 })
    .then()
}

async function prefilterFn(searchBy: string) {
  const items = await ChannelApi.search(props.businessId, searchBy)
  return items.map(i => ({
    value: `${i.value}`,
    searchValue: i.searchValue
  } as SearchItem))
}

function confirmDeleteChannel(channel: Channel) {
  formatMessage(channel)
    .then(message => dialogStore.show(
      'Delete Channel',
      message,
      DialogType.ERROR,
      buttonWithCallback('Delete', () => deleteChannel(channel)),
      CANCEL_BUTTON
    ))
}

function saveChannel(channel: ChannelSpec) {
  ChannelApi.createChannel(channel)
    .then(() => paginationCommand.fetch({ page: 0 }))
    .then(() => {
      formNewChannel.value?.reset()
      collapseInstance.hide()
    })
}

async function formatMessage(channel: Channel) {
  const exchangeRulesPage = await ExchangeRuleApi.getExchangeRules({ page: 0, size: 10, channelId: channel.id })

  let message: string

  if (exchangeRulesPage.content.length === 0) {
    message = `<p class="mt-3" >
              It is safe to delete the channel with the name ${formatPopupName(channel.name)} as no participant has been found.
            </p>`
  } else {
    message = `<div><p class="mt-3">The channel with the name ${formatPopupName(channel.name)} has the following participant(s) that will be deleted:</p><ul>
        ${formatListItems(exchangeRulesPage.content, exchangeRulesPage.totalElements)}</ul></div>`
  }
  return message + '<p class="mb-0">Would you like to proceed ?</p>'
}

function formatListItems(exchangeRules: Array<ExchangeRule>, totalElements: number) {
  const list = exchangeRules
    .map((exchangeRule) => `<li key="${exchangeRule}"> ${formatPopupName(exchangeRule.member.name)} which is <strong class="text-break">${exchangeRule.exchangeMode}</strong></li>`)
    .reduce((previousValue, currentValue) => previousValue + currentValue)

  if (totalElements > 10) {
    return list + `<li>${totalElements - 10} More participants are available but not shown, please check the channel configuration for more info ...</li>`
  } else {
    return list
  }
}

async function deleteChannel(channel: Channel) {
  await ChannelApi.deleteChannel(channel.id)
    .then(() => paginationCommand.fetch({
      page: paginationCommand
        .currentOrLast(store.channelPage.totalElements - 1, store.channelPage.number)
    }))
}

async function exportChannelsAndParticipants() {
  return ChannelApi.downloadExportChannelsAndParticipants(props.businessId)
}

async function sortingChanged(sortBy: string, isDesc: boolean) {
  paginationCommand.options.sortBy = sortBy
  paginationCommand.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
  await paginationCommand.fetch({ page: 0 })
    .then()
}

</script>
<style>
.text-end {
  text-align: right !important;
}
</style>
