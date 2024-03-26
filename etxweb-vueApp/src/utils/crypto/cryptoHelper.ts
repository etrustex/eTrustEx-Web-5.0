import { Confidentiality, MessageSummary } from '@/model/entities'

export function isBase64Encoded(text: string) {
  const base64regex = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/

  return base64regex.test(text)
}

export function isEncrypted(messageSummary: MessageSummary) {
  return messageSummary.confidentiality && messageSummary.confidentiality !== Confidentiality.PUBLIC
}

