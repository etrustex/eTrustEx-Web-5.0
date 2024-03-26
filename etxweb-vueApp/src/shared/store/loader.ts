import { defineStore } from 'pinia'

const nameSpace = 'loader'

interface State {
  setBy: Array<string>
  loaderVisible: boolean
}

const useLoaderStore = defineStore(nameSpace, {
  state: (): State => ({
    setBy: [],
    loaderVisible: false
  }),
  actions: {
    showLoader(setBy: string) {
      this.setBy.push(setBy)
      this.loaderVisible = true
    },

    hideLoader(setBy: string) {
      this.setBy.splice(this.setBy.findIndex(u => u === setBy), 1) // remove first matching element
      this.loaderVisible = this.setBy.length > 0
    }
  }

})

export default useLoaderStore
