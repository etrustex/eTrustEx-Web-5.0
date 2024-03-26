import { Direction, EntityItem, Group, PageImpl, RecipientPreferences, RestResponsePage } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { defineStore } from 'pinia'
import { GroupApi } from '@/shared/api/groupApi'

const nameSpace = 'group'

interface State {
  business: Group
  entity: Group
  groups: Array<Group>
  groupsPage: RestResponsePage<Group>
  adminGroupsPage: PageImpl<EntityItem>
}

const useGroupStore = defineStore(nameSpace, {
  state: (): State => ({
    business: new Group(),
    entity: new Group(),
    groups: [],
    groupsPage: new RestResponsePage<Group>(),
    adminGroupsPage: new PageImpl<EntityItem>()

  }),
  actions: {
    resetCurrentBusiness() {
      this.business = new Group()
    },
    setGroup(group: Group) {
      this.entity = group
    },

    fetchGroups(paginationOptions: PaginationOptions) {
      if (!paginationOptions.sortBy) {
        paginationOptions.sortBy = 'auditingEntity.createdDate'
      }

      if (!paginationOptions.sortOrder) {
        paginationOptions.sortOrder = Direction.DESC
      }

      return GroupApi.getGroups(paginationOptions)
        .then((groupsPage) => {
          this.groups = groupsPage.content
          this.groupsPage = groupsPage
        })
    },

    fetchAdminGroups(paginationOptions: PaginationOptions) {
      if (!paginationOptions.sortBy) {
        paginationOptions.sortBy = 'auditingEntity.createdDate'
      }

      if (!paginationOptions.sortOrder) {
        paginationOptions.sortOrder = Direction.DESC
      }

      return GroupApi.getAdminGroups(paginationOptions)
        .then((groupsPage) => {
          this.adminGroupsPage = groupsPage
        })
    },

    async fetchCurrentBusiness(groupId: number) {
      this.business = await GroupApi.get(groupId)
    },

    fetchCurrentEntity(groupId: number) {
      return GroupApi.get(groupId)
        .then((group) => {
          this.entity = group
        })
    },

    setRecipientPreferences(recipientPreferences: RecipientPreferences) {
      this.entity.recipientPreferences = recipientPreferences
    }
  }
})

export default useGroupStore
