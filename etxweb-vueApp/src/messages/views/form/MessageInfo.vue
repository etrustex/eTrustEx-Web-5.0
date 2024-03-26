 <template>
  <div class="row mt-3">
    <span class="col-lg-3"></span>
    <div class="col-lg-9 text-center">
      <h2 class="h3 variant">Receiver info</h2>
    </div>
  </div>

  <div class="row mt-3">
    <label class="col-lg-3 col-form-label" for="recipientGroup">To<span class="label-required">*</span></label>
    <div class="col-lg-9">
      <group-search :businessId="businessId" :validRecipients="validRecipients"
                    :selectedRecipientIds="selectedRecipientIds"
                    :showIcons="true" :showIdentifier="false"
                    @selected="selectRecipients"/>
      <input :value="messageRecipients" hidden="hidden" type="text">
      <span v-if="props.v$.messageRecipients.$error" class="error">
        {{ v$.messageRecipients.$errors[0].$message }}
      </span>
    </div>
  </div>

  <div class="row mt-3">
    <label class="col-lg-3 col-form-label" for="subject">Subject<span class="label-required">*</span></label>
    <div class="col-lg-9">
      <input id="subject" type="text" class="form-control" v-model.trim="message.subject">
      <span v-if="v$.message.subject.$error" class="error"> {{ v$.message.subject.$errors[0].$message }} </span>
    </div>
  </div>

  <div class="row mt-3">
    <label class="col-lg-3 col-form-label" for="text">Message</label>
    <div class="col-lg-9">
      <textarea id="text" class="form-control" v-model="message.text"></textarea>
    </div>
  </div>

  <div class="d-flex align-items-center mt-3">
    <div class="form-check form-switch">
      <input id="e2eEnabledCheckboxId" class="d-inline form-check-input" type="checkbox"
             :value="e2eEnabled" :disabled="e2eReadOnly" :checked="e2eEnabled" @change="$emit('toggle-e2e')">
      <label class="form-check-label" for="e2eEnabledCheckboxId">end-to-end encryption</label>
    </div>
    <button type="button" class="ico-standalone" v-on:click="e2eInfo = !e2eInfo">
      <span aria-hidden="true" class="ico-standalone"><i class="ico-info-primary"></i></span>
    </button>
  </div>

  <div class="mt-2" v-show="e2eInfo">
    <div role="alert" class="alert alert-info alert-dismissible show">
      <h3 class="h5">end-to-end encryption</h3>
      <p>To be able to send a message with end-to-end encryption the recipient entity needs to have a public
        key configured.</p>
      <p>Entities can have:</p>
      <ul>
        <li>
          <span class='ico-conjunction'><i class='ico-conjunction ico-encrypted-mandatory'/>end-to-end encryption is mandatory</span>
        </li>
        <li>
          <span class='ico-conjunction'><i class='ico-encrypted'/>end-to-end encryption is optional</span>
        </li>
        <li>
          <strong>no icon</strong> entity does not have a public key configured
        </li>
      </ul>
      <p class="mb-0">If the functionality is greyed out, the action is enforced.</p>
    </div>
  </div>

  <div class="mt-1">
    <div class="form-check form-switch">
      <input id="highImportanceCheckboxId" class="d-inline form-check-input" type="checkbox"
             v-model="message.highImportance">
      <label class="form-check-label" for="highImportanceCheckboxId"> High importance </label>
    </div>
  </div>

  <div v-if="isSignatureEnabled" class="mt-2">
    <div class="form-check form-switch">
      <input id="signingCheckboxId" class="d-inline form-check-input" type="checkbox"
             :value="sign" @change="$emit('toggle-sign')">
      <label class="form-check-label" for="signingCheckboxId"> Sign the message </label>
    </div>
    <certificate-view v-if="sign" :allow-expired="false"/>
  </div>
  <custom-template :v$="v$" :template="customFormTemplate"/>
</template>

<script setup lang="ts">
import GroupSearch from '@/admin/views/shared/group/GroupSearch.vue'
import CertificateView from '@/messages/views/CertificateView.vue'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { computed, ref, Ref } from 'vue'
import { Group } from '@/model/entities'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import CustomTemplate from '@/messages/views/CustomTemplate.vue'
import useMessageFormStore from '@/messages/store/messageForm'
import { storeToRefs } from 'pinia'

const props = defineProps({
  e2eEnabled: { type: Boolean, required: true },
  e2eReadOnly: { type: Boolean, required: true },
  sign: { type: Boolean, required: true },
  v$: { type: Object, required: true }
})

const emit = defineEmits(['select-recipients', 'toggle-e2e', 'toggle-sign'])

const currentGroupStore = useCurrentGroupStore()
const messageFormStore = useMessageFormStore()

const { currentGroup, recipients } = storeToRefs(currentGroupStore)
const businessId = computed(() => currentGroup.value.businessId)
const validRecipients = computed(() => currentGroupStore.recipients.map(value => value.id))

const businessConfigurationStore = useBusinessConfigurationStore()
const isSignatureEnabled = computed(() => businessConfigurationStore.signatureGroupConfiguration.active)

const { message, messageRecipients, templateVariables } = storeToRefs(messageFormStore)
const selectedRecipientIds = computed(() => messageRecipients.value.map(value => value.id))

const e2eInfo: Ref<boolean> = ref(false)

const customFormTemplate = computed(() => currentGroupStore.customFormTemplate)

const v$ = ref(props.v$)

function selectRecipients(groups: Array<Group>) {
  messageRecipients.value = recipients.value.filter(recipient => groups.find(group => group.id === recipient.id))
}

</script>
