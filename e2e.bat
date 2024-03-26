cd .\etxweb-e2e

:: del /s /f /q .\tests_output\*.*
:: for /f %%f in ('dir /ad /b .\tests_output\') do rd /s /q .\tests_output\%%f
:: ECHO Deleted tests_output dir

:: call npm config list

call npm i

:: call npm run test:e2e
call nightwatch -e selenium.chrome --reporter html-reporter.js --suiteRetries 2
call nightwatch -e selenium.firefox --reporter html-reporter.js --suiteRetries 2
call nightwatch -e selenium.edge --reporter html-reporter.js --suiteRetries 2

cd ..
