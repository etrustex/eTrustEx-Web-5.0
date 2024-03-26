import useAuthenticationStore, { AdminType } from '@/shared/store/authentication'

export function displayBusinessesBreadcrumbs(): boolean {
  const adminType: AdminType = useAuthenticationStore()
    .adminType()

  return adminType === AdminType.SYSTEM ||
    adminType === AdminType.MULTIPLE_BUSINESS ||
    adminType === AdminType.MULTIPLE_ENTITY_DIFFERENT_BUSINESS
}

export function displayEntitiesBreadcrumbs(): boolean {
  const adminType: AdminType = useAuthenticationStore()
    .adminType()
  return displayBusinessesBreadcrumbs() || adminType === AdminType.MULTIPLE_ENTITY_SAME_BUSINESS
}
