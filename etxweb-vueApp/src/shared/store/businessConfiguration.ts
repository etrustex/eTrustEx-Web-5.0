import {
  Alert,
  DisableEncryptionGroupConfiguration,
  EnforceEncryptionGroupConfiguration,
  FileSizeLimitationGroupConfiguration,
  ForbiddenExtensionsGroupConfiguration,
  GroupConfiguration,
  LogoGroupConfiguration,
  NotificationsEmailFromGroupConfiguration,
  NumberOfFilesLimitationGroupConfiguration,
  RetentionPolicyGroupConfiguration,
  RetentionPolicyNotificationGroupConfiguration,
  SignatureGroupConfiguration,
  SplashScreenGroupConfiguration,
  SupportEmailGroupConfiguration,
  TotalFileSizeLimitationGroupConfiguration,
  UnreadMessageReminderConfiguration,
  WelcomeEmailGroupConfiguration,
  WindowsCompatibleFilenamesGroupConfiguration,
} from '@/model/entities'
import { defineStore } from 'pinia'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import { AlertApi } from '@/admin/api/alertApi'

const nameSpace = 'businessConfiguration'
const toTitleCase = (txt: string) => txt[0].toLowerCase() + txt.substring(1)

interface State {
  windowsCompatibleFilenamesGroupConfiguration: WindowsCompatibleFilenamesGroupConfiguration
  forbiddenExtensionsGroupConfiguration: ForbiddenExtensionsGroupConfiguration
  retentionPolicyGroupConfiguration: RetentionPolicyGroupConfiguration
  retentionPolicyNotificationGroupConfiguration: RetentionPolicyNotificationGroupConfiguration
  splashScreenGroupConfiguration: SplashScreenGroupConfiguration
  disableEncryptionGroupConfiguration: DisableEncryptionGroupConfiguration
  enforceEncryptionGroupConfiguration: EnforceEncryptionGroupConfiguration
  signatureGroupConfiguration: SignatureGroupConfiguration
  unreadMessageReminderConfiguration: UnreadMessageReminderConfiguration
  fileSizeLimitationGroupConfiguration: FileSizeLimitationGroupConfiguration
  totalFileSizeLimitationGroupConfiguration: TotalFileSizeLimitationGroupConfiguration
  numberOfFilesLimitationGroupConfiguration: NumberOfFilesLimitationGroupConfiguration
  logoGroupConfiguration: LogoGroupConfiguration
  supportEmailGroupConfiguration: SupportEmailGroupConfiguration
  notificationsEmailFromGroupConfiguration: NotificationsEmailFromGroupConfiguration
  welcomeEmailGroupConfiguration: WelcomeEmailGroupConfiguration
  euAlertConfiguration: Alert
}

const useBusinessConfigurationStore = defineStore(nameSpace, {
  state: (): State => ({
    windowsCompatibleFilenamesGroupConfiguration: new WindowsCompatibleFilenamesGroupConfiguration(),
    forbiddenExtensionsGroupConfiguration: new ForbiddenExtensionsGroupConfiguration(),
    retentionPolicyGroupConfiguration: new RetentionPolicyGroupConfiguration(),
    retentionPolicyNotificationGroupConfiguration: new RetentionPolicyNotificationGroupConfiguration(),
    splashScreenGroupConfiguration: new SplashScreenGroupConfiguration(),
    disableEncryptionGroupConfiguration: new DisableEncryptionGroupConfiguration(),
    enforceEncryptionGroupConfiguration: new EnforceEncryptionGroupConfiguration(),
    signatureGroupConfiguration: new SignatureGroupConfiguration(),
    unreadMessageReminderConfiguration: new UnreadMessageReminderConfiguration(),
    fileSizeLimitationGroupConfiguration: new FileSizeLimitationGroupConfiguration(),
    totalFileSizeLimitationGroupConfiguration: new TotalFileSizeLimitationGroupConfiguration(),
    numberOfFilesLimitationGroupConfiguration: new NumberOfFilesLimitationGroupConfiguration(),
    logoGroupConfiguration: new LogoGroupConfiguration(),
    supportEmailGroupConfiguration: new SupportEmailGroupConfiguration(),
    notificationsEmailFromGroupConfiguration: new NotificationsEmailFromGroupConfiguration(),
    welcomeEmailGroupConfiguration: new WelcomeEmailGroupConfiguration(),
    euAlertConfiguration: new Alert()
  }),
  actions: {
    async fetchCurrentBusinessConfigurations(groupId: number) {
      const groupConfigurations = await GroupConfigurationApi.getByGroup(groupId)

      if (groupConfigurations.length) {
        groupConfigurations.forEach(conf => {
          this.updateValue(conf)
        })
      }

      return groupConfigurations
    },
    async fetchAlertConfigurations(businessId: number) {
      return AlertApi.get(businessId, true)
        .then(result => this.euAlertConfiguration = result[0] ? result[0] : new Alert())
    },
    updateValue(conf: GroupConfiguration<unknown>) {
      // @ts-ignore: TS7053
      this[toTitleCase(conf.dtype)] = conf
    }
  }

})

export default useBusinessConfigurationStore
