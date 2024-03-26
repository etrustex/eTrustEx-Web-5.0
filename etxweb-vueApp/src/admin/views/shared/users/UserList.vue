<template>
  <div>
    <bulk-upload v-if="!hideUserBulkCreation"
                 :paginationCommand="paginationCommand" :uploadFunction="uploadFunction"
                 :sampleFileName="'users_bulk_creation'" :model="'user'" :modelPlural="'users'">
    </bulk-upload>
    <search searchBy="Name or EU Login" searchTooltip="Search a User"
            :filterFn="filter"
            :prefilterFn="prefilterFn"/>

    <div class="d-flex justify-content-end">
      <div class="dropdown mt-3 btn-group" v-if="selectedUsers.length > 0">
        <button class="btn btn-outline-primary dropdown-toggle" type="button" data-bs-toggle="dropdown"
                aria-expanded="false">
          Actions
        </button>
        <ul v-if="isRegistrationRequest" class="dropdown-menu">
          <li role="presentation">
            <button role="menuitem" class="dropdown-item" v-on:click="acceptOrRejectUsers(true)">
              <span aria-hidden="true" class="ico-standalone">
                <a href="javascript:void(0);" class="badge rounded-pill bg-success">Accept</a>
              </span>
            </button>
          </li>
          <li role="presentation">
            <button role="menuitem" class="dropdown-item" v-on:click="acceptOrRejectUsers(false)">
              <span aria-hidden="true" class="ico-standalone">
                <a href="javascript:void(0);" class="badge rounded-pill bg-danger">Reject</a>
              </span>
            </button>
          </li>
        </ul>
        <ul v-else class="dropdown-menu">
          <li role="presentation">
            <button class="dropdown-item" v-on:click="confirmDeleteSelectedUsers">
              <span aria-hidden="true" class="ico-standalone"><i class="ico-delete"></i></span>
              <span class="ps-2">Delete users</span>
            </button>
          </li>
          <li v-if="displayUserActivationDeactivationButtons" role="presentation">
            <button role="menuitem" class="dropdown-item" v-on:click="confirmChangeSelectedUsersStatus(true)">
              <span aria-hidden="true" class="ico-standalone">
                <i class="ico-visibility-white" style="background-color: green"></i>
              </span>
              <span class="ps-2">Activate users</span>
            </button>
            <button role="menuitem" class="dropdown-item" v-on:click="confirmChangeSelectedUsersStatus(false)">
              <span aria-hidden="true" class="ico-standalone">
                <i class="ico-visibility-off-white" style="background-color: red"></i></span>
              <span class="ps-2">Inactivate users</span>
            </button>
          </li>
        </ul>
      </div>
    </div>
    <b-table
      :fields="[
        ... !hideActionColumn ? [{ key: 'index', thClass: 'index-width', tdClass: 'index-width' }] : [],
        { key: 'name', sortable: true },
        { key: 'EULogin', label: 'EU Login ID', sortable: true },
        ... !hideRoleColumn ? [{ key:'role' }] : [],
        ... displayEntityColumn ? [{ key: 'EntityIdentifier', sortable: true }] : [],
        ... displayBusinessColumn ? [{ key: 'EntityName', sortable: true }] : [],
        ... displayBusinessColumn ? [{ key: 'BusinessName', sortable: true }] : [],
        ... !hideNotificationColumn ? [{ key:'notifications' }] : [],
        ... displayRolesAndNotificationsColumns && !isRegistrationRequest ? [{ key:'userStatus', label: 'User status' }] : [],
        ... !hideActionColumn ? [{ key:'actions' }] : [],
        'more'
      ]"
      :items="userListItems"
      :no-local-sorting="true"
      @sort-changed="sortingChanged"
      sort-icon-left
      class="mt-3"
    >
      <template #head(index)>
        <div class="d-flex align-items-md-center">
          <input class="form-check-input" type="checkbox" :checked="allSelected" v-on:change="toggleAllSelected()"/>
        </div>
      </template>
      <template #cell(index)="data">
        <div class="d-flex align-items-md-center">
          <input class="form-check-input" type="checkbox" :id="data.item.name + data.index + 1"
                 :checked="isUserSelected(data.item)" v-on:change="toggleUserSelected(data.item)"/>
        </div>
      </template>
      <template #cell(name)="data">
        <span class="text-break">{{ data.item.name }}</span>
      </template>
      <template #cell(EULogin)="data">
        <span class="text-break">{{ data.item.ecasId }}</span>
      </template>
      <template v-if="!hideRoleColumn" #cell(role)="data">
        <div v-if="data.item.groupType === 'ENTITY'">
          <span v-for="roleName in data.item.roleNames" :key="roleName"
                class="badge rounded-pill bg-primary">{{ toRoleNameLabel(roleName) }}</span>
        </div>
        <div v-else-if="data.item.groupType !== 'ENTITY'">
            <span v-for="roleName in data.item.roleNames" :key="roleName"
                  class="badge rounded-pill bg-primary">{{ 'Business Admin' }}</span>
        </div>
      </template>
      <template v-if="displayEntityColumn" #cell(EntityIdentifier)="data">
        <span v-if="data.item.groupType === 'ENTITY'" class="text-break">{{ data.item.groupIdentifier }}</span>
      </template>
      <template v-if="displayBusinessColumn" #cell(EntityName)="data">
        <span v-if="data.item.groupType === 'ENTITY'" class="text-break">{{ data.item.groupName }}</span>
      </template>
      <template v-if="displayBusinessColumn" #cell(BusinessName)="data">
        <div v-if="data.item.businessIdentifier === 'ROOT'">
          <span class="text-break">{{ data.item.groupName }}</span>
        </div>
        <div v-else>
          <span class="text-break">{{ data.item.businessIdentifier }}</span>
        </div>

      </template>
      <template v-if="displayRolesAndNotificationsColumns" #cell(notifications)="data">
        <span v-if="data.item.newMessageNotification" class="badge rounded-pill bg-primary">New Message</span>
        <span v-if="data.item.statusNotification" class="badge rounded-pill bg-primary">Status</span>
        <span v-if="data.item.retentionWarningNotification"
              class="badge rounded-pill bg-primary">Retention Warning</span>
      </template>
      <template #cell(userStatus)="data">
        <span v-if="data.item.status && (data.item.groupType === 'ENTITY' || !displayRolesAndNotificationsColumns)"
              class="badge rounded-pill bg-success">Active</span>
        <span v-if="!data.item.status && (data.item.groupType === 'ENTITY' || !displayRolesAndNotificationsColumns)"
              class="badge rounded-pill bg-danger">Inactive</span>
      </template>
      <template #cell(actions)="data">
        <ul class="ico-list">
          <li v-if="!hideEditButton">
            <a v-if="data.item.groupType === 'ENTITY' || !displayRolesAndNotificationsColumns"
               v-tooltip.tooltip="'Edit'"
               class="ico-standalone" type="button" v-on:click="!data.item.isEditMode && edit(data)">
              <i class="ico-edit"></i>
              <span>Edit</span>
            </a>
          </li>
          <li v-if="!isRegistrationRequest">
            <a class="ico-standalone delete"
               data-bs-target="#delete-user-dialog"
               v-tooltip.tooltip="'Delete'"
               href="javascript:void(0);" type="button"
               v-on:click="confirmDeleteUser(data.item)">
              <i class="ico-delete"></i>
              <span>Remove</span>
            </a>
          </li>
          <li v-if="isRegistrationRequest">
            <a class="badge rounded-pill bg-danger"
               v-tooltip.tooltip="'reject'"
               href="javascript:void(0);" type="button"
               v-on:click="confirmAcceptOrRejectUser(data.item, false)">
              Reject
            </a>
          </li>
          <li v-if="isRegistrationRequest">
            <a class="badge rounded-pill bg-success"
               v-tooltip.tooltip="'accept'"
               href="javascript:void(0);" type="button"
               v-on:click="confirmAcceptOrRejectUser(data.item, true)">
              Accept
            </a>
          </li>
        </ul>
      </template>
      <template #cell(more)="data">
        <a :class="`ico-standalone has-tooltip ${data.item._showDetails ? 'not-collapsed': 'collapsed'}`"
           type="button" v-on:click="!data.item.isEditMode && data.toggleDetails()">
          <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
        </a>
      </template>
      <template #row-details="data">
        <edit-user-details v-if="data.item.isEditMode" :displayEntityColumn="displayEntityColumn"
                           :displayRolesAndNotificationsColumns="displayRolesAndNotificationsColumns"
                           :isRegistrationRequest="isRegistrationRequest" :row="data"
                           :userListItem="data.item" @updated="refresh(0)"/>

        <display-user-details v-else :displayEntityColumn="displayEntityColumn"
                              :displayRoles="!hideRoleColumn"
                              :displayNotification="!hideNotificationColumn"
                              :displayBusiness="displayBusinessColumn"
                              :userListItem="data.item"/>
      </template>
    </b-table>

    <div class="container-fluid">
      <div class="row">
        <div class="col-12 mt-3">
          <pagination :page="userListItemsPage" :paginationCommand="paginationCommand"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, PropType, ref, Ref } from 'vue'
