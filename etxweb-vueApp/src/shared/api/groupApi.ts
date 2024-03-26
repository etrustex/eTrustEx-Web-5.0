import { EntityItem, Group, GroupSearchItem, GroupSpec, Link, PageImpl, Rels, RestResponsePage } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { extractLink } from '@/utils/linksHandler'
import { UploadFileApi } from '@/admin/api/uploadFileApi'

export class GroupApi {
  static getGroups(paginationOptions: PaginationOptions): Promise<RestResponsePage<Group>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUPS_GET)
    return HttpRequest.get(link, paginationOptions)
  }

  static getAdminGroups(paginationOptions: PaginationOptions): Promise<PageImpl<EntityItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUPS_ADMIN_GET)
    return HttpRequest.get(link, paginationOptions)
  }

  static get(groupId: number): Promise<Group> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUP_GET)
    return HttpRequest.get(link, { groupId })
  }

  static getRoot(): Promise<Group> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ROOT_GROUP_GET)
    return HttpRequest.get(link)
  }

  static searchAdminGroups(filterValue: string) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUPS_ADMIN_SEARCH)
    return HttpRequest.get(link, { filterValue })
  }

  static create(spec: GroupSpec): Promise<Group> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUP_CREATE)
    return HttpRequest.post(link, { body: JSON.stringify(spec) })
  }

  static isValid(spec: GroupSpec): Promise<boolean> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.IS_VALID_GROUP)
    return HttpRequest.post(link, { body: JSON.stringify(spec) })
  }

  static update(spec: GroupSpec, links: Array<Link>) {
    const link = extractLink(links, Rels.GROUP_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(spec) })
  }

  static search(businessId?: number, groupIdOrName?: string): Promise<Array<GroupSearchItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUPS_SEARCH)
    return HttpRequest.get(link, { businessId, groupIdOrName })
  }

  static isBusinessEmpty(groupId: number): Promise<boolean> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.IS_BUSINESS_EMPTY)
    return HttpRequest.get(link, { groupId })
  }

  static deleteBusiness(groupId: number): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.BUSINESS_DELETE)
    return HttpRequest.delete(link, { params: { groupId } })
  }

  static delete(groupId: number): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUP_DELETE)
    return HttpRequest.delete(link, { params: { groupId } })
  }

  static isEntityEmpty(groupId: number): Promise<boolean> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.IS_GROUP_EMPTY)
    return HttpRequest.get(link, { groupId })
  }

  static cancelBusinessDeletion(groupId: number): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CANCEL_BUSINESS_DELETE)
    return HttpRequest.put(link, { params: { groupId } })
  }

  static confirmBusinessDeletion(groupId: number): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CONFIRM_BUSINESS_DELETE)
    return HttpRequest.delete(link, { params: { groupId } })
  }

  static uploadBulk(file: File, groupId: number): Promise<Array<string>> {
    const rootLinksStore = useRootLinksStore()
    const uploadLink = rootLinksStore.link(Rels.GROUP_UPLOAD_BULK)
    return UploadFileApi.upload(file, groupId, uploadLink)
      .then(value => value as Array<string>)
  }
}
