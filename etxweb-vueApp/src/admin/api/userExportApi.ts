import useRootLinksStore from '@/shared/store/rootLinks'
import { Rels } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'

export class UserExportApi {
  static downloadExportUsersAndFunctionalMailboxes(businessId: number) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXPORT_USERS)
    return HttpRequest.doDownload(link, { params: { groupId: businessId } })
  }
}
