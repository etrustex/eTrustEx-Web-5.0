<template>

  <div v-if="editable" class="d-flex justify-content-end mt-3">
    <button @click="editMode = true" class="ico-standalone" type="button">
      <i class="ico-edit"></i>
    </button>
  </div>

  <form class="form-admin" v-bind:class="(editMode)?'form-admin-edit-mode':'form-admin-view-mode'">
    <div class="container">
      <div class="row">
        <label class="col-lg-6 col-form-label" for="name">Name</label>
        <div class="col-lg-6">
          <strong v-if="!editMode" class="text-break">{{ channelSpec.name }}</strong>
          <div v-else>
            <input class="form-control" id="name" v-model.trim="channelSpec.name" name="'Channel Name'">
            <span v-if="v$.name.$error" class="error"> {{ v$.name.$errors[0].$message }} </span>
          </div>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label" for="description">Description</label>
        <div class="col-lg-6">
          <strong v-if="!editMode" class="text-break">{{ channelSpec.description }}</strong>
          <div v-else>
            <input class="form-control" id="description" v-model.trim="channelSpec.description"
                   name="'Channel Description'">
            <span v-if="v$.description.$error" class="error"> {{ v$.description.$errors[0].$message }} </span>
          </div>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label">Status</label>
        <div class="col-lg-6">
          <strong>{{ props.channel.active ? 'Active' : 'Inactive' }}</strong>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label">Created on</label>
        <div class="col-lg-3">
          <strong>{{ toDateTime(props.channel.auditingEntity.createdDate) }}</strong>
        </div>
        <div class="col-lg-3">
          By <strong>{{ props.channel.auditingEntity.createdBy }}</strong>
        </div>
      </div>
      <div class="row">
        <label class="col-lg-6 col-form-label">Modified on</label>
        <div class="col-lg-3">
          <strong>{{ toDateTime(props.channel.auditingEntity.modifiedDate) }}</strong>
        </div>
        <div class="col-lg-3">
          By <strong>{{ props.channel.auditingEntity.modifiedBy }}</strong>
        </div>
      </div>
      <div class="row">
        <div class="col-lg-6 col-form-label">
          Default <img v-if="!editMode && channelSpec.defaultChannel" alt="Default Channel" style="width: 24px;"
                       src="@/assets/ico/rounded-check-circle-success.svg">
        </div>
        <div class="col-lg-6">
          <strong v-if="!editMode" class="text-break font-weight-bold">{{ channelSpec.defaultExchangeMode }}</strong>
          <div v-else>
            <input id="defaultChannel" type="checkbox" role="switch" class="form-check-input"
                   v-model="channelSpec.defaultChannel" v-on:change="changeDefaultChannel()">
            <strong class="form-check-label" for="defaultChannel">
              Set this channel as default when creating an entity
            </strong>
            <select class="form-select mt-2" id="exchangeMode" v-model="channelSpec.defaultExchangeMode">
              <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
                {{ option.text }}
              </option>
            </select>
            <span v-if="v$.defaultExchangeMode.$error" class="error"> {{ v$.defaultExchangeMode.$errors[0].$message }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="editMode" class="d-flex justify-content-end mt-3">
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
</template>

<script lang="ts" setup>
import { onMounted, PropType, Ref, ref } from 'vue'
import { Channel, ChannelSpec, ExchangeMode } from '@/model/entities'
import useValidationStore from '@/shared/store/validation'
import { ChannelApi } from '@/admin/api/channelApi'
import useChannelStore from '@/admin/store/channel'
import { toDateTime } from '@/utils/formatters'
import useVuelidate from '@vuelidate/core'
import { channelRules } from '@/admin/views/shared/validation/ChannelValidator'
import { onBeforeRouteUpdate } from 'vue-router'

const props = defineProps({
  channel: { type: Object as PropType<Channel>, required: true },
  editable: { type: Boolean }
})

const store = useChannelStore()
const editMode = ref(false)
const channelSpec: Ref<Omit<ChannelSpec, 'defaultExchangeMode'> & {
  defaultExchangeMode: ExchangeMode | undefined
}> = ref({} as ChannelSpec)

const v$ = useVuelidate(channelRules, channelSpec)

onMounted(() => reset())

onBeforeRouteUpdate((to, from, next) => {
  reset()
  next()
})

const exchangeModeOptions = [
  { value: ExchangeMode.BIDIRECTIONAL, text: ExchangeMode.BIDIRECTIONAL },
  { value: ExchangeMode.SENDER, text: ExchangeMode.SENDER },
  { value: ExchangeMode.RECIPIENT, text: ExchangeMode.RECIPIENT }
]

function changeDefaultChannel() {
  if (!channelSpec.value.defaultChannel) {
    channelSpec.value.defaultExchangeMode = undefined
  }
}

function save() {
  v$.value.$validate()

  if (v$.value.$error) {
    return
  }

  return ChannelApi.updateChannel(props.channel.id, channelSpec.value as ChannelSpec)
    .then(() => ChannelApi.getChannel(props.channel.id))
    .then((value) => {
      store.channel = value
      editMode.value = false
    })
}

function reset() {
  // this.$refs.observer.reset()
  useValidationStore()
    .setServerSideValidationErrors([])
  channelSpec.value = {
    businessId: props.channel.business.businessId,
    isActive: true,
    id: props.channel.id,
    name: props.channel.name,
    description: props.channel.description,
    defaultChannel: props.channel.defaultChannel,
    defaultExchangeMode: props.channel?.defaultExchangeMode
  } as ChannelSpec
  editMode.value = false
  v$.value.$reset()
}
</script>
