import { MessageSummaryListItem, RestResponsePage } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { defineStore } from 'pinia'
import { MessageApi } from '@/admin/api/messageApi'

const nameSpace = 'messageSummaryListItem'

interface State {
  messageSummaryListItemPage: RestResponsePage<MessageSummaryListItem>
  messageSummaryListItems: Array<MessageSummaryListItem>
}

const useMessageSummaryListItemStore = defineStore(nameSpace, {
  state: (): State => ({
    messageSummaryListItemPage: {} as RestResponsePage<MessageSummaryListItem>,
    messageSummaryListItems: [],
  }),
  actions: {
    async fetchMessageDisplay(paginationOptions: PaginationOptions) {
      let messages = await MessageApi.getMessagesDisplay(paginationOptions)
      this.messageSummaryListItemPage = messages
      this.messageSummaryListItems = messages.content
    },
  },
})
export default useMessageSummaryListItemStore
