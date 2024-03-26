import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { LogLevelItem, Page, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

export class LogLevelApi {
  static get(paginationOptions: PaginationOptions): Promise<Page<LogLevelItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.LOG_LEVEL_SEARCH)

    return HttpRequest.get(link, { ...paginationOptions })
  }

  static update(logger: string, level: string) : Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.LOG_LEVEL_UPDATE)

    return HttpRequest.put(link, { params: { logger, level } })
  }

  static resetConfig() : Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.LOG_LEVEL_RESET)

    return HttpRequest.put(link, {})
  }
}
