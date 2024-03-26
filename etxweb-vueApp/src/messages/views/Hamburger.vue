<template>
  <a id="hamburger"
     :class="['m-0','ico-standalone', {tripleCheese: menuCollapsed}]"
     :aria-expanded="menuCollapsed ? 'false' : 'true'"
     @click="toggleMenuCollapsed">
    <i class="ico-menu"></i>
    <i class="ico-menu-open"></i>
  </a>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import useViewConfigStore from '@/messages/store/messagesView'

export default defineComponent({
  name: 'Hamburger',
  computed: {
    menuCollapsed: () => useViewConfigStore().sidebarActive
  },
  setup() {
    function toggleMenuCollapsed() {
      useViewConfigStore().toggleSidebar()
    }

    return {
      toggleMenuCollapsed
    }
  }
})
</script>
