import { Rels, UserGuideSpec } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'
import { UploadFileApi } from '@/admin/api/uploadFileApi'

export class UserGuideApi {
  static get(options: { role: string, groupType: string, businessId: number }): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_GUIDE_GET)

    return HttpRequest.doDownload(link, { params: options })
  }

  static getByBusinessId(businessId: number | string): Promise<Array<UserGuideSpec>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_GUIDES_GET)
    return HttpRequest.get(link, { businessId })
  }

  static upload(file: File, userGuideSpec: UserGuideSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_GUIDE_FILE)

    return UploadFileApi.uploadUserGuide(file, userGuideSpec, link)
  }

  static delete(userGuideSpec: UserGuideSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_GUIDE_DELETE)
    const params = {
      businessId: userGuideSpec.businessId,
      role: userGuideSpec.role,
      groupType: userGuideSpec.groupType
    }

    return HttpRequest.delete(link, { params })
  }
}
