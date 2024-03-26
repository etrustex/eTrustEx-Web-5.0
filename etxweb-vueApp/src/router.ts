import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import DefaultLayout from '@/shared/views/DefaultLayout.vue'
import { MessageBoxRouteNames, messageRoutes } from '@/messages/router'
import useAuthenticationStore from '@/shared/store/authentication'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import Logout from '@/shared/views/Logout.vue'
import useOpenIdStore from '@/shared/store/openId'
import { adminRoute } from '@/admin/router'
import useUserStore from '@/admin/store/user'
import UserDto from '@/model/userDto'
import UserRegistration from '@/shared/views/UserRegistration.vue'
import { devRoutes } from '@/dev/router'
import UpdateCertificate from '@/shared/views/UpdateCertificate.vue'
import { CertificateUpdateApi } from '@/admin/api/certificateUpdateApi'
import { isSupportedBrowser } from '@/utils/browserUtils'

export enum routeNames {
  UNREGISTERED = 'unregistered',
  UNSUPPORTED = 'unsupported',
  FORBIDDEN = 'forbidden',
  LOGOUT = ' logout',
  NOT_FOUND = '404',
  USER_REGISTRATION_REQUEST = 'userRegistrationRequest',
  UPDATE_CERTIFICATE_REQUEST = 'updateCertificateRequest'
}

const routes: Array<RouteRecordRaw> = [
  {
    name: 'root',
    path: '/',
    component: DefaultLayout,
    beforeEnter: (to, from, next) => {
      if (!isSupportedBrowser()) {
        next({ name: routeNames.UNSUPPORTED })
        return
      }
      const authenticationStore = useAuthenticationStore()
      if (authenticationStore.isAdminRoute(to) && (authenticationStore.isAnAdmin() || authenticationStore.isOfficialInCharge())) {
        if (!authenticationStore.userDetails.username) {
          authenticationStore.loadUserDetails()
            .then(() => next())
        } else {
          next()
        }
      } else {
        const currentGroupStore = useCurrentGroupStore()
        if ((authenticationStore.isAnAdmin() || authenticationStore.isOfficialInCharge()) && !authenticationStore.userEntities.length) {
          next({ name: AdminRouteNames.ADMIN })
        } else if (!authenticationStore.userEntities.length) {
          next({ name: routeNames.UNREGISTERED })
        } else if (to.params && to.params.currentGroupId) {
          if (!authenticationStore.userEntities.find(u => `${ u.id }` === to.params.currentGroupId.toString())) {
            next({ name: routeNames.FORBIDDEN })
          } else if (`${ currentGroupStore.group.id }` !== to.params.currentGroupId.toString()) {
            currentGroupStore.changeGroup(Number(to.params.currentGroupId)).then(() => next())
          } else {
            next()
          }
        } else {
          next({
            name: MessageBoxRouteNames.INBOX,
            params: {
              ...to.params,
              currentGroupId: authenticationStore.firstUserEntityId,
            },
          })
        }
      }
    },
    children: [adminRoute, ...messageRoutes, ...devRoutes],
  },
  {
    path: '/user-registration',
    component: DefaultLayout,
    beforeEnter(to, from, next) {
      const authenticationStore = useAuthenticationStore()
      authenticationStore.loadUserDetails()
        .then(() => useUserStore().setUser({
          ecasId: authenticationStore.username,
          name: authenticationStore.fullName,
          euLoginEmailAddress: authenticationStore.userDetails.email,
        } as UserDto))
        .then(() => next())
    },
    children: [
      {
        path: ':groupId/:groupIdentifier',
        name: routeNames.USER_REGISTRATION_REQUEST,
        component: UserRegistration,
        props: route => ({
          user: useUserStore().user,
          groupId: +route.params.groupId,
          groupIdentifier: route.params.groupIdentifier,
        }),
      },
    ],
  },
  {
    path: '/certificate-update',
    component: DefaultLayout,
    beforeEnter(to, from, next) {
      const authenticationStore = useAuthenticationStore()
      authenticationStore.loadUserDetails()
        .then(userDetails => {
          if (+userDetails.id !== +to.params.userId) {
            next({ name: routeNames.NOT_FOUND })
          }
        })

      CertificateUpdateApi.isValidRedirectLink(+to.params.groupId, to.params.groupIdentifier, +to.params.userId)
        .then(isValid => {
          if (!isValid) {
            next({ name: routeNames.NOT_FOUND })
          }
        }).then(() => next())
    },
    children: [
      {
        path: ':groupId/:groupIdentifier/:userId',
        name: routeNames.UPDATE_CERTIFICATE_REQUEST,
        component: UpdateCertificate,
        props: route => ({
          user: useUserStore().user,
          groupId: +route.params.groupId,
          groupIdentifier: route.params.groupIdentifier,
        }),
      },
    ],
  },
  {
    path: '/error/unregistered',
    name: routeNames.UNREGISTERED,
    component: () => import(/* webpackChunkName: "Lazy" */ './shared/views/UnregisteredUser.vue'),
  },
  {
    path: '/error/unsupported',
    name: routeNames.UNSUPPORTED,
    component: () => import(/* webpackChunkName: "Lazy" */ './shared/views/Unsupported.vue'),
  },
  {
    path: '/error/forbidden',
    name: routeNames.FORBIDDEN,
    component: () => import(/* webpackChunkName: "Lazy" */ './shared/views/Forbidden.vue'),
  },
  {
    path: '/error/404',
    name: routeNames.NOT_FOUND,
    component: () => import(/* webpackChunkName: "Lazy" */ './shared/views/NotFound.vue'),
    props: true,
  },
  {
    path: '/logout',
    name: routeNames.LOGOUT,
    component: Logout,
    beforeEnter(to, from, next) {
      useOpenIdStore().deleteKeyPair()
      next()
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: routeNames.NOT_FOUND,
    component: () => import(/* webpackChunkName: "Lazy" */ './shared/views/NotFound.vue'),
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
