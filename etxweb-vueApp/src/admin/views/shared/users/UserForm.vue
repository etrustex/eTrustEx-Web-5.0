<template>
  <div class="collapse collapse-content-box form-admin-edit-mode" :id="id" ref="collapseElement">
    <div class="container" v-if="!euloginLoaded">
      <div class="row">
        <label class="col-lg-6 col-form-label">
          User
          <more-information :content="moreInformationContent" placement="right"/>
        </label>
        <div class="col-lg-6">
          <div class="input-group">
            <input type="text"
                   placeholder="Search by email address or unique identifier"
                   v-model.trim="user.ecasId"
                   v-on:keyup.enter="$event.preventDefault();loadUser()"
                   class="form-control">
            <div class="input-group-append">
              <a class="ico-standalone" @click="loadUser">
                <i class="ico-search" title="Perform Search"></i>
              </a>
            </div>
          </div>
          <span v-if="v$.user.ecasId.$error" class="error"> {{ v$.user.ecasId.$errors[0].$message }} </span>
        </div>
      </div>
      <div v-if="displaySendRequest" class="row justify-content-end">
        <div class="col-lg-6 text-center">
          <span>--- OR ---</span>
        </div>
      </div>
      <div v-if="displaySendRequest" class="row">
        <label class="col-lg-6 col-form-label">
          Send request via email
        </label>
        <div class="col-lg-6">
          <button type="button" class="btn btn-primary btn-ico" @click="showPopup()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-send-white"></i></span>
            <span>Send request</span>
          </button>
        </div>
      </div>

    </div>

    <form>
      <div v-if="euloginLoaded">
        <div class="container">
          <div class="row">
            <label class="col-lg-6 col-form-label">
              EU Login ID
              <more-information :content="moreInformationContent" placement="right"/>
            </label>
            <div class="col-lg-6">
              {{ user.ecasId }}
            </div>
          </div>
          <div class="row">
            <label class="col-lg-6 col-form-label">Name</label>
            <div v-if="isDevEnvironment" class="col-lg-6">
              <input type="text" v-model.trim="user.name" class="form-control">
            </div>
            <div v-else class="col-lg-6">
              {{ user.name }}
            </div>
          </div>
          <div class="row">
            <label class="col-lg-6 col-form-label">EU Login Email address</label>
            <div class="col-lg-6">
              {{ user.euLoginEmailAddress }}
            </div>
          </div>
          <div class="row justify-content-end" v-if="displayRolesAndNotifications">
            <div class="col-lg-6">
              <div class="form-check">
                <input type="checkbox" class="form-check-input" v-model="user.alternativeEmailUsed"/>
                <label class="form-check-label">Use an alternative email address to receive notifications</label>
              </div>
            </div>
          </div>
          <div class="row" v-if="displayRolesAndNotifications">
            <label class="col-lg-6 col-form-label">Alternative email address</label>
            <div class="col-lg-6">
              <input type="email" class="form-control" v-model.trim="user.alternativeEmail">
              <span v-if="v$.user.alternativeEmail.$error"
                    class="error"> {{ v$.user.alternativeEmail.$errors[0].$message }}</span>
            </div>
          </div>
          <div class="row" v-if="displayRolesAndNotifications">
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
                <input hidden type="text" v-model="roleNames">
                <span v-if="v$.roleNames.$error" class="error"> {{ v$.roleNames.$errors[0].$message }} </span>
              </fieldset>
            </div>
          </div>

          <div class="row" v-if="displayRolesAndNotifications" :key="notificationsKey">
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
                         v-model="user.statusNotification">
                  <label class="form-check-label"><span class="font-weight-normal">Status</span></label>
                </div>
                <div class="form-check">
                  <input :disabled="!isOperator" type="checkbox" class="form-check-input"
                         v-model="user.newMessageNotification">
                  <label class="form-check-label"><span class="font-weight-normal">New Message</span></label>
                </div>
                <div class="form-check">
                  <input :disabled="!isOperator" type="checkbox" class="form-check-input"
                         v-model="user.retentionWarningNotification">
                  <label class="form-check-label"><span
                    class="font-weight-normal">Retention Warning</span></label>
                </div>
              </fieldset>
            </div>
          </div>

        </div>

        <div class="d-flex justify-content-end mt-3 btns">
          <button type="button" class="btn btn-outline-secondary btn-ico" @click="reset()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
            <span>Cancel</span>
          </button>
          <button type="button" class="btn btn-primary btn-ico" @click="save">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
            <span>Save</span>
          </button>
        </div>

      </div>

    </form>
  </div>
  <email-notification-dialog v-if="userRegistrationNotificationEmail" :id="`popup-send-request`"
                             :displayBcc="displayBcc"
                             :notificationEmail="userRegistrationNotificationEmail"
                             ref="emailNotificationsModal"
                             @close-send-request="closedPopup"/>
</template>
<script setup lang="ts">
import { computed, ComputedRef, onMounted, PropType, Ref, ref, watch } from 'vue'
import { NotificationEmailSpec, RoleName, TemplateType } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import useSettingsStore from '@/shared/store/settings'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import useGroupStore from '@/admin/store/group'
import useUserStore from '@/admin/store/user'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import useValidationStore from '@/shared/store/validation'
import { Collapse } from 'bootstrap'
import useVuelidate from '@vuelidate/core'
import { userDtoRules } from '@/admin/views/shared/validation/UserValidator'
import Modal from '@/shared/views/modal/Modal.vue'
import EmailNotificationDialog from '@/admin/views/shared/dialog/EmailNotificationDialog.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'

const props = defineProps({
  id: { type: String },
  role: { type: String as PropType<RoleName | undefined> },
  groupId: { type: Number, required: true },
  displaySendRequest: { type: Boolean },
  paginationCommand: { type: Object as PropType<PaginationCommand>, required: true }
})

