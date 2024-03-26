import { emailsLength, emailsMax, uniqueEmails, validEmails } from '@/admin/views/shared/validation/EmailValidator'

export const userRegistrationRequestRules = {
  notificationEmail: {
    validEmails,
    uniqueEmails,
    emailsLength,
    emailsMax
  }
}
