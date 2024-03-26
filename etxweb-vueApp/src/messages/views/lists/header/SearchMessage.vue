<template>
  <div>

    <div class="row g-2">

      <div class="col-12">
        <label for="search" class="form-label">Search</label>
        <input id="search" type="text" class="form-control"
               v-model="searchFilter.search" :placeholder="placeholder"
               v-on:keyup.enter="filter(); $event.preventDefault();">
        <span v-if="v$.search.$error" class="error"> {{ v$.search.$errors[0].$message }} </span>
      </div>

      <div class="col-lg-6">
        <label for="from" class="form-label">From</label>
        <vue-date-picker id="from" format="dd/MM/yyyy"
                         :enable-time-picker="false"
                         v-model="searchFilter.startDate">
        </vue-date-picker>
      </div>

      <div class="col-lg-6">
        <label for="to" class="form-label">To</label>
        <vue-date-picker id="to" format="dd/MM/yyyy"
                         :enable-time-picker="false"
                         v-model="searchFilter.endDate">
        </vue-date-picker>
        <span v-if="v$.endDate.$error" class="error"> {{ v$.endDate.$errors[0].$message }} </span>
      </div>

      <div class="col-12 mt-3">
        <div class="text-end btns">
          <button type="button" class="btn btn-outline-secondary btn-ico" @click="cancel()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
            <span>Cancel</span>
          </button>
          <button type="button" class="btn btn-primary btn-ico" @click="filter()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-search-white"></i></span>
            <span>Search</span>
          </button>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup lang="ts">
import { PropType, Ref, ref } from 'vue'
import useVuelidate from '@vuelidate/core'
import { endDate } from '@/admin/views/shared/validation/DateValidator'
import { SearchFilter } from '@/model/searchFilter'
import { helpers } from '@vuelidate/validators'

const emit = defineEmits(['on-filter', 'on-cancel'])
const props = defineProps({
  searchFilter: { type: Object as PropType<SearchFilter>, required: true },
  placeholder: { type: String, required: true }
})

const searchFilter: Ref<SearchFilter> = ref(props.searchFilter)

const isEndDateGreaterThanStartDate = (value: string) => {
  return !value.includes(',')
}

const search = {
  endDate: helpers.withMessage('It is not allowed to use commas in the search.', isEndDateGreaterThanStartDate)
}

const searchFilterRules = {
  search,
  endDate
}

const v$ = useVuelidate(searchFilterRules, searchFilter)

function filter() {
  v$.value.$validate()
  if (!v$.value.$error) {
    emit('on-filter', searchFilter.value)
  }
}

function cancel() {
  v$.value.$reset()
  emit('on-cancel')
}
</script>
