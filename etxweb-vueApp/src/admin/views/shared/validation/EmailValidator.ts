import { helpers, maxLength, required } from '@vuelidate/validators'
import { commissionEmailRegExp, emailRegExp } from '@/admin/views/shared/validation/EmailRegExps'

const validateEmails = (emails: string) => {
  if (!emails || emails.length === 0) return true
  return emails.trim()
    .split(/[;,]/)
    .map(email => emailRegExp.test(email.trim()))
    .reduce((previousValue, currentValue) => previousValue && currentValue)
}

const validateUniqueEmails = (emails: string) => {
  if (emails.length === 0) return true
  const unique = emails.trim()
    .split(/[;,]/)
    .filter((item, i, ar) => ar.indexOf(item) === i)
  return emails.trim()
    .split(/[;,]/).length === unique.length
}

const validateArrayEmails = (emails: Array<string>) => {
  if (!emails || emails.length === 0) return true
  return emails.map(email => emailRegExp.test(email.trim()))
    .reduce((previousValue, currentValue) => previousValue && currentValue)
}
const validateEmailsLength = (emails: string) => {
  return emails.trim()
    .split(/[;,]/)
    .find(email => email.length > 255) === undefined
}

const validateEmailsNumber = (emails: string) => {
  return (emails.trim()
    .split(/[;,]/).length <= 10)
}

const validateUniqueEmail = (emails: string) => {
  if (!emails || emails.length === 0) return true
  return (emails.trim()
    .split(/[;,]/).length <= 1)
}

const validateCommissionEmail = (emails: string) => {
  if (!emails || emails.length === 0) return true
  return emails.trim()
    .split(/[;,]/)
    .map(email => commissionEmailRegExp.test(email.trim()))
    .reduce((previousValue, currentValue) => previousValue && currentValue)
}

const validateRequiredAlternativeEmail = (emails: string, param: any) => {
  if (!param.alternativeEmailUsed) return true

  return emails.trim().length > 0
}

export const validArrayEmails = helpers.withMessage('All email addresses should be valid emails', validateArrayEmails)
export const validEmails = helpers.withMessage('The email address should be a valid email', validateEmails)
export const uniqueEmails = helpers.withMessage('The email addresses should not be duplicated', validateUniqueEmails)
export const emailsLength = helpers.withMessage('Each email address should not exceed 255 characters', validateEmailsLength)
export const emailsNumber = helpers.withMessage('Email addresses should be at most 10', validateEmailsNumber)
export const uniqueEmail = helpers.withMessage('There should be only 1 email address', validateUniqueEmail)
export const emailsMax = helpers.withMessage('The emails should not exceed 2500 characters', maxLength(2500))

export const requiredEmail = helpers.withMessage('The Email address is required', required)
export const emailMax = helpers.withMessage('The Email address should not exceed 255 characters', maxLength(255))

export const validCommissionEmail = helpers.withMessage('The email address should be a commission email ending with @ec.europa.eu', validateCommissionEmail)

export const requiredAlternativeEmail = helpers.withMessage('The Email address is required', validateRequiredAlternativeEmail)
