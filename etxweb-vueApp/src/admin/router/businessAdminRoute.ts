import { RouteRecordRaw, RouteRecordRedirectOption } from 'vue-router'
import useGroupStore from '@/admin/store/group'
import { BreadcrumbsDisplayNames } from '@/shared/views/breadcrumbs/breadcrumbsDisplayNames'
import Users from '@/admin/views/shared/users/Users.vue'
import { GroupType, RoleName, UserRole } from '@/model/entities'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import Business from '@/admin/views/businesses/business/Business.vue'
import BusinessOverview from '@/admin/views/businesses/business/BusinessOverview.vue'
import BusinessesConfigurations from '@/admin/views/businesses/business/BusinessesConfigurations.vue'
import ChannelList from '@/admin/views/shared/channel/ChannelList.vue'
import ExchangeRuleList from '@/admin/views/shared/exchange_rule/ExchangeRuleList.vue'
import useChannelStore from '@/admin/store/channel'
import Entities from '@/admin/views/entities/Entities.vue'
import { entityAdminRoute } from '@/admin/router/entityAdminRoute'
import useAuthenticationStore, { AdminType } from '@/shared/store/authentication'
import { routeNames } from '@/router'
import { displayBusinessesBreadcrumbs, displayEntitiesBreadcrumbs } from '@/admin/router/breadcrumbsDisplayFns'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import BusinessQuickStart from '@/admin/views/businesses/business/BusinessQuickStart.vue'
import BusinessesTemplates from '@/admin/views/businesses/business/BusinessesTemplates.vue'
import MessageSummaryListItem from '@/admin/views/shared/messages/MessageSummaryListItem.vue'

