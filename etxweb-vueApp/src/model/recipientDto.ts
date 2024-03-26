import { Status } from '@/model/entities'

export default class RecipientDto {
  name: string
  status: Status
  statusModifiedDate: Date
  e2eEncryption: boolean

  constructor(name: string, status: Status, statusModifiedDate: Date, e2eEncryption: boolean) {
    this.name = name
    this.status = status
    this.statusModifiedDate = statusModifiedDate
    this.e2eEncryption = e2eEncryption
  }

  static compare(n1: RecipientDto, n2: RecipientDto): number {
    if (n1.name.toUpperCase() > n2.name.toUpperCase()) return 1
    if (n1.name.toUpperCase() < n2.name.toUpperCase()) return -1
    return 0
  }
}
