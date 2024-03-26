<template>
  <h2 class="title-secondary text-break"><span>Channel</span> {{ channel?.name }}</h2>

  <div class="d-flex justify-content-between align-items-center mt-3">
    <h3 class="variant">Overview</h3>
    <a data-bs-toggle="collapse" data-bs-target="#channel-details" class="ico-standalone">
      <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
    </a>
  </div>

  <div v-if="channel" class="collapse mt-3 show" ref="collapseElement" id="channel-details">
    <channel-details v-if="channel.id" :channel="channel" :editable="true"/>
  </div>

  <hr class="my-4">

  <div class="d-flex justify-content-between align-items-center">
    <h3 class="variant mb-0">Participants</h3>
    <button data-bs-toggle="collapse" data-bs-target="#exchange-rule-form"
            class="btn btn-outline-primary btn-collapse-arrow" type="submit">
      <span class="ico-conjunction">
        <i aria-hidden="true" class="ico-add-primary"></i>
        Add participant
        <span aria-hidden="true" class="collapse-arrow"></span>
      </span>
    </button>
  </div>

  <exchange-rule-form v-if="channel?.id" id="exchange-rule-form" :businessId="+route.params.businessId"
                      :paginationCommand="paginationCommand"/>
  <div class="mt-3">
  <bulk-upload :model-plural="'participants'" :model="'participants'" :upload-function="uploadFunction" :pagination-command="paginationCommand" :sample-file-name="'participant_entities_bulk_creation'"></bulk-upload>
  </div>

  <search :filterFn="filter" :prefilterFn="prefilterFn" searchBy="Entity Name or Entity Display Name"
          searchTooltip="Search a Participant"/>
  <b-table
    :fields="[{key: 'entity_identifier', sortable: true}, {key: 'entity_name', sortable: true}, {key: 'system_connection', sortable: true}, {key: 'role', sortable: true}, 'actions', 'more']"
    :items="exchangeRules"
    :no-local-sorting="true"
    @sort-changed="sortingChanged"
    sort-icon-left
    class="mt-3"
  >
    <template #cell(entity_identifier)="data">
      <span class="text-break">{{ data.item.member.identifier }}</span>
    </template>
    <template #cell(entity_name)="data">
      <span class="text-break">{{ data.item.member.name }}</span>
    </template>
    <template #cell(system_connection)="data">
      <span v-if="data.item.member.isSystem" class="badge rounded-pill bg-primary ms-3">Back-end system</span>
    </template>
    <template #cell(role)="data">
      {{ data.item.exchangeMode }}
    </template>
    <template #cell(actions)="data">
      <ul class="ico-list">
        <li>
          <a v-tooltip.tooltip="'Edit'"
             class="ico-standalone" href="javascript:void(0);" type="button"
             @click="!data.item.isEditMode && edit(data)">
            <i class="ico-edit"></i>
            <span>Edit</span>
          </a>
        </li>
        <li>
          <a v-tooltip.tooltip="'Delete'"
             class="ico-standalone delete" href="javascript:void(0);" type="button"
             v-on:click="confirmDeleteParticipant(data.item, channel!.id)">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
        </li>
      </ul>
    </template>
    <template #cell(more)="data">
      <a :class="`ico-standalone has-tooltip ${data.item._showDetails ? 'not-collapsed': 'collapsed'}`"
         type="button" @click="!data.item.isEditMode && data.toggleDetails()">
        <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
      </a>
    </template>
    <template #row-details="row">
      <exchange-rule-details :exchangeRule="row.item" :paginationCommand="paginationCommand" :row="row"/>
    </template>
  </b-table>

  <div class="container">
    <div class="row">
      <div class="col mt-3">
        <pagination :page="exchangeRulePage" :paginationCommand="paginationCommand"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, Ref, ref } from 'vue'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { Channel, Direction, ExchangeRule, ExchangeRuleId, SearchItem } from '@/model/entities'
