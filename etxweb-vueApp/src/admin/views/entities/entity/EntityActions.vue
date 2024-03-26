<template>
  <div class="d-flex justify-content-between align-items-center">
    <h2 class="title-secondary text-break"><span>Entity</span>{{ group.name }}</h2>
    <div class="btns">
      <button type="button" class="btn btn-outline-primary btn-ico" @click="exportUsersAndFunctionalMailboxes()">
        <span class="ico-standalone">
          <i aria-hidden="true" class="ico-file-download-primary"></i>
        </span>
        <span>
          Export users
        </span>
      </button>
      <button type="button" class="btn btn-primary btn-ico" @click="showPopup()">
        <span class="ico-standalone">
          <i aria-hidden="true" class="ico-send-white"></i>
        </span>
        <span>
          Send message
        </span>
      </button>
    </div>

    <email-notification-dialog v-if="notificationEmail.bcc && notificationEmail.bcc.length > 0"
                               :id="`popup-email`"
                               :displayBcc="true"
                               :notificationEmail="notificationEmail"
                               ref="emailNotificationsModal"/>
  </div>
</template>
<script setup lang="ts">

import EmailNotificationDialog from '@/admin/views/shared/dialog/EmailNotificationDialog.vue'
import { onMounted, PropType, Ref, ref } from 'vue'
import { Group, NotificationEmailSpec } from '@/model/entities'
import { UserExportApi } from '@/admin/api/userExportApi'
import useDialogStore from '@/shared/store/dialog'
import useGroupStore from '@/admin/store/group'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import Modal from '@/shared/views/modal/Modal.vue'

const props = defineProps({
  group: { type: Object as PropType<Group>, required: true }
})

const dialogStore = useDialogStore()
const notificationEmail = ref(new NotificationEmailSpec())
const emailNotificationsModal: Ref<typeof Modal | null> = ref(null)

onMounted(() => {
  notificationEmail.value.businessId = useGroupStore().business.businessId
  notificationEmail.value.priority = 0
  notificationEmail.value.groupId = props.group.id
  notificationEmail.value.groupIdentifier = props.group.identifier
  notificationEmail.value.userRegistration = false
  UserProfileApi.getNotificationEmails(props.group.id)
    .then(value => {
      notificationEmail.value.bcc = Array.from(value)
    })
    .catch(reason => console.log('Problem getting the emails: ' + reason))
})

function exportUsersAndFunctionalMailboxes() {
  return UserExportApi.downloadExportUsersAndFunctionalMailboxes(props.group.id)
}

function showPopup() {
  if (notificationEmail.value.bcc.length === 0) {
    dialogStore.show('Not Possible to send messages', 'There are no functional mailboxes or users configured to receive notifications in: ' + props.group.identifier)
  } else {
    emailNotificationsModal.value?.show()
  }
}

</script>
