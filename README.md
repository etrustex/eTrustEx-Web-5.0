# Prerequisites
## Gradle 
Check the developers howto [Configure Gradle and add EC maven repositories](https://citnet.tech.ec.europa.eu/CITnet/confluence/x/EKCZMg). <br/>
Then go to Setting in IntelliJ => "Build, Execution, Deployment" => Build tools => Gradle => and Select "Use default gradle wrapper (recommended). <br/>
Also, uncheck the "offline mode" in the settings if it's not the case.

## npm
Check the developers howto [Configure npm](https://citnet.tech.ec.europa.eu/CITnet/confluence/x/PqCZMg)
### For Linux
Recommended to use [nvm](https://github.com/nvm-sh/nvm)


## Vue.js & vue CLI
   ```npm install -g @vue/cli```
    

## IntelliJ settings and code conventions
### Tslinting
Languages & Frameworks > TypeScript > TSLint and select Automatic TSLint configuration
![Automatic TSLint configuration](doc/screenshots/IJ_tslint_conf.png)

### Shares code style configurations
1. Navigate to Settings > Editor > Code Style
2. Ensure the schema is set to "Project"
![IJ Share Code Style Settings](doc/screenshots/IJ_Sync_Code_Style_Settings.png)

### Lombok 
- Make sure you have the Lombok plugin for IntelliJ installed
- Preferences (Ctrl + Alt + S) > Build, Execution, Deployment > Compiler > Annotation Processors > Enable annotation processing

### [SDK configuration](https://www.jetbrains.com/help/idea/2016.3/configuring-intellij-platform-plugin-sdk.html)

----------
   
# Build
## Prerequisites

## Production build
  ```
    gradle buildAll
  ```
The artifact is generated in etxweb-rest/build/libs/etrustex#web.war

## Development build
  ```
    gradle buildAll -PtargetEnv=local
  ```
  Where `targetEnv` parameter value matches the name of the folder under [environment folder](https://citnet.tech.ec.europa.eu/CITnet/stash/projects/ETRUSTEX/repos/etrustex-web/browse/environment) corresponding to the target environment
  
  That is, if you want to build for an artifact to be deployed in docker tomcat and mysql containers, you will build with:
  ```
    gradle buildAll -PtargetEnv=docker-tomcat-mysql
  ```
   
  
----------

# Development
## Note on environment specific properties
  - Environment specific properties are placed in ```environment/${environment}/```
  - Properties shared across all environments in ```/src/main/resources/application.properties```

## Add `openid.properties` file to local environment

and place it in `environment/docker-weblogic-oracle/openid.properties`


## Run with docker-compose


### Configure development login
Update OpenId dev settings in application.yaml
Set client_id and loginDelegateUrl

First generate the artifact to deploy.
E.g. if you want to build for an artifact to be deployed in docker Weblogic and Oracle containers, you will build with:
  ```
    gradle buildAll -PtargetEnv=docker-weblogic-oracle
  ```
Then follow [Docker README](Docker/weblogic-oracle/README.md)



### Set Weblogic Hostname Verification to None
This is to avoid "failed hostname verification check. Certificate contained". 

Go to http://localhost:7001/console and Servers -> AdminServer -> Configuration SSL tab -> Advanced -> Change Hostname Verification dropdown to None

https://stackoverflow.com/questions/27550586/weblogic-12-sslkeyexception-hostname-verification-failed-after-restart


## Run web client app on another port for hot swapping and other goodies
In *src/vue* run:
  ```
    npm run dev
  ```

## IntelliJ Run/Debug configurations
### npm tasks to generate js assets and start local dev server
   1. Run > Edit Configurations... > + > npm
   2. package.json: 'your package.json' / Command: 'run' / Scripts: 'dev'  
     ![IJ create npm conf for local dev](doc/screenshots/run_conf_npm_run_dev.png)
   3. Before launch > add the gradle generateTypeScript task
   4. Repeat previous steps but with Scripts: 'serve' instead of dev
   5. You can also create Javascript debug configuration  
     ![IJ create bootWar conf for local dev](doc/screenshots/run_conf_JS_debug.png)

### gradle task to generate war
  1. Run > Edit Configurations... > + > Gradle > task: bootWar / Arguments: -PtargetEnv=local
  2. Server tab > Before launch > add the 'npm run dev' task   
    ![IJ create bootWar conf for local dev](doc/screenshots/run_conf_bootWar_dev.png)
    

### [IJ docker-compose run/debug configuration](https://citnet.tech.ec.europa.eu/CITnet/confluence/x/0zJFNg) 



----------

# Tests
## Unit tests

### Javascript
Unit testing for Typescript and VUE components is setup in this project with Jest.

Tests can be run once using the task test:uni or kept in background and re-executed at each update in the sources using test:watch.

Some UI tests use snapshots to verify that UI rendering does not change with respect to previous executions ([see jest snapshot-testing](https://jestjs.io/docs/en/snapshot-testing)). Snapshots can be updated by running task
test:updateSnapshots.

The tasks are defined in the package.json file and are reported below. 

```
"test:unit": "vue-cli-service test:unit --runInBand",
"test:updateSnapshots": "vue-cli-service test:unit --runInBand -u",
"test:watch": "vue-cli-service test:unit --watch",
```

Below there is an example on how to configure the javascript unit test tasks in intellij
![Typescript unit testing](doc/screenshots/ts_tests_ij_conf.png)

When executed, the run configuration opens a Chrome browser that will be used to execute the unit tests defined in vue/tests/unit/*.ts files.
Tests are executed automatically when files are updated.

----------

## Show Coverage data
### Add coverage to Gradle test configuration
![IJ_Run_configuration_coverage](doc/screenshots/IJ_Run_configuration_coverage.png)

You can add/exclude classess, packages, etc. For example if you want to check just what you implemented.

### Add coverage to Jest test configuration
![IJ_Run_configuration_coverage_jest](doc/screenshots/IJ_Run_configuration_coverage_jest.png)

You can add `--watch` to Jest Options if you want test to run for a test after every change

### Run tests with coverage
![IJ_run_with_coverage](doc/screenshots/IJ_run_with_coverage.png)

After tests are run you can see the Coverage results
![IJ_Coverage_result](doc/screenshots/IJ_Coverage_result.png)

### Show test coverage data
![IJ_show_code_coverage](doc/screenshots/IJ_show_code_coverage.png)
![IJ_choose_coverage_suite](doc/screenshots/IJ_choose_coverage_suite.png)

----------


# Javadoc
*javadoc* task creates the combined javadoc for all subprojects 

The javadoc is copied in the resources folder in the etxweb-rest project. Configured as shown here ![javadoc generation](doc/screenshots/global_javadoc.png)

----------
# Sonarqube and dependencyCheck
## dependencyCheck
In order to run sonarQube locally, we should run first the dependencyCheckAggregate task. This task may need to use the 
proxy settings if executed from within the EC. If this is the case, add the parameter `-P useProxy=1'.
![dependencyCheckAggregate](doc/screenshots/run_dendendencyCheckAggregate.png)

False positives can be added to dependencyCheck-suppresions.xml file. See http://jeremylong.github.io/DependencyCheck/general/suppression.html

## SonarQube gradle task
The sonarqube task uses the grade.properties file to get some parameters
```
systemProp.sonar.host.url=<SONARQUBE HOST>
systemProp.sonar.projectKey=ETRUSTEX-WEB
systemProp.sonar.login=<user>
systemProp.sonar.password=<pwd>
systemProp.sonar.branch.name=<local_username or similar>
systemProp.sonar.projectVersion=local
```
Then run sonarqube task with the following configuration
![sonarqube](doc/screenshots/run_sonarQube.png)

the local branch will then appear in the sonarqube website with the name used for property
systemProp.sonar.branch.name
![localsonarquberun](doc/screenshots/local_sonarqube_run_result.png)

## SonarQube IJ configuration
1. Install SonarLint plugin
2. Add SonarQube connection
    - Settings > Tools > SonarLint > SonarQube / SonarCloud connecitons > +
    ![newSonarConn](doc/screenshots/IJ_SonarLint_new_connection.png)
    - Add connection name (E.g. DIGIT)
    - Select SonarQube,  Next
    ![newSonarConn1](doc/screenshots/IJ_SonarLint_new_connection1.png)
    - Authentication type: Login / Password and use those from your gradle.properties, Next
    - Tick "Receive notifications from SonarQube, Next, Finish
3. Bind project
    - Settings > Tools > SonarLint > Project Settings
    - Tick "Bind project to SonarQube"
    - Select Connection (E.g. DIGIT)
    - Input project key (E.g. ETRUSTEX-WEB)
      ![newSonarConn2](doc/screenshots/IJ_SonarLint_bind_project.png)

4. Run

    There is a SonarLint tab at the bottom of the IDE ![newSonarConn3](doc/screenshots/IJ_SonarLint_run.png)

    Analyze the whole project ![sonarLintRun](doc/screenshots/IJ_SonarLint_run_analyze.png)

5. Pre commit hook
    In the commit window, click on the settings icon and select "Perform Sonarlint Analsysis"
    ![sonarLintPreCommmit](doc/screenshots/IJ_SonarLint_pre_commit.png)

    Result
   ![sonarLintPreCommmitResult](doc/screenshots/IJ_SonarLint_pre_commit_result.png)

# Troubleshooting
## Gradle build times
  If gradle build taks too long the ```--profile``` parameter can be used. Eg.
  ```
    gradle --profile build -x test
  ```
----------


### IntelliJ run configuration for Tomcat
   1. Run > Edit Configurations... > + > Tomcat server > Local
   2. Server tab > Before launch > add the gradle bootWar task
   3. Deployment tab > + > Artifact > "Gradle:eu.europa.ec:etrustex#war"  
      ![IJ create bootWar conf for local dev](doc/screenshots/run_conf_tomcat_1.png)
   4. Deployment tab > Application context: "**/etrustex/web**"  
      ![IJ create bootWar conf for local dev](doc/screenshots/run_conf_tomcat_2.png)
 
 

## [Vue.js](https://citnet.tech.ec.europa.eu/CITnet/confluence/x/RDz4Mg)
  
### [vue-devtols](https://github.com/vuejs/vue-devtools/blob/master/shells/electron/README.md)
  To run vue-devtools, at the prompt:
  ```./node_modules/.bin/vue-devtools```
  
## Typescript code generation
  We use [typescript-generator](https://github.com/vojtechhabarta/typescript-generator) to generate typescript definitions for entity java clases running the following gradle task:
  ```
    gradle generateTypeScript
  ``` 
  This command will gerete the ts definitions in ```src/vue/src/model/entities.d.ts```
  
  Please, note that we git-ignore the generated file to to avoid constant commits of this file generated on each build
  
## HATEOAS
  The REST API uses HATEOAS links. To see how these are implemented see
  - java side: javadoc starting from class RootLinksController
  - javascript side: see jsdoc in linksHandler.ts (usage examples in, e.g., attachmentApi.ts) 


## DB generation and source control
  DB can be generated using ```spring.jpa.hibernate.ddl-auto=create``` in our [application.properties](environment/local/etrustex.web.properties)
   
  We use [liquibase](https://www.liquibase.org/) as source control for our database.
  
  As a starting point, we can generate liquibase changelog ddl from DB using [liquibase-gradle-plugin](https://github.com/liquibase/liquibase-gradle-plugin) ```generateChangelog``` task. Eg.
  ```cmd
    gradle generateChangelog
  ```
   This task by default is configured for mysql dbms.
   
   It generates a new [```mysql/db.changelog.yaml```](src/main/resources/db/changelog/mysql/db.changelog.yaml) as ```mysql/db.changelog.${yyyyMMddHHmmss}.yaml``` 
   
   In order to run it for Oracle: 
   1. CHANGE LIQUIBASE PROPERTIES for generateChangelog task
           localDBurl = 'oracle DB url'
           localDBusername = '...'
           localDBpassword = '...' 
           
   2. ```gradle generateChangelog -Poracle```
   
   This task is configured [```in build.gradle``](build.gradle) with the following default parameter-values:
   - ```localDBurl = 'jdbc:mysql://localhost/etrustex_web?createDatabaseIfNotExist=true&useLegacyDatetimeCode=false&serverTimezone=UTC'```
   - ```localDBusername = 'root'```
   - ```localDBpassword = 'mysql'```
   
 ### IMPORTANT NOTES
   - **__The generated changelog can be used as a starting point__**. Afterwards, the changelog should be updated manually
   -  It does not export the following types of objects:
        - Stored procedures, functions, packages
        - Triggers

  More info in http://www.liquibase.org/documentation/generating_changelogs.html

## List dependencies licenses
### Gradle dependencies
Run ```./gradlew generateLicenseReport```

Reports will be generated in ```<PROJECT-ROOT>build/reports/dependency-license```
https://github.com/jk1/Gradle-License-Report

### nodejs dependencies
1. Install license-checker
```npm i -g license-checker```
2. In etxweb-vueApp dir, run ```license-checker --summary > licenses-summary.txt```
