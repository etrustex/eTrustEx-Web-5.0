<template>
  <form class="form-admin form-admin-edit-mode" v-on:change="emit('on-validated', null, undefined)">
    <div class="container">
      <div class="row">
        <label class="col-lg-6 col-form-label" for="identifier">Identifier<span class="label-required">* </span>
          <more-information
            :content="'You should only use letters, digits, hyphens and/or underscores for the Identifier'"
            placement="right"/>
        </label>

        <div class="col-lg-6">
          <input id="identifier" type="text" v-model.trim="groupForm.identifier" class="form-control">
          <span v-if="v$.identifier.$error" class="error"> {{ v$.identifier.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-6 col-form-label" for="displayName">Name<span class="label-required">*</span></label>
        <div class="col-lg-6">
          <input id="displayName" type="text" v-model.trim="groupForm.displayName" class="form-control">
          <span v-if="v$.displayName.$error" class="error"> {{ v$.displayName.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-6 col-form-label" for="description">Description<span
          class="label-required">*</span></label>
        <div class="col-lg-6">
          <input id="description" type="text" v-model.trim="groupForm.description" class="form-control">
          <span v-if="v$.description.$error" class="error"> {{ v$.description.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row" v-if="groupType === GroupType.ENTITY">
        <label class="col-lg-6 col-form-label" for="active">Status</label>
        <div class="col-lg-6">
          <select id="active" class="form-select" v-model="groupForm.active">
            <option :value="true">Active</option>
            <option :value="false">Inactive</option>
          </select>
        </div>
      </div>

      <div class="row">
        <div class="col-lg-6 col-form-label">Entity only used by a back-end system
          <more-information
            :content="'Select this if the entity will connect to eTrustEx via REST API'"
            placement="right"/>
        </div>
        <div class="col-lg-6">
          <input id="isSystem" type="checkbox" class="form-check-input" v-model="groupForm.system"
                 v-on:change="groupForm.endpoint = undefined">
        </div>
      </div>

      <div v-if="groupForm.system" class="row">
        <label class="col-lg-6 col-form-label" for="endpoint">Endpoint</label>
        <div class="col-lg-6">
          <input id="endpoint" type="text" v-model.trim="groupForm.endpoint" class="form-control">
        </div>
      </div>

      <div class="row" v-if="groupType === GroupType.ENTITY && !onlyValidate">
        <label class="col-lg-6 col-form-label">Add to channel</label>
        <div class="col-lg-6">
          <div class="form-check form-switch form-control-lg py-0">
            <input type="checkbox" role="switch" class="form-check-input" name="'check-button'"
                   v-model="groupForm.addToChannel">
          </div>
        </div>
      </div>

      <div class="row" v-if="groupType === GroupType.ENTITY && groupForm.addToChannel && !onlyValidate">
        <label class="col-lg-6 col-form-label" for="channelId">Channel name</label>
        <div class="col-lg-6">
          <select class="form-select form-control-lg py-0" id="channelId" v-model="groupForm.channelId">
            <option v-for="option in channels" :key="option.id" :value="option.id">
              {{ option.name }}
            </option>
          </select>
          <span v-if="v$.channelId.$error" class="error"> {{ v$.channelId.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row" v-if="groupType === GroupType.ENTITY && groupForm.addToChannel && !onlyValidate">
        <label class="col-lg-6 col-form-label" for="exchangeMode">Entity role</label>
        <div class="col-lg-6">
          <select class="form-select form-control-lg py-0" id="exchangeMode" v-model="groupForm.entityRole">
            <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
              {{ option.text }}
            </option>
          </select>
          <span v-if="v$.entityRole.$error" class="error"> {{ v$.entityRole.$errors[0].$message }} </span>
        </div>
      </div>
    </div>

    <div class="d-flex justify-content-end mt-3">
      <div v-if="onlyValidate" class="btns">
        <button type="button" class="btn btn-primary btn-ico" v-on:click="validate()">
          <span>Validate</span>
        </button>
      </div>

      <div v-else class="btns">
        <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="reset()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
          <span>Cancel</span>
        </button>
        <button type="button" class="btn btn-primary btn-ico" v-on:click="save()">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
          <span>Save</span>
        </button>
      </div>
    </div>

  </form>
</template>
<script setup lang="ts">
import { computed, onMounted, PropType, ref } from 'vue'
import { ExchangeMode, GroupSpec, GroupType } from '@/model/entities'
import useValidationStore from '@/shared/store/validation'
import useVuelidate from '@vuelidate/core'
import { groupRules } from '@/admin/views/shared/validation/GroupValidator'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import useChannelStore from '@/admin/store/channel'
import { GroupApi } from '@/shared/api/groupApi'

const props = defineProps({
  id: { type: String },
  groupType: { type: String as PropType<GroupType>, required: true },
  parentGroupId: { type: Number },
  onlyValidate: { type: Boolean, default: false },
})
const emit = defineEmits(['on-save', 'on-validated', 'cancel'])
defineExpose({ save, reset })

const currentGroupStore = useCurrentGroupStore()
const channelStore = useChannelStore()
const groupForm = ref(new GroupSpec())
const channels = computed(() => channelStore.channels)
const defaultChannel = computed(() => channelStore.channels.filter(value => value.defaultChannel)[0])

const v$ = useVuelidate(groupRules, groupForm, { $stopPropagation: true })

const exchangeModeOptions = [
  { value: ExchangeMode.BIDIRECTIONAL, text: ExchangeMode.BIDIRECTIONAL },
  { value: ExchangeMode.SENDER, text: ExchangeMode.SENDER },
  { value: ExchangeMode.RECIPIENT, text: ExchangeMode.RECIPIENT },
]

onMounted(() => {
  reset()

  channelStore.fetchChannels({ businessId: props.parentGroupId })
    .then(value => {
      if (!!defaultChannel && defaultChannel.value && defaultChannel.value.id) {
        groupForm.value.addToChannel = true
        groupForm.value.channelId = defaultChannel.value.id
        groupForm.value.entityRole = defaultChannel.value.defaultExchangeMode
      } else {
        groupForm.value.addToChannel = false
      }
    })
})

function validate() {
  v$.value.$validate()

  if (v$.value.$error) {
    emit('on-validated', null, false)
    return
  }

  GroupApi.isValid(groupForm.value)
    .then(isValid => isValid ? emit('on-validated', groupForm.value, true) : emit('on-validated', null, false))
    .catch(() => emit('on-validated', null, false))
}

function save() {
  v$.value.$validate()

  if (v$.value.$error) {
    return
  }

  emit('on-save', groupForm.value, true)
}

function reset() {
  v$.value.$reset()
  groupForm.value = new GroupSpec()
  groupForm.value.active = true
  groupForm.value.system = false
  groupForm.value.type = props.groupType
  groupForm.value.parentGroupId = <number>props.parentGroupId
  if (!!defaultChannel && defaultChannel.value && defaultChannel.value.id) {
    groupForm.value.addToChannel = true
    groupForm.value.channelId = defaultChannel.value.id
    groupForm.value.entityRole = defaultChannel.value.defaultExchangeMode
  } else {
    groupForm.value.addToChannel = false
  }

  useValidationStore()
    .setServerSideValidationErrors([])

  emit('cancel')
}

</script>
