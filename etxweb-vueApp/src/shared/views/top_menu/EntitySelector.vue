<template>
  <div class="btn-party-name">
    <span aria-label="Select an entity" class="btn-party-name-label">Entity</span>
    <span v-if="entities.length === 1" class="d-block single-party-name">
        <span id="single-entity-name" ref="singleEntityName" class="ellipsis">
          {{ firstEntity?.name }}
        </span>
     </span>

    <div v-else class="dropdown">
      <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
        <span v-if="currentGroup.identifier !== 'ROOT'" class="ellipsis m-0">{{ currentGroup.name }}</span>
      </button>
      <ul class="dropdown-menu">
        <li v-for="group in entities" :key="group.id"
            :class="[(group.id.toString() === currentGroup.identifier) ? 'active':'']">
          <a class="dropdown-item" v-on:click="changeUserGroup(group.id)">
            {{ group.name }}
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import useAuthenticationStore from '@/shared/store/authentication'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { MessageBoxRouteNames } from '@/messages/router'
import { useRouter } from 'vue-router'
import useAlertUserStatusStore from '@/shared/store/alertUserStatus'

export default defineComponent({
  name: 'EntitySelector',
  setup() {
    const router = useRouter()
    const authenticationStore = useAuthenticationStore()
    const currentGroupStore = useCurrentGroupStore()
    const alertUserStatusStore = useAlertUserStatusStore()

    const entities = computed(() => authenticationStore.userEntities)
    const firstEntity = computed(() => entities.value?.length > 0 ? entities.value[0] : null)
    const currentGroup = computed(() => currentGroupStore.group)

    function changeUserGroup(groupId: number): void {
      if (currentGroupStore.group.id !== groupId) {
        currentGroupStore.changeGroup(Number(groupId)).then(() => {
          router.push({
            name: MessageBoxRouteNames.INBOX,
            params: { currentGroupId: '' + groupId }
          })
          alertUserStatusStore.setDisplayNotifications(false)
        })
      }
    }

    return {
      entities, firstEntity, currentGroup, changeUserGroup
    }
  }
})
</script>
