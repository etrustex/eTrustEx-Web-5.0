import { RoleName } from '@/model/entities'

export class RoleNameFormatter {
  static toRoleNameLabel(roleName: RoleName): string {
    switch (roleName) {
      case RoleName.OPERATOR:
        return 'Operator'
      case RoleName.GROUP_ADMIN:
      case RoleName.SYS_ADMIN:
        return 'Admin'
      case RoleName.OFFICIAL_IN_CHARGE:
        return 'Official'
    }
  }
}
