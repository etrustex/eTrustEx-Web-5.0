import { helpers } from '@vuelidate/validators'

const isStartDateNotEmpty = (value: string, param: any) => {
  if (!param.active) return true

  return !(value === undefined || value === null)
}

const isEndDateGreaterThanStartDate = (value: Date, param: any) => {
  return !value || new Date(value) >= new Date(param.startDate)
}

export const endDate = {
  endDate: helpers.withMessage('Start date/time cannot be greater than End date/time', isEndDateGreaterThanStartDate)
}

export const startDate = {
  startDate: helpers.withMessage('Start date/time cannot be empty', isStartDateNotEmpty)
}
