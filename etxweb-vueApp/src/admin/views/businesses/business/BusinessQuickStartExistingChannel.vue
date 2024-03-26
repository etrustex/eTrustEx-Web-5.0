<template>
  <form class="form-admin form-admin-edit-mode" v-on:change="emit('on-validated', null, undefined)">
    <div class="container">
      <div class="row">
        <label class="col-lg-6 col-form-label" for="channelId">Channel name</label>
        <div class="col-lg-6">
          <select class="form-select form-control-lg py-0" id="channelId" v-model="channelSpec.channelId">
            <option v-for="option in channels" :key="option.id" :value="option.id">
              {{ option.name }}
            </option>
          </select>
          <span v-if="v$.channelId.$error" class="error"> {{ v$.channelId.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-6 col-form-label" for="exchangeMode">Entity role</label>
        <div class="col-lg-6">
          <select class="form-select form-control-lg py-0" id="exchangeMode" v-model="channelSpec.entityRole">
            <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
              {{ option.text }}
            </option>
          </select>
          <span v-if="v$.entityRole.$error" class="error"> {{ v$.entityRole.$errors[0].$message }} </span>
        </div>
      </div>
    </div>

    <div class="d-flex justify-content-end mt-3">
      <div class="btns">
        <button type="button" class="btn btn-primary btn-ico" v-on:click="validate()">
          <span>Validate</span>
        </button>
      </div>
    </div>
  </form>
</template>
<script setup lang="ts">

import { computed, onMounted, PropType, Ref, ref } from 'vue'
import useChannelStore from '@/admin/store/channel'
import { ExchangeMode, GroupSpec } from '@/model/entities'
import useVuelidate from '@vuelidate/core'
import { channelConfigRules } from '@/admin/views/shared/validation/ChannelConfigValidator'
import useValidationStore from '@/shared/store/validation'

export type ChannelConfigSpec = {
  channelId: number;
  entityRole: ExchangeMode;
  addToChannel: boolean;
}

const props = defineProps({
  businessId: { type: Number as PropType<number>, required: true }
})
const emit = defineEmits(['on-validated'])
defineExpose({ validate, reset })

const channelStore = useChannelStore()

const channelSpec: Ref<ChannelConfigSpec> = ref({} as ChannelConfigSpec)
const channels = computed(() => channelStore.channels)
const defaultChannel = computed(() => channelStore.channels.filter(value => value.defaultChannel)[0])

const v$ = useVuelidate(channelConfigRules, channelSpec, { $stopPropagation: true })

const exchangeModeOptions = [
  { value: ExchangeMode.BIDIRECTIONAL, text: ExchangeMode.BIDIRECTIONAL },
  { value: ExchangeMode.SENDER, text: ExchangeMode.SENDER },
  { value: ExchangeMode.RECIPIENT, text: ExchangeMode.RECIPIENT }
]

onMounted(() => {
  channelStore.fetchChannels({ businessId: props.businessId })
    .then(() => {
      channelSpec.value.addToChannel = true
      if (defaultChannel.value && defaultChannel.value.id) {
        channelSpec.value.channelId = defaultChannel.value.id
        channelSpec.value.entityRole = defaultChannel.value.defaultExchangeMode
      }
    })
})

function validate() {
  channelSpec.value.addToChannel = true

  v$.value.$validate()

  if (v$.value.$error) {
    emit('on-validated', null, false)
    return
  }

  emit('on-validated', channelSpec.value, true)
}

function reset() {
  channelSpec.value = new GroupSpec()

  useValidationStore()
    .setServerSideValidationErrors([])
  v$.value.$reset()
}
</script>
