import { CertificateSpec, MessageSummary, MessageSummaryPage, Rels } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'
import useDialogStore, { DialogType } from '@/shared/store/dialog'

export class InboxApi {
  static fetchMessageSummariesPromise: Promise<MessageSummaryPage>

  static fetchMessageSummaries(paginationOptions: PaginationOptions): Promise<MessageSummaryPage> {
    const rootLinksStore = useRootLinksStore()
    const messageSummariesLink = rootLinksStore.link(Rels.MESSAGE_SUMMARIES_GET)
    return HttpRequest.get(messageSummariesLink, paginationOptions)
  }

  static async fetchMessageSummaryByMessageId(messageId: number | string, recipientEntityId: number): Promise<MessageSummary> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_SUMMARY_GET)
    return HttpRequest.get(link, { messageId, recipientEntityId }, true)
  }

  static async markUnread(messageIds: Array<number>, recipientEntityId: number) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_SUMMARIES_MARK_READ)
    return HttpRequest.put(link, { params: { recipientEntityId }, body: JSON.stringify(messageIds) })
  }

  static async countUnread(recipientEntityId: number): Promise<number> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_SUMMARIES_COUNT_UNREAD)
    return HttpRequest.get(link, { recipientEntityId })
  }

  static disable(compromisedCertificateSpec: CertificateSpec) {
    const dialogStore = useDialogStore()
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_SUMMARY_DISABLE)
    return HttpRequest.put(link, { body: JSON.stringify(compromisedCertificateSpec) })
      .catch((reason) => {
        dialogStore.show('', reason, DialogType.ERROR)
        throw new Error(reason)
      })
  }

  static countMessageSummariesToDisable(compromisedCertificateSpec: CertificateSpec) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.COUNT_MESSAGE_SUMMARIES_TO_DISABLE)
    return HttpRequest.put(link, { body: JSON.stringify(compromisedCertificateSpec) })
  }
}
