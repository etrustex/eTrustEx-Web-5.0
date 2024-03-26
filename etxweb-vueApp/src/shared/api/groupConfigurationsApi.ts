import { GroupConfiguration, GroupConfigurationSpec, Rels } from '@/model/entities'
import { extractLink } from '@/utils/linksHandler'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'

export class GroupConfigurationApi {
  static get<T extends GroupConfiguration<any>>(groupId: number, conf: string): Promise<T> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUP_CONFIGURATION_GET)
    return HttpRequest.get(link, { groupId, dtype: conf })
  }

  static getByGroup(groupId: number): Promise<Array<GroupConfiguration<any>>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.GROUP_CONFIGURATIONS_GET)
    return HttpRequest.get(link, { groupId })
  }

  static updateConfiguration<Q, T extends GroupConfiguration<Q>>(configuration: T, spec: GroupConfigurationSpec<Q>): Promise<T> {
    const link = extractLink(configuration.links, Rels.GROUP_CONFIGURATION_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(spec) })
  }
}
