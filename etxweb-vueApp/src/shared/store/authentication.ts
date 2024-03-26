import { GroupType, RoleName, UserDetails, UserRole } from '@/model/entities'
import { defineStore } from 'pinia'
import { UserApi } from '@/shared/api/userApi'
import { RouteLocation } from 'vue-router'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'

export interface GroupSummary {
  id: number
  name: string
}

export enum AdminType {
  SYSTEM,
  MULTIPLE_BUSINESS,
  SINGLE_BUSINESS,
  MULTIPLE_ENTITY_DIFFERENT_BUSINESS,
  MULTIPLE_ENTITY_SAME_BUSINESS,
  SINGLE_ENTITY,
  OFFICIAL_IN_CHARGE,
  NOT_AN_ADMIN
}

const nameSpace = 'authentication'

interface State {
  userDetails: UserDetails,
  userEntities: Array<GroupSummary>,
  paginationSize: number
}

const useAuthenticationStore = defineStore(nameSpace, {
  state: (): State => ({
    userDetails: new UserDetails(),
    userEntities: [],
    paginationSize: parseInt(process.env.VUE_APP_PAGE_SIZE as string, 10)
  }),
  getters: {
    username: (state) => state.userDetails.username,
    roles: (state) => state.userDetails.roles,
    fullName: (state) => `${state.userDetails.lastName} ${state.userDetails.firstName}`,
    firstUserEntityId: (state) => state.userEntities.length ? state.userEntities[0].id : undefined,
    firstBusinessIdForBusinessAdmin: (state) => {
      const role = state.userDetails.roles
        ?.find(r => (
          r.role === RoleName.GROUP_ADMIN &&
          r.groupType === GroupType.BUSINESS
        ))

      return role ? role.groupId : undefined
    },
    firstEntityIdForEntityAdmin: (state) => {
      const role = state.userDetails.roles
        ?.find(r => (
          r.role === RoleName.GROUP_ADMIN &&
          r.groupType === GroupType.ENTITY
        ))

      return role ? role.groupId : undefined
    },
    rootGroupId: (state) => state.userDetails.roles
      .find(role => (role.role === RoleName.SYS_ADMIN || role.role === RoleName.OFFICIAL_IN_CHARGE))?.groupId

  },

  actions: {
    /*
     * Missing handling Business admin in one business and entity admin in another business
     */
    adminType() {
      if (this.isSysAdmin()) {
        return AdminType.SYSTEM
      } else if (this.isBusinessAdminInMultipleBusiness()) {
        return AdminType.MULTIPLE_BUSINESS
      } else if (this.isBusinessAdmin()) {
        return AdminType.SINGLE_BUSINESS
      } else if (this.isEntityAdminInMultipleBusiness()) {
        return AdminType.MULTIPLE_ENTITY_DIFFERENT_BUSINESS
      } else if (this.isMultipleEntityAdminInBusiness()) {
        return AdminType.MULTIPLE_ENTITY_SAME_BUSINESS
      } else if (this.isEntityAdmin()) {
        return AdminType.SINGLE_ENTITY
      } else if (this.isOfficialInCharge()) {
        return AdminType.OFFICIAL_IN_CHARGE
      } else {
        console.error('Not an admin?')
        return AdminType.NOT_AN_ADMIN
      }
    },
    isAnAdmin() {
      return this.isSysAdmin() || this.isBusinessAdmin() || this.isEntityAdmin()
    },
    isSysAdmin(): boolean {
      return this.userDetails.roles?.some(role => role.role === RoleName.SYS_ADMIN && role.groupIsActive)
    },
    isOfficialInCharge(): boolean {
      return this.userDetails.roles?.some(role => role.role === RoleName.OFFICIAL_IN_CHARGE && role.groupIsActive)
    },
    isBusinessAdmin(businessId?: number): boolean {
      return this.isGroupAdmin(GroupType.BUSINESS, businessId)
    },
    isBusinessAdminInMultipleBusiness() {
      return this.userDetails.roles?.filter(role => role.role === RoleName.GROUP_ADMIN && role.groupType === GroupType.BUSINESS && role.groupIsActive).length > 1
    },
    isMultipleEntityAdminInBusiness(businessId?: number) {
      if (businessId) {
        return this.userDetails.roles
          ?.filter(role => role.role === RoleName.GROUP_ADMIN && role.groupType === GroupType.ENTITY && role.businessId === businessId)
          .length > 1
      } else {
        const entityAdminRoles = this.userDetails.roles
          ?.filter(role => role.role === RoleName.GROUP_ADMIN && role.groupType === GroupType.ENTITY)

        const numberOfBusinesses = Array.from(new Set(entityAdminRoles
          .map(role => role.businessId)))
          .length

        return entityAdminRoles.length > 1 && numberOfBusinesses === 1
      }
    },
    isEntityAdminInMultipleBusiness() {
      const numberOfBusinesses = Array.from(new Set(this.userDetails.roles
        ?.filter(role => role.role === RoleName.GROUP_ADMIN && role.groupType === GroupType.ENTITY)
        .map(role => role.businessId)))
        .length

      return numberOfBusinesses > 1
    },
    isEntityAdmin(entityId?: number): boolean {
      return this.isGroupAdmin(GroupType.ENTITY, entityId)
    },
    isOperator(): boolean {
      return this.userDetails.roles?.some(role => role.role === RoleName.OPERATOR && role.groupIsActive)
    },
    getAdminUserRole(roleName: RoleName, groupType: GroupType = GroupType.ROOT, businessId?: number): UserRole | undefined {
      if (businessId) {
        return this.userDetails.roles?.find(role => role.role === roleName && role.groupType === groupType && role.businessId === businessId)
      } else {
        return this.userDetails.roles?.find(role => role.role === roleName && role.groupType === groupType)
      }
    },
    getGroupAdmins(): Array<UserRole> {
      return this.userDetails.roles?.filter(role => role.role === RoleName.GROUP_ADMIN && role.groupType === GroupType.ENTITY) || []
    },
    getBusinessIds(): Array<number> {
      return [...new Set(this.userDetails.roles?.filter(role => role.role === RoleName.GROUP_ADMIN)
        .map(role => role.businessId))]
    },
    isGroupAdmin(groupType: GroupType, groupId?: number) {
      return this.userDetails.roles?.some(role => (
        role.role === RoleName.GROUP_ADMIN &&
        role.groupType === groupType &&
        (groupId ? role.groupId === groupId : true)
      ))
    },
    isAdminRoute(route: RouteLocation) {
      return route.matched.find(value => AdminRouteNames.ADMIN === value.name) !== undefined
    },
    getAdminRoot() {
      return { name: AdminRouteNames.ADMIN }
    },
    loadUserDetails() {
      return UserApi.getUserDetails()
        .then((userDetails: UserDetails) => {
          this.userDetails = userDetails
          this.paginationSize = this.userDetails.userPreferencesSpec.paginationSize
          this.userEntities = [...new Set(this.userDetails.roles
            .filter(role => role.groupId && GroupType.ENTITY === role.groupType && RoleName.OPERATOR === role.role && role.groupIsActive)
            .map(role => ({ name: role.groupName, id: role.groupId })))]
            .sort((a, b) => a.name.localeCompare(b.name))
          return this.userDetails
        })
    },
    canDeleteEntityAdmin(groupId: number, userName: string) {
      if (this.isSysAdmin() || this.isBusinessAdmin()) {
        return true
      }
      return this.isEntityAdmin(groupId) && this.username !== userName
    },
    canDeleteBusinessAdmin(groupId: number, userName: string) {
      if (this.isSysAdmin()) {
        return true
      }
      return this.isBusinessAdmin(groupId) && this.username !== userName
    }
  }
})

export default useAuthenticationStore
