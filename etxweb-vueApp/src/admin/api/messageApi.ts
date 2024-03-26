import { MessageSummaryListItem, Rels, RestResponsePage, SearchItem } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

export class MessageApi {
  static getMessagesDisplay(paginationOptions: PaginationOptions): Promise<RestResponsePage<MessageSummaryListItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGES_DISPLAY)

    return HttpRequest.get(link, { ...paginationOptions })
  }

  static search(businessId: number, messageIdOrSubject: string): Promise<Array<SearchItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGES_SEARCH)

    return HttpRequest.get(link, { businessId, messageIdOrSubject })
  }

  static updateActiveStatus(businessId: number, messageId: number, recipientIdentifier: string, isActive: boolean) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_SUMMARY_UPDATE_ACTIVE_STATUS)

    return HttpRequest.put(link, { params: { businessId, messageId, recipientIdentifier, isActive } })
  }

  static updateMessageSummariesActiveStatus(businessId: number, messageSummaryListItems: Array<MessageSummaryListItem>, activate: boolean) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_SUMMARIES_UPDATE)

    return HttpRequest.put(link, { body: JSON.stringify(messageSummaryListItems), params: { businessId, activate } })
  }
}
