<template>
  <div class="d-flex">
    <div class="w-100">
      <input id="nameOrIdentifierInput" type="text" class="form-control" autocomplete="off"
             :disabled="disableInput"
             v-model.trim="nameOrIdentifierInput"
             @input="search"
             @focus="openDropdown"/>
    </div>
    <div style="width: 36px;">
      <button type="button" v-if="showCancelSearch" class="ico-standalone" v-on:click="cancelSearch">
        <i class="ico-search-off" title="Clear"></i>
      </button>
      <button type="button" v-else class="ico-standalone" v-on:click="search">
        <i class="ico-search" title="Search an Entity"></i>
      </button>
    </div>
  </div>

  <ul v-if="showDropdown" class="p-2 list-unstyled search-result" style="overflow-y: hidden; max-height: none;">
    <li v-if="!singleSelection" class="form-check">
      <input id="selectAllCheckbox" type="checkbox" class="form-check-input"
             :checked="allSelected" v-on:change="toggleAll">
      <label for="selectAllCheckbox" class="form-check-label">{{ selectAllLabel }}</label>
    </li>

    <li style="overflow-y: scroll; max-height: 150px">
      <div v-for="group in groups" :key="group.id" class="form-check">
        <input :id="group.identifier" type="checkbox" class="form-check-input"
               :checked="isGroupSelected(group)" @change="toggleGroup(group)">
        <label :for="group.identifier" style="display: inline-block; text-align: left;">
          <span class="text-wrap text-break">
            <span v-if="showIdentifier" class="ico-conjunction2">
              {{ group.identifier }} - {{ group.name }} <i v-if="showIcons" :class="getEncryptionConfigIcon(group)"/>
            </span>
            <span v-else class="ico-conjunction2">
              {{ group.name }}<i v-if="showIcons" :class="getEncryptionConfigIcon(group)"/>
            </span>
        </span>
        </label>
      </div>
    </li>

    <li v-if="!singleSelection" class="text-end">
      <button type="button" class="btn btn-primary mt-2" v-on:click="select">Ok</button>
    </li>
  </ul>
</template>
<script setup lang="ts">
import { computed, onMounted, PropType, ref, Ref, watch, WritableComputedRef } from 'vue'
import { Confidentiality, GroupSearchItem } from '@/model/entities'
import { RouteLocation, useRoute } from 'vue-router'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import { GroupApi } from '@/shared/api/groupApi'

const props = defineProps({
  businessId: { type: Number },
  showIcons: { type: Boolean },
  showIdentifier: { type: Boolean },
  validRecipients: { type: Array as PropType<Array<number>>, default: [] },
  selectedRecipientIds: { type: Array as PropType<Array<number>>, default: [] },
  singleSelection: { type: Boolean, required: false, default: false }
})
const emit = defineEmits(['selected'])

const route = useRoute()
const savedGroups: Ref<Array<GroupSearchItem>> = ref([])
const groups: Ref<Array<GroupSearchItem>> = ref([])
const selectedOptions: Ref<Array<GroupSearchItem>> = ref([])

const disableInput = ref(true)
const showDropdown = ref(false)
const keywords = ref('')
const nameOrIdentifierInput: WritableComputedRef<string> = computed({
  get: () => {
    if (showDropdown.value) {
      return keywords.value
    }
    return props.showIdentifier
      ? selectedOptions.value.map(g => `${g.identifier} - ${g.name}`)
        .join(', ')
      : selectedOptions.value.map(x => x.name)
        .join(', ')
  },
  set: (value) => {
    keywords.value = value
  }
})

const showCancelSearch = computed(() => !!nameOrIdentifierInput.value)
const selectAllLabel = computed(() => nameOrIdentifierInput.value ? 'Select all search results' : 'Select all')
const allSelected = computed(() => selectedOptions.value.length > 0 && groups.value.every(x => selectedOptions.value.find(y => x.id === y.id)))

onMounted(async () => {
  await loadValues()
})

watch(() => props.selectedRecipientIds, (current, _) => {
  // needed when a new message is reloaded from a draft
  if (current?.length > 0) {
    selectedOptions.value = savedGroups.value.filter(group => current.includes(group.id))
  }
})
watch(() => props.businessId, async () => {
  selectedOptions.value = []
  showDropdown.value = false
  groups.value = [] // required until load
  keywords.value = ''

  await loadValues()
})

async function loadValues() {
  disableInput.value = true
  await GroupApi.search(props.businessId, nameOrIdentifierInput.value)
    .then(groupsValue => {
      if (isAdminRoute(route)) {
        savedGroups.value = groupsValue
      } else {
        savedGroups.value = groupsValue.filter(group => props.validRecipients.includes(group.id))
        groups.value = [...savedGroups.value]
      }

      if (groupsValue.length === 1) {
        selectedOptions.value = groupsValue
        select()
      }
    })
    .then(() => disableInput.value = false)
}

function openDropdown() {
  showDropdown.value = true
  groups.value = [...savedGroups.value]
  selectedOptions.value = selectedOptions.value.length === 0
    ? groups.value.filter(x => selectedOptions.value.find(y => x.id === y.id))
    : groups.value.filter(x => {
      return selectedOptions.value.find(y => x.id === y.id)
    })
  search()
}

function search() {
  groups.value = savedGroups.value.filter(value => value.name.toLowerCase()
    .includes(nameOrIdentifierInput.value.toLowerCase()) ||
    value.identifier.toLowerCase()
      .includes(nameOrIdentifierInput.value.toLowerCase()))
}

function isGroupSelected(group: GroupSearchItem) {
  return selectedOptions.value.find(g => g.identifier === group.identifier)
}

function toggleAll() {
  selectedOptions.value = allSelected.value ? [] : [...groups.value]
}

function toggleGroup(group: GroupSearchItem) {
  if (isGroupSelected(group)) {
    selectedOptions.value = [...selectedOptions.value.filter(g => g.id !== group.id)]
  } else {
    if (props.singleSelection) {
      emit('selected', [group])
      selectedOptions.value = [group]
      keywords.value = ''
      showDropdown.value = false
      return
    }
    selectedOptions.value = [...selectedOptions.value, group]
  }
}

function cancelSearch() {
  if (showCancelSearch.value) {
    emit('selected', [])
    reset()
  }
}

function select() {
  if (selectedOptions.value && selectedOptions.value.length > 0) {
    showDropdown.value = false
    emit('selected', selectedOptions.value)
  }
}

function reset() {
  selectedOptions.value = []
  keywords.value = ''
  search()
}

function isAdminRoute(route: RouteLocation) {
  return route.matched.find(value => AdminRouteNames.ADMIN === value.name) !== undefined
}

function getEncryptionConfigIcon(groupParam: GroupSearchItem) {
  if (!!groupParam.publicKeyFileName && groupParam.confidentiality === Confidentiality.LIMITED_HIGH) {
    return 'ico-encrypted-mandatory'
  } else if (!!groupParam.publicKeyFileName && groupParam.confidentiality === Confidentiality.PUBLIC) {
    return 'ico-encrypted'
  } else {
    return ''
  }
}

defineExpose({ cancelSearch })
</script>
