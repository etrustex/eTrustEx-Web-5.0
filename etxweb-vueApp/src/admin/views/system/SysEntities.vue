<template>
  <div>
    <search searchBy="Name or Identifier" searchTooltip="Name or Identifier"
            :filterFn="filterEntityItems"
            :prefilterFn="prefilterFn"/>

    <div>
      <b-table
        :fields="fields"
        :items="entityItems"
        :no-local-sorting="true"
        @sort-changed="sortingChanged"
        sort-icon-left
        class="mt-3"
      >
        <template #cell(businessName)="data">
          <span class="text-break">{{ data.item.businessName }}</span>
        </template>
        <template #cell(entityIdentifier)="data">
          <span class="text-break">{{ data.item.entityIdentifier }}</span>
        </template>
        <template #cell(entityName)="data">
          <span class="text-break">{{ data.item.entityName }}</span>
        </template>
        <template #cell(channelName)="data">
          <span class="text-break">{{ data.item.channelName }}</span>
        </template>
        <template #cell(exchangeMode)="data">
          <span class="text-break">{{ data.item.exchangeMode }}</span>
        </template>
        <template #cell(active)="data">
          <span>{{ data.item.active ? 'Active' : 'Inactive' }}</span>
        </template>
        <template #cell(confidentiality)="data">
          <span v-if="isEncrypted(data.item.confidentiality) && data.item.hasPublicKey" class="ico-conjunction">
            <i class="ico-encrypted-mandatory" title="Encrypted"></i>
          </span>
          <span v-else-if="data.item.hasPublicKey" class="ico-conjunction">
            <i class="ico-encrypted" title="Encrypted"></i></span>
          <span v-else class="ico-conjunction"><i class="ico-not-encrypted" title="Not encrypted"></i></span>
        </template>
        <template #cell(more)="row">
          <a :class="`ico-standalone has-tooltip ${row.item._showDetails ? 'not-collapsed': 'collapsed'}`" type="button"
             @click="row.toggleDetails">
            <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
          </a>
        </template>
        <template #row-details="row">
          <sys-entity :entity="row.item"/>
        </template>
      </b-table>

      <div class="container-fluid">
        <div class="row">
          <div class="col-12 mt-3">
            <pagination :page="entityItemPage" :paginationCommand="paginationCommand"/>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { Confidentiality, Direction } from '@/model/entities'
import useGroupStore from '@/admin/store/group'
import { GroupApi } from '@/shared/api/groupApi'
import Pagination from '@/shared/views/Pagination.vue'
import SysEntity from '@/admin/views/system/SysEntity.vue'
import Search from '@/shared/views/Search.vue'
import TableField from '@/shared/views/bootstrap_vue/types/TableField'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'

const entityItemPage = computed(() => useGroupStore().adminGroupsPage)
const entityItems = computed(() => useGroupStore().adminGroupsPage.content)
const groupStore = useGroupStore()
const fields: Array<TableField> = [
  { key: 'businessName', sortable: true },
  { key: 'entityIdentifier', sortable: true },
  { key: 'entityName', sortable: true },
  { key: 'channelName', sortable: true },
  { key: 'exchangeMode', label: 'Role', sortable: true },
  { key: 'active', label: 'Status' },
  { key: 'confidentiality', label: 'E2E encryption' },
  { key: 'more' }
]
const paginationCommand: PaginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => groupStore.fetchAdminGroups(paginationOptions),
  { sortBy: 'auditingEntity.createdDate' }
)

onMounted(() => {
  paginationCommand.fetch()
    .then()
})

const isEncrypted = (confidentiality: Confidentiality): boolean => {
  return confidentiality ? confidentiality === Confidentiality.LIMITED_HIGH : false
}

const filterEntityItems = (filterValue: string): void => {
  paginationCommand.options.filterBy = 'nameOrIdentifierInput'
  paginationCommand.options.filterValue = filterValue
  paginationCommand.fetch({ page: 0 })
    .then()
}

const prefilterFn = (searchBy: string): Promise<any> => {
  return GroupApi.searchAdminGroups(searchBy)
}

const sortingChanged = (key: string, isDesc: boolean): void => {
  paginationCommand.options.sortBy = getSortBy(key)
  paginationCommand.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
  paginationCommand.fetch({ page: 0 })
    .then()
}

function getSortBy(key: string) {
  let sortBy: string
  switch (key) {
    case 'businessName':
      sortBy = 'parent.identifier'
      break
    case 'entityIdentifier':
      sortBy = 'identifier'
      break
    case 'entityName':
      sortBy = 'name'
      break
    case 'channelName':
      sortBy = 'c.name'
      break
    case 'exchangeMode':
      sortBy = 'er.exchangeMode'
      break
    default:
      sortBy = 'auditingEntity.createdDate'
      break
  }

  return sortBy
}

</script>
