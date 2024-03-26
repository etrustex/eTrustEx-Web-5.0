import urltemplate from 'url-template'
import { Link, Rels } from '@/model/entities'

/**
 *  retrieves a the link having the given rel from links and uses {@link #linkToUrl linkToUrl} to obtain the url
 *  from the url template and the provided parameters
 * @param links
 * @param rel enumerated value, defined in Rels.java class and available in entities.d.ts
 * @param params
 * @returns {string} the absolute target URL.
 */
export function extractRelUrl(links: Array<Link>, rel: Rels, params?: any): string {
  return linkToUrl(extractLink(links, rel), params)
}

/**
 * obtains an absolute URL from the relative URL template inside the provided link and the provided params
 * @param link
 * @param params
 * @returns {string} the absolute target URL.
 */
export function linkToUrl(link: Link, params: any = {}): string {
  return localUrlToFullyQualifiedUrl(link.href, params)
}

/**
 * adds the window.location.href to the href to obtain a fully qualified url
 * @param href
 * @param params
 */
export function localUrlToFullyQualifiedUrl(href: string, params: any = {}): string {
  const template = urltemplate.parse(href)
  const url = new URL(template.expand(params), window.location.href)
  return url.toString()
}

/**
 *
 * @param links
 * @param rel enumerated value, defined in Rels.java class and available in entities.d.ts
 * @returns {Link}
 */
export function extractLink(links: Array<Link>, rel: Rels): Link {
  return links.filter(link => link.rel === rel)[0]
}

/**
 * Retrieves the value for the given paramName, or null if the value cannot be retrieved
 * @param url
 * @param paramName
 */
export function getParamValue(url: string, paramName: string): string | null {
  if (!url) {
    return null
  }
  const template = urltemplate.parse(url)
  const targetUrl = new URL(template.expand({}))
  return targetUrl.searchParams.get(paramName)
}

export function addParameter(url: string, paramName: string, paramValue: string) {
  const template = urltemplate.parse(url)
  const targetUrl = new URL(template.expand({}))
  targetUrl.searchParams.append(paramName, paramValue)
  return targetUrl.href
}

export function addParameterToHash(url: string, paramName: string, paramValue: string) {
  let response: string
  if (url.match(/.*#.+/)) {
    response = `${url}&${paramName}=${paramValue}`
  } else if (url.match(/.*#/)) {
    response = `${url}?${paramName}=${paramValue}`
  } else {
    response = `${url}#?${paramName}=${paramValue}`
  }
  return response
}
