import { DbStringListsSeparators } from '@/model/entities'

const splitSymbols = /[\s,;]+/

export function formatListForInput(inputList: string): string {
  if (!inputList) {
    return ''
  }
  return inputList.trim()
    .split(splitSymbols)
    .map(text => text.trim())
    .filter(text => text)
    .join(DbStringListsSeparators.DB_STRING_LIST_SEPARATOR)
}

export function formatListForDisplay(inputList: string): string {
  if (!inputList) {
    return ''
  }
  return inputList.trim()
    .split(splitSymbols)
    .join(DbStringListsSeparators.DISPLAY_STRING_LIST_SEPARATOR)
}

export function formatSetForDisplay(items: Set<string>): string {
  return Array.from(items.values()).join(DbStringListsSeparators.DISPLAY_STRING_LIST_SEPARATOR)
}

export function formatSetForInput(items: Set<string>): string {
  if (items.size) {
    return Array.from(items).join(DbStringListsSeparators.DB_STRING_LIST_SEPARATOR)
  }
  return ''
}

export function toArray(items: string) {
  return formatListForInput(items).split(DbStringListsSeparators.DB_STRING_LIST_SEPARATOR)
    .filter(value => value)
}

export function getExtension(fileName: string) {
  const regExp = /(\.[a-zA-Z0-9.]+)$/
  const results = regExp.exec(fileName)
  return results && results[1] ? results[1].substring(1).toUpperCase() : null
}

export function stringListContains(stringList: string, value: string) {
  if (!stringList) {
    return false
  }
  const found = stringList
    .split(splitSymbols)
    .find(item => matchRule(value, item))
  return found !== undefined
}

function matchRule(stringParam: string, rule: string) {
  const escapeRegex = (s: string) => s.replace(/([.*+?^=!:${}()|[\]/\\])/g, '\\$1')
  return new RegExp('^' + rule.split('*').map(escapeRegex)
    .join('.*') + '$').test(stringParam)
}
