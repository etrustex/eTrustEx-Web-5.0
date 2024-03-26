import { AttachmentSpecWrapper, AttachmentStatus } from '@/model/attachmentDto'
import { getExtension, stringListContains } from '@/utils/stringListHelper'
import { formatBytes } from '@/utils/formatters'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import FileNameHelper from '@/utils/attachments/fileNameHelper'

export default class AttachmentStatusHelper {
  static getStatusClass(status: AttachmentStatus) {
    switch (status) {
      case AttachmentStatus.Pending:
        return 'bg-light'
      case AttachmentStatus.Uploading:
        return 'bg-primary'
      case AttachmentStatus.Error:
      case AttachmentStatus.Forbidden:
        return 'bg-danger'
      case AttachmentStatus.Success:
        return 'bg-success'
    }
  }

  static setStatusAndErrorTooltip(attachmentSpecWrapper: AttachmentSpecWrapper, fileName: string) {
    const extension = getExtension(fileName)
    const businessConfigurationStore = useBusinessConfigurationStore()

    if (extension && stringListContains(businessConfigurationStore.forbiddenExtensionsGroupConfiguration.stringValue, extension)) {
      // forbidden due to the extension
      attachmentSpecWrapper.errorTooltip = `The extension ${extension} of this file is forbidden.`
    } else if (businessConfigurationStore.windowsCompatibleFilenamesGroupConfiguration.active && !FileNameHelper.isWindowsCompatible(fileName)) {
      // forbidden due to the windows compatibility
      attachmentSpecWrapper.errorTooltip = `The file with name ${fileName} is not windows compatible and therefore forbidden.`
    } else if (!attachmentSpecWrapper.file.size) {
      // forbidden due to empty file
      attachmentSpecWrapper.errorTooltip = 'It is forbidden to add an empty file'
    } else if (businessConfigurationStore.fileSizeLimitationGroupConfiguration.active) {
      const maxBytes = businessConfigurationStore.fileSizeLimitationGroupConfiguration.integerValue * 1024 * 1024
      if (attachmentSpecWrapper.file.size > maxBytes) {
        // forbidden due to large file
        attachmentSpecWrapper.errorTooltip = `It is forbidden to add files larger than ${formatBytes(maxBytes)}`
      }
    }

    const reader = new FileReader()
    reader.onload = () => {
      if (!reader.result || !(reader.result as string).replace(/\s/g, '').length) {
        // forbidden due to empty file
        attachmentSpecWrapper.errorTooltip = 'It is forbidden to add an empty file'
        attachmentSpecWrapper.status = AttachmentStatus.Forbidden
      }
      reader.abort()
    }
    if (attachmentSpecWrapper.file.size > 1024) {
      reader.readAsBinaryString(attachmentSpecWrapper.file.slice(0, 1024))
    } else {
      reader.readAsBinaryString(attachmentSpecWrapper.file)
    }

    if (attachmentSpecWrapper.errorTooltip) {
      attachmentSpecWrapper.status = AttachmentStatus.Forbidden
    } else {
      attachmentSpecWrapper.status = AttachmentStatus.Pending
    }
  }
}
