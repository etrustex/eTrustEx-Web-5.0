import { RouteRecordRaw, RouteRecordRedirectOption } from 'vue-router'
import Administration from '@/admin/views/Administration.vue'
import { GroupType, RoleName, UserRole } from '@/model/entities'
import useAuthenticationStore, { AdminType } from '@/shared/store/authentication'
import { routeNames } from '@/router'
import { systemAdminRoute } from '@/admin/router/systemAdminRoute'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import { businessAdminRoute } from '@/admin/router/businessAdminRoute'
import BusinessesOverview from '@/admin/views/businesses/overview/BusinessesOverview.vue'
import { displayBusinessesBreadcrumbs } from '@/admin/router/breadcrumbsDisplayFns'

export const adminRoute: RouteRecordRaw = {
  path: `/${AdminRouteNames.ADMIN}`,
  name: AdminRouteNames.ADMIN,
  component: Administration,
  redirect: to => {
    let route: RouteRecordRedirectOption
    const authenticationStore = useAuthenticationStore()
    const adminType: AdminType = authenticationStore.adminType()
    let userRole: UserRole | undefined

    switch (adminType) {
      case AdminType.SYSTEM:
        route = { name: AdminRouteNames.SYSTEM }
        break
      case AdminType.MULTIPLE_BUSINESS:
      case AdminType.MULTIPLE_ENTITY_DIFFERENT_BUSINESS:
      case AdminType.OFFICIAL_IN_CHARGE:
        route = { name: AdminRouteNames.BUSINESSES }
        break
      case AdminType.SINGLE_BUSINESS:
        userRole = authenticationStore.getAdminUserRole(RoleName.GROUP_ADMIN, GroupType.BUSINESS)
        route = { name: AdminRouteNames.BUSINESS, params: { businessId: userRole?.businessId } }
        break
      case AdminType.MULTIPLE_ENTITY_SAME_BUSINESS:
        userRole = authenticationStore.getAdminUserRole(RoleName.GROUP_ADMIN, GroupType.ENTITY)
        route = { name: AdminRouteNames.BUSINESS, params: { businessId: userRole?.businessId } }
        break
      case AdminType.SINGLE_ENTITY:
        userRole = authenticationStore.getAdminUserRole(RoleName.GROUP_ADMIN, GroupType.ENTITY)
        route = {
          name: AdminRouteNames.ENTITY,
          params: { businessId: userRole?.businessId, entityId: userRole?.groupId }
        }
        break
      default:
        route = { name: routeNames.FORBIDDEN }
    }

    return route
  },
  children: [
    systemAdminRoute,
    {
      path: AdminRouteNames.BUSINESSES,
      name: AdminRouteNames.BUSINESSES,
      meta: {
        displayName: () => displayBusinessesBreadcrumbs() ? AdminRouteNames.BUSINESSES.toUpperCase() : undefined
      },
      redirect: { name: AdminRouteNames.BUSINESSES_OVERVIEW },
      children: [
        {
          path: 'overview',
          name: AdminRouteNames.BUSINESSES_OVERVIEW,
          meta: {
            displayName: () => displayBusinessesBreadcrumbs() ? 'overview' : undefined
          },
          component: BusinessesOverview
        },

        businessAdminRoute
      ]
    }
  ]
}
