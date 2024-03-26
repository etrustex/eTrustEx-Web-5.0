export class DialogProps {
  title: string
  message: string
  okTitle: string
  id: string
  fn = () => Promise.resolve()
  cancelFn = () => Promise.resolve()

  constructor(id: string, title: string, okTitle = 'Ok') {
    this.id = id
    this.title = title
    this.okTitle = okTitle
  }
}
