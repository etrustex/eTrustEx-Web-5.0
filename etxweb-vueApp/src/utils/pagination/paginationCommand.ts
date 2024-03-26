import { PaginationOptions } from './paginationOptions'
import { Direction, RestResponsePage } from '@/model/entities'
import useAuthenticationStore from '@/shared/store/authentication'
import { UserApi } from '@/shared/api/userApi'

export class PaginationCommand {
  options: PaginationOptions

  fetchFn: (paginationOptions: PaginationOptions) => Promise<void | RestResponsePage<any>>

  constructor(fetchFunction: (paginationOptions: PaginationOptions) => Promise<void | RestResponsePage<any>>,
    paginationOptions: PaginationOptions,
    requestParams?: { [key: string]: any }) {
    this.fetchFn = fetchFunction

    this.options = paginationOptions

    if (requestParams) {
      Object.keys(requestParams)
        .forEach(key => this.options[key] = requestParams[key])
    }

    this.options.page = paginationOptions.page || 0
    this.options.size = paginationOptions.size || useAuthenticationStore().paginationSize
    this.options.sortBy = paginationOptions.sortBy || ''
    this.options.sortOrder = paginationOptions.sortOrder || Direction.DESC
    this.options.filterBy = paginationOptions.filterBy || ''
    this.options.filterValue = paginationOptions.filterValue || ''
  }

  /**
   * fetch command. Calls the fetchFn set at object initialisation.
   */
  fetch(opts?: PaginationOptions): Promise<any> {
    return this.fetchFn({ ...this.options, ...opts })
      .then(restResponsePage => {
        if (restResponsePage) {
          this.setPage(restResponsePage.number)
        }
        return restResponsePage
      })
  }

  setPage(page: number): void {
    this.options.page = page
  }

  setSize(size: number): void {
    this.options.size = size
    useAuthenticationStore().paginationSize = size
    UserApi.updateUserPreferences({ paginationSize: size })
  }

  sortOrder(direction: Direction) {
    this.options.sortOrder = direction
  }

  resetFilter() {
    this.options.page = 0
    this.options.filterBy = ''
    this.options.filterValue = ''
  }

  currentOrLast(totalElements: number, currentPage: number): number {
    if ((totalElements - 1) <= 0) { return 0 }
    const lastPage = Math.ceil(totalElements / this.options.size!) - 1
    if (lastPage < currentPage) {
      currentPage -= 1
    }
    return currentPage
  }

  setFilters(filters: { [key: string]: string | number | boolean | undefined }) {
    this.options.filterBy = Object.keys(filters)
      .filter(k => filters[k])
      .join(',')
    this.options.filterValue = Object.keys(filters)
      .filter(k => filters[k])
      .map(k => filters[k])
      .join(',')
  }
}
