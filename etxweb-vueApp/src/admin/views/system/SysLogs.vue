<template>
  <div class="row justify-content-end mt-3">
    <div class="col-md-12 col-lg-6">
      <div class="input-group">
        <input type="text"
               v-model="searchString"
               :placeholder="`Search by Package and/or Class`"
               v-on:keyup.enter="search"
               class="form-control">

        <div class="input-group-append">
          <a class="ico-standalone ms-2" v-on:click="search">
            <i aria-label="Perform Search" title="Perform Search" class="ico-search"></i>
          </a>
        </div>
      </div>
      <br/>
    </div>
    <div class="d-flex justify-content-end btns">
      <button type="button" class="btn btn-outline-secondary btn-ico justify-content-end" v-on:click="resetLogs">
        <span class="ico-standalone"><i aria-hidden="true" class="ico-sync-primary"></i></span>
        <span>Reset Configuration</span>
      </button>
    </div>
  </div>
    <div>
      <b-table
        :fields="fields"
        :items="logLevelItems"
        :no-local-sorting="true"
        class="mt-3"
      >
        <template #cell(loggerName)="data">
          <span class="text-break">{{ data.item.loggerName }}</span>
        </template>
        <template #cell(loggerLevel)="data">
          <span>
              <button v-for="level in LOG_LEVEL_ARRAY" :key="level"
                      type="button"
                      class="btn btn-primary btn-ico"
                      :disabled='level == data.item.loggerLevel' v-on:click='updateLevel(data.item.loggerName,level)'>
                <span>{{ level }}</span>
              </button>
          </span>
        </template>
      </b-table>

      <div class="container-fluid">
        <div class="row">
          <div class="col-12 mt-3">
            <pagination :page="logLevelItemPage" :paginationCommand="paginationCommand"/>
          </div>
        </div>
      </div>

    </div>
</template>
<script setup lang="ts">

import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import Pagination from '@/shared/views/Pagination.vue'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { computed, onMounted, ref, Ref } from 'vue'
import TableField from '../../../shared/views/bootstrap_vue/types/TableField'
import useLogLevelStore from '@/admin/store/logLevel'

const searchString: Ref<string> = ref('')
const logLevelStore = useLogLevelStore()
const logLevelItemPage = computed(() => logLevelStore.logLevelPage)
const logLevelItems = computed(() => logLevelStore.logLevels)
const fields: Array<TableField> = [
  { key: 'loggerName', sortable: false },
  { key: 'loggerLevel', sortable: false }
]
const paginationCommand: PaginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => logLevelStore.fetchLogLevels(paginationOptions),
  { }
)
const LOG_LEVEL_ARRAY = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'OFF', 'ALL']

onMounted(() => {
  paginationCommand.fetch()
    .then()
})
const search = (): void => {
  paginationCommand.options.filterValue = searchString.value
  paginationCommand.fetch()
    .then()
}
const resetLogs = (): void => {
  logLevelStore.resetConfig()
    .then(value => paginationCommand.fetch())
}
const updateLevel = (logger: string, level: string): void => {
  logLevelStore.updateLog(logger, level)
    .then(value => {
      paginationCommand.options.page = logLevelStore.logLevelPage.pageable.pageNumber
      paginationCommand.fetch()
    })
}
</script>
