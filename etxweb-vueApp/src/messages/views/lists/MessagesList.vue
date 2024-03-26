<template>
  <div class="row">
    <div class="col-md-6 message-list">
      <messages-list-header :title="title" :pagination-command="paginationCommand" :show-unread-filter="!isDraft"
                            :show-checkbox="!isDraft" :checkbox-active="allSelected" @toggle-all="toggleAll()"
                            :show-read-button="!isDraft && selectMessageIds.length > 0" @mark-read="markAsRead()"
                            :expand-subject="expandSubject" @toggle-subject="expandSubject = !expandSubject"
                            :show-status-filter="isSent" :search-placeholder="searchPlaceholder"
                            @on-sorted="sort" @toggle-unread="filterUnread" @refresh="refresh()"
                            @on-filter="filter" @filter-status="filterStatus" @clear-filters="resetFilters($event)">
      </messages-list-header>

      <div id="message-container">
        <ul id="message-container-ul" class="message-summary">
          <li v-for="summary in messages" class="item d-flex"
              :key="summary.id" :value="summary.id">
            <div v-if="!isDraft" class="my-auto me-2">
              <input :id="'' + summary.id" class="form-check-input" type="checkbox"
                     :checked="isMessageSummarySelected(summary)" v-on:change="toggleMessage(summary)">
            </div>

            <message-summary-view :message="summary.message" :message-summary="summary.messageSummary"
                                  :signature="summary.signature" :expand-subject="expandSubject"
                                  :pagination-command="paginationCommand" :route-name="detailsRoute"
                                  :sent-from="summary.sentFrom" :sent-to="summary.sentTo"
                                  :show-status-badge="isSent" :group="currentGroup"
                                  :selected="isMessageSummarySelected(summary)"
                                  @on-draft-deleted="refresh()">
            </message-summary-view>
          </li>
        </ul>
      </div>
      <pagination v-if="page" :page="page" :paginationCommand="paginationCommand"
                  @pageChanged="changePage()"/>
    </div>
    <div class="col-md-6 message-detail">
      <router-view></router-view>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, onMounted, PropType, Ref, ref, watch } from 'vue'
import { Direction, Message, MessageSummary, RestResponsePage, Status } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { InboxApi } from '@/messages/api/inboxApi'
import Pagination from '@/shared/views/Pagination.vue'
import MessagesListHeader from '@/messages/views/lists/header/MessagesListHeader.vue'
import MessageSummaryView from '@/messages/views/lists/MessageSummaryView.vue'
import useMessageStore, { MessageConfig } from '@/messages/store/message'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { onBeforeRouteUpdate, useRoute, useRouter } from 'vue-router'
import {
  isDraftRoute,
  isInboxRoute,
  isOverviewRoute,
  isSectionChanged,
  isSentRoute,
  MessageBoxRouteNames,
} from '@/messages/router'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { MessageApi } from '@/messages/api/messageApi'

export interface Summary {
  id: number
  signature?: string
  message: Message
  messageSummary?: MessageSummary
  sentFrom?: string
  sentTo?: string
}

