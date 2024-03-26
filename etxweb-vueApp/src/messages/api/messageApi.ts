import { Link, Message, MessagePage, Rels, SendMessageRequestSpec, UpdateMessageRequestSpec } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'
import { extractLink } from '@/utils/linksHandler'
import useDialogStore, { DialogType } from '@/shared/store/dialog'

export class MessageApi {
  static createMessage(senderEntityId: number): Promise<Message> {
    const rootLinksStore = useRootLinksStore()
    const messageCreateLink = rootLinksStore.link(Rels.MESSAGE_CREATE)

    return HttpRequest.post(messageCreateLink, { params: { senderEntityId } })
  }

  static getMessageDetails(messageId: number, senderEntityId: number): Promise<Message> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_GET)
    return HttpRequest.get(link, { messageId, senderEntityId }, true)
  }

  static getReceipt(message: Message) {
    const link = extractLink(message.links, Rels.MESSAGE_RECEIPT_GET)
    return HttpRequest.doDownload(link, { withPublicKey: true })
  }

  static getMessages(paginationOptions: PaginationOptions): Promise<MessagePage> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGES_GET)

    return HttpRequest.get(link, paginationOptions)
  }

  static countUnreadSent(senderEntityId: number): Promise<number> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGES_COUNT_UNREAD_SENT)

    return HttpRequest.get(link, { senderEntityId }) as Promise<number>
  }

  static sendMessage(links: Array<Link>, sendMessageRequestSpec: SendMessageRequestSpec): Promise<Response> {
    const link = extractLink(links, Rels.SELF)

    const errorMessage = 'There was an unexpected error, please refresh the page and try again'
    return this.updateMessage(link, sendMessageRequestSpec, errorMessage)
  }

  static isReadyToSend(messageId: number): Promise<Response> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGE_IS_READY_TO_SEND_GET)

    return HttpRequest.get(link, { messageId })
  }

  static draftMessage(links: Array<Link>, updateMessageRequestSpec: UpdateMessageRequestSpec): Promise<Response> {
    const link = extractLink(links, Rels.MESSAGE_DRAFT)

    const errorMessage = 'The message is not available for edition anymore.'
    return this.updateMessage(link, updateMessageRequestSpec, errorMessage)
  }

  static async markUnread(messageIds: Array<number>, recipientEntityId: number) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.MESSAGES_MARK_READ)
    return HttpRequest.put(link, { params: { recipientEntityId }, body: JSON.stringify(messageIds) })
  }

  static deleteDraftMessage(message: Message): Promise<Response> {
    const link = extractLink(message.links, Rels.MESSAGE_DELETE)
    return HttpRequest.delete(link, { params: { messageId: message.id } })
  }

  private static updateMessage(link: Link, updateMessageRequestSpec: UpdateMessageRequestSpec | SendMessageRequestSpec, errorMessage: string) {
    const dialogStore = useDialogStore()
    return HttpRequest.put(link, { body: JSON.stringify(updateMessageRequestSpec) })
      .catch((reason) => {
        dialogStore.show('', errorMessage, DialogType.ERROR)
        throw new Error(reason)
      })
  }
}
