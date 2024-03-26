import { defineStore } from 'pinia'
import { OidcClient, OidcClientSettings } from 'oidc-client'
import application from '@/application.json'
import {
  developmentPublicKeyUrlParamName,
  developmentRedirectUrlParamName,
  getDevelopmentLoginRedirectUrl,
  getDevelopmentPublicKey,
  getIdToken,
  isLocalhost,
  retrieveAndCleanupItem,
  retrieveItem,
  storeItem,
} from '@/utils/openIdHelper'
import { addParameter, addParameterToHash } from '@/utils/linksHandler'
import { KEYUTIL, KJUR, RSAKey } from 'jsrsasign'

enum LoginStatuses {
  NORMAL_START,
  NORMAL_COMPLETE,
  DELEGATE_START,
  DELEGATE_COMPLETE,
  DEV_START,
  DEV_COMPLETE
}

const nameSpace = 'openId'

interface State {
  euLoginAccessTokenUrl: string
  tokenRequestPortNumber: number | undefined
  idToken: string
  keyPair: { prvKeyObj: RSAKey; pubKeyObj: RSAKey }
  publicKeyInJWKFormat: string
  oidClientSettings: OidcClientSettings
  openIdUiDevelopmentSettings: OpenIdUiDevelopmentSettings
  oidClient: OidcClient
  sessionStorageSettings: any
  audience: string
  DEFAULT_ACCESS_TOKEN_SIGNING_ALGORITHM: string
  DEFAULT_ID_TOKEN_SIGNING_ALGORITHM: string
}

