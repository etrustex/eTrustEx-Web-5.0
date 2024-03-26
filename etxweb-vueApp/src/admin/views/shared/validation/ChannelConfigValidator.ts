import { helpers } from '@vuelidate/validators'
import { ExchangeMode } from '@/model/entities'

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

export const channelConfigRules = {
  channelId: {
    requiredChannelId: helpers.withMessage('The channel is required', validateChannelId)
  },
  entityRole: {
    requiredEntityRole: helpers.withMessage('The role is required', validateEntityRole)
  }

}