import {
  Direction,
  GrantedAuthoritySpec,
  GroupType,
  RoleName,
  UserListItem,
  UserRegistrationRequestSpec,
} from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { onBeforeRouteLeave, useRoute } from 'vue-router'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import useUserStore from '@/admin/store/user'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import { RoleNameFormatter } from '@/utils/RoleNameFormatter'
import { GrantedAuthorityApi } from '@/admin/api/grantedAuthorityApi'
import useGroupStore from '@/admin/store/group'
import { UserRegistrationRequestApi } from '@/admin/api/userRegistrationRequestApi'
import BulkUpload from '@/admin/views/shared/BulkUpload.vue'
import Pagination from '@/shared/views/Pagination.vue'
import DisplayUserDetails from '@/admin/views/shared/users/user_details/DisplayUserDetails.vue'
import EditUserDetails from '@/admin/views/shared/users/user_details/EditUserDetails.vue'
import Search from '@/shared/views/Search.vue'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import useAuthenticationStore from '@/shared/store/authentication'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import { storeToRefs } from 'pinia'

export type UsersListConfig = {
  isAllUsers: boolean;
  isRegistrationRequest: boolean;
  displayEntityColumn: boolean;
  displayBusinessColumn: boolean;
  hideEditButton: boolean;
  hideRoleColumn: boolean;
  hideNotificationColumn: boolean;
  hideUserBulkCreation: boolean;
  hideActionColumn: boolean;
}

