<template>
  <div class="collapse collapse-content-box" :id="id" ref="collapseElement">
    <form class="form-admin form-admin-edit-mode" @keydown.enter.prevent="$event.preventDefault()">
      <div class="container">

        <div class="row">
          <label class="col-lg-6 col-form-label" for="exchangeMode">Role<span class="label-required">*</span></label>
          <div class="col-lg-6">
            <select class="form-select" id="exchangeMode" v-model="exchangeRuleForm.exchangeMode">
              <option v-for="option in exchangeModeOptions" :key="option.value" :value="option.value">
                {{ option.text }}
              </option>
            </select>
            <span v-if="v$.exchangeMode.$error" class="error"> {{ v$.exchangeMode.$errors[0].$message }} </span>
          </div>
        </div>

        <div class="row">
          <label class="col-lg-6 col-form-label" for="groupSearch">Participant<span
            class="label-required">*</span></label>
          <div class="col-lg-6">
            <group-search ref="groupSearchRef" :businessId="businessId" :selectedRecipientIds="selectedGroups"
                          @selected="selectGroups" :showIcons="false" :showIdentifier="true"/>
          </div>
        </div>

      </div>

      <div v-if="showButtons" class="d-flex justify-content-end mt-3">
        <div class="btns">
          <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="reset">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
            <span>Cancel</span>
          </button>
          <button type="button" class="btn btn-primary btn-ico" v-on:click="save(false)">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
            <span>Save</span>
          </button>
        </div>
      </div>

    </form>
  </div>
</template>
<script setup lang="ts">

import { computed, onMounted, PropType, ref, Ref } from 'vue'
import { ExchangeMode, ExchangeRule, Group } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import useChannelStore from '@/admin/store/channel'
import useExchangeRuleStore from '@/admin/store/exchangeRule'
import useValidationStore from '@/shared/store/validation'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import ExchangeRuleDto from '@/model/ExchangeRuleDto'
import { Collapse } from 'bootstrap'
import GroupSearch from '@/admin/views/shared/group/GroupSearch.vue'
import { exchangeRuleRules } from '@/admin/views/shared/validation/ExchangeRuleValidator'
import useVuelidate from '@vuelidate/core'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'

const props = defineProps({
  id: { type: String },
  paginationCommand: { type: Object as PropType<PaginationCommand>, required: true },
  businessId: { type: Number }
})

const dialogStore = useDialogStore()
const channelStore = useChannelStore()
const exchangeRuleStore = useExchangeRuleStore()
const validationStore = useValidationStore()

const exchangeModeOptions = [
  { value: ExchangeMode.BIDIRECTIONAL, text: ExchangeMode.BIDIRECTIONAL },
  { value: ExchangeMode.SENDER, text: ExchangeMode.SENDER },
  { value: ExchangeMode.RECIPIENT, text: ExchangeMode.RECIPIENT }
]

const selectedGroups: Ref<Array<number>> = ref([])
const showButtons = computed(() => selectedGroups.value && selectedGroups.value.length > 0)
const exchangeRuleForm = computed({
  get: () => exchangeRuleStore.exchangeRuleForm,
  set: (value) => exchangeRuleStore.setExchangeRuleForm(value)
})
const collapseElement = ref<HTMLElement>()
let collapseInstance: Collapse = {} as Collapse

const groupSearchRef: Ref<typeof GroupSearch | null> = ref(null)

const v$ = useVuelidate(exchangeRuleRules, exchangeRuleForm)

onMounted(() => {
  collapseInstance = new Collapse(collapseElement.value as HTMLElement, { toggle: false })
  reset()
})

function save(forced = false) {
  v$.value.$validate()
  if (v$.value.$error) return
  const channelId = channelStore.channel.id
  const exchangeRules = selectedGroups.value.map(
    sg => ({ channelId, exchangeMode: exchangeRuleForm.value.exchangeMode, memberId: sg })
  )

  return ExchangeRuleApi.bulkCreate(exchangeRules, forced)
    .then((result) => {
      if (!forced && result.find(x => !x.auditingEntity.createdDate)) {
        // null exchange mode means the exchange rules where not added
        showErrorModal(result)
      } else {
        let promise
        if (props.paginationCommand !== undefined) {
          promise = props.paginationCommand.fetch({
            page: props.paginationCommand
              .currentOrLast(exchangeRuleStore.exchangeRulePage.totalElements, exchangeRuleStore.exchangeRulePage.number)
          })
        } else {
          promise = exchangeRuleStore.fetchExchangeRules({
            paginationOptions: { page: 0 },
            channelId: channelStore.channel.id
          })
        }
        return promise.then(() => reset())
      }
    })
    .catch((reason) => {
      console.log('error:', reason)
      validationStore.setServerSideValidationErrors(['Exchange Rule already exists in this Channel.'])
    })
}

function selectGroups(groups: Array<Group>) {
  selectedGroups.value = groups.map(value => value.id)
}

function showErrorModal(errored: Array<ExchangeRule>) {
  let message = '<p>Exchange Rule already exists in this Channel for the following entities: <br/></p>'
  message += '<ul>'
  errored.slice(0, 9)
    .forEach(e => message += '<li><b>' + e.member.identifier + '</b> which is <b>' + e.exchangeMode + '</b></li>')
  message += '</ul>'
  if (errored.length > 10) {
    message += '<p>' + (errored.length - 10) + ' More participants already exist in this channel but are not shown. Please check the list of participants for more information.<br/></p>'
  }
  message += '<p>Would you like to proceed adding the other participants except the ones listed? <br/></p>'

  dialogStore.show(
    'Add participants',
    message, DialogType.INFO,
    buttonWithCallback('Confirm', () => save(true)),
    CANCEL_BUTTON
  )
}

function reset() {
  exchangeRuleForm.value = new ExchangeRuleDto()
  selectedGroups.value = []
  validationStore.setServerSideValidationErrors([])
  collapseInstance.hide()
  groupSearchRef.value?.cancelSearch()
  v$.value.$reset()
}

</script>
