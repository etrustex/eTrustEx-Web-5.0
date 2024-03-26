import { defineStore } from 'pinia'
import { SplashScreenGroupConfiguration } from '@/model/entities'

const nameSpace = 'dialog'

export enum DialogType { INFO, ERROR }

export type DialogConfig = {
  show: boolean;
  title: string;
  message: string;
  hideIcon: boolean;
  type: DialogType;
  isSplash: boolean;
}

export type DialogButtonConfig = {
  show: boolean;
  title: string;
  callback?: CallableFunction;
}

interface State {
  config: DialogConfig,
  primaryButton: DialogButtonConfig,
  secondaryButton: DialogButtonConfig,
}

export const CANCEL_BUTTON: DialogButtonConfig = {
  show: true,
  title: 'Cancel'
}

export function buttonWithCallback(title: string, callback: CallableFunction) {
  return { show: true, title, callback }
}

const useDialogStore = defineStore(nameSpace, {
  state: (): State => ({
    config: { show: false, title: '', message: '', hideIcon: false, type: DialogType.INFO, isSplash: false },
    primaryButton: { show: true, title: 'OK' },
    secondaryButton: { show: false, title: 'Cancel' }
  }),
  actions: {
    show(
      title: string,
      message: string,
      type: DialogType = DialogType.INFO,
      primaryButton?: DialogButtonConfig,
      secondaryButton?: DialogButtonConfig,
      hideIcon = false
    ) {
      this.config = { ...this.config, title, message, type, hideIcon, show: true, isSplash: false }
      this.primaryButton = primaryButton || { show: true, title: 'Ok' }
      this.secondaryButton = secondaryButton || { show: false, title: 'Cancel' }
    },
    hide() {
      this.config.show = false
    },
    displaySplashScreen(authenticated: boolean, configuration: SplashScreenGroupConfiguration) {
      if (authenticated && configuration.active) {
        this.show('', configuration.stringValue)
        this.config.isSplash = true
      }
    }
  }
})

export default useDialogStore
