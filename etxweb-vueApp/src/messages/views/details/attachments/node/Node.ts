export interface NodeItem {
  id: number
  name: string
  selected: boolean
  children: Array<NodeItem>
  item: any
  showAdditionalActions: boolean
}
