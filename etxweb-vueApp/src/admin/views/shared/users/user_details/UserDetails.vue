<template>
  <div class="container">
    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Name</label>
      <div class="col-lg-6"><strong class="text-break">{{ userListItem.name }}</strong></div>
    </div>

    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">
        <span>EU Login ID</span>
        <more-information class="ms-1" :content="moreInformationContent" placement="right"/>
      </label>
      <div class="col-lg-6"><strong class="text-break">{{ userListItem.ecasId }}</strong></div>
    </div>

    <div v-if="displayNotification" class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">EU Login email address</label>
      <div class="col-lg-6"><strong class="text-break">{{ userListItem.euLoginEmailAddress }}</strong></div>
    </div>

    <div v-if="userListItem.groupType === 'ENTITY' && displayNotification" class="row">
      <label class="col-lg-6 col-form-label">
        <span>Alternative Email address</span>
        <img v-if="userListItem.alternativeEmailUsed" alt="Alternative email used" style="width: 24px;"
             src="@/assets/ico/rounded-check-circle-success.svg">
      </label>
      <div class="col-lg-6 d-flex align-items-center"><strong class="text-break">{{
          userListItem.alternativeEmail
        }}</strong></div>
    </div>

    <div v-if="displayEntityColumn" class="row">
      <label class="col-lg-6 col-form-label">Entity Identifier</label>
      <div class="col-lg-6 d-flex align-items-center">
        <div v-if="displayNotification">
          <strong v-if="userListItem.groupType === 'ENTITY'" class="text-break">
            {{ userListItem.groupName }}
          </strong>
        </div>
        <div v-else>
          <strong v-if="userListItem.groupType === 'ENTITY'" class="text-break">
            {{ userListItem.groupIdentifier }}
          </strong>
        </div>
      </div>
    </div>

    <div v-if="displayDate" class="row">
      <label class="col-lg-6 col-form-label">Created on</label>
      <div class="col-lg-2 d-flex align-items-center"><strong>{{ toDateTime(userListItem.createdDate) }}</strong></div>
      <div class="col-lg-1 d-flex align-items-center">By</div>
      <div class="col-lg-3 d-flex align-items-center"><strong>{{ userListItem.createdBy }}</strong></div>
    </div>

    <div v-if="displayDate" class="row">
      <label class="col-lg-6 col-form-label">Modified on</label>
      <div class="col-lg-2 d-flex align-items-center"><strong>{{ toDateTime(userListItem.modifiedDate) }}</strong></div>
      <div class="col-lg-1 d-flex align-items-center">By</div>
      <div class="col-lg-3 d-flex align-items-center"><strong>{{ userListItem.modifiedBy }}</strong></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, PropType } from 'vue'
import { UserListItem } from '@/model/entities'
import { toDateTime } from '@/utils/formatters'
import MoreInformation from '@/shared/views/MoreInformation.vue'

const props = defineProps({
  userListItem: { type: Object as PropType<UserListItem> },
  displayEntityColumn: { type: Boolean },
  displayNotification: { type: Boolean, default: true },
  displayDate: { type: Boolean, default: true }
})
const moreInformationContent = computed(() => {
  return 'EU Login ID is the unique identifier at the Commission (uid). To find it, connect to EU Login -> My account -> My account details.\n\n' +
    '<br/><a href="https://your.auth.service.host/cas/userdata/ShowDetails.cgi" class="underline-on-hover"> EU Login ID</a>'
})

</script>
