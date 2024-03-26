<template>
  <div class="container">
    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Entity Identifier</label>
      <div class="col-lg-6">
        <strong class="text-break">{{ exchangeRule.member.identifier }}</strong>
      </div>
    </div>

    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Entity Name</label>
      <div class="col-lg-6">
        <strong class="text-break">{{ exchangeRule.member.name }}</strong>
      </div>
    </div>

    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Back-end system connection</label>
      <div class="col-lg-6">
        <img v-if="exchangeRule.member.isSystem" alt="System Connection" style="width: 24px;"
             src="@/assets/ico/rounded-check-circle-success.svg">
      </div>
    </div>

    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Created on</label>
      <div class="col-lg-2 d-flex align-items-center"><strong>{{ toDateTime(exchangeRule.auditingEntity.createdDate) }}</strong></div>
      <div class="col-lg-1 d-flex align-items-center">By</div>
      <div class="col-lg-3 d-flex align-items-center"><strong>{{ exchangeRule.auditingEntity.createdBy }}</strong></div>
    </div>

    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Modified on</label>
      <div class="col-lg-2 d-flex align-items-center"><strong>{{ toDateTime(exchangeRule.auditingEntity.modifiedDate) }}</strong></div>
      <div class="col-lg-1 d-flex align-items-center">By</div>
      <div class="col-lg-3 d-flex align-items-center"><strong>{{ exchangeRule.auditingEntity.modifiedBy }}</strong></div>
    </div>

    <div class="row d-flex align-items-center">
      <label class="col-lg-6 col-form-label">Role</label>
      <div v-if="row.item.isEditMode" class="col-lg-6">
        <select class="form-select" id="exchangeMode" v-model="editExchangeMode">
          <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
            {{ option.text }}
          </option>
        </select>
      </div>
      <div v-else class="col-lg-6">
        <strong class="text-break">{{ exchangeRule.exchangeMode }}</strong>
      </div>
    </div>

    <div v-if="row.item.isEditMode" class="row mt-3 mb-3">
      <div class="col-lg-12 text-end btns">
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
  </div>
</template>
<script setup lang="ts">
import { onMounted, PropType, ref, Ref } from 'vue'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { ExchangeMode, ExchangeRule } from '@/model/entities'
import useChannelStore from '@/admin/store/channel'
import useExchangeRuleStore from '@/admin/store/exchangeRule'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import { toDateTime } from '@/utils/formatters'

const props = defineProps({
  exchangeRule: { type: Object as PropType<ExchangeRule>, required: true },
  paginationCommand: { type: Object as PropType<PaginationCommand>, required: true },
  row: { type: Object, required: true }
})

const channelStore = useChannelStore()
const exchangeRuleStore = useExchangeRuleStore()
const exchangeModeOptions = [
  { value: ExchangeMode.BIDIRECTIONAL, text: ExchangeMode.BIDIRECTIONAL },
  { value: ExchangeMode.SENDER, text: ExchangeMode.SENDER },
  { value: ExchangeMode.RECIPIENT, text: ExchangeMode.RECIPIENT }
]

const editExchangeMode: Ref<ExchangeMode> = ref(props.exchangeRule.exchangeMode)

onMounted(() => {
  editExchangeMode.value = props.exchangeRule.exchangeMode
})

function reset() {
  editExchangeMode.value = props.exchangeRule.exchangeMode
  props.row.item.isEditMode = false
  props.row.toggleDetails()
}

function save() {
  return ExchangeRuleApi.update({
    exchangeMode: editExchangeMode.value,
    channelId: channelStore.channel.id,
    memberId: props.exchangeRule.member.id
  })
    .then(() => props.paginationCommand.fetch({
      page: props.paginationCommand
        .currentOrLast(exchangeRuleStore.exchangeRulePage.totalElements, exchangeRuleStore.exchangeRulePage.number)
    }))
    .then(() => reset())
}
</script>
