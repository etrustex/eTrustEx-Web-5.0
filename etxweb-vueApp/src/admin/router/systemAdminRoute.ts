import { RouteRecordRaw } from 'vue-router'
import System from '@/admin/views/system/System.vue'
import SystemConfigurations from '@/admin/views/system/SystemConfigurations.vue'
import useAuthenticationStore from '@/shared/store/authentication'
import useGroupStore from '@/admin/store/group'
import { BreadcrumbsDisplayNames } from '@/shared/views/breadcrumbs/breadcrumbsDisplayNames'
import Users from '@/admin/views/shared/users/Users.vue'
import { RoleName } from '@/model/entities'
import SysEntities from '@/admin/views/system/SysEntities.vue'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import SysCertificates from '@/admin/views/system/SysCertificates.vue'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import SysLogs from '@/admin/views/system/SysLogs.vue'

export const systemAdminRoute: RouteRecordRaw =
  {
    path: 'system',
    name: AdminRouteNames.SYSTEM,
    component: System,
    meta: {
      displayName: 'SYSTEM'
    },
    redirect: { name: AdminRouteNames.ROOT_CONFIGURATIONS },
    children: [
      {
        path: `${AdminRouteNames.ROOT_CONFIGURATIONS}`,
        name: AdminRouteNames.ROOT_CONFIGURATIONS,
        component: SystemConfigurations,
        beforeEnter: (to, from, next) => {
          const rootGroupId = useAuthenticationStore().rootGroupId
          const groupStore = useGroupStore()
          const businessConfigurationStore = useBusinessConfigurationStore()

          if (!rootGroupId) {
            console.error('no root found')
            return
          }

          groupStore.fetchCurrentBusiness(rootGroupId)
            .then(() => Promise.all([
              businessConfigurationStore.fetchCurrentBusinessConfigurations(rootGroupId),
              businessConfigurationStore.fetchAlertConfigurations(rootGroupId)
            ])
              .then(() => next()))
        },
        props: () => ({
          rootGroup: useGroupStore().business
        }),
        meta: {
          displayName: BreadcrumbsDisplayNames.CONFIGURATIONS
        }
      },
      {
        path: 'administrators',
        name: AdminRouteNames.SYS_ADMINS,
        component: Users,
        props: () => ({
          role: RoleName.SYS_ADMIN,
          groupId: useAuthenticationStore().rootGroupId,
          listConfig: {
            hideRoleColumn: true,
            hideNotificationColumn: true,
            hideUserBulkCreation: true,
            hideEditButton: true
          }
        }),
        meta: {
          displayName: BreadcrumbsDisplayNames.ADMINISTRATORS
        }
      },
      {
        path: `${AdminRouteNames.SYS_ENTITIES}`,
        name: AdminRouteNames.SYS_ENTITIES,
        component: SysEntities,
        meta: {
          displayName: BreadcrumbsDisplayNames.Entities
        }
      },
      {
        path: `${AdminRouteNames.SYS_USERS}`,
        name: AdminRouteNames.SYS_USERS,
        component: Users,
        props: route => ({
          role: RoleName.SYS_ADMIN,
          groupId: useAuthenticationStore().rootGroupId,
          listConfig: {
            isAllUsers: true,
            displayEntityColumn: true,
            displayBusinessColumn: true,
            hideNotificationColumn: true,
            hideEditButton: true,
            hideUserBulkCreation: true,
            hideActionColumn: true
          }
        }),
        meta: {
          displayName: BreadcrumbsDisplayNames.USERS
        }
      },
      {
        path: 'officials',
        name: AdminRouteNames.SYS_OFFICIALS_IN_CHARGE,
        component: Users,
        props: () => ({
          role: RoleName.OFFICIAL_IN_CHARGE,
          groupId: useAuthenticationStore().rootGroupId,
          listConfig: {
            hideRoleColumn: true,
            hideNotificationColumn: true,
            hideUserBulkCreation: true,
            hideEditButton: true
          }
        }),
        meta: {
          displayName: BreadcrumbsDisplayNames.OFFICIALS_IN_CHARGE
        }
      },
      {
        path: `${AdminRouteNames.SYS_CERTIFICATES}`,
        name: AdminRouteNames.SYS_CERTIFICATES,
        component: SysCertificates,
        meta: {
          displayName: BreadcrumbsDisplayNames.Certificates
        }
      },
      {
        path: `${AdminRouteNames.SYS_LOGS}`,
        name: AdminRouteNames.SYS_LOGS,
        component: SysLogs,
        meta: {
          displayName: BreadcrumbsDisplayNames.SYSTEM_LOGS
        }
      }
    ]
  }
