<template>
  <div>
    <span class="ico-conjunction">
      Users
      <i v-if="validated === true" class="ms-2 ico-check-circle-success" title="Valid"/>
      <i v-if="validated === false" class="ms-2 ico-danger" title="Not valid"/>
    </span>
    <hr>
    <form class="form-admin form-admin-edit-mode" v-on:change="validated = undefined">
      <div class="container">
        <div class="row">
          <label class="col-lg-6 col-form-label" for="newUsers">
            New Users
            <more-information
              :content="'You can add EU Login IDs and/or email addresses. They should be comma-separated.'"
              placement="right"/>
          </label>
          <div class="col-lg-6">
            <textarea class="form-control" id="newUsers" v-model.trim="userEmailsOrEcasId"/>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="existingUsers">Existing Users</label>
          <div class="col-lg-6">
            <user-search ref="formExistingUsers"
                         :business-id="business.id"
                         :selected-users="(quickStartSpec.existingUsers || [])"
                         @selected="setExistingUsers"/>
            <span v-if="validated === false && (!quickStartSpec.existingUsers.length && !newUsers.length)" class="error">
              At least one user should be added
            </span>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label">Roles<span class="label-required">*</span></label>
          <div class="col-lg-6">
            <fieldset id="roles" class="form-group">
              <div class="form-check">
                <input type="checkbox" class="form-check-input" v-model="isAdmin">
                <label class="form-check-label"><span class="font-weight-normal">Administrator</span></label>
              </div>
              <div class="form-check">
                <input type="checkbox" class="form-check-input" v-model="isOperator">
                <label class="form-check-label"><span class="font-weight-normal">Operator</span></label>
              </div>
              <input hidden type="text" v-model="quickStartSpec.roleNames">
              <span v-if="v$.roleNames.$error" class="error">{{ v$.roleNames.$errors[0].$message }}</span>
            </fieldset>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label">
            Notifications
            <more-information
              :content="'It is required to be configured as an Operator to be able to set notifications'"
              placement="right"/>
          </label>

          <div class="col-lg-6">
            <fieldset id="roles" class="form-group">
              <div class="form-check">
                <input :disabled="!isOperator" type="checkbox" class="form-check-input"
                       v-model="quickStartSpec.statusNotification">
                <label class="form-check-label"><span class="font-weight-normal">Status</span></label>
              </div>
              <div class="form-check">
                <input :disabled="!isOperator" type="checkbox" class="form-check-input"
                       v-model="quickStartSpec.newMessageNotification">
                <label class="form-check-label"><span class="font-weight-normal">New Message</span></label>
              </div>
              <div class="form-check">
                <input :disabled="!isOperator" type="checkbox" class="form-check-input"
                       v-model="quickStartSpec.retentionWarningNotification">
                <label class="form-check-label"><span
                  class="font-weight-normal">Retention Warning</span></label>
              </div>
            </fieldset>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-end mt-3">
        <div class="btns">
          <button type="button" class="btn btn-primary btn-ico" v-on:click="isValid()">
            <span>Validate</span>
          </button>
        </div>
      </div>
    </form>

    <span class="ico-conjunction">
      Entity
      <i v-if="formGroupValid === true" class="ms-2 ico-check-circle-success" title="Valid"/>
      <i v-if="formGroupValid === false" class="ms-2 ico-danger" title="Not valid"/>
    </span>
    <hr>
    <group-form ref="formGroup"
                :parent-group-id="business.id"
                :group-type="GroupType.ENTITY"
                :only-validate="true"
                @on-validated="setGroup"/>

    <span class="ico-conjunction">
      Exchange Configuration
      <i v-if="(existingChannel && formExistingChannelValid === true || formNewChannelValid === true)"
         class="ms-2 ico-check-circle-success" title="Valid"/>
      <i v-if="(existingChannel && formExistingChannelValid === false || formNewChannelValid === false)"
         class="ms-2 ico-danger" title="Not valid"/>
    </span>
    <hr>
    <div>
      <div class="d-flex flex-row justify-content-center">
        <div class="form-check">
          <input id="existingChannels" class="form-check-input" type="radio" v-model="existingChannel"
                 v-on:change="formExistingChannelValid = undefined; formNewChannelValid = undefined;" :value="true"/>
          <label for="existingChannels">Existing Channel</label>
        </div>
        <div class="form-check ms-4">
          <input id="newChannels" class="form-check-input" type="radio" v-model="existingChannel"
                 v-on:change="formNewChannelValid = undefined; formExistingChannelValid = undefined;" :value="false"/>
          <label for="newChannels">New Channel</label>
        </div>
      </div>

      <div v-if="existingChannel">
        <business-quick-start-existing-channel ref="formExistingChannel" :business-id="business.id"
                                               @on-validated="setExistingChannel"/>
      </div>
      <div v-else>
        <channel-form ref="formNewChannel" :only-validate="true" @on-validated="setNewChannel"/>
      </div>
    </div>

    <div class="d-flex justify-content-end mt-3">
      <div class="btns">
        <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="reset()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
          <span>Cancel</span>
        </button>
        <button type="button" class="btn btn-primary btn-ico" v-on:click="save()" :disabled="!isSaveEnabled">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
          <span>Save</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ChannelSpec,
  DbStringListsSeparators,
  Group,
  GroupSpec,
  GroupType,
  QuickStartSpec,
  RoleName,
  UserListItem,
} from '@/model/entities'
import { computed, PropType, Ref, ref } from 'vue'
import GroupForm from '@/admin/views/shared/group/GroupForm.vue'
import ChannelForm from '@/admin/views/shared/channel/ChannelForm.vue'
import useVuelidate from '@vuelidate/core'
import UserSearch from '@/admin/views/shared/users/UserSearch.vue'
import BusinessQuickStartExistingChannel, {
  ChannelConfigSpec,
} from '@/admin/views/businesses/business/BusinessQuickStartExistingChannel.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { roleNameRules } from '@/admin/views/shared/validation/UserValidator'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import { formatListForInput } from '@/utils/stringListHelper'
import { QuickStartApi } from '@/shared/api/quickStartApi'

