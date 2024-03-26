import { helpers, required } from '@vuelidate/validators'

export const exchangeRuleRules = {
  exchangeMode: {
    requiredRole: helpers.withMessage('The Role is required', required)
  }
}
