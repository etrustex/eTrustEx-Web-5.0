<template>
  <div class="d-flex justify-content-between align-items-md-center mb-3">
    <div class="d-flex align-items-md-center">
      <input v-if="showCheckbox" class="form-check-input mt-0" type="checkbox" id="checkboxNoLabel"
             :checked="checkboxActive" @change="toggleCheckbox">
      <hamburger></hamburger>
      <h1 class="d-inline mb-0">{{ title }}</h1>
      <button v-if="showReadButton" type="button" class="btn btn-primary ms-3 badge badge-pill"
              @click="markAsRead()">
        Mark as read
      </button>
    </div>

    <nav aria-label="Messages list action" class="message-list-action">
      <ul class="list-unstyled m-0">
        <li v-if="showUnreadFilter">
          <a class="ico-standalone" href="javascript:void(0);" @click="filterUnread()">
            <i v-if="onlyUnread" class="ico-message-unread" title="Show all messages"></i>
            <i v-else class="ico-message-all" title="Show unread messages"></i>
          </a>
        </li>
        <li>
          <a class="ico-standalone" href="javascript:void(0);" @click="toggleFilters()">
            <i v-if="expandFilters" class="ico-filter-list-off" title="Close filters"></i>
            <i v-else class="ico-filter-list" title="Open filters"></i>
          </a>
        </li>
        <li>
          <a class="ico-standalone" href="javascript:void(0);" @click="toggleSearch()">
            <i v-if="expandSearch" class="ico-search-off" title="Close search"></i>
            <i v-else class="ico-search" title="Search a message"></i>
          </a>
        </li>
        <li>
          <a :class="`ico-standalone ${ expandSubject ? 'expand-subject-list' : 'collapse-subject-list'}`"
             href="javascript:void(0);" @click="toggleSubject()">
            <i class="ico-expand-subject-list" title="Expand subject list"></i>
            <i class="ico-collapse-subject-list" title="Collapse subject list"></i>
          </a>
        </li>
        <li>
          <a class="ico-standalone" href="javascript:void(0);" @click="refresh()">
            <i class="ico-refresh" data-test="refreshIcon" title="Refresh messages list"></i>
          </a>
        </li>
      </ul>
    </nav>
  </div>

  <div v-if="expandFilters" id="filter-collapse" class="mb-3">
    <div class="row g-2 justify-content-end">

      <sort-by :sort-direction="sortDirection" :sortDirections="sortDirections" @on-sorted="sort($event)"/>

      <div v-if="showStatusFilter" class="col-lg-6">
        <label class="form-label"> Filter by</label>
        <select class="form-select" aria-label="Default select example" v-model="selectedStatus"
                @change="filterStatus()">
          <option v-for="option in messageStatusList" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
      </div>

    </div>
  </div>

  <div v-if="expandSearch" id="search-collapse" class="mb-3">
    <search-message ref="search" :placeholder="searchPlaceholder" :search-filter="searchFilter"
                    @on-filter="filter($event)"
                    @on-cancel="resetFilters()">
    </search-message>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, Ref, ref, watch } from 'vue'
import Hamburger from '@/messages/views/Hamburger.vue'
import SortBy from '@/messages/views/lists/header/SortBy.vue'
import { SortDirectionSelectItem } from '@/utils/pagination/sortDirectionSelectItem'
import { Direction } from '@/model/entities'
import { DateDirectionLabel } from '@/utils/pagination/dateDirectionLabel'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import SearchMessage from '@/messages/views/lists/header/SearchMessage.vue'
import { useRoute } from 'vue-router'
import { SearchFilter } from '@/model/searchFilter'
import { isSectionChanged, MessageBoxRouteNames } from '@/messages/router'
import useCurrentGroupStore from '@/shared/store/currentGroup'

export default defineComponent({
  name: 'MessagesListHeader',
  components: {
    Hamburger,
    SortBy,
    SearchMessage
  },
  emits: ['toggle-all', 'toggle-subject', 'on-filter', 'filter-status', 'on-sorted', 'mark-read', 'toggle-unread', 'clear-filters', 'refresh'],
  props: {
    title: { type: String },
    paginationCommand: { type: Object as PropType<PaginationCommand> },
    searchPlaceholder: { type: String, required: true },
    checkboxActive: { type: Boolean },
    showCheckbox: { type: Boolean },
    showReadButton: { type: Boolean },
    showUnreadFilter: { type: Boolean },
    showStatusFilter: { type: Boolean },
    expandSubject: { type: Boolean }
  },
  setup(props, { emit }) {
    const route = useRoute()
    const onlyUnread = ref(false)
    const expandFilters = ref(false)
    const expandSearch = ref(false)

    const searchFilter: Ref<SearchFilter> = ref({ search: '', startDate: null, endDate: null })
    const sortDirection: Ref<SortDirectionSelectItem> = ref({ key: Direction.DESC, value: DateDirectionLabel.NEWEST })

    const selectedStatus: Ref<string> = ref('All')
    const messageStatusList = ['All', 'Delivered', 'Sent', 'Read', 'Multiple', 'Failed']
    const sortDirections: Array<SortDirectionSelectItem> = [
      { key: Direction.DESC, value: DateDirectionLabel.NEWEST },
      { key: Direction.ASC, value: DateDirectionLabel.OLDEST }
    ]

    watch(() => useCurrentGroupStore().group, (to, from) => (!from || to.id !== from.id) && resetView())
    watch(() => route.name, (to, from) => isSectionChanged(to as MessageBoxRouteNames, from as MessageBoxRouteNames) && resetView())

    function resetView() {
      searchFilter.value = { search: '', startDate: null, endDate: null }
      sortDirection.value = { key: Direction.DESC, value: DateDirectionLabel.NEWEST }
      selectedStatus.value = 'All'
      onlyUnread.value = false
      expandFilters.value = false
      expandSearch.value = false
    }

    function toggleCheckbox() {
      emit('toggle-all')
    }

    function toggleSubject() {
      emit('toggle-subject')
    }

    function toggleFilters() {
      expandSearch.value = false
      expandFilters.value = !expandFilters.value
    }

    function toggleSearch() {
      expandFilters.value = false
      expandSearch.value = !expandSearch.value

      if (!expandSearch.value) { // closing
        resetFilters()
      }
    }

    function filter(filters: SearchFilter) {
      emit('on-filter', filters)
    }

    function filterStatus() {
      emit('filter-status', selectedStatus.value)
    }

    function sort(direction: SortDirectionSelectItem) {
      sortDirection.value = direction
      emit('on-sorted', direction.key)
    }

    function markAsRead() {
      emit('mark-read')
    }

    function filterUnread() {
      onlyUnread.value = !onlyUnread.value
      emit('toggle-unread', onlyUnread.value)
    }

    function resetFilters() {
      expandSearch.value = false
      searchFilter.value = { search: '', startDate: null, endDate: null }
      emit('clear-filters', onlyUnread.value)
    }

    function refresh() {
      emit('refresh')
    }

    return {
      expandFilters,
      expandSearch,
      onlyUnread,
      searchFilter,
      sortDirections,
      sortDirection,
      selectedStatus,
      messageStatusList,
      filterStatus,
      toggleCheckbox,
      toggleSubject,
      toggleFilters,
      toggleSearch,
      markAsRead,
      filterUnread,
      filter,
      sort,
      resetFilters,
      refresh
    }
  }
})
</script>
