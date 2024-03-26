import { Confidentiality, Group, GroupSpec } from '@/model/entities'

export enum EntityE2EEncryption {
  MANDATORY = 'MANDATORY',
  ENABLED = 'ENABLED',
  NOT_ENABLED = 'NOT_ENABLED',
}

export class GroupHelper {
  static toSpec(group: Group): GroupSpec {
    const spec: GroupSpec = new GroupSpec()
    spec.id = group.id
    spec.identifier = group.identifier
    spec.displayName = group.name
    spec.parentGroupId = group.parent ? group.parent.id : 0
    spec.active = group.active
    spec.system = group.system
    spec.endpoint = group.systemEndpoint
    spec.type = group.type
    spec.description = group.description
    spec.newMessageNotificationEmailAddresses = group.newMessageNotificationEmailAddresses
    spec.statusNotificationEmailAddress = group.statusNotificationEmailAddress
    spec.individualStatusNotifications = group.individualStatusNotifications

    if (group.recipientPreferences) {
      spec.recipientPreferencesId = group.recipientPreferences.id
    }

    if (group.senderPreferences) {
      spec.senderPreferencesId = group.senderPreferences.id
    }

    return spec
  }

  static getEncryptionConfig(group: Group) {
    if ((!!group.recipientPreferences) && (!!group.recipientPreferences.publicKey) &&
      (group.recipientPreferences.confidentiality === Confidentiality.LIMITED_HIGH)) {
      return EntityE2EEncryption.MANDATORY
    } else if ((!!group.recipientPreferences) && (!!group.recipientPreferences.publicKey) &&
      (group.recipientPreferences.confidentiality === Confidentiality.PUBLIC)) {
      return EntityE2EEncryption.ENABLED
    } else {
      return EntityE2EEncryption.NOT_ENABLED
    }
  }
}
