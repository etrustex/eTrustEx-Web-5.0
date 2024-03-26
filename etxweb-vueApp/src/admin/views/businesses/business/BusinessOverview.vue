<template>

  <div class="d-flex justify-content-end">
    <div class="btns">
      <button type="button" class="btn btn-outline-primary btn-ico" v-on:click="exportUsersAndFunctionalMailboxes()">
        <span class="ico-standalone">
          <i aria-hidden="true" class="ico-file-download-primary"></i>
        </span>
        <span>Export users and Functional mailboxes</span>
      </button>
      <button type="button" class="btn btn-primary btn-ico" v-on:click="showNotificationsEmailDialog()">
        <span class="ico-standalone">
          <i aria-hidden="true" class="ico-send-white"></i>
        </span>
        <span>Send message</span>
      </button>
    </div>
  </div>

  <div class="d-flex justify-content-end mt-3">
    <a @click="editForm = true"
       class="ico-standalone" type="button">
      <i class="ico-edit"></i>
    </a>
  </div>

  <form class="form-admin" v-bind:class="(editForm)?'form-admin-edit-mode':'form-admin-view-mode'">
    <div class="container">
      <div class="row">
        <label class="col-lg-6 col-form-label" for="name">Identifier<span class="label-required">*</span></label>
        <div class="col-lg-6">
          <div v-if="editForm">
            <input class="form-control" id="name" v-model.trim="businessSpec.identifier" name="Identifier">
            <span v-if="v$.identifier.$error" class="error"> {{ v$.identifier.$errors[0].$message }} </span>
          </div>
          <div v-else>
            <strong>{{ businessSpec.identifier }}</strong>
          </div>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label" for="display_name">Name<span class="label-required">*</span></label>
        <div class="col-lg-6">
          <div v-if="editForm">
            <textarea class="form-control" id="display_name" v-model.trim="businessSpec.displayName"></textarea>
            <span v-if="v$.displayName.$error" class="error"> {{ v$.displayName.$errors[0].$message }} </span>
          </div>
          <div v-else>
            <strong>{{ businessSpec.displayName }}</strong>
          </div>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label" for="description">Description<span
          class="label-required">*</span></label>
        <div class="col-lg-6">
          <div v-if="editForm">
            <textarea class="form-control" id="description" v-model.trim="businessSpec.description"></textarea>
            <span v-if="v$.description.$error" class="error"> {{ v$.description.$errors[0].$message }} </span>
          </div>
          <div v-else><strong>{{ businessSpec.description }}</strong></div>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label" for="status">Status</label>
        <div class="col-lg-6"><strong>{{ businessSpec.active ? 'Active' : 'Inactive' }}</strong></div>
      </div>
      <div v-if="business.auditingEntity" class="row">
        <label class="col-lg-6 col-form-label">Created on</label>
        <div class="col-lg-6">
          <strong>{{ toDateTime(business.auditingEntity.createdDate) }}</strong>
        </div>
      </div>
      <div v-if="business.auditingEntity" class="row">
        <label class="col-lg-6 col-form-label">By</label>
        <div class="col-lg-6"><strong>{{ business.auditingEntity.createdBy }}</strong></div>
      </div>
    </div>

    <div v-if="editForm" class="d-flex justify-content-end">
      <div class="btns">
        <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="reset()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span><span>Cancel</span>
        </button>
        <button type="button" class="btn btn-primary btn-ico" v-on:click="save()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span><span>Save</span>
        </button>
      </div>
    </div>

  </form>

  <email-notification-dialog v-if="notificationEmail.bcc && notificationEmail.bcc.length > 0"
                             ref="emailNotificationsModal"
                             :id="`popup-email`"
                             :displayBcc="true"
                             :notificationEmail="notificationEmail"/>
</template>

<script setup lang="ts">
import { computed, Ref, ref, watch } from 'vue'
import { GroupSpec, NotificationEmailSpec } from '@/model/entities'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import useDialogStore from '@/shared/store/dialog'
import { GroupHelper } from '@/utils/groupHelper'
import { GroupApi } from '@/shared/api/groupApi'
import { toDateTime } from '@/utils/formatters'
import { UserExportApi } from '@/admin/api/userExportApi'
import EmailNotificationDialog from '@/admin/views/shared/dialog/EmailNotificationDialog.vue'
import Modal from '@/shared/views/modal/Modal.vue'
import { useRoute } from 'vue-router'
import useVuelidate from '@vuelidate/core'
import { groupRules } from '@/admin/views/shared/validation/GroupValidator'
import useGroupStore from '@/admin/store/group'

const props = defineProps({
  businessId: { type: Number, required: true }
})

const route = useRoute()
const dialogStore = useDialogStore()
const groupStore = useGroupStore()
const notificationEmail = ref(new NotificationEmailSpec())
const editForm = ref(false)
const business = computed(() => groupStore.business)
const businessSpec = ref(new GroupSpec())
const emailNotificationsModal: Ref<typeof Modal | null> = ref(null)

const v$ = useVuelidate(groupRules, businessSpec)

watch(
  () => route.params.businessId,
  async (newId, previousId) => {
    if (newId && newId !== previousId) {
      businessSpec.value = GroupHelper.toSpec(business.value)
      route.meta.displayName = () => business.value?.name
      notificationEmail.value.businessId = business.value.id
      notificationEmail.value.priority = 0
      notificationEmail.value.userRegistration = false

      UserProfileApi.getNotificationEmails(business.value.id)
        .then(value => {
          notificationEmail.value.bcc = Array.from(value)
        })
        .catch(reason => console.log('Problem getting the emails: ' + reason))
    }
  }, { immediate: true, deep: true }
)

const showNotificationsEmailDialog = () => {
  if (notificationEmail.value.bcc && notificationEmail.value.bcc.length === 0) {
    dialogStore.show('Not Possible to send messages', 'There are no functional mailboxes or users configured to receive notifications in: ' + business.value.identifier)
  } else {
    emailNotificationsModal.value?.show()
  }
}

const exportUsersAndFunctionalMailboxes = () => {
  return UserExportApi.downloadExportUsersAndFunctionalMailboxes(business.value.id)
}

const reset = async () => {
  businessSpec.value = GroupHelper.toSpec(business.value)
  editForm.value = false
  v$.value.$reset()
}

const save = async () => {
  await v$.value.$validate()

  let shouldBlock = false
  v$.value.$errors.forEach(value => {
    for (const groupRulesKey in groupRules) {
      if (groupRulesKey === value.$property) {
        shouldBlock = true
        break
      }
    }
  })

  if (!shouldBlock) {
    console.log('Perform Upgrade')
    groupStore.business = await GroupApi.update(businessSpec.value, business.value.links)
    businessSpec.value = GroupHelper.toSpec(business.value)
    editForm.value = false
  }
}
</script>
