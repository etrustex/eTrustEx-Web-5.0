import { helpers, maxValue, required } from '@vuelidate/validators'
import { GroupConfiguration } from '@/model/entities'

const validateMinNumberOfDays = (value: number, param: any) => {
  if (!param.active) {
    return true
  }

  return value > 0
}

const requiredNumberOfDays = helpers.withMessage('The Number of days is required', required)
const minNumberOfDays = helpers.withMessage('The value must be an integer number greater than zero', validateMinNumberOfDays)
const maxNumberOfDays = helpers.withMessage('The value cannot be greater than 7300 days (~20 years)', maxValue(7300))

const numberOfDaysRules = {
  integerValue: {
    requiredNumberOfDays,
    minNumberOfDays
  }
}

const contentNotEmptyBasedOnActiveParam = (value: string, param: GroupConfiguration<any>) => {
  const template = document.createElement('template')
  if (!param.active && !value) {
    return true
  }
  template.innerHTML = value.trim()
  const div = template.content.firstChild as ChildNode
  return div.textContent !== null && div.textContent.trim() !== ''
}

const contentNotEmpty = (value: string) => {
  const template = document.createElement('template')
  template.innerHTML = value.trim()
  const div = template.content.firstChild as ChildNode
  return div.textContent !== null && div.textContent.trim() !== ''
}

export const requiredHtmlContentBasedOnActiveParam = helpers.withMessage('The content is required', contentNotEmptyBasedOnActiveParam)
export const requiredHtmlContent = helpers.withMessage('The content is required', contentNotEmpty)

export const unreadMessageReminderRules = numberOfDaysRules
export const retentionPolicyNotificationRule = numberOfDaysRules

export const retentionPolicyRules = {
  integerValue: {
    requiredNumberOfDays,
    minNumberOfDays,
    maxNumberOfDays
  }
}
