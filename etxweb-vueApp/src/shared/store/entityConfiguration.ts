import { GroupConfiguration, RetentionPolicyEntityConfiguration } from '@/model/entities'
import { defineStore } from 'pinia'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'

const nameSpace = 'entityConfiguration'
const toTitleCase = (txt: string) => txt[0].toLowerCase() + txt.substring(1)

interface State {
  retentionPolicyEntityConfiguration: RetentionPolicyEntityConfiguration
}

const useEntityConfigurationStore = defineStore(nameSpace, {
  state: (): State => ({
    retentionPolicyEntityConfiguration: new RetentionPolicyEntityConfiguration()
  }),
  actions: {
    async fetchCurrentEntityConfigurations(groupId: number) {
      const groupConfigurations = await GroupConfigurationApi.getByGroup(groupId)

      if (groupConfigurations.length) {
        groupConfigurations.forEach(conf => {
          this.updateValue(conf)
        })
      }

      return groupConfigurations
    },
    updateValue(conf: GroupConfiguration<unknown>) {
      // @ts-ignore: TS7053
      this[toTitleCase(conf.dtype)] = conf
    }
  }

})

export default useEntityConfigurationStore
