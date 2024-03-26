import { AlertUserStatus, GroupType } from '@/model/entities'
import { defineStore } from 'pinia'
import { AlertUserStatusApi } from '@/shared/api/alertUserStatusApi'

const nameSpace = 'alertUserStatus'

interface State {
  alertUserStatuses: Array<AlertUserStatus>
  displayNotifications: boolean
}

const useAlertUserStatusStore = defineStore(nameSpace, {
  state: (): State => ({
    alertUserStatuses: [],
    displayNotifications: false
  }),
  actions: {
    add(alertUserStatus: AlertUserStatus) {
      if (alertUserStatus.alert.group.identifier === GroupType.ROOT) {
        this.alertUserStatuses.unshift(alertUserStatus)
      } else {
        this.alertUserStatuses.push(alertUserStatus)
      }
    },
    clear() {
      this.$reset()
    },
    setDisplayNotifications(display: boolean) {
      this.displayNotifications = display
    },
    fetch(groupId: number) {
      this.clear()
      AlertUserStatusApi.get(groupId).then(alertUserStatuses => {
        alertUserStatuses.forEach(alertUserStatus => {
          this.add(alertUserStatus)
        })
      })
    }
  }
})

export default useAlertUserStatusStore