const props = defineProps({
  groupId: { type: Number, required: true },
  role: { type: String as PropType<RoleName | undefined>, required: true },
  config: { type: Object as PropType<UsersListConfig>, required: true },
  paginationCommand: { type: Object as PropType<PaginationCommand>, required: true },
})

const isAllUsers = computed(() => props.config?.isAllUsers)
const isRegistrationRequest = computed(() => props.config?.isRegistrationRequest)
const displayEntityColumn = computed(() => props.config?.displayEntityColumn)
const displayBusinessColumn = computed(() => props.config?.displayBusinessColumn)
const hideEditButton = computed(() => props.config?.hideEditButton)
const hideActionColumn = computed(() => props.config?.hideActionColumn)
const hideNotificationColumn = computed(() => props.config?.hideNotificationColumn)
const hideRoleColumn = computed(() => props.config?.hideRoleColumn)
const hideUserBulkCreation = computed(() => props.config?.hideUserBulkCreation)

const route = useRoute()
const authenticationStore = useAuthenticationStore()
const dialogStore = useDialogStore()
const userStore = useUserStore()

const { userListItemsPage, userListItems } = storeToRefs(userStore)

const displayUserActivationDeactivationButtons = computed(() => !route.path.includes('administrators') && props.role !== RoleName.OFFICIAL_IN_CHARGE)
const displayRolesAndNotificationsColumns = computed(() => !hideRoleColumn.value && !hideNotificationColumn.value)

const selectedUsers: Ref<Array<string>> = ref([])
const allSelected = computed(() => selectedUsers.value.length > 0 && selectedUsers.value.length === userListItems.value.length)

const uploadFunction = (file: File, groupId: number) => UserProfileApi.uploadBulk(file, groupId)

