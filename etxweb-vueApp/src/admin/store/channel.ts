import { Channel, RestResponsePage } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { defineStore } from 'pinia'
import { ChannelApi } from '@/admin/api/channelApi'

const nameSpace = 'channel'

interface State {
  formVisible: boolean
  channel: Channel
  channelForm: Channel
  channels: Array<Channel>
  channelPage: RestResponsePage<Channel>
}

const useChannelStore = defineStore(nameSpace, {
  state: (): State => ({
    formVisible: false,
    channel: new Channel(),
    channelForm: new Channel(),
    channels: [],
    channelPage: new RestResponsePage<Channel>()
  }),
  actions: {
    fetchChannels(paginationOptions: PaginationOptions) {
      return ChannelApi.getChannels(paginationOptions)
        .then((channelsPage: RestResponsePage<Channel>) => {
          this.channels = channelsPage.content
          this.channelPage = channelsPage
        })
    },
    fetchChannel(channelId: number) {
      return ChannelApi.getChannel(channelId)
        .then((channel: Channel) => this.channel = channel)
    },
    setChannelForm(channel: Channel) {
      this.channelForm = channel
    },
    setFormVisible(visible: boolean) {
      this.formVisible = visible
    }
  }
})

export default useChannelStore
