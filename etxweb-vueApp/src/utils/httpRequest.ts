import { Link } from '@/model/entities'
import { getSaveStream } from '@/utils/saveStreamFactory'
import useSettingsStore from '@/shared/store/settings'
import { linkToUrl } from '@/utils/linksHandler'
import contentDisposition from 'content-disposition'

export class HttpRequest {
  static get(link: Link, params: any = {}, withPublicKey?: boolean) {
    return this.doRequest(link, 'GET', { params, withPublicKey })
  }

  static post(link: Link, options?: { params?: any, body?: string }) {
    return this.doRequest(link, 'POST', options)
  }

  static put(link: Link, options?: { params?: any, body?: string }) {
    return this.doRequest(link, 'PUT', options)
  }

  static delete(link: Link, options?: { params?: any, body?: string }) {
    return this.doRequest(link, 'DELETE', options)
  }

  static downloadJson(object: any, fileName: string) {
    const jsonString = JSON.stringify(object, null, 2)
    const blob = new Blob([jsonString], { type: 'application/json' })

    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = fileName
    link.click()

    // Cleanup
    URL.revokeObjectURL(link.href)
  }

  static async doDownload(link: Link, options?: { params?: any, withPublicKey?: boolean }) {
    const response = await this.doFetch(link, 'GET', options)
    if (!response.ok) {
      throw new Error(response.statusText)
    }
    const writer = await getSaveStream(this.getFileName(response))
    if (response.body) {
      await response.body.pipeTo(writer)
    }
  }

  static async doRequest(link: Link, method: string, options?: { params?: any, body?: string, withPublicKey?: boolean }) {
    const response = await this.doFetch(link, method, options)
    if (!response.ok) {
      throw new Error(response.statusText)
    }
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.indexOf('application/json') !== -1) {
      return response.json()
    } else {
      return response.text()
    }
  }

  private static doFetch(link: Link, method: string, options?: { params?: any, body?: string, withPublicKey?: boolean }) {
    const params = options ? options.params : undefined
    const body = options ? options.body : undefined
    const url = linkToUrl(link, params)

    let requestHeaders: HeadersInit = {}

    if (method === 'PUT' || method === 'POST' || method === 'DELETE') {
      requestHeaders = { ...requestHeaders, 'Content-Type': 'application/json' }
    }

    if (options && options.withPublicKey) {
      requestHeaders = { ...requestHeaders, 'CLIENT-PUBLIC-KEY': useSettingsStore().publicKeyDerBase64 }
    }

    const httpOptions: RequestInit = {
      method,
      headers: requestHeaders
    }

    httpOptions.body = body
    return fetch(url, httpOptions)
  }

  private static getFileName(response: Response) {
    const contentDispositionHeader = response.headers.get('Content-Disposition')

    try {
      return contentDispositionHeader ? contentDisposition.parse(contentDispositionHeader).parameters.filename : 'download'
    } catch (e) {
      return 'unnamed'
    }
  }
}