onBeforeRouteLeave((to, from, next) => {
  userStore.resetForm()
  next()
})

function toRoleNameLabel(roleName: RoleName) {
  return RoleNameFormatter.toRoleNameLabel(roleName)
}

function edit(row: any) {
  if (row.detailsShowing) {
    row.toggleDetails()
  }
  row.item.isEditMode = !row.item.isEditMode
  row.toggleDetails()
}

function filter(filterValue: string) {
  props.paginationCommand.options.filterBy = 'name,ecasId'
  props.paginationCommand.options.filterValue = filterValue
  props.paginationCommand.fetch({ page: 0 })
    .then()
}

function sortingChanged(key: string, isDesc: boolean) {
  props.paginationCommand.options.sortBy = getSortBy(key)
  props.paginationCommand.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
  props.paginationCommand.fetch({ page: 0 })
    .then()
}

function getSortBy(key: string) {
  let sortBy: string
  switch (key) {
    case 'name':
      sortBy = 'user.name'
      break
    case 'EULogin':
      sortBy = 'user.ecasId'
      break
    case 'EntityIdentifier':
      sortBy = 'group.identifier'
      break
    case 'EntityName':
      sortBy = 'group.name'
      break
    case 'BusinessName':
      sortBy = 'group.parent.name'
      break
    default:
      sortBy = 'auditingEntity.createdDate'
      break
  }

  return sortBy
}

function prefilterFn(searchBy: string) {
  const groupId = props.groupId || useGroupStore().business.id
  return UserProfileApi.search(groupId, searchBy, isRegistrationRequest.value || false, isAllUsers.value || false, props.role)
}

function buildDialogMessage(accept: boolean, messageDialog?: string, userListItem?: UserListItem) {
  let message = accept ? 'Are you sure you want to add the user' : 'Are you sure you want to reject the user'
  const userListName = userListItem ? userListItem.name : ''
  const userListGroupIdentifier = userListItem ? userListItem.groupIdentifier : ''

  message = '<p class="mt-3">' + message + '<strong class="text-break"> '
  return messageDialog || message + `${ userListName }</strong> to <strong class="text-break">${ userListGroupIdentifier }?</strong></p>`
}

function displayErrorDialog(userListItem: UserListItem) {
  dialogStore.show(
    'User deletion forbidden',
    `You are not allowed to remove user ${ userListItem.ecasId } from ${ ifRootDisplaySystemAdmin(userListItem) }`,
  )
}

function ifRootDisplaySystemAdmin(userListItem: UserListItem) {
  if (userListItem.groupType === GroupType.ROOT) {
    if (props.role === RoleName.SYS_ADMIN) {
      return 'System Administrator role'
    }
    if (props.role === RoleName.OFFICIAL_IN_CHARGE) {
      return 'Official in charge role'
    }
  }
  return userListItem.groupIdentifier
}

function canDeleteUser(userName: string, groupType: GroupType): boolean {
  if (props.role === RoleName.OFFICIAL_IN_CHARGE && authenticationStore.isSysAdmin()) {
    return true
  }

  if ((userName === authenticationStore.userDetails.username) && (groupType === GroupType.ROOT)) {
    return false
  }

  if (groupType === GroupType.ENTITY) {
    return authenticationStore.canDeleteEntityAdmin(+route.params.entityId, userName)
  }

  return authenticationStore.canDeleteBusinessAdmin(+route.params.businessId, userName)
}

function isBusinessAdmin(user: UserListItem): boolean {
  return user.roleNames.filter(roleName => RoleName.GROUP_ADMIN === roleName).length > 0 &&
    user.groupType === GroupType.BUSINESS
}

function isUserSelected(userListItem: UserListItem) {
  return selectedUsers.value.includes(`${ userListItem.ecasId }|${ userListItem.groupId }`)
}

function toggleUserSelected(userListItem: UserListItem) {
  if (isUserSelected(userListItem)) {
    selectedUsers.value = selectedUsers.value.filter(key => key !== `${ userListItem.ecasId }|${ userListItem.groupId }`)
  } else {
    selectedUsers.value = [...selectedUsers.value, `${ userListItem.ecasId }|${ userListItem.groupId }`]
  }
}

