<template>
  <form class="form-admin form-admin-edit-mode" v-on:change="emit('on-validated', null, undefined)">
    <div class="container">
      <div class="row">
        <label class="col-lg-6 col-form-label" for="name">Name<span class="label-required">*</span></label>
        <div class="col-lg-6">
          <input id="name" class="form-control" v-model.trim="channelSpec.name" name="'Channel Name'">
          <span v-if="v$.name.$error" class="error"> {{ v$.name.$errors[0].$message }} </span>
        </div>
      </div>

      <div class="row">
        <label class="col-lg-6 col-form-label" for="description">Description</label>
        <div class="col-lg-6">
          <input id="description" class="form-control" v-model.trim="channelSpec.description">
          <span v-if="v$.description.$error" class="error"> {{ v$.description.$errors[0].$message }} </span>
        </div>
      </div>

      <div v-if="onlyValidate" class="row">
        <label class="col-lg-6 col-form-label">Entity Role<span class="label-required">*</span></label>
        <div class="col-lg-6">
          <select class="form-select" id="exchangeMode" v-model="channelSpec.defaultExchangeMode">
            <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
              {{ option.text }}
            </option>
          </select>
          <span v-if="v$.defaultExchangeMode.$error" class="error">
            {{ v$.defaultExchangeMode.$errors[0].$message }}
          </span>

          <input id="defaultChannel" type="checkbox" role="switch" class="form-check-input"
                 v-model="channelSpec.defaultChannel"> &nbsp;
          <strong class="form-check-label">Set this channel as default when creating an entity</strong>
        </div>
      </div>

      <div v-else class="row">
        <label class="col-lg-6 col-form-label">Default</label>
        <div class="col-lg-6">
          <input id="defaultChannel" type="checkbox" role="switch" class="form-check-input"
                 v-model="channelSpec.defaultChannel"> &nbsp;
          <strong class="form-check-label">Set this channel as default when creating an entity</strong>
          <select class="form-select" id="exchangeMode" v-model="channelSpec.defaultExchangeMode">
            <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
              {{ option.text }}
            </option>
          </select>
          <span v-if="v$.defaultExchangeMode.$error" class="error">
            {{ v$.defaultExchangeMode.$errors[0].$message }}
          </span>
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
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ChannelSpec, ExchangeMode } from '@/model/entities'
import useValidationStore from '@/shared/store/validation'
import useVuelidate from '@vuelidate/core'
import { channelRules } from '@/admin/views/shared/validation/ChannelValidator'
import { ChannelApi } from '@/admin/api/channelApi'

const props = defineProps({
  onlyValidate: { type: Boolean, default: false }
})
const emit = defineEmits(['on-save', 'on-validated'])
defineExpose({ save, reset })

const route = useRoute()
const channelSpec = ref({ ...new ChannelSpec(), onlyValidate: props.onlyValidate })

const v$ = useVuelidate(channelRules, channelSpec, { $stopPropagation: true })

onMounted(() => {
  reset()
})

const exchangeModeOptions = [
  { value: ExchangeMode.BIDIRECTIONAL, text: ExchangeMode.BIDIRECTIONAL },
  { value: ExchangeMode.SENDER, text: ExchangeMode.SENDER },
  { value: ExchangeMode.RECIPIENT, text: ExchangeMode.RECIPIENT }
]

function save() {
  v$.value.$validate()

  if (v$.value.$error) {
    return
  }

  emit('on-save', channelSpec.value)
}

function validate() {
  v$.value.$validate()

  if (v$.value.$error || !channelSpec.value.defaultExchangeMode) {
    emit('on-validated', null, false)
    return
  }

  ChannelApi.validateChannel(channelSpec.value)
    .then(isValid => isValid ? emit('on-validated', channelSpec.value, true) : emit('on-validated', null, false))
    .catch(() => emit('on-validated', null, false))
}

function reset() {
  channelSpec.value = {
    businessId: +route.params.businessId,
    isActive: true,
    onlyValidate: props.onlyValidate
  } as ChannelSpec & { onlyValidate: boolean }

  useValidationStore()
    .setServerSideValidationErrors([])
  v$.value.$reset()
}

</script>
