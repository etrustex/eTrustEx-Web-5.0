import { Rels, Template, TemplateSpec, TemplateType } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'

export class TemplateApi {
  static getTemplate(businessId: number, type: TemplateType): Promise<Template> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.TEMPLATES_GET)
    return HttpRequest.get(link, { businessId, type })
  }

  static getDefaultTemplate(type: TemplateType): Promise<Template> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.DEFAULT_TEMPLATES_GET)
    return HttpRequest.get(link, { type })
  }

  static saveOrUpdate(templateSpec: TemplateSpec): Promise<Template> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.TEMPLATES_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(templateSpec) })
  }
}