function toggleAllSelected() {
  selectedUsers.value = allSelected.value ? [] : userListItems.value.map(u => `${ u.ecasId }|${ u.groupId }`)
}

function addUser(userListItem: UserListItem) {
  const specs: Array<GrantedAuthoritySpec> = []
  userListItem.roleNames.forEach(role => {
    if (role) {
      const spec = new GrantedAuthoritySpec()
      spec.userName = userListItem.ecasId
      spec.groupId = userListItem.groupId
      spec.enabled = true
      spec.roleName = role

      specs.push(spec)
    }
  })

  return GrantedAuthorityApi.acceptUserRegistrations(specs)
    .then(() => refresh(specs.length))
    .catch(() => displayErrorDialog(userListItem),
    )
}

function confirmDeleteUser(userListItem: UserListItem) {
  if (canDeleteUser(userListItem.ecasId, userListItem.groupType)) {
    const message = '<p class="mt-3">Are you sure you want to delete the user <strong class="text-break"> ' +
      `${ userListItem.name }</strong> from <strong class="text-break">${ ifRootDisplaySystemAdmin(userListItem) }?</strong></p><p>The action cannot be undone.</p>`
    dialogStore.show(
      'Delete User',
      message,
      DialogType.ERROR,
      buttonWithCallback('Delete', () => deleteUser(userListItem)),
      CANCEL_BUTTON,
    )
  } else {
    displayErrorDialog(userListItem)
  }
}

function deleteUser(userListItem: UserListItem) {
  if (canDeleteUser(userListItem.ecasId, userListItem.groupType)) {
    const promise = (props.role === RoleName.SYS_ADMIN || props.role === RoleName.OFFICIAL_IN_CHARGE)
      ? UserProfileApi.sysDelete({ ecasId: userListItem.ecasId, groupId: userListItem.groupId, roleName: props.role })
      : UserProfileApi.delete({ ecasId: userListItem.ecasId, groupId: userListItem.groupId })
    return promise.then(() => refresh())
      .catch(() => displayErrorDialog(userListItem))
  }
}

function confirmDeleteSelectedUsers() {
  const message = '<p class="mt-3">Are you sure you want to delete the selected users <strong class="text-break"> ' +
    '</p><p>The action cannot be undone.</p>'
  dialogStore.show(
    'Delete User',
    message,
    DialogType.ERROR,
    buttonWithCallback('Delete', () => deleteAllSelectedUsers()),
    CANCEL_BUTTON,
  )
}

async function deleteAllSelectedUsers() {
  const items = userListItems.value.filter(u => isUserSelected(u))
  await Promise.all(items.map(selectedUser => deleteUser(selectedUser)))
    .then(() => refresh(items.length))
}

function confirmAcceptOrRejectUser(userListItem: UserListItem, accept: boolean) {
  if (accept) {
    dialogStore.show(
      'Add User',
      buildDialogMessage(accept, '', userListItem),
      DialogType.INFO,
      buttonWithCallback('Add', () => addUser(userListItem)),
      CANCEL_BUTTON,
    )
  } else {
    dialogStore.show(
      'Reject User',
      buildDialogMessage(accept, '', userListItem),
      DialogType.INFO,
      buttonWithCallback('Reject', () => deleteUserRegistrationRequestApi(userListItem)),
      CANCEL_BUTTON,
    )
  }
}

function acceptOrRejectUsers(accept: boolean) {
  const selectedUserItems = userListItems.value.filter(u => isUserSelected(u))

  if (accept) {
    dialogStore.show(
      'Add User',
      buildDialogMessage(accept, 'Are you sure you want to add all selected users'),
      DialogType.INFO,
      buttonWithCallback('Add', () => acceptAllSelectedUsers(selectedUserItems)),
      CANCEL_BUTTON,
    )
  } else {
    dialogStore.show(
      'Reject User',
      buildDialogMessage(accept, 'Are you sure you want to reject all selected users'),
      DialogType.INFO,
      buttonWithCallback('Reject', () => rejectAllSelectedUsers(selectedUserItems)),
      CANCEL_BUTTON,
    )
  }
}

