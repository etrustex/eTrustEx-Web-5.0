import { defineStore } from 'pinia'
import { Link, Rels } from '@/model/entities'

const nameSpace = 'rootLinks'

interface State {
  rootLinks: Map<Rels, Link>
}

const useRootLinksStore = defineStore(nameSpace, {
  state: (): State => ({
    rootLinks: new Map<Rels, Link>(),
  }),

  getters: {
    link: (state) => (rel: Rels) => state.rootLinks.get(rel) as Link,
  },

  actions: {
    async fetchRootLinks(): Promise<Map<Rels, Link>> {
      const newRootLinks = new Map<Rels, Link>()
      let response = await fetch(baseUrl() + process.env.VUE_APP_BASE_API_PATH)
      let rootLinks = await response.json()

      rootLinks.links.forEach((link: Link) => newRootLinks.set(link.rel, link))
      this.rootLinks = newRootLinks
      return this.rootLinks
    },

    async fetchAdminLinks(): Promise<Map<Rels, Link>> {
      let response = await fetch(baseUrl() + process.env.VUE_APP_SYSTEM_ADMIN_API_PATH)
      let rootLinks = await response.json()
      rootLinks.links.forEach((link: Link) => this.rootLinks.set(link.rel, link))
      return this.rootLinks
    },
  },
})

function addTrailingSlashIfMissing(url: string) {
  return url.endsWith('/') ? url : url + '/'
}

export const baseUrl = (): string => addTrailingSlashIfMissing(location.protocol + '//' + location.host + location.pathname)
export default useRootLinksStore
