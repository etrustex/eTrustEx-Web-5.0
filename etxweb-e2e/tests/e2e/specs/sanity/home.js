// Using BDD describe: https://nightwatchjs.org/guide/using-nightwatch/using-bdd-describe.html
describe('Home page', () => {
  /*
   * we can add tags to test modules. Eg.
   *  $ nightwatch -a sanity -a login
   * or skip tags with
   *  $ nightwatch --skiptags login,something_else
   */
  this.tags = ['login', 'sanity']

  before(browser => {
    // console.log(browser.options)
    browser.page.euLoginPage().login()
  })

  test('Test Inbox is loaded', (browser) => {
    const inboxPage = browser.page.inboxPage()

    inboxPage.waitForElementVisible('@inboxLink')
      .api.pause(1000)
      .saveScreenshot('./tests_output/screenshots/' + browser.options.desiredCapabilities.browserName  + '_inbox.png')
  })

  after(browser => browser.end())
})
