<template>
  <div class="container form-admin form-admin-edit-mode">
    <div class="row">
      <label class="col-lg-6 col-form-label">Name</label>
      <div class="col-lg-6"><strong class="text-break">{{ editUserListItem.name }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">
        <span>EU Login ID</span>
        <more-information class="ms-1" :content="moreInformationContent" placement="right"/>
      </label>
      <div class="col col-lg-6"><strong class="text-break">{{ userListItem.ecasId }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">EU Login email address</label>
      <div class="col-lg-6"><strong class="text-break">{{ userListItem.euLoginEmailAddress }}</strong></div>
    </div>

    <div v-if="!isBusinessAdmin" class="row">
      <label class="col-lg-6 col-form-label"></label>
      <div class="col-lg-6">
        <input id="alternativeEmailUsed" type="checkbox" role="switch" class="form-check-input"
               v-model="editUserListItem.alternativeEmailUsed" @change="check($event)">
        <label class="form-check-label ms-1" for="alternativeEmailUsed">
          use an alternative email address to receive notifications
        </label>
      </div>
    </div>

    <div v-if="!isBusinessAdmin" class="row">
      <label class="col-lg-6 col-form-label">Alternative Email address</label>
      <div class="col-lg-6">
        <input id="emailAddress" class="form-control" v-model.trim="editUserListItem.alternativeEmail" type="email">
        <span v-if="v$.alternativeEmail.$error" class="error"> {{ v$.alternativeEmail.$errors[0].$message }} </span>
      </div>
    </div>

    <div v-if="displayEntityColumn" class="row">
      <label class="col-lg-6 col-form-label">Entity</label>
      <div class="col-lg-6"><strong>{{ userListItem.groupName }} - {{ userListItem.groupIdentifier }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">Created on</label>
      <div class="col-lg-2"><strong>{{ toDateTime(userListItem.createdDate) }}</strong></div>
      <div class="col-lg-1">By</div>
      <div class="col-lg-3"><strong>{{ userListItem.createdBy }}</strong></div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">Modified on</label>
      <div class="col-lg-2"><strong>{{ toDateTime(userListItem.modifiedDate) }}</strong></div>
      <div class="col-lg-1">By</div>
      <div class="col-lg-3"><strong>{{ userListItem.modifiedBy }}</strong></div>
    </div>

    <div v-if="displayRolesAndNotificationsColumns" class="row">
      <label class="col-lg-6 col-form-label">Roles</label>
      <div class="col-lg-3">
        <div class="mb-0">
          <div>
            <input id="isAdmin" type="checkbox" role="switch" class="form-check-input" v-model="isAdmin">
            <label class="form-check-label ms-1" for="isAdmin">Administrator</label>
          </div>
          <div>
            <input id="isOperator" type="checkbox" role="switch" class="form-check-input" v-model="isOperator">
            <label class="form-check-label ms-1" for="isOperator">Operator</label>
          </div>
          <input v-model="editUserListItem.roleNames" hidden type="text" @input="validate">
          <span v-if="v$.roleNames.$error" class="error"> {{ v$.roleNames.$errors[0].$message }} </span>
        </div>
      </div>
    </div>

    <div v-if="displayRolesAndNotificationsColumns" class="row">
      <label class="col-lg-6 col-form-label">
        Notifications
        <more-information :content="'It is required to be configured as an Operator to be able to set notifications'"
                          placement="right"/>
      </label>
      <div class="col-lg-3">
        <div class="mb-0">
          <div>
            <input id="statusNotification" type="checkbox" role="switch" class="form-check-input"
                   v-model="editUserListItem.statusNotification" :disabled="!isOperator">
            <label class="form-check-label ms-1" for="statusNotification">Status</label>
          </div>
          <div>
            <input id="newMessageNotification" type="checkbox" role="switch" class="form-check-input"
                   v-model="editUserListItem.newMessageNotification" :disabled="!isOperator">
            <label class="form-check-label ms-1" for="newMessageNotification">New Message</label>
          </div>
          <div>
            <input id="retentionWarningNotification" type="checkbox" role="switch" class="form-check-input"
                   v-model="editUserListItem.retentionWarningNotification" :disabled="!isOperator">
            <label class="form-check-label ms-1" for="retentionWarningNotification">Retention Warning</label>
          </div>
          <input v-model="editUserListItem.roleNames" hidden type="text" @input="validate">
        </div>
      </div>
    </div>

    <div v-if="displayRolesAndNotificationsColumns && !isRegistrationRequest" class="row">
      <label class="col-lg-6 col-form-label">User status</label>
      <div class="col-lg-6">
        <div class="mb-0 form-check form-switch">
          <input id="status" type="checkbox" role="switch" class="form-check-input"
                 v-model="editUserListItem.status">
          <strong>{{ editUserListItem.status ? 'Active' : 'Inactive' }}</strong>
        </div>
      </div>
    </div>

    <div class="row mb-3">
      <div class="col-lg-12 text-end btns">
        <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="reset()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
          <span>Cancel</span>
        </button>
        <button type="button" class="btn btn-primary btn-ico" v-on:click="save()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
          <span>Save</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, PropType, ref } from 'vue'
import { GrantedAuthoritySpec, GroupType, RoleName, UserListItem, UserRegistrationRequestSpec } from '@/model/entities'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import { GrantedAuthorityApi } from '@/admin/api/grantedAuthorityApi'
import useDialogStore from '@/shared/store/dialog'
import { UserRegistrationRequestApi } from '@/admin/api/userRegistrationRequestApi'
import { toDateTime } from '@/utils/formatters'
import useVuelidate from '@vuelidate/core'
import { editUserListItemRules } from '@/admin/views/shared/validation/UserValidator'
import useAuthenticationStore from '@/shared/store/authentication'
import MoreInformation from '@/shared/views/MoreInformation.vue'

const props = defineProps({
  row: { type: Object, required: true },
  userListItem: { type: Object as PropType<UserListItem>, required: true },
  displayEntityColumn: { type: Boolean },
  displayRolesAndNotificationsColumns: { type: Boolean },
  showRoles: { type: Boolean, default: true },
  isRegistrationRequest: { type: Boolean }
})
const emit = defineEmits(['updated'])

const dialogStore = useDialogStore()
const isOperator = computed({
  get: () => editUserListItem.value.roleNames.filter(roleName => RoleName.OPERATOR === roleName).length > 0,
  set: (value) => {
    setRole(RoleName.OPERATOR, value)
    if (!value) {
      disableNotifications()
    }
  }
})
const isAdmin = computed({
  get: () => editUserListItem.value.roleNames.filter(roleName => RoleName.GROUP_ADMIN === roleName).length > 0,
  set: (value) => {
    setRole(RoleName.GROUP_ADMIN, value)
  }
})
const isBusinessAdmin = computed(() =>
  editUserListItem.value.roleNames.filter(roleName => RoleName.GROUP_ADMIN === roleName).length > 0 &&
  editUserListItem.value.groupType === GroupType.BUSINESS)

const editUserListItem = ref({
  roleNames: []
} as unknown as UserListItem)

const v$ = useVuelidate(editUserListItemRules, editUserListItem)

const moreInformationContent = computed(() => {
  return 'EU Login ID is the unique identifier at the Commission (uid). To find it, connect to EU Login -> My account -> My account details.\n\n' +
    '<br/><a href="https://your.auth.service.host/cas/userdata/ShowDetails.cgi" class="underline-on-hover"> EU Login ID</a>'
})

function check(event: any) {
  console.log(event + ' : ' + editUserListItem.value.alternativeEmail)
  // this.$refs.observer.reset()
}

onMounted(() => {
  editUserListItem.value = { ...props.userListItem }
  editUserListItem.value.roleNames = [...props.userListItem.roleNames]
})

async function save() {
  await v$.value.$validate()
  if (v$.value.$error) {
    return
  }
  if (props.isRegistrationRequest) {
    const userRegistrationRequestSpec: UserRegistrationRequestSpec = {
      ecasId: editUserListItem.value.ecasId,
      groupId: editUserListItem.value.groupId,
      admin: editUserListItem.value.roleNames.filter(role => role === RoleName.GROUP_ADMIN).length > 0,
      operator: editUserListItem.value.roleNames.filter(role => role === RoleName.OPERATOR).length > 0
    } as UserRegistrationRequestSpec

    await UserRegistrationRequestApi.update(userRegistrationRequestSpec)
  } else {
    await updateGrantedAuthorities()
  }

  await updateUserProfile()

  emit('updated', editUserListItem.value)

  props.row.item.isEditMode = false
  props.row.toggleDetails()
}

function reset() {
  props.row.item.isEditMode = false
  props.row.toggleDetails()
}

function updateUserProfile() {
  return UserProfileApi.update({
    ecasId: editUserListItem.value.ecasId,
    name: editUserListItem.value.name,
    groupId: editUserListItem.value.groupId,
    euLoginEmailAddress: editUserListItem.value.euLoginEmailAddress,
    alternativeEmail: editUserListItem.value.alternativeEmail,
    alternativeEmailUsed: editUserListItem.value.alternativeEmailUsed,
    newMessageNotification: editUserListItem.value.newMessageNotification,
    statusNotification: editUserListItem.value.statusNotification,
    retentionWarningNotification: editUserListItem.value.retentionWarningNotification
  })
    .then(userProfile => {
      editUserListItem.value.modifiedBy = userProfile.auditingEntity.modifiedBy
      editUserListItem.value.modifiedDate = userProfile.auditingEntity.modifiedDate
    })
}

async function updateGrantedAuthorities() {
  const rolesToDelete = props.userListItem.roleNames.filter(x => !editUserListItem.value.roleNames.includes(x))
  canUpdateAuthorities(rolesToDelete)

  rolesToDelete.forEach(roleName => {
    GrantedAuthorityApi.delete({
      userName: props.userListItem.ecasId,
      groupId: props.userListItem.groupId,
      roleName,
      enabled: editUserListItem.value.status
    })
  })

  if (props.userListItem.status !== editUserListItem.value.status) {
    const spec: GrantedAuthoritySpec = new GrantedAuthoritySpec()
    spec.userName = props.userListItem.ecasId
    spec.groupId = props.userListItem.groupId
    spec.enabled = editUserListItem.value.status

    await GrantedAuthorityApi.updateGroup(spec)
  }

  for (const roleName of editUserListItem.value.roleNames) {
    if (props.userListItem.roleNames.filter(value => value === roleName).length === 0) {
      await GrantedAuthorityApi.create({
        userName: props.userListItem.ecasId,
        groupId: props.userListItem.groupId,
        roleName,
        enabled: editUserListItem.value.status
      })
    }
  }
}

function canUpdateAuthorities(rolesToDelete: Array<RoleName>) {
  const authenticationStore = useAuthenticationStore()
  if (!authenticationStore.canDeleteEntityAdmin(props.userListItem.groupId, props.userListItem?.ecasId)) {
    if (rolesToDelete.includes(RoleName.GROUP_ADMIN)) {
      dialogStore.show('Role deletion forbidden', `You are not allowed to remove user ${props.userListItem.ecasId} from ${props.userListItem?.groupIdentifier}`)
      throw new Error('Self deletion forbidden!')
    } else if (props.userListItem.status !== editUserListItem.value.status) {
      dialogStore.show('Status inactivation forbidden', `You are not allowed to inactivate user ${props.userListItem.ecasId} from ${props.userListItem.groupIdentifier}`)
      throw new Error('Self inactivation forbidden!')
    }
  }
}

function setRole(roleName: RoleName, value: boolean) {
  editUserListItem.value.roleNames = editUserListItem.value.roleNames.filter(existingRoleName => roleName !== existingRoleName)
  if (value) {
    editUserListItem.value.roleNames = [roleName, ...editUserListItem.value.roleNames]
  }
}

function disableNotifications() {
  editUserListItem.value.statusNotification = false
  editUserListItem.value.newMessageNotification = false
  editUserListItem.value.retentionWarningNotification = false
}

</script>