export default defineComponent({
  name: 'MessagesList',
  components: {
    MessageSummaryView,
    MessagesListHeader,
    Pagination
  },
  props: {
    initFilters: { type: Object as PropType<{ [key: string]: any }>, required: true }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    const currentGroupStore = useCurrentGroupStore()
    const currentGroup = computed(() => currentGroupStore.group)

    const page: ComputedRef<RestResponsePage<any> | undefined> = computed(
      () => isInboxRoute(route.name as MessageBoxRouteNames) ? messageStore.messageSummariesPage : messageStore.messagesPage)
    const paginationCommand: Ref<PaginationCommand> = ref({} as PaginationCommand)

    const title: ComputedRef<string> = computed(() => {
      if (isInboxRoute(route.name as MessageBoxRouteNames)) {
        return 'Inbox'
      } else if (isSentRoute(route.name as MessageBoxRouteNames)) {
        return 'Sent'
      } else if (isDraftRoute(route.name as MessageBoxRouteNames)) {
        return 'Draft'
      }
      return ''
    })
    const detailsRoute: ComputedRef<string> = computed(() => {
      if (isInboxRoute(route.name as MessageBoxRouteNames)) {
        return MessageBoxRouteNames.INBOX_MSG_DETAILS
      } else if (isSentRoute(route.name as MessageBoxRouteNames)) {
        return MessageBoxRouteNames.SENT_MSG_DETAILS
      } else if (isDraftRoute(route.name as MessageBoxRouteNames)) {
        return MessageBoxRouteNames.DRAFT_MSG_DETAILS
      }
      return ''
    })
    const filters = ref({ ...props.initFilters })
    const search: Ref<string> = ref('')
    const searchPlaceholder: ComputedRef<string> = computed(() => isInbox.value ? 'Search by subject or sender entity' : 'Search by subject or recipient entity')
    const expandSubject: Ref<boolean> = ref(false)
    const selectMessageIds: Ref<Array<number>> = ref([])
    const isInbox = computed(() => isInboxRoute(route.name as MessageBoxRouteNames))
    const isDraft = computed(() => isDraftRoute(route.name as MessageBoxRouteNames))
    const isSent = computed(() => isSentRoute(route.name as MessageBoxRouteNames))

    const messageStore = useMessageStore()

    const messages: ComputedRef<Array<MessageConfig>> = computed(() =>
      isInboxRoute(route.name as MessageBoxRouteNames)
        ? messageStore.messageSummaryConfigs
        : messageStore.messageConfigs)

    const allSelected: ComputedRef<boolean> = computed(() =>
      (selectMessageIds.value.length > 0 && messages.value.length === selectMessageIds.value.length))

    function setupPaginationAndLoad(retrieveMessageFromRoute: boolean, options?: PaginationOptions) {
      messageStore.clear_list()
      selectMessageIds.value = []

      if (isInboxRoute(route.name as MessageBoxRouteNames)) {
        paginationCommand.value = new PaginationCommand(
          (paginationOptions: PaginationOptions) => messageStore.fetchMessageSummaries(paginationOptions)
            .then(),
          { sortBy: 'message.sentOn' },
          { recipientEntityId: currentGroup.value.id }
        )
      } else if (isSentRoute(route.name as MessageBoxRouteNames)) {
        paginationCommand.value = new PaginationCommand(
          (paginationOptions: PaginationOptions) => messageStore.fetchMessages(paginationOptions)
            .then(),
          { sortBy: 'sentOn' },
          { senderEntityId: currentGroup.value.id }
        )
      } else if (isDraftRoute(route.name as MessageBoxRouteNames)) {
        paginationCommand.value = new PaginationCommand(
          (paginationOptions: PaginationOptions) => messageStore.fetchMessages(paginationOptions, true)
            .then(),
          { sortBy: 'auditingEntity.modifiedDate', filterBy: 'status', filterValue: Status.DRAFT },
          { senderEntityId: currentGroup.value.id }
        )
      } else {
        throw new Error(`${String(route.name)}`)
      }

      if (options?.filterBy && options.filterValue) {
        paginationCommand.value.options.filterBy = options.filterBy
        paginationCommand.value.options.filterValue = options.filterValue
      }
      if (options?.sortOrder) {
        paginationCommand.value.sortOrder(options.sortOrder as Direction)
      }
      if (retrieveMessageFromRoute && route.params.messageId) {
        paginationCommand.value.options.messageId = route.params.messageId
      }

      paginationCommand.value.fetch().then(() => delete paginationCommand.value.options.messageId)
    }

    onMounted(() => setupPaginationAndLoad(true))
    watch(() => route.name, (to, from) => {
      // refresh method will end up here, but we don't want to call 'setupPaginationAndLoad' in that case
      if (isOverviewRoute(to as MessageBoxRouteNames) && isSectionChanged(to as MessageBoxRouteNames, from as MessageBoxRouteNames)) {
        setupPaginationAndLoad(false)
      }
    })
    onBeforeRouteUpdate((to, from, next) => {
      if (isOverviewRoute(to.name as MessageBoxRouteNames) && (to.params.currentGroupId !== from.params.currentGroupId)) {
        setupPaginationAndLoad(false)
      }
      next()
    })

    function refresh() {
      if (!isOverviewRoute(route.name as MessageBoxRouteNames)) {
        if (isInbox.value) {
          router.replace({ name: MessageBoxRouteNames.INBOX })
        } else if (isSent.value) {
          router.replace({ name: MessageBoxRouteNames.SENT })
        } else if (isDraft.value) {
          router.replace({ name: MessageBoxRouteNames.DRAFT })
        }
      }
      messageStore.clear()
      paginationCommand.value.options.page = 0
      paginationCommand.value.options.messageId = null
      setupPaginationAndLoad(false, paginationCommand.value.options)
    }

    function changePage() {
      selectMessageIds.value = []
      if (!isOverviewRoute(route.name as MessageBoxRouteNames)) {
        messageStore.clear_details()
        if (isInbox.value) {
          router.replace({ name: MessageBoxRouteNames.INBOX })
        } else if (isSent.value) {
          router.replace({ name: MessageBoxRouteNames.SENT })
        } else if (isDraft.value) {
          router.replace({ name: MessageBoxRouteNames.DRAFT })
        }
      }
    }

    function markAsRead() {
      if (isInbox.value) {
        InboxApi.markUnread(selectMessageIds.value, currentGroup.value.id).then(() => {
          selectMessageIds.value = []
          return paginationCommand.value.fetch()
        })
      }
      if (isSent.value) {
        MessageApi.markUnread(selectMessageIds.value, currentGroup.value.id).then(() => {
          selectMessageIds.value = []
          return paginationCommand.value.fetch()
        })
      }
    }

    function toggleAll() {
      selectMessageIds.value = !allSelected.value
        ? messages.value.map(m => m.id)
        : []
    }

    function filterUnread(unread: boolean) {
      filters.value.unread = unread
      changeFiltersAndFetch()
    }

    function isMessageSummarySelected(summary: Summary) {
      return selectMessageIds.value.some(x => x === summary.id)
    }

    function toggleMessage(summary: Summary) {
      if (isMessageSummarySelected(summary)) {
        selectMessageIds.value = [...selectMessageIds.value.filter(x => x !== summary.id)]
      } else {
        selectMessageIds.value = [...selectMessageIds.value, summary.id]
      }
    }

    function resetFilters(unread = false) {
      search.value = ''
      expandSubject.value = false
      filters.value = { ...props.initFilters }
      filters.value.unread = unread
      paginationCommand.value.resetFilter()
      changeFiltersAndFetch()
    }

    function filterStatus(status: string) {
      filters.value.status = status === 'All' ? '' : status
      changeFiltersAndFetch()
    }

    function sort(direction: Direction) {
      paginationCommand.value.sortOrder(direction)
      paginationCommand.value.setPage(0)
      paginationCommand.value.fetch().then()
    }

    function filter({ search, startDate, endDate }: { search: string, startDate: Date | null, endDate: Date | null }) {
      if (startDate) startDate.setHours(0, 0, 0, 1)
      if (endDate) endDate.setHours(23, 59, 59, 99)

      if (isDraft.value) {
        filters.value.status = Status.DRAFT
      }

      if (isInbox.value) {
        filters.value.subject_or_sender = search
      } else {
        filters.value.subject_or_recipient = search
      }
      filters.value.startDate = startDate?.toISOString() || undefined
      filters.value.endDate = endDate?.toISOString() || undefined
      changeFiltersAndFetch()
    }

    function changeFiltersAndFetch() {
      paginationCommand.value.setFilters(filters.value)
      paginationCommand.value.fetch({ page: 0 }).then()
    }

    return {
      page,
      messages,
      currentGroup,
      allSelected,
      selectMessageIds,
      title,
      isDraft,
      isSent,
      detailsRoute,
      expandSubject,
      search,
      searchPlaceholder,
      paginationCommand,
      toggleAll,
      markAsRead,
      filterUnread,
      filterStatus,
      refresh,
      filter,
      resetFilters,
      isMessageSummarySelected,
      toggleMessage,
      sort,
      changePage
    }
  }
})
</script>
