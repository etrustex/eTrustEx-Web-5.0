import { defineStore } from 'pinia'

type Status = 'inProgress' | 'idle' | 'failed' | 'cancel'

const nameSpace = 'progressTracker'

export type ProgressConfig = {
  title: string;
  message: string;
  isUpload: boolean;
  progressCancelFunction?: () => Promise<unknown>
}

interface State {
  config: ProgressConfig
  totalBytes: number
  completedBytes: number
  status: Status
}

const useProgressTrackerStore = defineStore(nameSpace, {
  state: (): State => ({
    config: { title: '', message: '', isUpload: false },
    totalBytes: 0,
    completedBytes: 0,
    status: 'idle'
  }),
  getters: {
    progress: (state) => (state.status !== 'idle') ? state.completedBytes / state.totalBytes : 0,
    showProgress: (state) => state.status !== 'idle'
  },
  actions: {
    init(totalBytes: number) {
      this.totalBytes = totalBytes
      this.completedBytes = 0
      this.status = totalBytes ? 'inProgress' : 'idle'
    },
    handleError(error: Error) {
      if (error.name === 'AbortError') {
        this.status = 'cancel'
      } else {
        this.status = 'failed'
      }
    },
    addBytes(downloadedBytes: number) {
      if (this.status === 'inProgress') {
        this.completedBytes = this.completedBytes + downloadedBytes
      }
    },
    close() {
      this.status = 'idle'
    }
  }
})

export default useProgressTrackerStore
