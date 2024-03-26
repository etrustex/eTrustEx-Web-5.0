<template>
  <div class="main-wrapper">
    <div :class="['folder-menu', {sidebarActive: sidebarActive}]">
      <div class="d-grid col-12" v-if="currentGroupCanSend">
        <button class="btn btn-primary d-flex justify-content-center align-items-center mt-2 py-4"
                @click="goToNewMessage()">
          <span aria-hidden="true" class="ico-standalone"><i class="ico-mail-white"></i></span>
          <span class="ps-3">New message</span>
        </button>
        <hr class="mt-4 mb-2">
      </div>
      <nav aria-label="secondary" class="nav-secondary">
        <ul>
          <li>
            <router-link class="d-flex align-items-center"
                         :to="{ name: INBOX }">
              <span aria-hidden="true" class="ico-standalone"><i class="ico-inbox"></i></span>
              <span class="ps-2 nav-secondary-name">Inbox</span>
              <span v-if="numberOfUnreadMessageSummaries > 0" class="nav-secondary-number">
                {{ numberOfUnreadMessageSummaries }}
              </span>
            </router-link>
          </li>
          <li v-if="currentGroupCanSend">
            <router-link class="d-flex align-items-center"
                         :to="{ name: SENT }">
              <span aria-hidden="true" class="ico-standalone"><i class="ico-send"></i></span>
              <span class="ps-2 nav-secondary-name">Sent</span>
              <span v-if="numberOfUnreadMessages > 0" class="nav-secondary-number">
                {{ numberOfUnreadMessages }}
              </span>
            </router-link>
          </li>
          <li v-if="currentGroupCanSend">
            <router-link class="d-flex align-items-center"
                         :to="{ name: DRAFT }">
              <span aria-hidden="true" class="ico-standalone"><i class="ico-draft"></i></span>
              <span class="ps-2 nav-secondary-name">Draft</span>
            </router-link>
          </li>
        </ul>
      </nav>
    </div>

    <div class="container-fluid">
      <router-view></router-view>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import useMessageStore from '@/messages/store/message'
import { MessageBoxRouteNames, MessageFormRouteNames } from '../router'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { onBeforeRouteUpdate, useRoute, useRouter } from 'vue-router'
import useAuthenticationStore from '@/shared/store/authentication'
import { routeNames } from '@/router'
import useViewConfigStore from '@/messages/store/messagesView'

export default defineComponent({
  name: 'MessagesBox',
  computed: {
    numberOfUnreadMessageSummaries: () => useMessageStore().numberOfUnreadMessageSummaries,
    numberOfUnreadMessages: () => useMessageStore().numberOfUnreadMessages,
    currentGroupCanSend: () => useCurrentGroupStore().recipients.length > 0,
    sidebarActive: () => useViewConfigStore().sidebarActive
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const INBOX = MessageBoxRouteNames.INBOX
    const SENT = MessageBoxRouteNames.SENT
    const DRAFT = MessageBoxRouteNames.DRAFT

    // workaround for bug ETRUSTEX-7013 Loader does not show
    function goToNewMessage() {
      return router
        .push({ name: MessageFormRouteNames.MESSAGE_FORM, params: { currentGroupId: route.params.currentGroupId } })
        .then()
    }

    onBeforeRouteUpdate((to, from, next) => {
      const authenticationStore = useAuthenticationStore()
      const currentGroupStore = useCurrentGroupStore()
      if (!authenticationStore.userEntities.length) {
        next({ name: routeNames.FORBIDDEN })
      } else if (to.params && to.params.currentGroupId) {
        if (`${currentGroupStore.group.id}` !== `${to.params.currentGroupId}`) {
          currentGroupStore.changeGroup(Number(to.params.currentGroupId)).then(() => next({
            name: MessageBoxRouteNames.INBOX,
            params: {
              ...to.params,
              currentGroupId: to.params.currentGroupId
            }
          }))
        } else {
          next()
        }
      } else {
        next({
          name: MessageBoxRouteNames.INBOX,
          params: {
            ...to.params,
            currentGroupId: authenticationStore.firstUserEntityId
          }
        })
      }
    })

    return { INBOX, SENT, DRAFT, goToNewMessage }
  }
})

</script>
