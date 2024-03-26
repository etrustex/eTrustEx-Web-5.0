import { Rels, User, UserDetails, UserPreferencesSpec } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'

export class UserApi {
  static getUserDetails(): Promise<UserDetails> {
    const rootLinksStore = useRootLinksStore()
    const userProfileLink = rootLinksStore.link(Rels.USER_DETAILS_GET)

    return HttpRequest.get(userProfileLink)
  }

  static updateUserPreferences(userPreferencesSpec: UserPreferencesSpec): Promise<User> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PREFERENCES_UPDATE)

    return HttpRequest.put(link, { body: JSON.stringify(userPreferencesSpec) })
  }
}