const dialogStore = useDialogStore()

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const newUsers: Ref<Array<string>> = ref([])
const quickStartSpec: Ref<QuickStartSpec> = ref({
  groupSpec: new GroupSpec(),
  channelSpec: new ChannelSpec(),
  existingUsers: [],
  newUsers: [],
  roleNames: [],
  statusNotification: false,
  newMessageNotification: false,
  retentionWarningNotification: false
})

const existingChannel = ref(true)
const formGroup: Ref<typeof GroupForm | null> = ref(null)
const formNewChannel: Ref<typeof ChannelForm | null> = ref(null)
const formExistingChannel: Ref<typeof BusinessQuickStartExistingChannel | null> = ref(null)
const formExistingUsers: Ref<typeof UserSearch | null> = ref(null)

const validated: Ref<boolean | undefined> = ref()
const formGroupValid: Ref<boolean | undefined> = ref()
const formNewChannelValid: Ref<boolean | undefined> = ref()
const formExistingChannelValid: Ref<boolean | undefined> = ref()

const isOperator = computed({
  get: () => quickStartSpec.value.roleNames.filter(roleName => RoleName.OPERATOR === roleName).length > 0,
  set: (value) => {
    setRole(RoleName.OPERATOR, value)
    if (!value) {
      quickStartSpec.value.statusNotification = false
      quickStartSpec.value.newMessageNotification = false
      quickStartSpec.value.retentionWarningNotification = false
    }
  }
})
const isAdmin = computed({
  get: () => quickStartSpec.value.roleNames.filter(roleName => RoleName.GROUP_ADMIN === roleName).length > 0,
  set: (value) => setRole(RoleName.GROUP_ADMIN, value)
})
const userEmailsOrEcasId = ref('')

