import { ExchangeRule, ExchangeRuleId, Group, Rels } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'
import { UploadFileApi } from '@/admin/api/uploadFileApi'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import ExchangeRuleDto from '@/model/ExchangeRuleDto'

export class ExchangeRuleApi {
  static getValidRecipients(senderEntityId: number): Promise<Array<Group>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.VALID_RECIPIENTS_GET)

    return HttpRequest.get(link, { senderEntityId })
  }

  static getExchangeRules(paginationOptions: PaginationOptions) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXCHANGE_RULES_GET)
    return HttpRequest.get(link, paginationOptions)
  }

  static update(exchangeRule: ExchangeRuleDto): Promise<ExchangeRule> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXCHANGE_RULES_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(exchangeRule) })
  }

  static bulkCreate(exchangeRule: ExchangeRuleDto[], forced = false): Promise<ExchangeRule[]> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXCHANGE_RULES_BULK_CREATE)
    return HttpRequest.post(link, { params: { forced }, body: JSON.stringify(exchangeRule) })
  }

  static uploadBulk(file: File, groupId: number): Promise<Array<string>> {
    const rootLinksStore = useRootLinksStore()
    const uploadLink = rootLinksStore.link(Rels.EXCHANGE_RULES_UPLOAD_BULK)
    return UploadFileApi.upload(file, groupId, uploadLink)
      .then(value => value as Array<string>)
  }

  static uploadParticipantsBulk(file: File, groupId: number, channelId: number): Promise<Array<string>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXCHANGE_RULES_PARTICIPANTS_UPLOAD_BULK)
    return UploadFileApi.upload(file, groupId, link, channelId)
      .then(value => value as Array<string>)
  }

  static delete(exchangeRuleId: ExchangeRuleId): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXCHANGE_RULES_DELETE)
    return HttpRequest.delete(link, { body: JSON.stringify(exchangeRuleId) })
  }

  static searchExchangeRules(channelId: number, filterValue: string) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXCHANGE_RULES_SEARCH)
    return HttpRequest.get(link, { channelId, filterValue })
  }
}
