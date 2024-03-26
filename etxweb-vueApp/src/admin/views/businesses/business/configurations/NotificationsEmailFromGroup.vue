<template>
  <configuration-container v-if="notificationsEmailFromGroupConfiguration.dtype"
                           :title="'Notification email address'"
                           :info-content="'This functionality can only be configured by a System Administrator'"
                           :edit-mode="editMode"
                           :hide-edit-button="!isSysAdmin"
                           :show-info-button="!isSysAdmin"
                           @edit="editMode = true"
                           @confirm="save"
                           @reset="reset">

    <div class="row">
      <label for="notificationEmail" class="col-lg-4 col-form-label">
        Sender email address
        <more-information
          content="Set up a new email address to be used to send all notifications from the application."
          placement="right"/>
      </label>
      <div class="col-lg-8 align-self-center">
        <div v-if="editMode">
          <input id="notificationEmail" class="form-control"
                 v-model.trim="notificationsEmailFromGroupConfiguration.stringValue">
          <span v-if="v$.stringValue.$error" class="error"> {{ v$.stringValue.$errors[0].$message }} </span>
        </div>
        <strong v-else>{{ notificationsEmailFromGroupConfiguration.stringValue }}</strong>
      </div>
    </div>
  </configuration-container>
</template>

<script setup lang="ts">
import { computed, readonly, ref, Ref } from 'vue'
import { GroupConfigurationSpec, NotificationsEmailFromGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import useAuthenticationStore from '@/shared/store/authentication'
import { validEmails } from '@/admin/views/shared/validation/EmailValidator'
import useVuelidate from '@vuelidate/core'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const authenticationStore = useAuthenticationStore()

const editMode: Ref<boolean> = ref(false)

const { notificationsEmailFromGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let notificationsEmailFromGroupConfigurationCopy = readonly(deepCopy(notificationsEmailFromGroupConfiguration.value))

const isSysAdmin = computed(() => authenticationStore.isSysAdmin())

const notificationEmailFormRule = {
  stringValue: {
    validEmail: validEmails
  }
}

const v$ = useVuelidate(notificationEmailFormRule, notificationsEmailFromGroupConfiguration)

async function save() {
  await v$.value.$validate()
  if (!v$.value.$error) {
    const spec = new GroupConfigurationSpec<string>()
    spec.active = notificationsEmailFromGroupConfiguration.value.active
    spec.value = notificationsEmailFromGroupConfiguration.value.stringValue

    notificationsEmailFromGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<string, NotificationsEmailFromGroupConfiguration>(
      notificationsEmailFromGroupConfiguration.value, spec
    )
    notificationsEmailFromGroupConfigurationCopy = readonly(deepCopy(notificationsEmailFromGroupConfiguration.value))

    editMode.value = false
  }
}

function reset() {
  notificationsEmailFromGroupConfiguration.value = deepCopy(notificationsEmailFromGroupConfigurationCopy) as NotificationsEmailFromGroupConfiguration
  editMode.value = false
  v$.value.$reset()
}

</script>
