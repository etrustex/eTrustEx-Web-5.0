import useRootLinksStore from '@/shared/store/rootLinks'
import { BaseUserRegistrationRequestSpec, Rels, UserRegistrationRequest, UserRegistrationRequestSpec } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'

export class UserRegistrationRequestApi {
  static create(userRegistrationRequestSpec: UserRegistrationRequestSpec): Promise<UserRegistrationRequest> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_REGISTRATION_REQUEST_CREATE)
    return HttpRequest.post(link, { body: JSON.stringify(userRegistrationRequestSpec) })
  }

  static delete(userRegistrationRequestSpec: BaseUserRegistrationRequestSpec | Array<BaseUserRegistrationRequestSpec>): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_REGISTRATION_REQUEST_DELETE)
    return HttpRequest.delete(link, { body: JSON.stringify(userRegistrationRequestSpec) })
  }

  static update(userRegistrationRequestSpec: UserRegistrationRequestSpec): Promise<UserRegistrationRequest> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_REGISTRATION_REQUEST_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(userRegistrationRequestSpec) })
  }
}
