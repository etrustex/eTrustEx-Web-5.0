"use strict"

const fs = require("fs")
const JSZip = require("jszip")
const path = require('path')

describe('New message', () => {
  before(browser => {
    this.senderGroupId = 'TEST_PARTY'
    this.subject = 'Test Subject'
    this.messageText = 'Lorem ipsum...'
    this.filePath = path.resolve(__dirname + '/../test_files/JPG1.jpg')
    this.inboxPage = browser.page.inboxPage()
    this.newMessagePage = browser.page.newMessagePage()

    browser.page.euLoginPage().login()
    this.inboxPage.waitForElementVisible('@newMessageButton', 10000)
    browser.waitForElementNotPresent('.modal')
    this.inboxPage.click('@newMessageButton')
  })

  beforeEach(browser => {
    browser.pause(1000) // Otherwise, don't know why, input fields are cleared.
  })

  test('Display error if trying to send with no attachments', (browser) => {
    this.newMessagePage
      .selectGroup(this.senderGroupId)
      .setSubject(this.subject)
      .setMessageText(this.messageText)
      .click('@sendButton')
      .expect.element('@atLeastOneAttachmentError').to.be.visible
  })

  test('Send message', (browser) => {
    this.newMessagePage
      .selectGroup(this.senderGroupId)
      .setSubject(this.subject)
      .setMessageText(this.messageText)
      .addFile(this.filePath)
      .click('@uploadButton')
      .waitForElementVisible('@uploadingDialogTitle')
      .waitForElementNotPresent('@uploadingDialogTitle', 10000)
      .click('@sendButton')

    this.inboxPage
      .waitForElementPresent('@newMessageButton', 10000)
      .selectLastdMessageSummary()
  })

  test('Message is displayed in Inbox', (browser) => {
    this.inboxPage.verify.containsText('@lastMessageSummarySubject', this.subject)
    this.inboxPage.verify.containsText('@messageDetailsSubject', this.subject)
    this.inboxPage.verify.containsText('@messageDetailsMessage', this.messageText)

    browser.elements('css selector','#attachmentsTable tbody tr', (result) => {
      browser.verify.equal(result.value.length, 1)
    })
  })

  test('All attachments download', (browser) => {
    this.inboxPage.waitForElementVisible('@selectAllCheckbox')
      .click('@selectAllCheckbox')
      .click('@downloadButton')
      .waitForElementVisible('@downloadingDialogTitle')
      .waitForElementNotPresent('@downloadingDialogTitle', 10000)
  })

  test('Attachment is downloaded P Chrome downloads folder', (browser) => {
    if (browser.options.desiredCapabilities.browserName === 'chrome') {
      const downloadPath = path.resolve(__dirname + '/../../../../tests_output/downloads/')
      fs.readdir(downloadPath, (err, files) => {
        files.forEach(file => {
          fs.readFile(downloadPath + '/' + file, (err, data) => {
            if (err) throw err

            JSZip.loadAsync(data).then((zip) => {
              let count = 0
              zip.forEach((relativePath, zipEntry) => {
                console.log(zipEntry.name)
                ++count
              })
              browser.verify.equal(count, 1)
            })
          })
        })
      })
    }
  })

  after(browser => {
    browser.end()
  })
})
