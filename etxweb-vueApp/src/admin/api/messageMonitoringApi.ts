import { MessageSummaryUserStatusItem, Page, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'

export class MessageMonitoringApi {
  static get(recipientEntityId: number, paginationOptions: PaginationOptions): Promise<Page<MessageSummaryUserStatusItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGES_MONITORING)

    return HttpRequest.get(link, { ...paginationOptions, recipientEntityId })
  }
}
