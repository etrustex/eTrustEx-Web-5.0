import { GrantedAuthoritySpec, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

export class GrantedAuthorityApi {
  static create(grantedAuthoritySpec: GrantedAuthoritySpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GRANTED_AUTHORITY_CREATE)

    return HttpRequest.post(link, { body: JSON.stringify(grantedAuthoritySpec) })
  }

  static delete(grantedAuthoritySpec: GrantedAuthoritySpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GRANTED_AUTHORITY_DELETE)

    return HttpRequest.delete(link, { body: JSON.stringify(grantedAuthoritySpec) })
  }

  static updateGroup(grantedAuthoritySpec: Partial<GrantedAuthoritySpec>): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GRANTED_AUTHORITIES_UPDATE_GROUP)

    return HttpRequest.put(link, { body: JSON.stringify(grantedAuthoritySpec) })
  }

  static updateGroupBulk(grantedAuthoritySpec: Array<GrantedAuthoritySpec>): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GRANTED_AUTHORITIES_UPDATE_GROUP_BULK)

    return HttpRequest.put(link, { body: JSON.stringify(grantedAuthoritySpec) })
  }

  static acceptUserRegistrations(grantedAuthoritySpec: Array<GrantedAuthoritySpec>): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GRANTED_AUTHORITIES_CREATE_GROUP_BULK)

    return HttpRequest.post(link, { body: JSON.stringify(grantedAuthoritySpec) })
  }
}
