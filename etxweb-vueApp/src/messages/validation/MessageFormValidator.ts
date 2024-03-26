import { AttachmentSpecWrapper } from '@/model/attachmentDto'
import { Confidentiality, Group } from '@/model/entities'
import { helpers, maxLength, required } from '@vuelidate/validators'
import { validCommissionEmail, validEmails } from '@/admin/views/shared/validation/EmailValidator'

const validateE2EConfigured = (value: Array<Group>, param: any) => {
  let noRecipientPreferencesConfigured = 0
  let limitedHighConfigured = 0
  value.forEach(group => {
    if (group.recipientPreferences && group.recipientPreferences.confidentiality) {
      if (group.recipientPreferences.confidentiality === Confidentiality.LIMITED_HIGH) {
        limitedHighConfigured++
      } else if (group.recipientPreferences.confidentiality === Confidentiality.PUBLIC && !group.recipientPreferences.publicKey) {
        noRecipientPreferencesConfigured++
      }
    } else {
      noRecipientPreferencesConfigured++
    }
  })

  if (param.e2eEnabled.value === true && noRecipientPreferencesConfigured > 0) {
    return false
  }

  return !(param.e2eEnabled.value === true && noRecipientPreferencesConfigured > 0 && limitedHighConfigured > 0) || (param.isDraft.value === true)
}

const isRequiredCustomTemplateField = (value: any, param: any) => {
  if (param.isTemplatePresent) {
    return value.length > 0
  }
  return true
}

function validateRequiredAttachment(attachments: Array<AttachmentSpecWrapper>) {
  return attachments.length > 0
}

function validateRequiredUploadAttachment(attachments: Array<AttachmentSpecWrapper>) {
  return attachments.filter(attachment => attachment.status !== 'Success').length <= 0
}

const requiredField = helpers.withMessage('This field is required', isRequiredCustomTemplateField)
const requiredSubject = helpers.withMessage('The subject is required', required)
const subjectMax = helpers.withMessage('The subject should not exceed 1000 characters', maxLength(1000))

const requiredRecipientGroup = helpers.withMessage('The recipient group is required', required)

const e2eConfigured = helpers.withMessage('At least one entity does not have end-to-end encryption configured', validateE2EConfigured)

const requiredAttachment = helpers.withMessage('It is required to add at least one attachment.', validateRequiredAttachment)

const requiredUploadAttachment = helpers.withMessage('All files must be successfully uploaded. Please upload pending files and remove files in error or forbidden.', validateRequiredUploadAttachment)

export const messageFormRules = {
  message: {
    subject: {
      requiredSubject,
      subjectMax
    }
  },
  messageRecipients: {
    requiredRecipientGroup,
    e2eConfigured
  },
  e2eEnabled: {
    e2eConfigured: true
  },
  attachmentSpecDtos: {
    requiredAttachment,
    requiredUploadAttachment
  },
  templateVariables: {
    emailRecipients: {
      validEmails,
      validCommissionEmail
    },
    reference: {
      required: requiredField
    },
    instrument: {
      required: requiredField
    },
    isTemplatePresent: {
      requiredTemplate: true
    }
  }

}
