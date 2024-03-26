<template>
  <div class="btn-party-name ms-4 mt-4">
    <span aria-label="Select an entity" class="h6 btn-party-name-label font-weight-bold">Business</span>
    <div class="dropdown">
      <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
        <span class="ellipsis m-0">{{ selectedBusiness?.name || 'Business' }}</span>
      </button>
      <ul class="dropdown-menu">
        <li v-for="group in businesses" :key="group.id"
            :class="[(group.id === selectedBusiness?.id) ? 'active':'']">
          <a class="dropdown-item" v-on:click="changeBusiness(group)">
            {{ group.name }}
          </a>
        </li>
      </ul>
    </div>
  </div>

  <div class="btn-party-name ms-4 mt-4">
    <span aria-label="Select an entity" class="h6 btn-party-name-label font-weight-bold">Entity</span>
    <div class="dropdown">
      <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false"
              :disabled="!selectedBusiness || entities?.length === 0">
        <span class="ellipsis m-0">{{ selectedEntity?.name || 'Entity' }}</span>
      </button>
      <ul class="dropdown-menu">
        <li v-for="group in entities" :key="group.id"
            :class="[(group.id === selectedEntity?.id) ? 'active':'']">
          <a class="dropdown-item" v-on:click="changeEntity(group)">
            {{ group.name }}
          </a>
        </li>
      </ul>
    </div>
  </div>

  <button type="button" class="btn btn-primary ms-4 mt-4" v-on:click="deleteOrphanFiles()">Delete orphan files</button>
  <button type="button" class="btn btn-primary ms-4 mt-4" v-on:click="clear()">Clear selection</button>
</template>

<script setup lang="ts">
import { HttpRequest } from '@/utils/httpRequest'
import useRootLinksStore from '@/shared/store/rootLinks'
import { Group, GroupType, Rels } from '@/model/entities'
import { onMounted, Ref, ref } from 'vue'
import { GroupApi } from '@/shared/api/groupApi'
import useAuthenticationStore from '@/shared/store/authentication'
import useDialogStore from '@/shared/store/dialog'

const businesses: Ref<Group[]> = ref([])
const entities: Ref<Group[]> = ref([])
const selectedBusiness: Ref<Group | undefined> = ref()
const selectedEntity: Ref<Group | undefined> = ref()

onMounted(async () => {
  let groupsPage = await GroupApi.getGroups({
    sortBy: 'auditingEntity.createdDate',
    size: 10000,
    groupType: GroupType.BUSINESS,
    parentId: useAuthenticationStore().rootGroupId,
  })
  businesses.value = groupsPage.content
})

onMounted(async () => {
  await useRootLinksStore().fetchAdminLinks()
})

async function changeBusiness(business: Group) {
  selectedBusiness.value = business

  let groupsPage = await GroupApi.getGroups({
    sortBy: 'auditingEntity.createdDate',
    size: 10000,
    groupType: GroupType.ENTITY,
    parentId: business.id,
  })
  entities.value = groupsPage.content
}

function changeEntity(entity: Group) {
  selectedEntity.value = entity
}

function deleteOrphanFiles(): void {
  const groupId = selectedEntity.value?.id || selectedBusiness.value?.id || undefined
  if (!groupId) {
    useDialogStore().show('No group selected!', 'Please select a Business or a Entity.')
    return
  }
  
  const groupIdentifier = selectedEntity.value?.identifier || selectedBusiness.value?.identifier || undefined
  HttpRequest.delete(useRootLinksStore().link(Rels.DELETE_GROUP_ORPHAN_FILES), { params: { groupId } })
    .then(() => alert(`Orphans deleted for ${ groupIdentifier }`))
}

function clear() {
  selectedEntity.value = undefined
  entities.value = []
  selectedBusiness.value = undefined
}
</script>
