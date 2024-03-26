import { LogLevelItem, RestResponsePage } from '@/model/entities'
import { defineStore } from 'pinia'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { LogLevelApi } from '@/admin/api/logLevelApi'

const nameSpace = 'logLevel'

interface State {
  logLevels: Array<LogLevelItem>,
  logLevelPage: RestResponsePage<LogLevelItem>
}

const useLogLevelStore = defineStore(nameSpace, {
  state: (): State => ({
    logLevels: [],
    logLevelPage: new RestResponsePage<LogLevelItem>()
  }),
  actions: {
    async fetchLogLevels(paginationOptions: PaginationOptions) {
      const logLevelPage = await LogLevelApi.get(paginationOptions)
      this.logLevels = logLevelPage.content
      this.logLevelPage = logLevelPage
    },
    async updateLog(logger: string, level: string) {
      return LogLevelApi.update(logger, level)
    },
    async resetConfig() {
      return LogLevelApi.resetConfig()
    }
  }
})

export default useLogLevelStore
