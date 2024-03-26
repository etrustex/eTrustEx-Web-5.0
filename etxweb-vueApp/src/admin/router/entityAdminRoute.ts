import { RouteRecordRaw } from 'vue-router'
import useGroupStore from '@/admin/store/group'
import { BreadcrumbsDisplayNames } from '@/shared/views/breadcrumbs/breadcrumbsDisplayNames'
import Users from '@/admin/views/shared/users/Users.vue'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import Entity from '@/admin/views/entities/entity/Entity.vue'
import MessagesMonitoring from '@/admin/views/shared/messages_monitoring/MessagesMonitoring.vue'
import EntityConfigurations from '@/admin/views/entities/entity/EntityConfigurations.vue'
import useEntityConfigurationStore from '@/shared/store/entityConfiguration'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'

export const entityAdminRoute: RouteRecordRaw = {
  path: ':entityId',
  name: AdminRouteNames.ENTITY,
  component: Entity,
  beforeEnter: (to, from, next) => {
    const groupStore = useGroupStore()

    groupStore.fetchCurrentEntity(+to.params.entityId)
      .then(() => next())
  },
  meta: {
    displayName: () => useGroupStore().entity.name
  },
  props: () => ({
    configurationsRoute: AdminRouteNames.CONFIGURATIONS,
    usersRoute: AdminRouteNames.ENTITY_USERS,
    registrationRequestsRoute: AdminRouteNames.REGISTRATION_REQUESTS,
    messagesMonitoringRoute: AdminRouteNames.MESSAGES_MONITORING,
    isRegistrationRequest: false
  }),
  redirect: { name: AdminRouteNames.ENTITY_USERS },
  children: [
    {
      path: 'configurations',
      name: AdminRouteNames.CONFIGURATIONS,
      component: EntityConfigurations,
      meta: { displayName: BreadcrumbsDisplayNames.CONFIGURATIONS },
      props: route => ({
        entityId: +route.params.entityId
      }),
      beforeEnter: (to, from, next) => {
        Promise.all([
          useBusinessConfigurationStore()
            .fetchCurrentBusinessConfigurations(+to.params.businessId),
          useEntityConfigurationStore()
            .fetchCurrentEntityConfigurations(+to.params.entityId)
        ])
          .then(() => next())
      }
    },
    {
      path: 'users',
      name: AdminRouteNames.ENTITY_USERS,
      component: Users,
      meta: { displayName: BreadcrumbsDisplayNames.USERS },
      props: route => ({
        groupId: +route.params.entityId,
        listConfig: {
          hideUserBulkCreation: true
        }
      })
    },
    {
      path: 'registration-request',
      name: AdminRouteNames.REGISTRATION_REQUESTS,
      component: Users,
      props: route => ({
        groupId: +route.params.entityId,
        listConfig: {
          isRegistrationRequest: true,
          hideUserBulkCreation: true
        },
        hideGlobalActions: true
      }),
      meta: {
        displayName: BreadcrumbsDisplayNames.REGISTRATION_REQUEST
      }
    },
    {
      path: 'messages-monitoring',
      name: AdminRouteNames.MESSAGES_MONITORING,
      component: MessagesMonitoring,
      props: route => ({
        groupId: +route.params.entityId
      }),
      meta: { displayName: BreadcrumbsDisplayNames.MESSAGES_MONITORING }
    }
  ]
}
