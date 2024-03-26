import { GroupType, RoleName } from '@/model/entities'

export interface AdminGuide {
  id?: string
  label: string
  role: RoleName
  groupType: GroupType
}
