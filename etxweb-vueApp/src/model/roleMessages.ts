import { RoleName } from '@/model/entities'

export class RoleMessages {
  static [RoleName.SYS_ADMIN] = new RoleMessages({
    addUserButtonLabel: 'Add administrator',
    usersTableTitle: 'Administrators'
  })

  static [RoleName.GROUP_ADMIN] = new RoleMessages({
    addUserButtonLabel: 'Add administrator',
    usersTableTitle: 'Administrators'
  })

  static [RoleName.OPERATOR] = new RoleMessages({ addUserButtonLabel: 'Add user', usersTableTitle: 'Users' })
  static [RoleName.OFFICIAL_IN_CHARGE] = new RoleMessages({
    addUserButtonLabel: 'Add officials in charge',
    usersTableTitle: 'Officials in charge'
  })

  addUserButtonLabel: string
  usersTableTitle: string

  private constructor(messages: { addUserButtonLabel: string | undefined, usersTableTitle: string | undefined }) {
    this.addUserButtonLabel = messages.addUserButtonLabel ? messages.addUserButtonLabel : 'Add user'
    this.usersTableTitle = messages.usersTableTitle ? messages.usersTableTitle : 'Users'
  }
}
