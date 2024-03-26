import { Channel, ChannelSpec, Rels, RestResponsePage, SearchItem } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useRootLinksStore from '@/shared/store/rootLinks'

export class ChannelApi {
  static getChannels(paginationOptions: PaginationOptions): Promise<RestResponsePage<Channel>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNELS_GET)
    return HttpRequest.get(link, paginationOptions)
  }

  static getChannelsByGroup(businessId: number, entityId: number): Promise<Array<Channel>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_GET_BY_GROUP)
    return HttpRequest.get(link, { businessId, entityId })
  }

  static getChannel(channelId: number): Promise<Channel> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_GET)
    return HttpRequest.get(link, { channelId })
  }

  static search(businessId?: number, name?: string): Promise<Array<SearchItem>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_SEARCH)
    return HttpRequest.get(link, { businessId, name })
  }

  static createChannel(channelSpec: ChannelSpec): Promise<Channel> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_CREATE)
    return HttpRequest.post(link, { body: JSON.stringify(channelSpec) })
  }

  static validateChannel(channelSpec: ChannelSpec): Promise<boolean> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_VALIDATE)
    return HttpRequest.post(link, { body: JSON.stringify(channelSpec) })
  }

  static updateChannel(channelId: number, channelSpec: ChannelSpec): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(channelSpec), params: { channelId } })
  }

  static deleteChannel(channelId: number): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CHANNEL_DELETE)
    return HttpRequest.delete(link, { params: { channelId } })
  }

  static downloadExportChannelsAndParticipants(businessId: number) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.EXPORT_CHANNELS)
    return HttpRequest.doDownload(link, { params: { groupId: businessId } })
  }
}
