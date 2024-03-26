import { CertificateUpdateDto, Rels, UpdateCertificateSpec } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'
import { RouteParamValue } from 'vue-router'

export class CertificateUpdateApi {
  static createLink(updateCertificateSpec: UpdateCertificateSpec): Promise<string> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CERTIFICATE_UPDATE)
    return HttpRequest.post(link, { body: JSON.stringify(updateCertificateSpec) })
  }

  static updateCompromisedMessages(certificateUpdateDto: CertificateUpdateDto): Promise<void> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CERTIFICATE_UPDATE_COMPROMISED_MESSAGES)
    return HttpRequest.put(link, { body: JSON.stringify(certificateUpdateDto) })
  }

  static isValidRedirectLink(groupId: number, groupIdentifier: string | RouteParamValue[], userId: number): Promise<boolean> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.CERTIFICATE_UPDATE_IS_VALID_REDIRECT_LINK)
    return HttpRequest.get(link, { groupId, groupIdentifier, userId })
  }
}
