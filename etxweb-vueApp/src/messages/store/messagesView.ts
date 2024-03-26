import { defineStore } from 'pinia'

const nameSpace = 'viewConfig'

interface State {
  sidebarActive: boolean
}

const useViewConfigStore = defineStore(nameSpace, {
  state: (): State => ({
    sidebarActive: false
  }),
  actions: {
    clear() {
      this.$reset()
    },
    toggleSidebar() {
      this.sidebarActive = !this.sidebarActive
    }
  }
})
export default useViewConfigStore