const useOpenIdStore = defineStore(nameSpace, {
  state: (): State => ({
    euLoginAccessTokenUrl: '',
    tokenRequestPortNumber: -1,
    idToken: '',
    keyPair: {} as { prvKeyObj: RSAKey; pubKeyObj: RSAKey },
    publicKeyInJWKFormat: '',
    oidClientSettings: {} as OidcClientSettings,
    openIdUiDevelopmentSettings: {} as OpenIdUiDevelopmentSettings,
    oidClient: {} as OidcClient,
    sessionStorageSettings: {
      KEY_ID_TOKEN: 'ux-openid-connect-id-token',
      PUBLIC_KEY: 'ux-openid-connect-public-key',
      PRIVATE_KEY: 'ux-openid-connect-private-key',
      TARGET_HASH: 'ux-openid-connect-target-hash',
      DEVELOPMENT_LOGIN_REDIRECT_URL: 'ux-openid-connect-development-login-redirect-url'
    },
    audience: '',
    DEFAULT_ACCESS_TOKEN_SIGNING_ALGORITHM: 'ES256',
    DEFAULT_ID_TOKEN_SIGNING_ALGORITHM: 'ES256'
  }),

  getters: {
    authenticated: (state) => state.keyPair.pubKeyObj !== undefined
  },

  actions: {
    fetchSettings(envName: string) {
      const env = (application.env as any)[envName]
      const { oidClientSettings } = env
      const oidClient = new OidcClient(oidClientSettings)
      const { euLoginAccessTokenUrl } = env
      const { tokenRequestPortNumber } = env
      const { openIdUiDevelopmentSettings } = env
      const { audience } = env

      this.oidClient = oidClient
      this.oidClientSettings = oidClientSettings
      this.euLoginAccessTokenUrl = euLoginAccessTokenUrl
      this.tokenRequestPortNumber = tokenRequestPortNumber
      this.openIdUiDevelopmentSettings = openIdUiDevelopmentSettings as OpenIdUiDevelopmentSettings
      this.audience = audience

      return env
    },

    getLoginStatus(): LoginStatuses {
      if (this.openIdUiDevelopmentSettings.allowDevelopmentLogin) {
        console.log('DevelopmentLogin')
        if (getDevelopmentLoginRedirectUrl(window.location.href)) {
          console.log('LoginStatuses.DEV_START')
          return LoginStatuses.DEV_START
        }
        if (retrieveItem(this.sessionStorageSettings.DEVELOPMENT_LOGIN_REDIRECT_URL)) {
          console.log('LoginStatuses.DEV_COMPLETE')
          return LoginStatuses.DEV_COMPLETE
        }
      }

      if (this.openIdUiDevelopmentSettings.delegateLogin) {
        if (getIdToken()) {
          return LoginStatuses.DELEGATE_COMPLETE
        }

        return LoginStatuses.DELEGATE_START
      }

      if (getIdToken()) {
        return LoginStatuses.NORMAL_COMPLETE
      }

      return LoginStatuses.NORMAL_START
    },

    loginOrRestore(): Promise<void> {
      return new Promise((resolve, reject) => {
        switch (this.getLoginStatus()) {
          case LoginStatuses.NORMAL_START:
            this.login()
              .then(() => reject('normal login'))
            break
          case LoginStatuses.NORMAL_COMPLETE:
          case LoginStatuses.DELEGATE_COMPLETE:
            this.restore()
              .then(() => resolve())
            break
          case LoginStatuses.DELEGATE_START:
            this.delegateLogin()
              .finally(() => reject('delegating login'))
            break
          case LoginStatuses.DEV_START:
            this.initDevelopmentLogin(window.location.href)
              .then(() => reject('init dev login'))
            break
          case LoginStatuses.DEV_COMPLETE:
            this.completeDevelopmentLogin()
              .then(() => resolve())
            break
        }
      })
    },

    async login(): Promise<void> {
      await this.saveRequestHash()
      await this.generateKeyPair()
      await this.renewIdToken()
    },

    async restore(): Promise<void> {
      this.idToken = getIdToken() as string
      await this.restoreLocationHash()
      await this.restoreKeyPairFromSession()
    },

    async initDevelopmentLogin(url: string): Promise<void> {
      const developmentRedirectUrl = getDevelopmentLoginRedirectUrl(url) as string

      if (!isLocalhost(developmentRedirectUrl)) {
        return
      }

      storeItem(this.sessionStorageSettings.DEVELOPMENT_LOGIN_REDIRECT_URL, developmentRedirectUrl)

      const dummyKeyPair = KEYUTIL.generateKeypair('EC', 'secp256r1')
      const publicKey = getDevelopmentPublicKey(url) as string
      const keyPair = {
        prvKeyObj: dummyKeyPair.prvKeyObj,
        pubKeyObj: KEYUTIL.getKey(publicKey)
      }
      await this.setKeys(keyPair as { prvKeyObj: RSAKey; pubKeyObj: RSAKey })
      await this.renewIdToken()
    },

    async completeDevelopmentLogin(): Promise<void> {
      const developmentRedirectUrl = retrieveAndCleanupItem(this.sessionStorageSettings.DEVELOPMENT_LOGIN_REDIRECT_URL) as string
      window.location.href = addParameterToHash(developmentRedirectUrl, 'id_token', getIdToken() as string)
    },

    async delegateLogin(): Promise<void> {
      await this.saveRequestHash()
      await this.generateKeyPair()

      let delegateLoginTarget = addParameter(
        this.openIdUiDevelopmentSettings.loginDelegateUrl,
        developmentRedirectUrlParamName, window.location.href
      )

      const publicKeyPem: string = KEYUTIL.getPEM(this.keyPair.pubKeyObj)

      delegateLoginTarget = addParameter(
        delegateLoginTarget,
        developmentPublicKeyUrlParamName, publicKeyPem
      )

      window.location.href = delegateLoginTarget
    },

    async getAccessToken(options: { signal?: AbortSignal }): Promise<{ token_type: string, access_token: string }> {
      const signedIdToken: string = await this.getSignedIdToken()
      const parameters = '?' +
        'grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer' +
        '&assertion=' + signedIdToken +
        '&audience=' + this.audience +
        '&client_id=' + this.oidClientSettings.client_id
      const authUrl = this.euLoginAccessTokenUrl + parameters

      const headers = {
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
      }
      return fetch(authUrl, { method: 'POST', credentials: 'include', headers, signal: options.signal })
        .then(response => response.json())
        .then(value => value as { token_type: string, access_token: string })
    },

    renewIdToken(): Promise<void> {
      return new Promise((resolve) => {
        this.oidClient.createSigninRequest({
          redirect_uri: this.oidClientSettings.redirect_uri,
          extraQueryParams: { req_cnf: this.publicKeyInJWKFormat }
        })
          .then(value => {
            if (value !== null && value.url !== null) {
              resolve()
              window.top!.location.href = value.url
            }
          })
      })
    },

    async saveRequestHash(): Promise<void> {
      storeItem(this.sessionStorageSettings.TARGET_HASH, window.location.hash)
    },

    async restoreLocationHash(): Promise<void> {
      window.top!.location.hash = retrieveAndCleanupItem(this.sessionStorageSettings.TARGET_HASH) as string
    },

    async generateKeyPair(): Promise<void> {
      const keyPair = KEYUTIL.generateKeypair('EC', 'secp256r1')

      storeItem(this.sessionStorageSettings.PRIVATE_KEY, KEYUTIL.getPEM(keyPair.prvKeyObj, 'PKCS8PRV') as unknown as string)
      storeItem(this.sessionStorageSettings.PUBLIC_KEY, KEYUTIL.getPEM(keyPair.pubKeyObj) as unknown as string)

      return this.setKeys(keyPair as { prvKeyObj: RSAKey; pubKeyObj: RSAKey })
    },

    async restoreKeyPairFromSession(): Promise<void> {
      const serializedPrivateKey = retrieveAndCleanupItem(this.sessionStorageSettings.PRIVATE_KEY) as string
      const serializedPublicKey = retrieveAndCleanupItem(this.sessionStorageSettings.PUBLIC_KEY) as string
      const keyPair: { prvKeyObj: RSAKey; pubKeyObj: RSAKey } = {
        prvKeyObj: KEYUTIL.getKey(serializedPrivateKey) as RSAKey,
        pubKeyObj: KEYUTIL.getKey(serializedPublicKey) as RSAKey
      }
      await this.setKeys(keyPair)
    },

    async setKeys(keyPair: { prvKeyObj: RSAKey; pubKeyObj: RSAKey }): Promise<void> {
      this.keyPair = keyPair
      this.publicKeyInJWKFormat = window.btoa(JSON.stringify(KEYUTIL.getJWKFromKey(keyPair.pubKeyObj)))
    },

    async getSignedIdToken(): Promise<string> {
      const algorithm = this.DEFAULT_ID_TOKEN_SIGNING_ALGORITHM
      const now = new Date().getTime()
      const expiration = now + 1000 * 60
      const body = {
        iss: this.oidClientSettings.client_id,
        sub: this.oidClientSettings.client_id,
        aud: this.euLoginAccessTokenUrl,
        jti: KJUR.crypto.Util.getRandomHexOfNbytes(32),
        exp: expiration,
        iat: now,
        id_token: this.idToken
      }
      return KJUR.jws.JWS.sign(algorithm, { alg: algorithm }, JSON.stringify(body), this.keyPair.prvKeyObj)
    },

    signAccessToken(accessToken: string): string {
      const algorithm = this.DEFAULT_ACCESS_TOKEN_SIGNING_ALGORITHM
      const body = {
        at: accessToken,
        ts: new Date().getTime()
      }

      return KJUR.jws.JWS.sign(algorithm, { alg: algorithm }, JSON.stringify(body), this.keyPair.prvKeyObj)
    },

    deleteKeyPair() {
      this.keyPair = {} as { prvKeyObj: RSAKey; pubKeyObj: RSAKey }
    }
  }
})

export class OpenIdUiDevelopmentSettings {
  allowDevelopmentLogin: boolean
  delegateLogin: boolean
  loginDelegateUrl: string
}

export default useOpenIdStore
