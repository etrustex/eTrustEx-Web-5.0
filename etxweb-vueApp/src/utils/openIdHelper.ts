import { addParameter, getParamValue } from '@/utils/linksHandler'
import { matchesFirefox } from '@/utils/browserUtils'

export function getIdToken(): null | string {
  const idToken = getIdTokenFromUrl(window.location.href)
  if (idToken) {
    return idToken
  }
  return getIdTokenFromHash(window.location.hash)
}

export function getIdTokenFromUrl(url: string): null | string {
  return (getParamValue(url, 'id_token'))
}

export function getIdTokenFromHash(hash: string): null | string {
  const idTokenSearchString = 'id_token='
  hash = hash.replace('#/', '').replace('#', '')
  const params = hash.split('&')
  const idTokenParam = params.find(value => value.indexOf(idTokenSearchString) > -1)
  if (idTokenParam === undefined) {
    return null
  }
  return idTokenParam.slice(idTokenSearchString.length, idTokenParam.length)
}

export const developmentRedirectUrlParamName = 'developmentRedirectUrl'

export function getDevelopmentLoginRedirectUrl(url: string) {
  const param = getParamValue(url, developmentRedirectUrlParamName)
  if (param) {
    return param
  }
  return null
}

export const developmentPublicKeyUrlParamName = 'developmentPublicKey'

export function getDevelopmentPublicKey(url: string) {
  const param = getParamValue(url, developmentPublicKeyUrlParamName)
  if (param) {
    return param
  }
  return null
}

export function isRequestToECAS(request: string | Request, tokenRequestUrl: string): boolean {
  const target = (request as Request).url ? (request as Request).url : request as string
  return target.startsWith(tokenRequestUrl)
}

export function addTicket(url: string, ticket: string): string {
  return addParameter(url, 'ticket', ticket)
}

export function isLocalhost(href: string): boolean {
  try {
    const url = new URL(href)
    return url.hostname === 'localhost' || url.hostname === '127.0.0.1'
  } catch (e) {
    return false
  }
}

export function storeItem(key: string, value: string) {
  getStorageFunction().setItem(key, value)
}

export function retrieveItem(key: string) {
  return getStorageFunction().getItem(key)
}

export function retrieveAndCleanupItem(key: string) {
  const item = retrieveItem(key)
  if (item) {
    getStorageFunction().removeItem(key)
  }
  return item
}

function getStorageFunction() {
  // Workaround for Firefox sessionStorage being wiped out on redirection to the eulogin server
  return matchesFirefox ? localStorage : sessionStorage
}
