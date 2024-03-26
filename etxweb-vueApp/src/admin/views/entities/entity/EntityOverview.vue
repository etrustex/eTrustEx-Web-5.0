<template>
  <div class="d-flex justify-content-between align-items-md-center mt-3">
    <h3 class="variant mb-0">Overview</h3>
    <a data-bs-toggle="collapse" data-bs-target="#channel-details" class="ico-standalone">
      <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
    </a>
  </div>

  <div class="collapse mt-3 show" ref="collapseElement" id="channel-details">

    <div class="d-flex justify-content-end">
      <a @click="editableFormOn()"
         class="ico-standalone" type="button">
        <i class="ico-edit"></i>
      </a>
    </div>

    <form class="form-admin" v-bind:class="(editForm)?'form-admin-edit-mode':'form-admin-view-mode'">
      <div class="container">

        <div class="row">
          <label class="col-lg-6 col-form-label"></label>
          <div class="col-lg-6">
            <span class="ico-conjunction" v-if="isEncrypted && props.group.recipientPreferences?.publicKey">
              <i title="Encryption enforced" class="ico-encrypted-mandatory"></i> End-to-end encryption enforced
            </span>
            <span class="ico-conjunction" v-else-if="props.group.recipientPreferences?.publicKey">
              <i title="Encrypted" class="ico-encrypted"></i> End-to-end encryption enabled
            </span>
            <span class="ico-conjunction" v-else><i title="Not encrypted" class="ico-not-encrypted"></i> End-to-end encryption disabled</span>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="identifier">Identifier<span
            class="label-required">*</span></label>
          <div class="col-lg-6">
            <div v-if="editForm">
              <div v-if="isEntityAdmin && !isBusinessOrSysAdmin">
                <strong>{{ props.group.identifier }}</strong>
              </div>
              <div v-else>
                <input class="form-control" id="identifier" v-model.trim="groupSpec.identifier" name="'Group Name'">
                <span v-if="v$.identifier.$error" class="error"> {{ v$.identifier.$errors[0].$message }} </span>
              </div>
            </div>
            <div v-else>
              <strong>{{ props.group.identifier }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="display_name">Name<span class="label-required">*</span></label>
          <div class="col-lg-6">
            <div v-if="editForm">
              <textarea class="form-control" id="display_name" v-model.trim="groupSpec.displayName"></textarea>
              <span v-if="v$.displayName.$error" class="error"> {{ v$.displayName.$errors[0].$message }} </span>
            </div>
            <div v-else>
              <strong>{{ props.group.name }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="description">Description<span
            class="label-required">*</span></label>
          <div class="col-lg-6">
            <div v-if="editForm">
              <textarea class="form-control" id="description" v-model.trim="groupSpec.description"></textarea>
              <span v-if="v$.description.$error" class="error"> {{ v$.description.$errors[0].$message }} </span>
            </div>
            <div v-else>
              <strong>{{ props.group.description }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="description">Status<span class="label-required">*</span></label>
          <div class="col-lg-6">
            <div v-if="editForm" class="form-check form-switch">
              <input id="statusCheckbox" type="checkbox" role="switch" class="form-check-input" name="'check-button'"
                     :disabled="isEntityAdmin && !isBusinessOrSysAdmin" v-model="groupSpec.active">
              <label class="form-check-label" for="statusCheckbox">
                {{ groupSpec.active ? 'Active' : 'Inactive' }}
              </label>
            </div>
            <div v-else>
              <strong>{{ props.group.active ? 'Active' : 'Inactive' }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label v-if="!editForm" class="col-lg-6 col-form-label" for="description">
            Back-end system connection <img v-if="!editForm && groupSpec.system" alt="Back-end System Connection"
                                            style="width: 24px;" src="@/assets/ico/rounded-check-circle-success.svg">
          </label>
          <div v-else class="col-lg-6 col-form-label"></div>

          <div class="col-lg-6">
            <div v-if="editForm">
              <input id="isSystem" type="checkbox" class="form-check-input" v-model="groupSpec.system"
                     v-on:change="groupSpec.endpoint = undefined"> &nbsp;
              <strong class="form-check-label me-1">Entity only used by a back-end system</strong>
              <more-information
                :content="'Select this if the entity will connect to eTrustEx via REST API'"
                placement="right"/>
            </div>
            <strong v-else>{{ groupSpec.endpoint }}</strong>
          </div>
        </div>

        <div v-if="editForm" class="row">
          <label class="col-lg-6 col-form-label" for="endpoint">Endpoint</label>
          <div class="col-lg-6">
            <input class="form-control" id="endpoint" v-model.trim="groupSpec.endpoint" name="'Endpoint'">
          </div>
        </div>

        <div v-if="props.group.auditingEntity" class="row">
          <label class="col-lg-6 col-form-label">Created on</label>
          <div class="col-lg-6">
            <strong>{{ toDateTime(props.group.auditingEntity.createdDate) }}</strong>
          </div>
        </div>

        <div v-if="props.group.auditingEntity" class="row">
          <label class="col-lg-6 col-form-label">By</label>
          <div class="col-lg-6">
            <strong>{{ props.group.auditingEntity.createdBy }}</strong>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label">
            <span>New message notification
              <more-information content="You can add up to 10 email addresses separated by a comma or a semicolon."
                                placement="right"/>
            </span>
          </label>

          <div class="col-lg-6">
            <div v-if="editForm">
              <textarea class="form-control" id="new_message_notification"
                        v-model.trim="groupSpec.newMessageNotificationEmailAddresses">
              </textarea>
              <span v-if="v$.newMessageNotificationEmailAddresses.$error" class="error">
                {{ v$.newMessageNotificationEmailAddresses.$errors[0].$message }}
              </span>
            </div>
            <div v-else>
              <strong>{{ newMessageNotificationEmailAddresses }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="status_notification">Status notification</label>
          <div class="col-lg-6">
            <div v-if="editForm">
              <input class="form-control" id="status_notification"
                     v-model.trim="groupSpec.statusNotificationEmailAddress">
              <span v-if="v$.statusNotificationEmailAddress.$error" class="error">
                {{ v$.statusNotificationEmailAddress.$errors[0].$message }}
              </span>
            </div>
            <div v-else>
              <strong>{{ statusNotificationEmailAddress }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="retention_warning_notification">Retention Policy Warning
            notification</label>
          <div class="col-lg-6">
            <div v-if="editForm">
              <input class="form-control" id="retention_warning_notification"
                     v-model.trim="groupSpec.retentionWarningNotificationEmailAddresses">
              <span v-if="v$.retentionWarningNotificationEmailAddresses.$error" class="error">
                {{ v$.retentionWarningNotificationEmailAddresses.$errors[0].$message }}
              </span>
            </div>
            <div v-else>
              <strong>{{ retentionWarningNotificationEmailAddresses }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label">Individual Status Notifications</label>
          <div class="col-lg-6">
            <div v-if="editForm" class="form-check form-switch">
              <input id="individualStatusNotificationsCheckbox" type="checkbox" role="switch" class="form-check-input"
                     v-model="groupSpec.individualStatusNotifications">
              <label class="form-check-label" for="individualStatusNotificationsCheckbox">
                {{ groupSpec.individualStatusNotifications ? 'Active' : 'Inactive' }}
              </label>
            </div>
            <div v-else>
              <strong>{{ props.group.individualStatusNotifications ? 'Active' : 'Inactive' }}</strong>
            </div>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label">User registration request notifications</label>
          <div class="col-lg-6">
            <div v-if="editForm">
              <textarea class="form-control" id="new_message_notification"
                        v-model.trim="groupSpec.registrationRequestNotificationEmailAddresses"></textarea>
              <span v-if="v$.registrationRequestNotificationEmailAddresses.$error" class="error">
                {{ v$.registrationRequestNotificationEmailAddresses.$errors[0].$message }}
              </span>
            </div>
            <div v-else>
              <strong>{{ registrationRequestNotificationEmailAddresses }}</strong>
            </div>
          </div>
        </div>

      </div>

      <div v-if="editForm" class="d-flex justify-content-end mt-3">
        <div class="btns">
          <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="editableFormOff()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span><span>Cancel</span>
          </button>
          <button type="button" class="btn btn-primary btn-ico" v-on:click="save()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span><span>Save</span>
          </button>
        </div>
      </div>

    </form>
  </div>
</template>

<script setup lang="ts">

import { computed, onMounted, PropType, ref } from 'vue'
import { Confidentiality, Group, GroupSpec, GroupType } from '@/model/entities'
import { formatListForDisplay, formatListForInput } from '@/utils/stringListHelper'
import useAuthenticationStore from '@/shared/store/authentication'
import { GroupApi } from '@/shared/api/groupApi'
import useGroupStore from '@/admin/store/group'
import { GroupHelper } from '@/utils/groupHelper'
import { toDateTime } from '@/utils/formatters'
import useVuelidate from '@vuelidate/core'
import {
  groupRules,
  newMessageNotification,
  registrationRequestNotification,
  retentionWarningNotification,
  statusNotification,
} from '@/admin/views/shared/validation/GroupValidator'
import MoreInformation from '@/shared/views/MoreInformation.vue'

const props = defineProps({
  group: { type: Object as PropType<Group>, required: true },
})

const authenticationStore = useAuthenticationStore()
const groupSpec = ref(new GroupSpec())
const editForm = ref(false)

const isEncrypted = computed(
  () => props.group.recipientPreferences && props.group.recipientPreferences.confidentiality === Confidentiality.LIMITED_HIGH)

const statusNotificationEmailAddress = computed(
  () => formatListForDisplay(props.group.statusNotificationEmailAddress))

const newMessageNotificationEmailAddresses = computed(
  () => formatListForDisplay(props.group.newMessageNotificationEmailAddresses))

const registrationRequestNotificationEmailAddresses = computed(
  () => formatListForDisplay(props.group.registrationRequestNotificationEmailAddresses))

const retentionWarningNotificationEmailAddresses = computed(
  () => formatListForDisplay(props.group.retentionWarningNotificationEmailAddresses))

const isEntityAdmin = computed(
  () => authenticationStore.isEntityAdmin(props.group.id))

const isBusinessOrSysAdmin = computed(
  () => authenticationStore.isSysAdmin() || authenticationStore.isBusinessAdmin(props.group.businessId))

const rules = { ...groupRules, ...newMessageNotification, ...statusNotification, ...retentionWarningNotification, ...registrationRequestNotification }

const v$ = useVuelidate(rules, groupSpec)

onMounted(() => shallowCopyGroup())

function editableFormOff() {
  shallowCopyGroup()
  editForm.value = false
}

function editableFormOn() {
  if (!editForm.value) {
    shallowCopyGroup()
    editForm.value = true
  }
}

function shallowCopyGroup() {
  groupSpec.value = GroupHelper.toSpec(props.group)
  if (GroupType.ENTITY === props.group.type) {
    groupSpec.value.newMessageNotificationEmailAddresses = formatListForDisplay(props.group.newMessageNotificationEmailAddresses)
    groupSpec.value.statusNotificationEmailAddress = formatListForDisplay(props.group.statusNotificationEmailAddress)
    groupSpec.value.registrationRequestNotificationEmailAddresses = formatListForDisplay(props.group.registrationRequestNotificationEmailAddresses)
    groupSpec.value.retentionWarningNotificationEmailAddresses = formatListForDisplay(props.group.retentionWarningNotificationEmailAddresses)
    groupSpec.value.individualStatusNotifications = props.group.individualStatusNotifications
  }
}

function save() {
  v$.value.$validate()
  if (!v$.value.$error) {
    groupSpec.value.newMessageNotificationEmailAddresses = formatListForInput(groupSpec.value.newMessageNotificationEmailAddresses)
    groupSpec.value.statusNotificationEmailAddress = formatListForInput(groupSpec.value.statusNotificationEmailAddress)
    groupSpec.value.registrationRequestNotificationEmailAddresses = formatListForInput(groupSpec.value.registrationRequestNotificationEmailAddresses)
    groupSpec.value.retentionWarningNotificationEmailAddresses = formatListForInput(groupSpec.value.retentionWarningNotificationEmailAddresses)
    return GroupApi.update(groupSpec.value, props.group.links)
      .then(() => useGroupStore()
        .fetchCurrentEntity(props.group.id))
      .then(() => editableFormOff())
  }
}

</script>
