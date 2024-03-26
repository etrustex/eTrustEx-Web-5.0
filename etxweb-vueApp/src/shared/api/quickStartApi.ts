import { QuickStartSpec, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

export class QuickStartApi {
  static create(spec: QuickStartSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.QUICK_START)
    return HttpRequest.post(link, { body: JSON.stringify(spec) })
  }
}
