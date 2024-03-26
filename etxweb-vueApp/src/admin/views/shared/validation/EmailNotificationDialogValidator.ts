import { requiredHtmlContent } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import { helpers, required } from '@vuelidate/validators'
import { validArrayEmails } from '@/admin/views/shared/validation/EmailValidator'

const requiredBccOrCc = (value: boolean, param: any) => {
  return param.savedCc.value.length > 0 || param.savedBcc.value.length > 0
}

export const emailNotificationRules = {
  savedNotificationEmail: {
    body: {
      requiredHtmlContent
    },
    subject: {
      required: helpers.withMessage('The subject is required', required)
    }
  },
  savedBcc: {
    validArrayEmails,
    requiredBccOrCc: helpers.withMessage('At least one email address should be added', requiredBccOrCc)
  },
  savedCc: {
    validArrayEmails,
    requiredBccOrCc: helpers.withMessage('At least one email address should be added', requiredBccOrCc)
  }
}
