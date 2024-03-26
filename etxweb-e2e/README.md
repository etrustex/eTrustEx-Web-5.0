# e2e tests with nightwatch

## Conventions
- Developers can run e2e tests on their machines when needed.
- The one reviewing Virginie's PR will take care of updating the tests.
- From the etx-automation VM tests will run on TEST environment every night.
- We will check if it is possible to trigger the Jenkins build from bamboo after a new deployment in TEST.

## Running the tests
### From command line
```
TARGET_ENV=acc EULOGIN_USR=eulogin EULOGIN_PWD=password nightwatch -e selenium.firefox --reporter html-reporter.js --suiteRetries 2
```
Where
`TARGET_ENV` can have any of the following values;
- `test` (default) will point to 'https://test.acceptance.ec.europa.eu/etrustex/web'
- `acc`  will point to 'https://webgate.acceptance.ec.europa.eu/etrustex/web'
- `dev` will point to 'https://localhost:7003/etrustex/web'
- `4200` will point to 'https://localhost:4200/etrustex/web'

For a complete list of command line options (environments, groups, tags, etc.) check https://nightwatchjs.org/guide/running-tests/command-line-options.html

### As npm script
```
npm run test:e2e
```
Verify that `test:e2e` script in `etxweb-e2e/package.json` suits your needs

### Headless or displaying browsers?
Drivers must run in headless mode in order to run on etx-automation VM when the user is not logged in.
If you want to see the browser during the automated tests, you can comment the `args : ["headless"]` line 
of the corresponding browser in `nightwatch.conf.js` file

## Writing tests
We use [BDD describe](https://nightwatchjs.org/guide/using-nightwatch/using-bdd-describe.html)
and [Page Objects](https://nightwatchjs.org/guide/working-with-page-objects/)

## Important
- Test suites can be run in parallel for one browser at a time because multiple nightwatch processes produce unexpected results
- If Edge is automatically updated, the `etxweb-e2e/msedgedriver.exe` file may need to be updated https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
- Same for Firefox and `etxweb-e2e/geckodriver.exe`

I didn't maange to configure geckodriver to not use system proxy settings. 
For the moment, ***WE NEED TO DISABLE SYSTEM PROXY SETTINGS*** on the VM that runs e2e tests


## Jenkins
Jenkins can be started from a shell with
```
 java -jar C:\Program Files\Jenkins\Jenkins.war
 ```
