<template>
    <nav aria-label="Breadcrumb">
      <ol class="breadcrumb">
        <li v-for="item in items.filter(i => i.text)" class="breadcrumb-item">
          <router-link :to="item.to">{{ item.text }}</router-link>
        </li>
      </ol>
    </nav>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useRoute } from 'vue-router'

export default defineComponent({
  name: 'Breadcrumbs',
  setup() {
    const route = useRoute()

    const items = computed(
      () => route.matched
        .filter(route => route.name && route.meta.displayName)
        .map(route => {
          const text = (typeof route.meta.displayName === 'function') ? route.meta.displayName(route) : route.meta.displayName
          return { text, to: { name: route.name } }
        }))

    return {
      items
    }
  }
})
</script>
