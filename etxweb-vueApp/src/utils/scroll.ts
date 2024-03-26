export const scrollPageTo = (id: string) => {
  const element = document.querySelector(`#${id}`)
  element?.scrollIntoView({ behavior: 'smooth' })
}
