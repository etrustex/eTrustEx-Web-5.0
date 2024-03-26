import { Link, UserGuideSpec } from '@/model/entities'
import { linkToUrl } from '@/utils/linksHandler'

export class UploadFileApi {
  static async upload(file: File, groupId: number, link: Link, channelId?: number) {
    const params = { businessId: groupId, channelId }
    const headers = {
      'Content-Type': 'application/json'
    }

    const requestInit: RequestInit = {
      method: 'PUT',
      redirect: 'follow',
      credentials: 'omit',
      body: file,
      headers
    }
    const requestURL = linkToUrl(link, params)
    return fetch(requestURL, requestInit)
      .then(response => response.json())
  }

  static async uploadUserGuide(file: File, userGuidSpec: UserGuideSpec, link: Link) {
    const params = {
      fileName: userGuidSpec.filename,
      businessId: userGuidSpec.businessId,
      role: userGuidSpec.role,
      groupType: userGuidSpec.groupType
    }
    const headers = {
      'Content-Type': 'application/json'
    }

    const requestInit: RequestInit = {
      method: 'PUT',
      redirect: 'follow',
      credentials: 'omit',
      body: file,
      headers
    }
    const requestURL = linkToUrl(link, params)
    return fetch(requestURL, requestInit)
      .then(response => response.json())
  }
}
