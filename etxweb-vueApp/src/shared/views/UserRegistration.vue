<template>
  <form>
    <div class="container-fluid my-5">
      <div class="row mt-3">
        <div class="col text-center">
          <h1>User Registration</h1>
          <p>To Request access to the below entity in eTrustEx Web, please check the information and click on 'Send'.</p>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col col-lg-5 text-lg-end">
          Entity
        </div>
        <div class="col col-lg-4">
          <strong>{{ userRegistrationRequestSpec.groupIdentifier }}</strong>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col col-lg-5 text-lg-end">
          User name
        </div>
        <div class="col col-lg-4">
          <strong>{{ name }}</strong>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col col-lg-5 text-lg-end">
          User email
        </div>
        <div class="col col-lg-4">
          <strong>{{ userRegistrationRequestSpec.requesterEmailAddress }}</strong>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col col-lg-5 text-lg-end">
          <label>
            Notification email address
            <more-information
              :content="'All notifications are sent to the email registered in EU Login. If you would like to receive the notifications in a different email address, please add it to this field.'"
              placement="right"/>
          </label>
        </div>
        <div class="col col-lg-4">
          <input id="notificationEmail" type="text" class="form-control"
                 v-model.trim="userRegistrationRequestSpec.notificationEmail">
          <span v-if="v$.notificationEmail.$error" class="error"> {{ v$.notificationEmail.$errors[0].$message }} </span>
        </div>
      </div>

      <div v-if="sent" class="row">
        <div class="col col-lg-5"></div>
        <div class="col col-lg-4">
          <p class="alert alert-success"><strong>Your request has been successfully submitted.</strong></p>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col col-lg-5"></div>
        <div class="col col-lg-4 text-end">
          <button type="button" class="btn btn-primary" :disabled="sent" v-on:click="send()">Send</button>
        </div>
      </div>


    </div>
  </form>
</template>

<script setup lang="ts">
import { computed, PropType, Ref, ref } from 'vue'
import { User, UserRegistrationRequestSpec } from '@/model/entities'
import useAuthenticationStore from '@/shared/store/authentication'
import { UserRegistrationRequestApi } from '@/admin/api/userRegistrationRequestApi'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import useVuelidate from '@vuelidate/core'
import { userRegistrationRequestRules } from '@/admin/views/shared/validation/UserRegistrationRequestValidator'

const authenticationStore = useAuthenticationStore()

const props = defineProps({
  groupId: { type: Number, required: true },
  groupIdentifier: { type: String, required: true },
  user: { type: Object as PropType<User>, required: true }
})

const sent = ref(false)
const name = computed(() => authenticationStore.fullName)
const userRegistrationRequestSpec: Ref<UserRegistrationRequestSpec> = ref(({
  ecasId: props.user.ecasId,
  groupId: props.groupId,
  groupIdentifier: props.groupIdentifier,
  notificationEmail: '',
  requesterEcasId: authenticationStore.username,
  requesterEmailAddress: authenticationStore.userDetails.email,
  isAdmin: false,
  isOperator: true,
  admin: false,
  operator: true
}))

const v$ = useVuelidate(userRegistrationRequestRules, userRegistrationRequestSpec)

function send() {
  UserRegistrationRequestApi.create(userRegistrationRequestSpec.value).then(() => sent.value = true)
}
</script>
