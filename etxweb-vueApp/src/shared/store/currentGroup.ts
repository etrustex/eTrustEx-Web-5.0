import { Group, GroupType, TemplateType } from '@/model/entities'
import { defineStore } from 'pinia'
import useOpenIdStore from '@/shared/store/openId'
import useAlertStore from '@/shared/store/alert'
import useAlertUserStatusStore from '@/shared/store/alertUserStatus'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import useDialogStore from '@/shared/store/dialog'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import { GroupApi } from '@/shared/api/groupApi'

const nameSpace = 'currentGroup'

export function isBusinessChanged(first: Group, second: Group): boolean {
  if (first && second) {
    return first.businessId !== second.businessId
  } else {
    return true
  }
}

interface State {
  group: Group
  recipients: Array<Group>
}

const useCurrentGroupStore = defineStore(nameSpace, {
  state: (): State => ({
    group: {} as Group,
    recipients: []
  }),
  getters: {
    currentGroup: (state) => state.group,
    customFormTemplate: (state) => state.group.parent?.templates
      ? state.group.parent.templates.find(t => t.type === TemplateType.NEW_MESSAGE_FORM)?.content || ''
      : ''
  },
  actions: {
    setGroup(newGroup: Group) {
      const businessConfigurationStore = useBusinessConfigurationStore()
      const previousGroup = this.group

      if (newGroup.type == GroupType.ENTITY && isBusinessChanged(newGroup, previousGroup)) {
        businessConfigurationStore.fetchCurrentBusinessConfigurations(newGroup.businessId)
          .then(() =>
            useDialogStore().displaySplashScreen(
              useOpenIdStore().authenticated,
              businessConfigurationStore.splashScreenGroupConfiguration)
          )
        useAlertStore().fetch({ businessId: newGroup.businessId, retrieveInactive: false })
          .then(() => useAlertUserStatusStore().fetch(newGroup.businessId))
      }

      this.group = { ...newGroup }
    },

    changeGroup(groupId: number) {
      return GroupApi.get(groupId).then((group: Group) => {
        this.setGroup(group)
        if (GroupType.ENTITY === group.type) {
          return ExchangeRuleApi.getValidRecipients(groupId)
            .then(value => {
              this.recipients = value
              return group
            })
        }
        return group
      })
    },

    systemAdmin() {
      return GroupApi.getRoot().then((group: Group) => {
        this.setGroup(group)
        useAlertStore().configureSysAdminAlerts().then()
        return group
      })
    }
  }
})

export default useCurrentGroupStore
