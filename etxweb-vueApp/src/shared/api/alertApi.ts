import { Alert as EUAlert, AlertSpec, Rels } from '@/model/entities'
import useRootLinksStore from '@/shared/store/rootLinks'
import { HttpRequest } from '@/utils/httpRequest'

export class AlertsApi {
  static get(businessId: number, retrieveInactive?: boolean): Promise<Array<EUAlert>> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ALERT_GET)
    return HttpRequest.get(link, { businessId, retrieveInactive })
  }

  static create(alert: EUAlert): Promise<EUAlert> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ALERT_CREATE)
    return HttpRequest.post(link, { body: JSON.stringify(this.toAlertSpec(alert)) })
  }

  static update(alert: EUAlert): Promise<EUAlert> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.ALERT_CREATE)
    return HttpRequest.put(link, { body: JSON.stringify(this.toAlertSpec(alert)) })
  }

  private static toAlertSpec(alert: EUAlert): AlertSpec {
    const alertSpec = new AlertSpec()
    alertSpec.active = alert.active
    alertSpec.groupId = alert.group.id
    alertSpec.content = alert.content
    alertSpec.title = alert.title
    alertSpec.type = alert.type
    alertSpec.startDate = alert.startDate
    alertSpec.endDate = alert.endDate

    return alertSpec
  }
}