export const businessAdminRoute: RouteRecordRaw = {
  path: ':businessId',
  name: AdminRouteNames.BUSINESS,
  meta: {
    displayName: () => displayBusinessesBreadcrumbs() ? useGroupStore().business.name : undefined
  },
  beforeEnter: (to, from, next) => {
    const groupStore = useGroupStore()

    groupStore.fetchCurrentBusiness(+to.params.businessId)
      .then(() => next())
  },
  component: Business,
  redirect: to => {
    let route: RouteRecordRedirectOption
    const authenticationStore = useAuthenticationStore()

    switch (authenticationStore.adminType()) {
      case AdminType.SYSTEM:
      case AdminType.MULTIPLE_BUSINESS:
      case AdminType.SINGLE_BUSINESS:
        route = { name: AdminRouteNames.BUSINESS_OVERVIEW }
        break
      case AdminType.MULTIPLE_ENTITY_SAME_BUSINESS:
        route = { name: AdminRouteNames.ENTITIES }
        break
      case AdminType.MULTIPLE_ENTITY_DIFFERENT_BUSINESS:
        if (authenticationStore.isMultipleEntityAdminInBusiness(+to.params.businessId)) {
          route = { name: AdminRouteNames.ENTITIES }
          break
        }
      case AdminType.SINGLE_ENTITY:
        const userRole: UserRole | undefined = authenticationStore.getAdminUserRole(RoleName.GROUP_ADMIN, GroupType.ENTITY, +to.params.businessId)
        route = { path: `${to.path}/entities/${userRole?.groupId}` }
        break
      default:
        route = { name: routeNames.FORBIDDEN }
    }
    return route
  },
  children: [
    {
      path: 'overview',
      name: AdminRouteNames.BUSINESS_OVERVIEW,
      component: BusinessOverview,
      props: route => ({
        businessId: +route.params.businessId
      }),
      meta: {
        displayName: BreadcrumbsDisplayNames.OVERVIEW
      }
    },
    {
      path: 'administrators',
      name: AdminRouteNames.BUSINESS_ADMINISTRATORS,
      component: Users,
      props: route => ({
        groupId: +route.params.businessId,
        role: RoleName.GROUP_ADMIN,
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
      path: 'configurations',
      name: AdminRouteNames.BUSINESS_CONFIGURATIONS,
      component: BusinessesConfigurations,
      meta: {
        displayName: BreadcrumbsDisplayNames.CONFIGURATIONS
      },
      props: () => ({
        business: useGroupStore().business
      }),
      beforeEnter: (to, from, next) => {
        const groupStore = useGroupStore()
        const businessConfigurationStore = useBusinessConfigurationStore()
        Promise.all([
          businessConfigurationStore.fetchCurrentBusinessConfigurations(groupStore.business.id),
          businessConfigurationStore.fetchAlertConfigurations(groupStore.business.id)
        ])
          .then(() => next())
      }
    },
    {
      path: 'templates',
      name: AdminRouteNames.BUSINESS_TEMPLATES,
      component: BusinessesTemplates,
      meta: {
        displayName: BreadcrumbsDisplayNames.TEMPLATES
      },
      props: () => ({
        business: useGroupStore().business
      }),
      beforeEnter: (to, from, next) => {
        const groupStore = useGroupStore()
        const businessConfigurationStore = useBusinessConfigurationStore()
        Promise.all([
          businessConfigurationStore.fetchCurrentBusinessConfigurations(groupStore.business.id),
          businessConfigurationStore.fetchAlertConfigurations(groupStore.business.id)
        ])
          .then(() => next())
      }
    },
    {
      path: 'quick-start',
      name: AdminRouteNames.BUSINESS_QUICK_START,
      meta: {
        displayName: 'Initial Setup'
      },
      props: () => ({
        business: useGroupStore().business
      }),
      component: BusinessQuickStart
    },
    {
      path: 'exchange-configurations',
      name: AdminRouteNames.BUSINESS_CHANNELS,
      redirect: { name: AdminRouteNames.BUSINESS_CHANNELS_OVERVIEW },
      meta: {
        displayName: 'Exchange Configurations'
      },
      children: [
        {
          path: 'overview',
          name: AdminRouteNames.BUSINESS_CHANNELS_OVERVIEW,
          component: ChannelList,
          meta: {
            displayName: BreadcrumbsDisplayNames.OVERVIEW
          },
          props: route => ({
            businessId: +route.params.businessId
          })
        },
        {
          path: ':channelId',
          name: AdminRouteNames.BUSINESS_CHANNEL,
          component: ExchangeRuleList,
          meta: {
            displayName: () => useChannelStore().channel.name
          }

        }
      ]
    },
    {
      path: 'entities',
      name: AdminRouteNames.ENTITIES,
      meta: {
        displayName: () => displayEntitiesBreadcrumbs() ? 'Entities' : undefined
      },
      redirect: { name: AdminRouteNames.BUSINESS_ENTITIES_OVERVIEW },
      children: [
        {
          path: 'overview',
          name: AdminRouteNames.BUSINESS_ENTITIES_OVERVIEW,
          component: Entities,
          meta: {
            displayName: BreadcrumbsDisplayNames.OVERVIEW
          }
        },
        entityAdminRoute
      ]
    },
    {
      path: 'users',
      name: AdminRouteNames.BUSINESS_USERS,
      component: Users,
      props: route => ({
        groupId: +route.params.businessId,
        role: undefined,
        listConfig: {
          displayEntityColumn: true
        },
        hideGlobalActions: true
      }),
      meta: {
        displayName: BreadcrumbsDisplayNames.USERS
      }
    },
    {
      path: 'registration-request',
      name: AdminRouteNames.BUSINESS_USER_REGISTRATION_REQUESTS,
      component: Users,
      props: route => ({
        groupId: +route.params.businessId,
        role: undefined,
        listConfig: {
          isRegistrationRequest: true,
          displayEntityColumn: true
        },
        hideGlobalActions: true
      }),
      meta: {
        displayName: BreadcrumbsDisplayNames.REGISTRATION_REQUEST
      }
    },
    {
      path: 'message-display',
      name: AdminRouteNames.BUSINESS_MESSAGE_SUMMARY_LIST_ITEMS,
      component: MessageSummaryListItem,
      props: route => ({
        businessId: +route.params.businessId
      }),
      meta: {
        displayName: BreadcrumbsDisplayNames.MESSAGES_DISPLAY
      }
    }
  ]
}
