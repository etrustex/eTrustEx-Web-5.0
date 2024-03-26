import { helpers, maxLength, required } from '@vuelidate/validators'

export const passwordRules = {
  password: {
    required: helpers.withMessage('The password is required', required),
    max: helpers.withMessage('The password should not exceed 255 characters', maxLength(255))
  }
}
