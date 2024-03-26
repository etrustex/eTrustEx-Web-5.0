<template>
  <div ref="usersSearch">
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
      <li class="form-check">
        <input id="selectAllCheckbox" type="checkbox" class="form-check-input"
               :checked="allSelected" v-on:change="toggleAll">
        <label for="selectAllCheckbox" class="form-check-label">{{ selectAllLabel }}</label>
      </li>

      <li style="overflow-y: scroll; max-height: 150px">
        <div v-for="user in users" :key="user.ecasId" class="form-check">
          <input :id="user.ecasId" type="checkbox" class="form-check-input"
                 :checked="isUserSelected(user)" @change="toggleUser(user)">
          <label :for="user.ecasId" style="display: inline-block; text-align: left;">
          <span class="text-wrap text-break">
            <span class="ico-conjunction2">
              {{ user.ecasId }} - {{ user.name }}
            </span>
        </span>
          </label>
        </div>
      </li>

      <li class="text-end">
        <button type="button" class="btn btn-primary mt-2" v-on:click="select">Ok</button>
      </li>
    </ul>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, PropType, ref, Ref, watch, WritableComputedRef } from 'vue'
import { UserListItem } from '@/model/entities'
import { useRoute } from 'vue-router'
import { UserProfileApi } from '@/admin/api/userProfileApi'
import { onClickOutside } from '@vueuse/core'

const props = defineProps({
  businessId: { type: Number },
  selectedUsers: { type: Array as PropType<Array<{ ecasId: string, name: string }>>, default: [] }
})
const emit = defineEmits(['selected'])

const route = useRoute()
const usersSearch = ref(null)
const savedUsers: Ref<Array<UserListItem>> = ref([])
const users: Ref<Array<UserListItem>> = ref([])
const selectedOptions: Ref<Array<UserListItem>> = ref([])

const disableInput = ref(true)
const showDropdown = ref(false)
const keywords = ref('')
const nameOrIdentifierInput: WritableComputedRef<string> = computed({
  get: () => {
    if (showDropdown.value) {
      return keywords.value
    }
    return selectedOptions.value.map(g => `${g.ecasId} - ${g.name}`)
      .join(', ')
  },
  set: (value) => {
    keywords.value = value
  }
})

const showCancelSearch = computed(() => !!nameOrIdentifierInput.value)
const selectAllLabel = computed(() => nameOrIdentifierInput.value ? 'Select all search results' : 'Select all')
const allSelected = computed(() => selectedOptions.value.length > 0 && users.value.every(x => selectedOptions.value.find(y => x.ecasId === y.ecasId)))

onMounted(async () => {
  await loadValues()
})

onClickOutside(usersSearch, () => select())

watch(() => props.selectedUsers, (current, _) => {
  // needed when a new message is reloaded from a draft
  if (current?.length > 0) {
    selectedOptions.value = savedUsers.value.filter(user => current?.find(u => u.ecasId === user.ecasId && u.name === user.name))
  }
})
watch(() => props.businessId, async () => {
  selectedOptions.value = []
  showDropdown.value = false
  users.value = [] // required until load
  keywords.value = ''

  await loadValues()
})

async function loadValues() {
  disableInput.value = true
  UserProfileApi.getListItems({ groupId: props.businessId, size: 10000 }, false)
    .then(userItems => {
      const uniqueUsers: Array<UserListItem> = []
      userItems.content.forEach(u => {
        if (!uniqueUsers.find(u1 => u1.ecasId === u.ecasId && u.name === u1.name)) {
          uniqueUsers.push(u)
        }
      })

      savedUsers.value = uniqueUsers
      users.value = [...savedUsers.value]
    })
    .then(() => disableInput.value = false)
}

function openDropdown() {
  showDropdown.value = true
  users.value = [...savedUsers.value]
  selectedOptions.value = selectedOptions.value.length === 0
    ? users.value.filter(x => selectedOptions.value.find(y => x.ecasId === y.ecasId && x.name === y.name))
    : users.value.filter(x => {
      return selectedOptions.value.find(y => x.ecasId === y.ecasId && x.name === y.name)
    })
  search()
}

function search() {
  users.value = savedUsers.value
    .filter(value => value.name.toLowerCase()
      .includes(nameOrIdentifierInput.value.toLowerCase()) ||
          value.ecasId.toLowerCase()
            .includes(nameOrIdentifierInput.value.toLowerCase()))
}

function isUserSelected(user: UserListItem) {
  return selectedOptions.value.find(u => u.ecasId === user.ecasId && u.name === user.name)
}

function toggleAll() {
  selectedOptions.value = allSelected.value ? [] : [...users.value]
}

function toggleUser(user: UserListItem) {
  if (isUserSelected(user)) {
    selectedOptions.value = [...selectedOptions.value.filter(g => g.ecasId !== user.ecasId && g.name !== user.name)]
  } else {
    selectedOptions.value = [...selectedOptions.value, user]
  }
}

function cancelSearch() {
  if (showCancelSearch.value) {
    emit('selected', [])
    reset()
  }
}

function select() {
  showDropdown.value = false
  emit('selected', selectedOptions.value)
}

function reset() {
  selectedOptions.value = []
  keywords.value = ''
  search()
  emit('selected', [])
}

defineExpose({ cancelSearch, reset })
</script>
