<template>

  <div class="col-lg-6">
    <label class="form-label">Sort by</label>
    <select class="form-select" aria-label="Default select example" v-model="sortDirection"
            @change="emit('on-sorted', sortDirection)">
      <option v-for="option in sortDirections" :key="option.key" :value="option">
        {{ option.value }}
      </option>
    </select>
  </div>

</template>

<script setup lang="ts">
import { PropType, ref, Ref } from 'vue'
import { SortDirectionSelectItem } from '@/utils/pagination/sortDirectionSelectItem'

const emit = defineEmits(['on-sorted'])
const props = defineProps({
  sortDirection: { type: Object as PropType<SortDirectionSelectItem>, required: true },
  sortDirections: { type: Array as PropType<Array<SortDirectionSelectItem>>, required: true }
})

const sortDirection: Ref<SortDirectionSelectItem> = ref(props.sortDirection)
</script>
