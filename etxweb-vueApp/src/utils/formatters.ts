import moment from 'moment'
import { Message } from '@/model/entities'

export function formatBytes(bytes: number, rounded = false): string {
  if (bytes === 0) {
    return '0 Bytes'
  }
  const unitIndex: number = Math.floor(Math.log(bytes) / Math.log(1024))
  const value: number = (bytes / Math.pow(1024, unitIndex))
  const valueWithoutUnit = rounded
    ? `${Math.round(value)}`
    : value.toFixed(2)
      .replace(/\.0+$/, '')
  return `${valueWithoutUnit} ${['Bytes', 'kB', 'MB', 'GB'][unitIndex]}`
}

export function parseLocalDateTime(date: Date) {
  if (date) {
    return toDateTime(date)
  } else {
    return ''
  }
}

export function formatDate(date: Date | string): string {
  const isCurrentDay: boolean = moment(date)
    .isSame(new Date(), 'day')
  const dateFormat: string = isCurrentDay ? 'HH:mm:ss' : 'DD/MM/YYYY HH:mm:ss'
  return moment(date)
    .format(dateFormat)
}

export function toDateTime(date: Date) {
  const dateFormat = 'DD/MM/YYYY HH:mm:ss'
  return date
    ? moment(date)
      .format(dateFormat)
    : ''
}

export function toDate(date: Date) {
  const dateFormat = 'DD/MM/YYYY'
  return moment(date)
    .format(dateFormat)
}

export function formatZipFileName(message: Message): string {
  if (message.templateVariables) {
    let { zipFileName } = JSON.parse(message.templateVariables)
    zipFileName = zipFileName || message.senderGroup.name
    return formatDateTime(message.sentOn) + '_' + zipFileName + '.zip'
  }

  return formatDateTime(message.sentOn) + '_' + message.senderGroup.name + '.zip'
}

export function formatDateTime(date: Date) {
  const dateFormat = 'YYYY-MM-DD_HH-mm-ss'
  return moment(date)
    .format(dateFormat)
}

export function formatPopupName(name: string) {
  const formatted = `<strong class="text-break">${name.substring(0, 110)}</strong>`
  const ellipsis = formatted.length > 110 ? '<strong>...</strong>' : ''
  return formatted + ellipsis
}

export function toTitleCase(str: string) {
  return str.toLowerCase()
    .split(' ')
    .map((word) => word.replace(word[0], word[0].toUpperCase()))
    .join(' ')
}
