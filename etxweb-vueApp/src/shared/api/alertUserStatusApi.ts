import { AlertUserStatus, AlertUserStatusSpec, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

export class AlertUserStatusApi {
  static get(businessId: number): Promise<Array<AlertUserStatus>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ALERT_USER_STATUS_GET)
    return HttpRequest.get(link, { businessId })
  }

  static create(alertUserStatusSpec: AlertUserStatusSpec): Promise<AlertUserStatus> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ALERT_USER_STATUS_CREATE)
    return HttpRequest.post(link, { body: JSON.stringify(alertUserStatusSpec) })
  }

  static update(alertUserStatus: AlertUserStatus): Promise<AlertUserStatus> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ALERT_USER_STATUS_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(this.toAlertSpec(alertUserStatus)) })
  }

  private static toAlertSpec(alertUserStatus: AlertUserStatus): AlertUserStatusSpec {
    const alertUserStatusSpec = new AlertUserStatusSpec()
    alertUserStatusSpec.groupId = alertUserStatus.alert.group.id
    alertUserStatusSpec.alertId = alertUserStatus.alert.id
    alertUserStatusSpec.status = alertUserStatus.status
    alertUserStatusSpec.userId = alertUserStatus.user.id

    return alertUserStatusSpec
  }
}
