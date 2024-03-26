const commands = {
  selectLastdMessageSummary() {
    this.click('@lastMessageSummaryLink')
    return this
  }
}

module.exports = {
  commands: [commands],
  elements: {
    newMessageButton: '#newMessageButton',
    downloadButton: {
      selector: '//button[text()="Download"]',
      locateStrategy: 'xpath'
    },
    inboxLink: { selector: 'a[href="#/inbox"]' },
    lastMessageSummarySubject: {
      selector: '//span[contains(@class, "message-summary-subject")]',
      locateStrategy: 'xpath',
      index: 0
    },
    downloadingDialogTitle: {
      selector: '//h1[text()="Downloading..."]',
      locateStrategy: 'xpath'
    },
    lastMessageSummaryLink: {
      selector: '//a[contains(@class, "unread-message") or contains(@class, "read-message")]',
      locateStrategy: 'xpath',
      index: 0
    },
    messageDetailsSubject: '.message-detail-header > div > h2',
    messageDetailsMessage: {
      selector: '//*[contains(@class, "message-detail-message")]',
      locateStrategy: 'xpath'
    },
    messageDetailsHeader: '.message-detail-header',
    selectAllCheckbox: '.custom-checkbox'
  }
}
