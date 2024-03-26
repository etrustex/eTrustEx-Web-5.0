const commands = {
  selectGroup(groupId) {
    this.setValue('@recipientGroupSelect', groupId)
    return this
  },

  setSubject(value) {
    this.sendKeys('@subjectInput', value)
    return this
  },

  setMessageText(value) {
    this.sendKeys('@textInput', value)
    return this
  },

  addFile(path) {
    this.sendKeys('@fileInput', path)
    return this
  }
}

module.exports = {
  commands: [commands],
  elements: {
    recipientGroupSelect: { selector: '#recipientGroup' },
    subjectInput: {
      selector: '//input[@id="subject"]',
      locateStrategy: 'xpath'
    },
    textInput: { selector: '#text' },
    fileInput: { selector: '#file-upload' },
    browseButton: {
      selector: '//span[contains(@class, "file-uploads")]',
      locateStrategy: 'xpath'
    },
    uploadButton: {
      selector: '//button[text()=" Upload "]',
      locateStrategy: 'xpath'
    },
    uploadingDialogTitle: {
      selector: '//h1[text()="Uploading..."]',
      locateStrategy: 'xpath'
    },
    sendButton: {
      selector: '//button[text()="Send"]',
      locateStrategy: 'xpath'
    },
    atLeastOneAttachmentError: {
      selector: '//span[contains(@class, "error") and text()="It is required to add at least one attachment."]',
      locateStrategy: 'xpath'
    }
  }
}
