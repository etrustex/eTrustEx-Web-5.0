import { NewServerCertificate, Rels } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'

export class NewServerCertificateApi {
  static get(): Promise<NewServerCertificate> {
    const rootLinksStore = useRootLinksStore()
    return HttpRequest.get(rootLinksStore.link(Rels.NEW_CERTIFICATE))
  }

  static launchJob() {
    const rootLinksStore = useRootLinksStore()
    return HttpRequest.post(rootLinksStore.link(Rels.NEW_CERTIFICATE_JOB))
  }

  static resetUpdatedFlag() {
    const rootLinksStore = useRootLinksStore()
    return HttpRequest.post(rootLinksStore.link(Rels.NEW_CERTIFICATE_RESET_UPDATED_FLAG))
  }
}
