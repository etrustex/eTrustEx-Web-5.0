import { Environment, getEnvironment } from '@/model/environment'
import { Rels, RsaPublicKeyDto } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'

export class SettingsApi {
  static getEnvironment(): Promise<Environment> {
    const contextPath = window.location.pathname.replace(/\/?$/, '/')

    return fetch(window.location.origin + contextPath + 'settings/environment')
      .then(response => response.text())
      .then((env) => getEnvironment(env))
  }

  static getPublicKey(): Promise<RsaPublicKeyDto> {
    const link = useRootLinksStore().link(Rels.SETTINGS_PUBLIC_KEY_GET)
    return HttpRequest.get(link)
  }
}
