import { defineStore } from 'pinia'

const nameSpace = 'validation'

interface State {
  serverValidationErrors: string [],
  title: string
}

const useValidationStore = defineStore(nameSpace, {
  state: (): State => ({
    serverValidationErrors: [],
    title: 'Server validation failed:'
  }),
  actions: {
    setServerSideValidationErrors(serverValidationErrors: string[]) {
      this.serverValidationErrors = serverValidationErrors
    },
    setTitle(title: string) {
      this.title = title
    }
  }
})

export default useValidationStore
