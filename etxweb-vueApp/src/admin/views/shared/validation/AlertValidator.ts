import { requiredHtmlContent } from '@/admin/views/shared/validation/GroupConfigurationValidator'
import { helpers, maxLength, required } from '@vuelidate/validators'
import { endDate, startDate } from '@/admin/views/shared/validation/DateValidator'

export const alertRules = {
  title: {
    requiredTitle: helpers.withMessage('The title is required', required),
    titleMax: helpers.withMessage('The Title should not exceed 255 characters', maxLength(255))
  },
  content: {
    required: requiredHtmlContent
  },
  startDate,
  endDate
}
