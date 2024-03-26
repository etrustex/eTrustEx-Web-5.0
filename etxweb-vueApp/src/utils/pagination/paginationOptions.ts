/**
 * Pagination options object
 */
export declare interface PaginationOptions {
  /**
   * zero-based page index
   */
  page?: number
  /**
   * the size of the page to be returned
   */
  size?: number

  /**
   * List of properties to sort for that must not include null or empty strings
   */
  sortBy?: string
  /**
   * The sort direction, defaults to DESC
   */
  sortOrder?: string

  /**
   * Optional parameter to filter results
   */
  filterBy?: string

  filterValue?: string

  /**
   * Other request parameters
   */
  [key: string]: any
}
