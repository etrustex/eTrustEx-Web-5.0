#!/bin/bash

# if datasource is not created
count=`ls -1 $DOMAIN_HOME/config/jdbc/*.xml 2>/dev/null | wc -l`
if [ $count != 0 ]
then
  echo "Domain already imported"
else
  echo "Waiting for log file before creating custom domain "

  while [ ! -f $DOMAIN_HOME/servers/${CUSTOM_ADMIN_NAME}/logs/${CUSTOM_ADMIN_NAME}.log ]; do sleep 5; done

  echo "Done Waiting for log file"

  echo "Wait for AdminServer to finish before importing domain"
  ( tail -f -n +1 $DOMAIN_HOME/servers/${CUSTOM_ADMIN_NAME}/logs/${CUSTOM_ADMIN_NAME}.log & ) | grep -q "deployment state ADMIN-TO-PRODUCTION"

  echo "Done waiting for deployment state ADMIN-TO-PRODUCTION"

  echo "importing domain with wlstapi"
  /u01/oracle/wlstapi-1.9.1/bin/wlstapi.sh /u01/oracle/wlstapi-1.9.1/scripts/import.py --property /u01/oracle/wlstapi-1.9.1/LocalEnvSetup.properties

  echo "Copy eulogin etrustex-admin-web.properties"
  cp /u01/eulogin/admin-console-properties/ecas-config-etrustex-admin-web.properties "${DOMAIN_HOME}"/

  echo "Adding Node specific options to ${DOMAIN_HOME}/bin/setDomainEnv.sh"
  echo 'JAVA_OPTIONS="${JAVA_OPTIONS} -DANTLR_USE_DIRECT_CLASS_LOADING=true"' >> ${DOMAIN_HOME}/bin/setDomainEnv.sh
  echo 'export JAVA_OPTIONS' >> "$DOMAIN_HOME"/bin/setDomainEnv.sh
  cp -a /u01/oracle/etx_config "$DOMAIN_HOME"/
  mkdir $DOMAIN_HOME/etrFileStore

   echo "Copy eulogin security.properties & client jar"
   mkdir -p "${WL_HOME}"/server/lib/consoleapp/webapp/WEB-INF/classes
   cp /u01/eulogin/properties/security.properties "${WL_HOME}"/server/lib/consoleapp/webapp/WEB-INF/classes/
   cp /u01/eulogin/client/*.jar "${DOMAIN_HOME}"/lib/

   echo "Add Ecas Authentication provider to config.xml. It cannot be created with wlst because it is not Weblogic delivered"
   # https://github.com/oracle/weblogic-deploy-tooling/issues/164

   # Ecas xsd
   sed -i 's|http://xmlns.oracle.com/weblogic/1.0/security.xsd|http://xmlns.oracle.com/weblogic/1.0/security.xsd https://www.cc.cec/cas/schemas|g' "$DOMAIN_HOME"/config/config.xml

   # Provider
   ECAS_PROVIDER="      <sec:authentication-provider xmlns:sch=\"https://www.cc.cec/cas/schemas\" xsi:type=\"sch:ecas-identity-asserter-v2Type\">\n        <sec:name>EcasIdentityAsserterV2</sec:name>\n        <sch:control-flag>OPTIONAL</sch:control-flag>\n        <sch:ecas-base-url>https://ecas.ec.europa.eu</sch:ecas-base-url>\n        <sch:excluded-context-path>/console</sch:excluded-context-path>\n        <sch:excluded-context-path>/etrustex</sch:excluded-context-path>\n      </sec:authentication-provider>"
   C=$(echo $ECAS_PROVIDER | sed 's/\//\\\//g')
   sed -i "/<sec:role-mapper/ s/.*/${C}\n&/" "${DOMAIN_HOME}"/config/config.xml

   echo "done creating custom domain"
   echo "############### IMPORTANT! ##############################################################################"
   echo "##### You need to restart container in order to activate Ecas Authentication provider ###################"
   echo "#########################################################################################################"
fi

