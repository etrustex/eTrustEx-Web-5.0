import { ReservedCharacters, ReservedFilenames } from '@/model/entities'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'

const FOLDER_SEPARATOR = '/'
const ADD_COUNTER_REG_EXP = /(.+)(\.[^.]+$)/

export default class FileNameHelper {
  static trimNameIfWindowsCompatibleFileNameConfigured(fileName: string) {
    if (!useBusinessConfigurationStore().windowsCompatibleFilenamesGroupConfiguration.active) {
      return fileName
    }
    return fileName.split(FOLDER_SEPARATOR)
      .map(value => value.trim())
      .join(FOLDER_SEPARATOR)
  }

  static isWindowsCompatible(filePath: string) {
    const pathAndName = filePath.split(FOLDER_SEPARATOR)
    return !this.usesReservedCharacters(pathAndName) && !this.usesReservedNames(pathAndName)
  }

  static usesReservedCharacters(pathAndName: Array<string>) {
    return pathAndName.find(value => {
      for (const reservedCharacterKey in ReservedCharacters) {
        // @ts-ignore: TS7053
        if (ReservedCharacters[reservedCharacterKey] && value.includes(ReservedCharacters[reservedCharacterKey])) {
          return true
        }
      }
    }) !== undefined
  }

  static usesReservedNames(pathAndName: Array<string>) {
    return pathAndName.find(value => {
      for (const reservedNameKey in ReservedFilenames) {
        // @ts-ignore: TS7053
        if (ReservedFilenames[reservedNameKey]) {
          const upperCasedPathAndName = value.toUpperCase()
          // @ts-ignore: TS7053
          if (upperCasedPathAndName === ReservedFilenames[reservedNameKey] || upperCasedPathAndName.startsWith(ReservedFilenames[reservedNameKey] + '.')) {
            return true
          }
        }
      }
    }) !== undefined
  }

  static addCounterToFileName(fileName: string, counter: number) {
    if (counter === 0) {
      return fileName
    }
    const matches = ADD_COUNTER_REG_EXP.exec(fileName)
    if (matches && matches[2]) {
      return matches[1] + '_' + counter + matches[2] // there is at least a dot inside the file name (and not at the first character)
    } else {
      return fileName + '_' + counter // there are no dots or the only dot is the first character
    }
  }
}
