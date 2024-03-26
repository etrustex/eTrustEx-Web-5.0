import {
  CreateUserProfileSpec,
  DeleteUserProfileSpec,
  NotificationEmailSpec,
  Rels,
  RestResponsePage,
  RoleName,
  SearchItem,
  SysDeleteUserProfileSpec,
  UserListItem,
  UserProfile,
  UserProfileSpec,
} from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { UploadFileApi } from '@/admin/api/uploadFileApi'

export class UserProfileApi {
  static create(userProfileSpec: CreateUserProfileSpec): Promise<UserProfile> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_CREATE)

    return HttpRequest.post(link, { body: JSON.stringify(userProfileSpec) })
  }

  static update(userProfileSpec: UserProfileSpec): Promise<UserProfile> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_UPDATE)

    return HttpRequest.put(link, { body: JSON.stringify(userProfileSpec) })
  }

  static fetchEuLoginInfo(groupId: number, mailOrEcasId: string): Promise<UserProfileSpec> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_EULOGIN)
    return HttpRequest.get(link, { groupId, mailOrEcasId })
  }

  static fetchUserProfileInfo(groupId: number, ecasId: string): Promise<UserProfileSpec> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_INFO)
    return HttpRequest.get(link, { groupId, ecasId })
  }

  static getListItems(paginationOptions: PaginationOptions, isSysAdmin: boolean): Promise<RestResponsePage<UserListItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = isSysAdmin && (paginationOptions.role === RoleName.SYS_ADMIN || paginationOptions.role === RoleName.OFFICIAL_IN_CHARGE)
      ? rootLinksStore.link(Rels.SYS_ADMIN_LIST_ITEMS_GET)
      : rootLinksStore.link(Rels.USER_LIST_ITEMS_GET)
    return HttpRequest.get(link, { ...paginationOptions })
  }

  static search(groupId: number, euLoginOrName: string, isRegistrationRequest: boolean, isAllUsers: boolean, roleName?: RoleName): Promise<Array<SearchItem>> {
    const rootLinksStore = useRootLinksStore()

    if (isAllUsers) {
      const link = rootLinksStore.link(Rels.SYS_ADMIN_PROFILES_SEARCH)
      return HttpRequest.get(link, { groupId, euLoginOrName, isRegistrationRequest, roleName, isAllUsers })
    } else {
      const link = rootLinksStore.link(Rels.USER_PROFILES_SEARCH)
      return HttpRequest.get(link, { groupId, euLoginOrName, isRegistrationRequest, roleName })
    }
  }

  static getNotificationEmails(groupId: number): Promise<Set<string>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_NOTIFICATION_EMAILS)

    return HttpRequest.get(link, { groupId })
  }

  static sendNotificationEmails(notificationEmailSpec: NotificationEmailSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_SEND_NOTIFICATION_EMAILS)

    return HttpRequest.post(link, { body: JSON.stringify(notificationEmailSpec) })
  }

  static delete(deleteUserProfileSpec: DeleteUserProfileSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.USER_PROFILE_DELETE)

    return HttpRequest.delete(link, { body: JSON.stringify(deleteUserProfileSpec) })
  }

  static sysDelete(deleteUserProfileSpec: SysDeleteUserProfileSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.SYS_ADMIN_PROFILE_DELETE)

    return HttpRequest.delete(link, { body: JSON.stringify(deleteUserProfileSpec) })
  }

  static uploadBulk(file: File, groupId: number): Promise<Array<string>> {
    const rootLinksStore = useRootLinksStore()
    const uploadLink = rootLinksStore.link(Rels.USER_PROFILE_UPLOAD_BULK)
    return UploadFileApi.upload(file, groupId, uploadLink)
      .then(value => value as Array<string>)
  }
}
