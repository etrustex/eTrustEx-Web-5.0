import { requiredAlternativeEmail, uniqueEmail, validEmails } from '@/admin/views/shared/validation/EmailValidator'
import { helpers, maxLength, required } from '@vuelidate/validators'
import { RoleName } from '@/model/entities'

const validateRoleNames = (roles: RoleName[]) => {
  if (!roles) {
    return true
  }
  return roles.length > 0
}

const requiredName = helpers.withMessage('The User name is required', required)

const requiredEULoginOrEmail = helpers.withMessage('The EU Login ID or Email is required', required)

const nameMax = helpers.withMessage('The User should not exceed 255 characters', maxLength(255))

const EULoginOrEmailMax = helpers.withMessage('The value should not exceed 255 characters', maxLength(255))

const minRolesRequired = helpers.withMessage('It is required to select at least one role.', validateRoleNames)

const alternativeEmail = {
  requiredEmail: requiredAlternativeEmail,
  validEmail: validEmails,
  uniqueEmail
}

const roleNames = {
  min: minRolesRequired
}
export const userDtoRules = {
  user: {
    ecasId: {
      required: requiredEULoginOrEmail,
      max: EULoginOrEmailMax
    },
    alternativeEmail
  },
  roleNames
}

export const roleNameRules = {
  roleNames
}

export const editUserListItemRules = {
  alternativeEmail,
  roleNames
}