async function rejectAllSelectedUsers(selectedUsers: Array<UserListItem>) {
  const specs: Array<UserRegistrationRequestSpec> = selectedUsers.map(userListItem => {
    const spec = new UserRegistrationRequestSpec()
    spec.ecasId = userListItem.ecasId
    spec.groupId = userListItem.groupId

    return spec
  })
  UserRegistrationRequestApi.delete(specs)
    .then(() => dialogStore.show('Reject users:', 'Users have been successfully rejected'))
    .then(() => refresh())
}

async function acceptAllSelectedUsers(selectedUsers: Array<UserListItem>) {
  const grantedAuthoritySpecs: Array<GrantedAuthoritySpec> = []
  selectedUsers.forEach(userListItem => {
    userListItem.roleNames.forEach(role => {
      if (role) {
        const grantedAuthority = new GrantedAuthoritySpec()
        grantedAuthority.userName = userListItem.ecasId
        grantedAuthority.groupId = userListItem.groupId
        grantedAuthority.roleName = role
        grantedAuthority.enabled = true
        grantedAuthoritySpecs.push(grantedAuthority)
      }
    })
  })
  GrantedAuthorityApi.acceptUserRegistrations(grantedAuthoritySpecs)
    .then(() => dialogStore.show('Accept users:', 'All users have been successfully accepted'))
    .then(() => refresh())
}

function deleteUserRegistrationRequestApi(userListItem: UserListItem) {
  return UserRegistrationRequestApi.delete([{ ecasId: userListItem.ecasId, groupId: userListItem.groupId }])
    .then(() => refresh())
    .catch(() => displayErrorDialog(userListItem),
    )
}

function confirmChangeSelectedUsersStatus(status: boolean) {
  let selectedUserItems = userListItems.value.filter(u => isUserSelected(u))
  if (selectedUserItems.length === 0) {
    return
  }

  if (route.path.includes(AdminRouteNames.ENTITIES)) {
    if (selectedUserItems.some(u => u.ecasId === authenticationStore.username)) {
      // we are in entity users view
      selectedUserItems = selectedUserItems.filter(u => u.ecasId !== authenticationStore.username)
      const callback = selectedUserItems.length > 0 ? changeSelectedUsersStatus : () => refresh(0)
      dialogStore.show(
        'Update user status:',
        'The status for your user will not change.\nThe rest of the users will have the status updated.',
        DialogType.INFO,
        buttonWithCallback('Ok', () => callback(selectedUserItems, status)),
      )
    } else {
      changeSelectedUsersStatus(selectedUserItems, status)
    }
  } else {
    // we are in business users view
    if (selectedUserItems.some(u => isBusinessAdmin(u))) {
      selectedUserItems = selectedUserItems.filter(u => !isBusinessAdmin(u))
      const callback = selectedUserItems.length > 0 ? changeSelectedUsersStatus : () => refresh(0)
      dialogStore.show(
        'Update user status:',
        'The status for Business administrators will not change. Any change needed for Business administrators should be done from \'Administrators\' section.\nThe rest of the users will have the status updated.',
        DialogType.INFO,
        buttonWithCallback('Ok', () => callback(selectedUserItems, status)),
      )
    } else {
      changeSelectedUsersStatus(selectedUserItems, status)
    }
  }
}

function changeSelectedUsersStatus(selectedUsers: Array<UserListItem>, status: boolean) {
  const specs: Array<GrantedAuthoritySpec> = selectedUsers.map(userListItem => {
    const spec = new GrantedAuthoritySpec()
    spec.userName = userListItem.ecasId
    spec.groupId = userListItem.groupId
    spec.enabled = status

    return spec
  })

  GrantedAuthorityApi.updateGroupBulk(specs)
    .then(() => dialogStore.show('Update user status:', `Users have been successfully ${ status ? 'activated' : 'inactivated' }`))
    .then(() => refresh(0))
}

function refresh(removedCount = 1) {
  selectedUsers.value = []
  return props.paginationCommand.fetch({
    page: props.paginationCommand.currentOrLast(
      userStore.userListItemsPage.totalElements - removedCount,
      userStore.userListItemsPage.number),
  })
}
</script>