import { formatPopupName } from '@/utils/formatters'
import useChannelStore from '@/admin/store/channel'
import useExchangeRuleStore from '@/admin/store/exchangeRule'
import { useRoute } from 'vue-router'
import ChannelDetails from '@/admin/views/shared/channel/ChannelDetails.vue'
import ExchangeRuleForm from '@/admin/views/shared/exchange_rule/ExchangeRuleForm.vue'
import Search from '@/shared/views/Search.vue'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import ExchangeRuleDetails from '@/admin/views/shared/exchange_rule/ExchangeRuleDetails.vue'
import Pagination from '@/shared/views/Pagination.vue'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import { storeToRefs } from 'pinia'
import BulkUpload from '@/admin/views/shared/BulkUpload.vue'

const route = useRoute()
const dialogStore = useDialogStore()

const channelStore = useChannelStore()
const channel: Ref<Channel | undefined> = ref(undefined)

const exchangeRuleStore = useExchangeRuleStore()
const { exchangeRulePage, exchangeRules } = storeToRefs(exchangeRuleStore)

const uploadFunction = (file: File, groupId: number) => ExchangeRuleApi.uploadParticipantsBulk(file, groupId, +route.params.channelId)

const paginationCommand: PaginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => exchangeRuleStore.fetchExchangeRules(paginationOptions),
  { sortBy: 'member.name', sortOrder: Direction.ASC },
  { channelId: route.params.channelId },
)

onMounted(() => {
  channelStore.fetchChannel(+route.params.channelId)
    .then((c) => {
      channel.value = c
      paginationCommand.fetch().then()
    })
})

function edit(row) {
  if (row.detailsShowing) {
    row.toggleDetails()
  }
  row.item.isEditMode = !row.item.isEditMode
  row.toggleDetails()
}

function confirmDeleteParticipant(exchangeRule: ExchangeRule, channelId: number) {
  const message = `<p class="mt-3">
      Are you sure you want to delete the participant below? <br/>
      The action cannot be undone <br/>
    </p>
    <p class="mb-0">
        ${ formatPopupName(exchangeRule.member.name) }
    </p>`
  dialogStore.show(
    'Delete Participant',
    message,
    DialogType.ERROR,
    buttonWithCallback('Delete', () => deleteParticipant(exchangeRule, channelId)),
    CANCEL_BUTTON,
  )
}

function prefilterFn(searchBy: string) {
  if (!channel.value) {
    return
  }
  return ExchangeRuleApi.searchExchangeRules(channel.value.id, searchBy)
    .then(items => items.map(i => ({
      value: `${ i.member.identifier } - ${ i.member.name }`,
      searchValue: i.member.identifier,
    } as SearchItem)))
}

async function deleteParticipant(exchangeRule: ExchangeRule, channelId: number) {
  const erId: ExchangeRuleId = new ExchangeRuleId()
  erId.channel = channelId
  erId.member = exchangeRule.member.id
  await ExchangeRuleApi.delete(erId)
    .then(() => paginationCommand.fetch({
      page: paginationCommand
        .currentOrLast(useExchangeRuleStore().exchangeRulePage.totalElements - 1, useExchangeRuleStore().exchangeRulePage.number),
    }))
}

async function filter(filterValue: string) {
  paginationCommand.options.filterBy = 'entity_identifier,entity_name'
  paginationCommand.options.filterValue = filterValue
  await paginationCommand.fetch({ page: 0 })
    .then()
}

async function sortingChanged(key: string, isDesc: boolean) {
  paginationCommand.options.sortBy = getSortBy(key)
  paginationCommand.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
  await paginationCommand.fetch({ page: 0 })
    .then()
}

function getSortBy(key: string) {
  let sortBy: string
  switch (key) {
  case 'entity_identifier':
    sortBy = 'member.identifier'
    break
  case 'role':
    sortBy = 'exchangeMode'
    break
  case 'system_connection':
    sortBy = 'member.isSystem'
    break
  case 'entity_name':
  default:
    sortBy = 'member.name'
    break
  }
  return sortBy
}

</script>
