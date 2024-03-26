# Create weblogic domains for Node & Web

**Note for Windows running Docker with Toolbox. Replace 'localhost' with '192.168.99.100'**

## Before you start
Ensure that you don't have dangling containers, images or volumes that may conflict with this docker compose configuration
Once you remove unneeded containers and images run
```
    docker system prune
    docker volume prune
```

### For Windows machines 
Go to Resources ->File sharing configuration in your Docker for Windows app and add Docker\weblogic-oracle\oracle and then restart.

### For Linux machines
Make oradata folder writable
``` 
chmod 777 Docker/weblogic-oracle/oracle/oradata/
```

### Ensure that you can login to our docker repository
Either from IJ (File | Settings | Build, Execution, Deployment | Docker | Docker Registry)

Or from terminal
```sh
docker login -u etrustex -p <PASSWORD> docker.io 
```

### Test email settings
Create a file if does not exist named `local.env` at the same level of `docker-compose.yml` with the following contents:
    ```
    MTP_USER=<test-email-user>
    MTP_PASS=<test-email-password>
    ```
    email user and password can be found in keepass etrustex/Mail/gmail 

## 1 Start docker compose
Right click on `Docker/weblogic-oracle/docker-compose.yml` and click on `Run`

Alternatively, if you want to run it from command line E.g. ``` docker-compose -f docker-compose.yml up -d```


## 2 Restart Weblogic (Important)
First time docker compose is run, Weblogic container (etx-all-weblogic) needs to be restarted to activate ECAS Authentication Provider. 

## 3 Deploy
Drop the artifact/s that you want to deploy in 'base_domain/autodeploy' dir

First time the artifact is placed in autodeploy folder, the server (or container) may need to be restarted. The container log will tell you. 
 
## That's it!


## Building the artifacts
### Node ear 
`mvn clean install -Dmaven.test.skip=true -P LOCAL-WEBLO-DOCKER,oracle,weblogic,openetrustex`)

### Web 3.x ear
`mvn clean package -P webpack,wl-docker`

### Web 4.x war
`./gradlew clean buildAll -PtargetEnv=docker-weblogic-oracle`


## URLs
### Weblogic Admin console 
http://localhost:7001/console (weblogic/weblogic01)

### ETX Node Web Application 
http://localhost:7001/etrustex

### ETX Node  admin web 
http://localhost:7001/etrustex-admin-web

### ETX 3.x Web 
http://localhost:7001/e-trustex

### ETX 4.x Web
https://localhost:7003/


## Database
url: jdbc:oracle:thin:@//localhost:1521/ETX
Service name: ETX

### Node schema
username: ETRUSTEX
password: ETXWDEV1
Service name: ETX

### Web 3.x schema
username: ETXWEB

password: ETXWDEV1

Service name: ETX

### Web 4.x schema
username: ETXWEB4

password: ETXWDEV1

Service name: ETX

### System
username: Sys

password: Oradoc_db1

SID: ORCLCDB

# Further notes
## First time docker-compose is run
- 2 containers will be created and started;
    - Oracle DB with 3 schemas (Node, Old Web& New Web)
    - Weblogic AS with domain configured (Datasources, JMS queues, Mail server & ECAS) in which the 3 components can be deployed

- 1 image will be created (etx-all-weblogic:latest)

- Weblogic domain will be mounted as volume in Docker/weblogic-oracle/weblogic/base_domain

Keep that in mind if you want to start from scratch or modify Dockerfile, etc. **You will have to delete all of the above**.

## Adding/modifying test data
If you want more test data, such as parties and ICAs, available every time you deploy, instead of adding them manually you can update the relevant liquibase changelog.

## If you want to see the logging of the Ecas Identity Asserter V2 on the standard output, turn on WebLogic logging using WebLogic Administration Console.
In weblogic admin console, navigate to “Environment” > “Servers” > “Admin Server” > “Logging” >  “General” > “Advanced” 
and check the box “Redirect sdtout logging enabled” and set the “Severity level” either on "Debug", "Info" or "Notice".

Then Save and restart

# Prevent Oracle password expiration (or in case 'ORA-28001: the password has expired')
Access DB with SqlDeveloper with System user 

    username: Sys
    
    password: Oradoc_db1
    
    SID: ORCLCDB

and run in editor
```sql
ALTER SESSION SET CONTAINER=ETX;

alter profile "DEFAULT" limit password_life_time unlimited;

alter user ETXWEB identified by ETXWDEV1 account unlock;
alter user ETXWEB4 identified by ETXWDEV1 account unlock;
alter user ETRUSTEX identified by ETXWDEV1 account unlock;
```


# Potential issues
## 60337: Node Manager location not writable
```
com.oracle.cie.domain.script.ScriptException: 60337: Node Manager location not writable.
60337: The Node Manager location does not have write permission.
60337: Correct permissions or select different domain location.
```
Fix:
```
$ chmod a+w base_domain/
```


## java.rmi.ConnectException: No available router to destination
Check url/port in `etx-config.properties` for property `java.naming.provider.url`
As well as in `ETR_TB_METADATA` for `MD_TYPE=SERVER_URL`


## standard_init_linux.go:211: exec user process caused "no such file or directory"
If running docker on Windows, you need to change from CRLF to LF all the files under `weblogic/container-scripts/` both for node and web


## Couldn't find env file: ...\etrustex-web\Docker\weblogic-oracle\local.env
See [test email settings](#test-email-settings)

## "User not registered" message after logging in :

Possibilities:

1-Authentication Provider not properly configured because of a failure:
-Check that under "Docker/weblogic-oracle/weblogic/eulogin/client" you put the latest authentication provider 
at the time of writing is: eulogin-weblogic-12-authprovider-9.8.6-jdk8.jar
-Check that the authentication provider is correctly configured by:
Logging in to: http://localhost:7001/console (instructions earlier in the readme)
Making sure to have the entry: Security Realms -> myrealm -> providers -> EcasIdentityAsserterV2

2-Under windows Docker Desktop, the containers may not have access to internet, you can verify it by checking the 'network' tab in your browser dev tools after logging in:

![networkTab.png](doc/screenshots/networkTab.png)

There should be a Status Code: 307 Temporary Redirect in the header while validating the ticket.
This situation can occur if you have several network adapters configured in your system (Ususally needed if you use virtualization)
You could try to provide connection to the containers through a bridge.  A more drastic solution would be to to uninstall HyperV and install it again in order to have just one vEthernet (WSL) adapter, plus the one you are using to connect to internet (Ethernet or Wifi).

## Compilation problem during the build because of a missing class

Because of some occasional indexing issues, the node WSDL classes used for binding are not generated because Graddle thinks they already exist and skips the tasks.
In order to force the regeneration of your wsdl2java you can add the following to the graddle build in order to force the re-generation and remove it later on:

```
buildAll wsdl2javaApplicationResponse --rerun-tasks
```

