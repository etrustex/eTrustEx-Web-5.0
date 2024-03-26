export const matchesFirefox = window.navigator.userAgent.match(/Firefox\/(\d+)\./)
export const matchesChrome = window.navigator.userAgent.match(/Chrome\/(\d+)\./)
export const matchesEdge = window.navigator.userAgent.match(/Edge\/(\d+)\./)
export const firefoxVersion = matchesFirefox ? Number(matchesFirefox[1]) : 0

export const SUPPORTED_CHROMIUM = 86
export const SUPPORTED_FIREFOX = 111
export function isSupportedBrowser() {
  const chromeVersion = matchesChrome ? Number(matchesChrome[1]) : 0
  const edgeVersion = matchesEdge ? Number(matchesEdge[1]) : 0
  const firefoxVersion = matchesFirefox ? Number(matchesFirefox[1]) : 0

  const otherBrowser = !matchesFirefox && !matchesEdge && !matchesChrome

  return chromeVersion >= SUPPORTED_CHROMIUM || edgeVersion >= SUPPORTED_CHROMIUM || firefoxVersion >= SUPPORTED_FIREFOX || otherBrowser
}
