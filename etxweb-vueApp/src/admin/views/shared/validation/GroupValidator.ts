import { helpers, maxLength, required } from '@vuelidate/validators'
import { emailsLength, emailsMax, emailsNumber, uniqueEmail, uniqueEmails, validEmails } from '@/admin/views/shared/validation/EmailValidator'
import { ExchangeMode } from '@/model/entities'

const groupIdentifierRegex = /^[a-zA-Z0-9-_]+$/
const shouldContain = (value: string) => groupIdentifierRegex.test(value)

export const newMessageNotification = {
  newMessageNotificationEmailAddresses: {
    validEmails,
    uniqueEmails,
    emailsLength,
    emailsNumber
  }
}

export const statusNotification = {
  statusNotificationEmailAddress: {
    validEmail: validEmails,
    emailsLength,
    uniqueEmail
  }
}

export const retentionWarningNotification = {
  retentionWarningNotificationEmailAddresses: {
    validEmails,
    uniqueEmails,
    emailsLength,
    emailsMax
  }
}

function validateChannelId(value: number, params: any) {
  if (!params.addToChannel) {
    return true
  }
  return value !== undefined
}

function validateEntityRole(value: ExchangeMode, params: any) {
  if (!params.addToChannel) {
    return true
  }
  return value.length > 0
}

export const registrationRequestNotification = {
  registrationRequestNotificationEmailAddresses: {
    validEmails,
    uniqueEmails,
    emailsLength,
    emailsMax
  }
}
export const groupRules = {
  identifier: {
    requiredIdentifier: helpers.withMessage('The identifier is required', required),
    identifierMax: helpers.withMessage('The Identifier should not exceed 255 characters', maxLength(255)),
    validIdentifier: helpers.withMessage('The Identifier can only contain letters, digits, hyphens and underscores', shouldContain)
  },
  displayName: {
    requiredDisplayName: helpers.withMessage('The Name is required', required),
    displayNameMax: helpers.withMessage('The Name should not exceed 100 characters', maxLength(100))
  },
  description: {
    requiredDescription: helpers.withMessage('The Description is required', required),
    descriptionMax: helpers.withMessage('The Description should not exceed 255 characters', maxLength(255))
  },
  channelId: {
    requiredChannelId: helpers.withMessage('The channel is required', validateChannelId)
  },
  entityRole: {
    requiredEntityRole: helpers.withMessage('The role is required', validateEntityRole)
  }

}