const emit = defineEmits(['reset-validation'])
const collapseElement = ref<HTMLElement>()
let collapseInstance: Collapse = {} as Collapse

const dialogStore = useDialogStore()
const groupStore = useGroupStore()
const userStore = useUserStore()

const isDevEnvironment: ComputedRef<boolean> = computed(() => useSettingsStore().env === 'dev')
const sent = ref(false)
const roleNames: Ref<Array<RoleName>> = ref([])
const notificationsKey = ref(0)
const euloginLoaded = ref(false)
const displayBcc = ref(false)
const emailNotificationsModal: Ref<typeof Modal | null> = ref(null)

const user = computed({
  get: () => userStore.user,
  set: (value) => userStore.setUser(value)
})

const displayRolesAndNotifications = computed(() => props.role !== RoleName.GROUP_ADMIN && props.role !== RoleName.SYS_ADMIN && props.role !== RoleName.OFFICIAL_IN_CHARGE)
const isOperator = computed({
  get: () => roleNames.value.filter(roleName => RoleName.OPERATOR === roleName).length > 0,
  set: (value) => {
    setRole(RoleName.OPERATOR, value)
    if (!value) {
      disableNotifications()
    }
  }
})
const isAdmin = computed({
  get: () => roleNames.value.filter(roleName => RoleName.GROUP_ADMIN === roleName).length > 0,
  set: (value) => setRole(RoleName.GROUP_ADMIN, value)
})
const userRegistrationNotificationEmail: Ref<NotificationEmailSpec> = ref({
  subject: 'Registration service - eTrustEx Web',
  body: groupStore.entity.parent?.templates ? groupStore.entity.parent.templates.find(t => t.type === TemplateType.USER_REGISTRATION_REQUEST_NOTIFICATION)?.content || '' : '',
  businessId: groupStore.business.businessId,
  priority: 0,
  groupId: groupStore.entity.id,
  groupIdentifier: groupStore.entity.identifier,
  userRegistration: true
} as NotificationEmailSpec)

const moreInformationContent = computed(() => {
  return 'To find the unique identifier at the Commission (uid),  connect to EU Login -> My account -> My account details.\n\n' +
    '<br/><a href="https://your.auth.service.host/cas/userdata/ShowDetails.cgi" class="underline-on-hover"> EU Login ID</a>'
})

const v$ = useVuelidate(userDtoRules, { user, roleNames })

onMounted(() => {
  collapseInstance = new Collapse(collapseElement.value as HTMLElement, { toggle: false })
  reset()
})
watch(props, () => reset(), { deep: true })

function initRoles() {
  roleNames.value = []
  if (props.role) {
    roleNames.value.push(props.role)
  }
}

function setRole(roleName: RoleName, value: boolean) {
  roleNames.value = roleNames.value.filter(existingRoleName => roleName !== existingRoleName)
  if (value) {
    roleNames.value.push(roleName)
  }
}

function disableNotifications() {
  user.value.statusNotification = false
  user.value.newMessageNotification = false
  user.value.retentionWarningNotification = false
  notificationsKey.value++
}

function closedPopup(value: boolean) {
  if (value) {
    dialogStore.show('Confirmation request', 'The request email Notifications have been successfully sent', DialogType.INFO)

    emailNotificationsModal.value?.hide()
    return props.paginationCommand.fetch({ page: 0 })
      .then()
  }
}

function save() {
  v$.value.$validate()
  if (v$.value.user.$error || v$.value.roleNames.$error) {
    return
  }
  return UserProfileApi.create({
    groupId: props.groupId,
    ecasId: user.value.ecasId.toLowerCase(),
    name: user.value.name,
    alternativeEmail: user.value.alternativeEmail,
    euLoginEmailAddress: user.value.euLoginEmailAddress,
    alternativeEmailUsed: user.value.alternativeEmailUsed,
    newMessageNotification: user.value.newMessageNotification,
    statusNotification: user.value.statusNotification,
    retentionWarningNotification: user.value.retentionWarningNotification,
    roleNames: roleNames.value
  })
    .then(() => {
      reset()

      emailNotificationsModal.value?.hide()
      return props.paginationCommand.fetch({ page: 0 })
        .then()
    })
    .catch(err => {
      dialogStore.show('Error', 'Something went wrong', DialogType.ERROR)
    })
}

function reset() {
  userStore.resetForm()
  resetValidation()
  initRoles()
  euloginLoaded.value = false
  collapseInstance.hide()
}

function resetValidation() {
  emit('reset-validation')
  useValidationStore()
    .setServerSideValidationErrors([])
  v$.value.$reset()
}

function loadUser() {
  v$.value.user.$validate()
  if (v$.value.user.$error) {
    return
  }
  v$.value.$reset()
  if (useSettingsStore().env === 'dev') {
    euloginLoaded.value = true
  } else {
    UserProfileApi.fetchEuLoginInfo(props.groupId, user.value.ecasId)
      .then(userProfileSpec => {
        if (Object.keys(userProfileSpec).length === 0) {
          dialogStore.show('Account not found', `No account was found for the reference <b>${user.value.ecasId}</b>`, DialogType.INFO)
        } else {
          user.value.ecasId = userProfileSpec.ecasId
          user.value.euLoginEmailAddress = userProfileSpec.euLoginEmailAddress
          user.value.name = userProfileSpec.name
          euloginLoaded.value = true
        }
      }
      )
      .catch(
        (reason) => console.log('Error retrieving the user with error: ' + reason.value))
  }
}

function showPopup() {
  displayBcc.value = false
  emailNotificationsModal.value?.show()
}

</script>
