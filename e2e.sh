#! /bin/sh

cd ./etxweb-e2e
rm -rf tests_output

nightwatch -e selenium.chrome --reporter html-reporter.js --suiteRetries 2 &
nightwatch -e selenium.firefox --reporter html-reporter.js --suiteRetries 2 &
nightwatch -e selenium.edge --reporter html-reporter.js --suiteRetries 2 && fg

