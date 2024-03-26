import { defineStore } from 'pinia'
import { RestResponsePage, UserListItem } from '@/model/entities'
import UserDto from '@/model/userDto'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useValidationStore from '@/shared/store/validation'

const nameSpace = 'user'

interface State {
  userListItemsPage: RestResponsePage<UserListItem>,
  userListItems: Array<UserListItem>,
  user: UserDto,
  euLoginOrNameInput: string
}

const useUserStore = defineStore(nameSpace, {
  state: (): State => ({
    userListItemsPage: {} as RestResponsePage<UserListItem>,
    userListItems: [],
    user: { active: true } as UserDto,
    euLoginOrNameInput: ''
  }),

  actions: {
    fetchUsers(paginationOptions: PaginationOptions, isSysAdmin = false) {
      return UserProfileApi.getListItems(paginationOptions, isSysAdmin)
        .then((page) => {
          this.userListItemsPage = page
          this.userListItems = page.content
        })
    },

    async setUser(user: UserDto) {
      this.user = user
    },

    resetForm() {
      this.user = { active: true } as UserDto
      this.euLoginOrNameInput = ''
      useValidationStore()
        .setServerSideValidationErrors([])
    }
  }
})

export default useUserStore