const v$ = useVuelidate(roleNameRules, quickStartSpec, { $stopPropagation: true })

const isSaveEnabled = computed(() => {
  return validated.value &&
    formGroupValid.value &&
    ((existingChannel.value && formExistingChannelValid.value) || formNewChannelValid.value)
})

function setGroup(group: GroupSpec, valid: boolean) {
  formGroupValid.value = valid
  if (validated.value) {
    quickStartSpec.value.groupSpec = { ...quickStartSpec.value.groupSpec, ...group }
  }
}

function setNewChannel(channel: ChannelSpec, valid: boolean) {
  formNewChannelValid.value = valid
  if (valid) {
    quickStartSpec.value.groupSpec.addToChannel = false
    quickStartSpec.value.channelSpec = channel
  }
}

function setExistingChannel(channel: ChannelConfigSpec, valid: boolean) {
  formExistingChannelValid.value = valid
  if (valid) {
    quickStartSpec.value.channelSpec = new ChannelSpec()
    quickStartSpec.value.groupSpec = { ...quickStartSpec.value.groupSpec, ...channel }
  }
}

function setExistingUsers(users: Array<UserListItem>) {
  quickStartSpec.value.existingUsers = users
}

function setRole(roleName: RoleName, value: boolean) {
  quickStartSpec.value.roleNames = quickStartSpec.value.roleNames.filter(existingRoleName => roleName !== existingRoleName)
  if (value) {
    quickStartSpec.value.roleNames.push(roleName)
  }
}

async function isValid() {
  await v$.value.$validate()

  newUsers.value = formatListForInput(userEmailsOrEcasId.value)
    .split(DbStringListsSeparators.DB_STRING_LIST_SEPARATOR)
    .filter(u => u.length > 0)

  if (!newUsers.value.length) {
    validated.value = !v$.value.$error && quickStartSpec.value.existingUsers.length > 0
    return Promise.resolve(!v$.value.$error && quickStartSpec.value.existingUsers.length > 0)
  }

  const users = await Promise.all(newUsers.value.map(mailOrEcasId => UserProfileApi.fetchEuLoginInfo(props.business?.id, mailOrEcasId)))
  const nullIndexes: Array<number> = []
  users.forEach((u, i) => {
    if (!u) {
      nullIndexes.push(i)
    }
  })

  if (nullIndexes.length) {
    const list = nullIndexes
      .map(i => newUsers.value[i])
      .map(u => `<li>${u}</li>`)
      .join('')

    dialogStore.show('Add users!', `<p class="mt-3">No account was found for the following users.</p><p><ul>${list}</ul></p><p class="mt-3"> Please check and fix the incorrect values and validate the form again.</p>`, DialogType.ERROR)

    validated.value = false
    return false
  }
  quickStartSpec.value.newUsers = users
  validated.value = !v$.value.$error
  return !v$.value.$error
}

async function save() {
  if (!(await isValid()) || v$.value.$error) {
    return
  }

  QuickStartApi.create(quickStartSpec.value)
    .then(() => dialogStore.show('Success!', '<p class="mt-3">New data added successfully.</p>', DialogType.INFO))

  reset()
}

function reset() {
  userEmailsOrEcasId.value = ''
  formGroup.value?.reset()
  formNewChannel.value?.reset()
  formExistingChannel.value?.reset()
  formExistingUsers.value?.reset()

  v$.value.$reset()

  validated.value = undefined
  formGroupValid.value = undefined
  formNewChannelValid.value = undefined
  formExistingChannelValid.value = undefined

  quickStartSpec.value = {
    groupSpec: new GroupSpec(),
    channelSpec: new ChannelSpec(),
    existingUsers: [],
    newUsers: [],
    roleNames: [],
    statusNotification: false,
    newMessageNotification: false,
    retentionWarningNotification: false
  }
}

</script>
