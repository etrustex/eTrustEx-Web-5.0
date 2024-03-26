import { RecipientPreferences, RecipientPreferencesSpec, Rels } from '@/model/entities'
import { HttpRequest } from '@/utils/httpRequest'
import { extractLink } from '@/utils/linksHandler'
import useRootLinksStore from '@/shared/store/rootLinks'

export class RecipientPreferencesApi {
  static get(recipientPreferencesId: number) {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.RECIPIENT_PREFERENCES_GET)
    return HttpRequest.get(link, { recipientPreferencesId })
  }

  static create(recipientPreferencesSpec: RecipientPreferencesSpec): Promise<RecipientPreferences> {
    const rootLinksStore = useRootLinksStore()
    const link = rootLinksStore.link(Rels.RECIPIENT_PREFERENCES_CREATE)
    return HttpRequest.post(link, { body: JSON.stringify(recipientPreferencesSpec) })
  }

  static update(updated: RecipientPreferences): Promise<RecipientPreferences> {
    const link = extractLink(updated.links, Rels.RECIPIENT_PREFERENCES_UPDATE)
    return HttpRequest.put(link, { body: JSON.stringify(RecipientPreferencesApi.toRecipientPreferencesSpec(updated)) })
  }

  private static toRecipientPreferencesSpec(recipientPreferences: RecipientPreferences): RecipientPreferencesSpec {
    const recipientPreferencesSpec: RecipientPreferencesSpec = new RecipientPreferencesSpec()

    recipientPreferencesSpec.confidentiality = recipientPreferences.confidentiality
    recipientPreferencesSpec.publicKey = recipientPreferences.publicKey
    recipientPreferencesSpec.publicKeyFileName = recipientPreferences.publicKeyFileName

    return recipientPreferencesSpec
  }
}
