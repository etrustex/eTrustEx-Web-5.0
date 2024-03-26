<template>
  <user-details :display-entity-column="displayEntityColumn" :user-list-item="userListItem"
      :displayDate="displayNotification" :displayNotification="displayNotification"></user-details>

  <div class="container">
    <div v-if="displayBusiness" class="row">
      <label class="col-lg-6 col-form-label">Entity Name</label>
      <div class="col-lg-6 d-flex align-items-center">
        <div class="col-lg-6"><strong v-if="userListItem.groupType === 'ENTITY'" class="text-break">{{ userListItem.groupName }}</strong></div>
      </div>
    </div>
    <div v-if="displayBusiness" class="row">
      <label class="col-lg-6 col-form-label">Business Name</label>
      <div class="col-lg-6 d-flex align-items-center">
        <div class="col-lg-6">
          <strong v-if="userListItem.groupType === 'BUSINESS'" class="text-break">{{ userListItem.groupIdentifier }} - ({{ userListItem.groupName }})</strong>
        <strong v-else class="text-break">{{ userListItem.businessIdentifier }} - ({{ userListItem.businessName }})</strong></div>
      </div>
    </div>
    <div v-if="displayRoles" class="row">
      <label class="col-lg-6 col-form-label">Roles</label>
      <div class="col-lg-6 d-flex align-items-center">
        <span v-if="isAdmin">
          <span v-if="userListItem.groupType === 'ENTITY'" class="badge rounded-pill bg-primary">Administrator</span>
          <span v-else class="badge rounded-pill bg-primary">Business Administrator</span>
        </span>
        <span v-if="isOperator" class="badge rounded-pill bg-primary">Operator</span>
      </div>
    </div>

    <div v-if="userListItem.groupType === 'ENTITY' && displayNotification" class="row">
      <label class="col-lg-6 col-form-label">Notifications</label>
      <div class="col-lg-6 d-flex align-items-center">
        <span v-if="userListItem.newMessageNotification" class="badge rounded-pill bg-primary">New Message</span>
        <span v-if="userListItem.statusNotification" class="badge rounded-pill bg-primary">Status</span>
        <span v-if="userListItem.retentionWarningNotification"
              class="badge rounded-pill bg-primary">Retention Warning</span>
      </div>
    </div>
    <div v-if="userListItem.groupType === 'ENTITY' && displayNotification" class="row">
      <label class="col-lg-6 col-form-label">User status</label>
      <div class="col-lg-6 d-flex align-items-center">
        <span v-if="userListItem.status" class="badge rounded-pill bg-success">Active</span>
        <span v-else class="badge rounded-pill bg-danger">Inactive</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, PropType } from 'vue'
import { RoleName, UserListItem } from '@/model/entities'
import UserDetails from '@/admin/views/shared/users/user_details/UserDetails.vue'

const props = defineProps({
  userListItem: { type: Object as PropType<UserListItem>, required: true },
  displayEntityColumn: { type: Boolean },
  displayRoles: { type: Boolean, default: true },
  displayNotification: { type: Boolean, default: true },
  displayBusiness: { type: Boolean, default: false }
})

const isOperator = computed(() => props.userListItem.roleNames.filter(roleName => RoleName.OPERATOR === roleName).length > 0)
const isAdmin = computed(() => props.userListItem.roleNames.filter(roleName => RoleName.GROUP_ADMIN === roleName).length > 0)
</script>
