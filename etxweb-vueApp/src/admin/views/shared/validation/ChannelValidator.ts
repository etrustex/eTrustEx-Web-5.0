import { helpers, maxLength, required } from '@vuelidate/validators'
import { ExchangeMode } from '@/model/entities'

function validateDefaultExchangeMode(value: ExchangeMode, params: any) {
  if (!params.defaultChannel && !params.onlyValidate) {
    return true
  }
  return value.length > 0
}

export const channelRules = {
  name: {
    requiredName: helpers.withMessage('The Name is required', required),
    nameMax: helpers.withMessage('The Name should not exceed 255 characters', maxLength(255))
  },
  description: {
    descriptionMax: helpers.withMessage('The Description should not exceed 255 characters', maxLength(255))
  },
  defaultExchangeMode: {
    requiredDefaultExchangeMode: helpers.withMessage('The Role is required', validateDefaultExchangeMode)
  }
}
