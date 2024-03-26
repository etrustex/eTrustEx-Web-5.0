const commands = {
  login() {
    const username = this.api.globals.euLoginUsername
    const password = this.api.globals.euLoginPassword
    const { launchUrl } = this.api

    this.api.url(launchUrl)
    this.api.pause(1000)
    this.waitForElementVisible('@usernameInput', 10000)
      .setValue('@usernameInput', username)
      .click('@userSubmitButton')
      .api.pause(1000)

    this.waitForElementVisible('@passwordInput', 10000)
      .setValue('@passwordInput', password)
      .click('@passwordSubmitButton')
      .api.pause(1000)

    this.api.assert.titleContains('eTrustEx')

    return this // Return page object for chaining
  }
}

module.exports = {
  commands: [commands],
  elements: {
    usernameInput: { selector: 'input[id=username]' },
    userSubmitButton: { selector: 'input[name="whoamiSubmit"]' },
    loginForm: { selector: 'input[id=loginForm]' },
    passwordInput: { selector: 'input[id=password]' },
    passwordSubmitButton: { selector: 'input[name="_submit"]' }
  }
}
