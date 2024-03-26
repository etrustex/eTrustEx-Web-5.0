<template>
  <div class="row justify-content-end mt-3">
    <div class="col-md-12 col-lg-6">
      <div id="search-collapse">
        <div class="input-group">
          <input type="text"
                 v-model="searchString"
                 :placeholder="`Search by ${searchBy}`"
                 v-on:keyup.enter="search"
                 @input="preSearch"
                 class="form-control">

          <div class="input-group-append">
            <a class="ico-standalone ms-2" @click="search">
              <i aria-label="Perform Search" title="Perform Search" class="ico-search"></i>
            </a>
          </div>
        </div>

        <ul v-if="!!prefilterFn && expandDropdown" class="list-unstyled search-result">
          <li v-for="item in items" :key="item" role="presentation">
            <a v-on:click="selectItem(item)">
              <span class="text-wrap text-break">{{ dropdownValue(item) }}</span>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, Ref } from 'vue'
import { SearchItem } from '@/model/entities'
import { onBeforeRouteLeave } from 'vue-router'

const props = defineProps({
  searchBy: String,
  searchTooltip: String,
  filterFn: { type: Function, required: true },
  prefilterFn: Function,
})

const searchString: Ref<string> = ref('')
const expandDropdown: Ref<boolean> = ref(false)
const items: Ref<SearchItem[] | string[]> = ref([])

onBeforeRouteLeave(() => {
  searchString.value = ''
  items.value = []
})

function search() {
  props.filterFn(searchString.value)
  expandDropdown.value = false
  items.value = []
}

function preSearch() {
  if (props.prefilterFn) {
    if (!searchString.value) {
      items.value = []
    } else {
      props.prefilterFn(searchString.value)
        .then((value: any) => items.value = value)
    }
  }
  expandDropdown.value = true
}

function selectItem(item: SearchItem | string) {
  searchString.value = typeof item === 'string' ? item : item.searchValue
  search()
}

function dropdownValue(item: SearchItem | string) {
  return typeof item === 'string' ? item : item.value
}
</script>
