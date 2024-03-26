export class AbortableFetch {
  controller: AbortController = new AbortController()
  signal: AbortSignal = this.controller.signal

  abort() {
    this.controller.abort()
  }

  resetAbortController() {
    this.controller = new AbortController()
    this.signal = this.controller.signal
  }
}
