import { AlertType } from '@/model/entities'

export class AlertTypeFormatter {
  static toAlertTypeTitle(type: AlertType): string {
    switch (type) {
      case AlertType.INFO:
        return 'Information message'
      case AlertType.SUCCESS:
        return 'Success message'
      case AlertType.WARNING:
        return 'Warning message'
      case AlertType.DANGER:
        return 'Error message'
    }
  }
}
