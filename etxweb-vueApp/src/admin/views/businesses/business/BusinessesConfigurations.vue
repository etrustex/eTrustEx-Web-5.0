<template>
  <div class="container-fluid">
    <div class="row justify-content-between">
      <div class="col-lg-8">
        <splash-screen id="splash_screen"/>
        <hr>
        <alert-details id="notification_banner" :business="business"/>
        <hr>
        <forbidden-extensions id="forbidden_file_extensions"/>
        <hr>
        <windows-compatible-filenames id="windows_compatible_filenames"/>
        <hr>
        <retention-policy id="retention_policy"/>
        <hr>
        <retention-policy-notification id="retention_policy_notification"/>
        <hr>
        <disable-encryption id="disable_encryption"/>
        <hr>
        <enforce-encryption id="enforce_encryption"/>
        <hr>
        <signature id="signature"/>
        <hr>
        <user-guides id="user_guides" :business="business"/>
        <hr>
        <unread-message-reminder id="unread_message_reminder"/>
        <hr>
        <transmission-limitation id="transmission_limitation" :business="business"/>
        <hr>
        <application-logo id="application_logo" :business="business"/>
        <hr>
        <notifications-email-from-group id="notification_email_address"/>
        <hr>
        <welcome-email id="welcome_email"/>
        <hr>
        <support-email-configuration id="support_email"/>
      </div>

      <div class="col-lg-3">
        <div class="sticky-top">
          <h3 class="h6 mb-3 pb-3 border-bottom">On this page</h3>
          <nav aria-label="Table of contents" class="nav-table-of-contents">
            <ul class="list-unstyled">
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('splash_screen')">Splash Screen</router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('notification_banner')">
                  Notification banner
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('forbidden_file_extensions')">
                  Forbidden file extensions
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('windows_compatible_filenames')">
                  Accept only Windows compatible filenames
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('retention_policy')">
                  Retention policy
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('retention_policy_notification')">
                  Retention policy warning notification
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('disable_encryption')">
                  Disable end-to-end encryption
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('enforce_encryption')">
                  Enforce end-to-end encryption
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('signature')">Signature</router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('user_guides')">User guides</router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('unread_message_reminder')">
                  Unread message reminder
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('transmission_limitation')">
                  Transmission limitations
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('application_logo')">
                  Application logo
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('notification_email_address')">
                  Notification email address
                </router-link>
              </li>
              <li>
                <router-link :to="configurationRoute" @click="scrollPageTo('welcome_email')">Welcome email</router-link>
              </li>
              <li>
                <router-link id="node_configuration" :to="configurationRoute" @click="scrollPageTo('support_email')">
                  Support email address
                </router-link>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed, ComputedRef, PropType } from 'vue'
import { scrollPageTo } from '@/utils/scroll'
import useAuthenticationStore from '@/shared/store/authentication'
import SplashScreen from '@/admin/views/businesses/business/configurations/SplashScreen.vue'
import { Group } from '@/model/entities'
import AlertDetails from '@/admin/views/shared/AlertDetails.vue'
import ForbiddenExtensions from '@/admin/views/businesses/business/configurations/ForbiddenExtensions.vue'
import WindowsCompatibleFilenames from '@/admin/views/businesses/business/configurations/WindowsCompatibleFilenames.vue'
import RetentionPolicy from '@/admin/views/businesses/business/configurations/RetentionPolicy.vue'
import RetentionPolicyNotification
  from '@/admin/views/businesses/business/configurations/RetentionPolicyNotification.vue'
import DisableEncryption from '@/admin/views/businesses/business/configurations/DisableEncryption.vue'
import EnforceEncryption from '@/admin/views/businesses/business/configurations/EnforceEncryption.vue'
import Signature from '@/admin/views/businesses/business/configurations/Signature.vue'
import UserGuides from '@/admin/views/shared/configurations/UserGuides.vue'
import UnreadMessageReminder from '@/admin/views/businesses/business/configurations/UnreadMessageReminder.vue'
import TransmissionLimitation from '@/admin/views/businesses/business/configurations/TransmissionLimitation.vue'
import ApplicationLogo from '@/admin/views/businesses/business/configurations/ApplicationLogo.vue'
import NotificationsEmailFromGroup
  from '@/admin/views/businesses/business/configurations/NotificationsEmailFromGroup.vue'
import WelcomeEmail from '@/admin/views/businesses/business/configurations/WelcomeEmail.vue'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import SupportEmailConfiguration from '@/admin/views/businesses/business/configurations/SupportEmailConfiguration.vue'

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const authenticationStore = useAuthenticationStore()
const isSystemAdmin: ComputedRef<boolean> = computed(() => authenticationStore.isSysAdmin())
const configurationRoute = { name: AdminRouteNames.BUSINESS_CONFIGURATIONS }

</script>
