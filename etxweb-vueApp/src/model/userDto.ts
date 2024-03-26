import { AuditingEntity, User, UserPreferences, UserProfile } from '@/model/entities'

export default class UserDto implements User {
  auditingEntity: AuditingEntity
  id: number
  ecasId: string
  name: string
  alternativeEmailUsed: boolean
  alternativeEmail: string
  euLoginEmailAddress: string
  active = true
  userProfiles: Array<UserProfile>
  newMessageNotification: boolean
  statusNotification: boolean
  retentionWarningNotification: boolean
  userPreferences: UserPreferences
}
