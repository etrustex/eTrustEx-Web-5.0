<template>
  <entity-configuration-container :title="'Encryption'"
                                  :edit-mode="editMode"
                                  @edit="editMode = true"
                                  @confirm="save"
                                  @reset="cancel">
    <div class="row">
      <label class="col-lg-6 col-form-label">Enforce End-To-End Encryption</label>
      <div class="col-lg-6">
        <div v-if="editMode" class="form-check form-switch">
          <input id="recipientPreferencesCheckbox" type="checkbox" role="switch" class="form-check-input"
                 v-model="statusChecked" v-on:change="toggleEncryptionPreferences()">
          <label class="form-check-label" for="recipientPreferencesCheckbox">{{ activeLabel }}</label>
        </div>
        <div v-else>
          <label class="form-check-label" for="alertDetailsCheckbox">{{ activeLabel }}</label>
        </div>
      </div>
    </div>

    <div class="row">
      <label class="col-lg-6 col-form-label">
        Public Key<span v-if="isPublicKeyRequired()" class="label-required">*</span>
      </label>
      <div class="col col-lg-6">
        <public-key ref="publicKey"
                    :publicKey="publicKeyContent"
                    :publicKeyFileName="publicKeyFileName"
                    :editMode="editMode"
                    @updated="publicKeyContent = $event.rawKey; publicKeyFileName = $event.name"/>
      </div>
    </div>
  </entity-configuration-container>
</template>
<script setup lang="ts">
import { Confidentiality, RecipientPreferences } from '@/model/entities'
import { computed, onMounted, Ref, ref } from 'vue'
import { GroupApi } from '@/shared/api/groupApi'
import { GroupHelper } from '@/utils/groupHelper'
import useGroupStore from '@/admin/store/group'
import { RecipientPreferencesApi } from '@/shared/api/recipientPreferencesApi'
import PublicKey from '@/admin/views/shared/recipient_preferences/PublicKey.vue'
import EntityConfigurationContainer
  from '@/admin/views/entities/entity/configurations/shared/EntityConfigurationContainer.vue'

const groupStore = useGroupStore()
const LIMITED_HIGH = Confidentiality.LIMITED_HIGH
const PUBLIC = Confidentiality.PUBLIC

const publicKeyContent: Ref<string> = ref('')
const publicKeyFileName: Ref<string> = ref('')

const recipientPreferences: Ref<RecipientPreferences> = ref(new RecipientPreferences())
const statusChecked: Ref<boolean> = ref(false)
const editMode: Ref<boolean> = ref(false)
const activeLabel = computed(() => statusChecked.value ? 'Active' : 'Inactive')

onMounted(() => {
  shallowCopyRecipientPreferences()
  statusChecked.value = recipientPreferences.value.confidentiality === LIMITED_HIGH
})

async function save() {
  recipientPreferences.value.publicKey = publicKeyContent.value
  recipientPreferences.value.publicKeyFileName = publicKeyFileName.value

  if (recipientPreferences.value.id) {
    await RecipientPreferencesApi.update(recipientPreferences.value)
      .then(value => {
        groupStore.setRecipientPreferences(value)
      })
  } else {
    await RecipientPreferencesApi.create(recipientPreferences.value)
      .then(value => {
        groupStore.setRecipientPreferences(value)
        const { entity } = groupStore
        GroupApi.update(GroupHelper.toSpec(entity), entity.links)
      })
  }

  editMode.value = false
  shallowCopyRecipientPreferences()
  statusChecked.value = recipientPreferences.value.confidentiality === LIMITED_HIGH
}

function cancel() {
  recipientPreferences.value = { ...groupStore.entity.recipientPreferences }
  statusChecked.value = recipientPreferences.value.confidentiality === LIMITED_HIGH
  editMode.value = false

  publicKeyContent.value = recipientPreferences.value.publicKey
  publicKeyFileName.value = recipientPreferences.value.publicKeyFileName
}

function shallowCopyRecipientPreferences() {
  recipientPreferences.value = groupStore.entity.recipientPreferences
    ? { ...groupStore.entity.recipientPreferences }
    : getDefault()
}

function getDefault(): RecipientPreferences {
  const recipientPreferencesL = new RecipientPreferences()
  recipientPreferencesL.confidentiality = PUBLIC
  return recipientPreferencesL
}

function isPublicKeyRequired() {
  return recipientPreferences.value.confidentiality !== Confidentiality.PUBLIC
}

function toggleEncryptionPreferences() {
  recipientPreferences.value.confidentiality = statusChecked.value ? LIMITED_HIGH : PUBLIC
}

</script>
