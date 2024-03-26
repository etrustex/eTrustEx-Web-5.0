let launchUrl
switch (process.env.TARGET_ENV) {
  case 'test':
    launchUrl = 'https://webgate.test.ec.europa.eu/etrustex/web'
    break
  case 'acc':
    launchUrl = 'https://webgate.acceptance.ec.europa.eu/etrustex/web'
    break
  case 'dev':
    launchUrl = 'https://localhost:7003/etrustex/web'
    break
  case '4200':
    launchUrl = 'https://localhost:4200/etrustex/web'
    break
  default:
    launchUrl = 'https://webgate.test.ec.europa.eu/etrustex/web'
}

module.exports = {
  src_folders: ['tests/e2e/specs'],
  output_folder: 'tests_output/reports',
  page_objects_path: ['tests/e2e/pages'],
  screenshots: {
    enabled: true,
    path: './tests_output/screenshots',
    on_failure: true,
    on_error: true
  },

  test_workers: {
    enabled: true,
    workers: 'auto'
  },

  test_settings: {
  // Reference https://www.w3.org/TR/webdriver/
    default: {
      selenium_port: 4444,
      selenium_host: 'localhost',
      // silent: true,
      launch_url: launchUrl,
      globals: {
        euLoginUsername: process.env.EULOGIN_USR,
        euLoginPassword: process.env.EULOGIN_PWD
      }
    },

    selenium: {
      // Selenium Server is running locally and is managed by Nightwatch
      selenium: {
        start_process: true,
        port: 4444,
        host: '127.0.0.1',
        server_path: require('selenium-server').path,
        cli_args: {
          'webdriver.gecko.driver': process.platform === 'win32' ?  './geckodriver.exe' : require('geckodriver').path,
          'webdriver.chrome.driver':  require('chromedriver').path,
          'webdriver.edge.driver': './msedgedriver.exe'
        }
      }
    },

    'selenium.chrome': {
      extends: 'selenium',
      // proxy settings are commented so that it works without proxy settings.
      // Anyway we still need to disable Windows proxy settings on remote VM, so no need for them for the moment
      // proxy: {
      //   host:'158.169.131.13',
      //   port:8012,
      //   protocol: 'https',
      //   noProxy: ['localhost', '127.0.0.1','*.cec.eu.int','webgate.*']
      // },
      desiredCapabilities: {
        browserName: 'chrome',
        acceptSslCerts: true,
        acceptInsecureCerts: true,
        chromeOptions : {
          w3c: false,
         args : ["headless"],
          prefs: {
            download: {
              prompt_for_download: false,
              default_directory: require('path').resolve(__dirname + '/tests_output/downloads/')
            }
          }
        }
      }
    },

    'selenium.firefox': {
      extends: 'selenium',
      desiredCapabilities: {
        browserName: 'firefox',
        javascriptEnabled: true,
        acceptSslCerts: true,
        acceptInsecureCerts: true,
        'moz:firefoxOptions': {
          args: [
            '-headless',
            // '-verbose'
          ]
        }
      }
    },

    'selenium.edge': {
      extends: 'selenium',
      desiredCapabilities: {
        browserName: 'MicrosoftEdge',
        proxy: {
          proxyType: 'manual',
          httpProxy: '158.169.131.13:8012',
          noProxy: ['localhost', '127.0.0.1','*.cec.eu.int','webgate.*']
        },
        avoidProxy: true,
        javascriptEnabled: true,
        acceptSslCerts: true,
        acceptInsecureCerts: true,
        edgeOptions:{
          w3c: false,
          args : ["headless"]
        }
      }
    }
  }
}
