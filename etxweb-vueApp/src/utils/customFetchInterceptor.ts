import { FetchInterceptor, FetchInterceptorResponse } from 'fetch-intercept'
import { ServerValidationErrors } from '@/model/entities'
import { isRequestToECAS } from '@/utils/openIdHelper'
import useOpenIdStore from '@/shared/store/openId'

import useValidationStore from '@/shared/store/validation'
import useLoaderStore from '@/shared/store/loader'
import router, { routeNames } from '@/router'

const ignoredByLoader: string[] = ['attachments', 'oauth2', 'search', 'messageSummarySearchItem']

export class CustomFetchInterceptor implements FetchInterceptor {
  request(url: string | Request, config: RequestInit): Promise<Array<any>> | Array<any> {
    const openIdStore = useOpenIdStore()

    const computedURL = typeof url === 'string' ? url : url.url
    showLoader(computedURL)

    if (url instanceof Request) {
      return [url]
    }

    if (isRequestToECAS(url, openIdStore.euLoginAccessTokenUrl)) {
      return [new Request(url, config)]
    }

    return openIdStore.getAccessToken({})
      .then((ticket: { token_type: string, access_token: string }) => {
        if (!config) {
          config = {} as RequestInit
        }

        config.headers = new Headers(config.headers)

        config.redirect = 'follow'
        config.credentials = 'omit'
        config.headers.set('Authorization', ticket.token_type + ' ' + openIdStore.signAccessToken(ticket.access_token))

        return [new Request(url, config)]
      })
  }

  requestError(error: any): Promise<any> {
    return Promise.reject(error)
  }

  response(response: FetchInterceptorResponse): FetchInterceptorResponse {
    hideLoader(response.url)

    if (response.status === 400) {
      response.json()
        .then((serverErrors: ServerValidationErrors) => {
          useValidationStore().setServerSideValidationErrors(serverErrors.errors)
        })
      return { request: response.request, ...Response.error(), status: response.status }
    }

    if (response.status === 403) {
      if (isResponseToFetchRootLinks(response)) {
        router.push({ name: routeNames.UNREGISTERED }).then()
      } else {
        router.push({ name: routeNames.FORBIDDEN }).then()
      }
      return { request: response.request, ...Response.error(), status: response.status }
    }

    if (response.status === 404) {
      router.push({ name: routeNames.NOT_FOUND }).then()
    }

    if (response.status === 401) {
      router.push({ name: routeNames.FORBIDDEN }).then()
    }

    return response
  }

  responseError(error: any): Promise<any> {
    return Promise.reject(error)
  }
}

function showLoader(url: string) {
  if (!ignoredByLoader.some(x => url.includes(x))) {
    const loaderStore = useLoaderStore()
    loaderStore.showLoader(url)
  }
}

function hideLoader(url: string) {
  if (!ignoredByLoader.some(x => url.includes(x))) {
    const loaderStore = useLoaderStore()
    loaderStore.hideLoader(url)
  }
}

function isResponseToFetchRootLinks(response: FetchInterceptorResponse) {
  const responseUrl = new URL(response.url)
  return responseUrl.pathname.endsWith(process.env.VUE_APP_BASE_API_PATH as string) ||
    responseUrl.pathname.endsWith(process.env.VUE_APP_SYSTEM_ADMIN_API_PATH as string)
}
