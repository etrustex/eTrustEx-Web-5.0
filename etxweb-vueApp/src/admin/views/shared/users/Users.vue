<template>
  <div v-if="paginationCommand">
    <div v-if="!hideGlobalActions" class="d-flex justify-content-end btns">
      <button data-bs-toggle="collapse" data-bs-target="#userForm" class="btn btn-outline-primary btn-collapse-arrow"
              type="submit">
        <span class="ico-standalone"><i aria-hidden="true" class="ico-add-primary"></i></span>
        <span class="ms-0">{{ addButtonText }}</span>
        <span aria-hidden="true" class="collapse-arrow"></span>
      </button>
      <button v-if="!listConfig?.hideRoleColumn && !listConfig?.hideNotificationColumn" type="button"
              class="btn btn-success btn-ico"
              @click="enableAllUsers()">
        <span class="ico-standalone"><i aria-hidden="true" class="ico-visibility-white"></i></span>
        <span>Activate all users</span>
      </button>
      <button v-if="!listConfig?.hideRoleColumn && !listConfig?.hideNotificationColumn" type="button"
              class="btn btn-danger btn-ico"
              @click="disableAllUsers()">
        <span class="ico-standalone"><i aria-hidden="true" class="ico-visibility-off-white"></i></span>
        <span>Inactivate all users</span>
      </button>
    </div>
    <user-form id="userForm" :groupId="groupId" :paginationCommand="paginationCommand" :role="role"
               :displaySendRequest="displaySendRequest"/>
    <user-list :groupId="groupId"
               :role="role"
               :config="{ ...listConfig, hideEditButton: !allowsEditUsers }"
               :paginationCommand="paginationCommand"/>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, PropType, ref, Ref, watch } from 'vue'
import { RoleName } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useUserStore from '@/admin/store/user'
import useDialogStore from '@/shared/store/dialog'
import { GrantedAuthorityApi } from '@/admin/api/grantedAuthorityApi'
import useAuthenticationStore from '@/shared/store/authentication'
import UserForm from '@/admin/views/shared/users/UserForm.vue'
import UserList, { UsersListConfig } from '@/admin/views/shared/users/UserList.vue'
import { RoleMessages } from '@/model/roleMessages'

const props = defineProps({
  groupId: { type: Number, required: true },
  role: { type: String as PropType<RoleName | undefined>, required: true },
  listConfig: { type: Object as PropType<UsersListConfig>, required: true },
  hideGlobalActions: { type: Boolean },
})

const dialogStore = useDialogStore()
const userStore = useUserStore()
const authenticationStore = useAuthenticationStore()
const addButtonText: Ref<string> = computed(() => props.role ? RoleMessages[props.role].addUserButtonLabel : RoleMessages[RoleName.OPERATOR].addUserButtonLabel)
const allowsEditUsers: Ref<boolean> = ref(!props.role)
const displaySendRequest: Ref<boolean> = ref(!props.role)
const paginationCommand: Ref<PaginationCommand | undefined> = ref()

onMounted(() => setPaginationAndLoad())
watch([() => props.groupId, () => props.role, () => props.listConfig.isRegistrationRequest, () => props.listConfig.isAllUsers], () => setPaginationAndLoad())

function setPaginationAndLoad() {
  paginationCommand.value = new PaginationCommand(
    (paginationOptions: PaginationOptions) => userStore.fetchUsers(paginationOptions, (props.role === RoleName.SYS_ADMIN || authenticationStore.isSysAdmin())),
    { sortBy: 'auditingEntity.createdDate' },
    {
      ...(props.role ? { role: props.role } : {}),
      ...(props.groupId ? { groupId: props.groupId } : {}),
      ...(props.listConfig?.isRegistrationRequest ? { isRegistrationRequest: props.listConfig.isRegistrationRequest } : {}),
      ...(props.listConfig?.isAllUsers ? { isAllUsers: props.listConfig.isAllUsers } : {}),
    },
  )
  paginationCommand.value?.fetch()
}

const enableAllUsers = () => {
  GrantedAuthorityApi.updateGroup({ groupId: props.groupId, enabled: true })
    .then(() => dialogStore.show('Update user status:', 'Users have been successfully activated'))
    .then(() => paginationCommand.value?.fetch())
}

const disableAllUsers = () => {
  GrantedAuthorityApi.updateGroup({ groupId: props.groupId, enabled: false })
    .then(() => dialogStore.show('Update user status:', 'Users have been successfully inactivated'))
    .then(() => paginationCommand.value?.fetch())
}
</script>
