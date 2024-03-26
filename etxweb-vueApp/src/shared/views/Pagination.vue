<template>
  <div>
    <nav v-if="totalElements" aria-label="Pagination" role="navigation">
      <ul class="pagination justify-content-center mb-0">
        <li class="page-item">
          <button :class="isFirstPage ? 'page-link page-link-inactive' : 'page-link'" aria-label="Go to previous page"
                  type="button" @click="goToPage(currentPage - 1)">
            <span aria-hidden="true" class="ico-standalone"><i class="ico-west"></i></span>
          </button>
        </li>
        <li v-for="(p) in navPages" :key="p" class="page-item">
          <button :aria-label="p" :class="p === currentPage + 1 ? 'page-link page-link-current' : 'page-link'"
                  type="button" @click="goToPage(p - 1)">
            {{ p }}
          </button>
        </li>
        <li class="page-item">
          <button :class="isLastPage ? 'page-link page-link-inactive' : 'page-link'" aria-label="Go to next page"
                  type="button" @click="goToPage(currentPage + 1)">
            <span aria-hidden="true" class="ico-standalone"><i class="ico-east"></i></span>
          </button>
        </li>
      </ul>
    </nav>
    <div v-if="totalElements" class="d-flex justify-content-between align-items-center">
      <div class="d-flex align-items-center">
        <span>View: </span>
        <div class="dropdown">
          <button class="btn btn-link dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
            {{ paginationCommand.options?.size }}
          </button>
          <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
            <li>
              <a class="dropdown-item" v-for="(size) in pageSizes" :key="size"
                   :class="[(size === paginationCommand.options?.size) ? 'active':'']"
                   v-on:click="setPageSize(size)">
              {{ size }}
              </a>
            </li>
          </ul>
        </div>
        <span> items per page</span>
      </div>
      <div> {{ startElement }} - {{ endElement }} out of {{ totalElements }}
        items
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, PropType, ref, Ref } from 'vue'
import { RestResponsePage } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'

export default defineComponent({
  name: 'Pagination',
  props: {
    page: { type: Object as PropType<RestResponsePage<any>>, required: true },
    paginationCommand: { type: Object as PropType<PaginationCommand>, required: true }
  },
  setup(props, { emit }) {
    const pageSizes: Array<number> = (process.env.VUE_APP_PAGE_SIZES as string).split(' ').map(Number)
    const processing: Ref<boolean> = ref(false)
    const currentPage: ComputedRef<number> = computed(() => (props.page.number || 0))
    const lastPage: ComputedRef<number> = computed(() => (props.page.totalPages))
    const numberOfElementsInPage: ComputedRef<number> = computed(() => (props.page.numberOfElements))
    const totalElements: ComputedRef<number> = computed(() => (props.page.totalElements))
    const startElement: ComputedRef<number> = computed(() => (currentPage.value * (props.paginationCommand.options?.size || 10) + 1))
    const endElement: ComputedRef<number> = computed(() => ((startElement.value - 1) + numberOfElementsInPage.value))
    const totalPages: ComputedRef<number> = computed(() => (props.page.totalPages))
    const isFirstPage: ComputedRef<boolean> = computed(() => (props.page.first))
    const isLastPage: ComputedRef<boolean> = computed(() => (props.page.last))
    const navPages = computed(() => {
      let navRange: Array<number>
      const range = (start: number, stop: number, step = 1) =>
        Array(Math.ceil((stop - start) / step)).fill(start)
          .map((x, y) => x + y + step)
      const navRangeSize = totalPages.value <= 5 ? totalPages.value : 5
      const offset = Math.floor(navRangeSize / 2)
      const firstPages = () => isFirstPage.value || (currentPage.value - offset < 1)
      const lastPages = () => currentPage.value + offset >= totalPages.value

      if (firstPages()) {
        navRange = range(0, navRangeSize)
      } else if (lastPages()) {
        navRange = range(totalPages.value - navRangeSize, totalPages.value)
      } else {
        navRange = range(currentPage.value - offset, currentPage.value + offset + 1)
      }

      return navRange
    })

    function goToPage(page: number) {
      if (!processing.value && (page >= 0 && page < totalPages.value)) {
        return props.paginationCommand.fetch({ page })
          .then(() => emit('pageChanged', page))
          .finally(() => processing.value = false)
      }

      return Promise.resolve()
    }

    async function setPageSize(size: number) {
      if (!processing.value) {
        props.paginationCommand.setSize(size)
        await goToPage(0)
      }
    }

    return {
      pageSizes,
      processing,
      currentPage,
      lastPage,
      numberOfElementsInPage,
      totalElements,
      startElement,
      endElement,
      totalPages,
      isFirstPage,
      isLastPage,
      navPages,
      goToPage,
      setPageSize,
      emit
    }
  }
})

</script>
