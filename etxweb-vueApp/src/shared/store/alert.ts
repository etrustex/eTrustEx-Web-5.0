import { Alert, AlertType, Group, GroupType } from '@/model/entities'
import { defineStore } from 'pinia'
import { AlertsApi } from '@/shared/api/alertApi'
import { NewServerCertificateApi } from '@/shared/api/newServerCertificateApi'
import moment from 'moment'

const nameSpace = 'alert'

export const CERTIFICATE_EXPIRATION = 'Certificate expiration!'

interface State {
  alerts: Array<Alert>
}

const useAlertStore = defineStore(nameSpace, {
  state: (): State => ({
    alerts: []
  }),
  getters: {
    firstAlert: (state) => state.alerts.length > 0 ? state.alerts[0] : undefined
  },
  actions: {
    add(alert: Alert) {
      if (alert.group.identifier === GroupType.ROOT) {
        this.alerts.unshift(alert)
      } else {
        this.alerts.push(alert)
      }
    },
    remove(index: number) {
      this.alerts.splice(index, 1)
    },
    fetch(options: { businessId: number, retrieveInactive: boolean }) {
      this.clear()
      return AlertsApi.get(options.businessId, options.retrieveInactive)
        .then(alerts => {
          if (alerts && alerts.length) {
            alerts.forEach(alert => this.add(alert))
          }
        })
    },
    configureSysAdminAlerts() {
      this.clear()
      return NewServerCertificateApi.get().then(newServerCertificate => {
        const today = new Date()
        const expiration = moment(newServerCertificate.expirationDate)
        const daysToExpire = expiration.diff(today, 'days')

        if (daysToExpire < 60) {
          const alert = new Alert()
          alert.active = true
          alert.type = AlertType.WARNING
          alert.group = new Group()
          alert.title = CERTIFICATE_EXPIRATION
          alert.content = `The certificate used for asymmetric encryption of the symmetric key is about to expire:
                    <br/><br/>
                    certificate name: ${newServerCertificate.oldCertificateAlias}<br/>
                    Expiration date: ${newServerCertificate.expirationDate}<br/>
                    <br/>
                    Please ensure that the certificate is updated before it's expiration date.`
          this.add(alert)
          return alert
        }
        return null
      })
    },
    clear() {
      this.$reset()
    }
  }
})

export default useAlertStore
