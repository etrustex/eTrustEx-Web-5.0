#!/bin/bash
PRG="$0"
PRGDIR=`dirname $0`

# Set up WL_HOME, the root directory of your WebLogic installation
if [ -z "${WL_HOME}" ] ; then
  echo "The WL_HOME environment variable is not defined correctly"
  echo "This environment variable is needed to run this program"
  echo "NB: WL_HOME should point to a weblogic installation directory."
  exit 1
fi

# Check whether args are properly set
if [ -z "$1" ] ; then
  echo "ERROR ARG 1: Can't find python file $1"
  exit 1
fi

# Set up WSCRIPTS_HOME, the installation directory of the WLSTAPI package
WSCRIPTS_HOME=`cd "$PRGDIR/..";pwd`

# Added by gregoan
SCRIPTS_HOME=`dirname $1`

CACHE_DIR=${CACHE_DIR:=/tmp/${USER}/cachedir}

# ORACLE_HOME variable must point to oracle common dir (usually ${MW_HOME}/oracle_common)
function setOracleCommonEnv() {
    COMMON_WLST_HOME="${ORACLE_HOME}/common/wlst"
    MW_HOME=`cd "${ORACLE_HOME}/.." ; pwd`

    # Set the home directories...
    . "${ORACLE_HOME}/common/bin/setHomeDirs.sh"

    # Set the WLST extended env...  
    if [ -f "${COMMON_COMPONENTS_HOME}"/common/bin/setWlstEnv.sh ] ; then
      . "${COMMON_COMPONENTS_HOME}"/common/bin/setWlstEnv.sh
    fi
    COMMON_WLST_HOME="${COMMON_COMPONENTS_HOME}/common/wlst"
    WLST_HOME="${COMMON_WLST_HOME}:${WLST_HOME}"
    export WLST_HOME
    export WLST_PROPERTIES="-DORACLE_HOME=${ORACLE_HOME} -DCOMMON_COMPONENTS_HOME=${COMMON_COMPONENTS_HOME}"

    # Appending additional jar files to the CLASSPATH...
    if [ -d "${ORACLE_HOME}/lib" ] ; then
      for file in "${COMMON_WLST_HOME}"/lib/*.jar ; do
        CLASSPATH="${CLASSPATH}:${file}"
      done
    fi

    # Appending additional resource bundles to the CLASSPATH...
    if [ -d "${COMMON_WLST_HOME}/resources" ] ; then
      for file in "${COMMON_WLST_HOME}"/resources/*.jar ; do
        CLASSPATH="${CLASSPATH}:${file}"
      done 
    fi
}

# ORACLE_HOME variable must point to oracle SOA dir (usually ${MW_HOME}/Oracle_SOA1)
function setSOAEnv() {
    # Set the directory to get wlst commands from...  
    WLST_HOME="${ORACLE_HOME}/common/wlst"
    export WLST_HOME

    # Set MW_HOME because it's used in setHomeDirs.sh
    MW_HOME=`cd "${ORACLE_HOME}/.." ; pwd`

    # Set the home directories...
    . "${ORACLE_HOME}/common/bin/setHomeDirs.sh"

    # Set the WLST extended env...
    if [ -f "${ORACLE_HOME}/common/bin/setWlstEnv.sh" ] ; then 
      . "${ORACLE_HOME}/common/bin/setWlstEnv.sh"
    fi

    # Appending additional jar files to the CLASSPATH...
    if [ -d "${WLST_HOME}/lib" ] ; then
        for file in "${WLST_HOME}"/lib/*.jar ; do
           CLASSPATH="${CLASSPATH}:${file}"
        done
    fi

    # Appending additional resource bundles to the CLASSPATH...
    if [ -d "${WLST_HOME}/resources" ] ; then
        for file in "${WLST_HOME}"/resources/*.jar ; do
          CLASSPATH="${CLASSPATH}:${file}"
       done 
    fi  
    setOracleCommonEnv
}

# For SOA and COMMON homes, add additional jars to the classpath to enable the usage of specific SOA/COMMON
# wlst commands (such as purgeMetadata or applyJRF for instance)
case "$4" in
  COMMON) echo "Adding additional libraries to classpath for ORACLE_COMMON wlst tasks"
    if [ -z "$5" ] ; then 
      echo "ERROR: Missing argument: Oracle Common home path"
      exit 1
    fi
    ORACLE_HOME="$5"
    export ORACLE_HOME
    setOracleCommonEnv
    ;;
  SOA) echo "Adding additional libraries to classpath for SOA wlst tasks"
    if [ -z "$5" ] ; then 
      echo "ERROR: Missing argument: Oracle SOA home path"
      exit 1
    fi
    ORACLE_HOME="$5"
    export ORACLE_HOME
    setSOAEnv
    ;;
esac

# Check if there's a WLST_JAVA_HOME
if [ -n "${WLST_HOME}" ] ; then
  WLST_PROPERTIES="-Dweblogic.wlstHome=${WLST_HOME} ${WLST_PROPERTIES}"
  export WLST_PROPERTIES
fi

# NOTE: before calling the setWLSEnv.sh script, the JAVA_HOME and JAVA_VENDOR MUST be set, otherwise they will be overriden
if [ -n "${WLST_JAVA_HOME}" ] ; then
  JAVA_HOME=${WLST_JAVA_HOME}
fi
if [ -z "${JAVA_VENDOR}" ] ; then
  JAVA_VENDOR="Unknown"
fi

# Disable the CLASSPATH and PATH output
WLS_NOT_BRIEF_ENV=false
export WLS_NOT_BRIEF_ENV

# Set up common environment
. "${WL_HOME}/server/bin/setWLSEnv.sh"


CLASSPATH="${CLASSPATH}:${WSCRIPTS_HOME}/lib/jpythonEU.jar:${WL_HOME}/../utils/config/10.3/config-launch.jar:${WL_HOME}/common/derby/lib/derbynet.jar:${WL_HOME}/common/derby/lib/derbyclient.jar:${WL_HOME}/common/derby/lib/derbytools.jar"

# Add jar files to classpath for ALSB release
grep -c "Service Bus" ${WL_HOME}/../license.bea 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/servicebus/lib/sb-public.jar:${WL_HOME}/servicebus/lib/sb-internal.jar:${WL_HOME}/servicebus/lib/sb-common.jar:${WL_HOME}/servicebus/lib/uddi_library.jar:${WL_HOME}/servicebus/lib/uddi_client_v3.jar
  # Handling ALSB 3.0
  CLASSPATH=${CLASSPATH}:${WL_HOME}/../alsb_3.0/lib/sb-kernel-api.jar:${WL_HOME}/../modules/com.bea.common.configfwk_1.1.0.0.jar:${WL_HOME}/common/eval/pointbase/lib/pbclient51.jar:${WL_HOME}/common/eval/pointbase/lib/pbembedded51.jar
fi
# Add jar files to classpath for OSB release
grep -c "Oracle Service Bus" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  # Handling OSB 10.3.0.0
  CLASSPATH=${CLASSPATH}:${WL_HOME}/../osb_10.3/lib/sb-kernel-api.jar:${WL_HOME}/../modules/com.bea.common.configfwk_1.2.0.0.jar:${WL_HOME}/common/eval/pointbase/lib/pbclient57.jar:${WL_HOME}/common/eval/pointbase/lib/pbembedded57.jar
  # Handling OSB 10.3.1.0
  CLASSPATH=${CLASSPATH}:${WL_HOME}/../modules/com.bea.common.configfwk_1.2.1.0.jar
fi

# Detect if the current installation is version 10
#grep -c "release=\"10." ${WL_HOME}/../license.bea 2>/dev/null 1>/dev/null
grep -c "<component name=\"WebLogic Server\" version=\"10.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib:${WL_HOME}/../modules/com.bea.cie.config_5.1.3.0.jar
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 10.3
grep -c "version=\"10.3.0.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib:${WL_HOME}/../modules/com.bea.cie.config_5.2.0.0.jar
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 11g
grep -c "version=\"10.3.1.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib:${WL_HOME}/../modules/com.bea.cie.config_6.0.0.0.jar
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 11g sp1
grep -c "version=\"10.3.2.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib:${WL_HOME}/../modules/com.bea.cie.config_6.1.0.0.jar
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 11g 10.3.3.0
grep -c "<component name=\"WebLogic Server\" version=\"10.3.3.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 11g 10.3.4.0
grep -c "<component name=\"WebLogic Server\" version=\"10.3.4.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 11g 10.3.5.0
grep -c "<component name=\"WebLogic Server\" version=\"10.3.5.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 11g 10.3.6.0
grep -c "<component name=\"WebLogic Server\" version=\"10.3.6.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 12c 12.1.1.0
grep -c "<component name=\"WebLogic Server\" version=\"12.1.1.0" ${WL_HOME}/../registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 12c 12.1.2.0
grep -c "<distribution status=\"installed\" name=\"WebLogic Server\" version=\"12.1.2.0.0" ${WL_HOME}/../inventory/registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 12c 12.1.3.0
grep -c "<distribution status=\"installed\" name=\"WebLogic Server\" version=\"12.1.3.0.0" ${WL_HOME}/../inventory/registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  JYTHON_OPTION=-Dprod.props.file=${WL_HOME}/.product.properties
fi

# Detect if the current installation is version 12cR2 12.2.1.0
grep -c "<distribution status=\"installed\" name=\"WebLogic Server\" version=\"12.2.1.0.0" ${WL_HOME}/../inventory/registry.xml 2>/dev/null 1>/dev/null
status=$?
if [ ${status} = 0 ]; then
  CLASSPATH=${CLASSPATH}:${WL_HOME}/server/lib
  # From version 12.2.1, .product.properties is not needed anymore
fi

# ------------- T3S support ---------------------------------------------------
DEBUG=false
#DEBUG=true

SSL_STORE="${WSCRIPTS_HOME}/SSL/EuropeanCommissionTrustStore.jks"
if [ "$DEBUG" = "true" ]; then
   SSL_DEBUG="-Dssl.debug=true -Dweblogic.StdoutDebugEnabled=true -Dweblogic.security.SSL.verbose=true"
else
   SSL_DEBUG=""
fi

# Default is 2
if [ -z "${SSL_IMPL}" ]; then SSL_IMPL=2; fi
case "$SSL_IMPL"  in
     0)   SSL_OPTIONS="${SSL_DEBUG}";;
     1)   SSL_OPTIONS="-Dweblogic.security.SSL.ignoreHostnameVerification=true -Dweblogic.security.CustomTrustKeyStoreType="jks" -Dweblogic.security.TrustKeyStore=CustomTrust -Dweblogic.security.CustomTrustKeyStoreFileName=${SSL_STORE} -Dweblogic.security.allowCryptoJDefaultJCEVerification=true -Dweblogic.security.allowCryptoJDefaultPRNG=true ${SSL_DEBUG}";;
     2)   SSL_OPTIONS="-DUseSunHttpHandler=true -Dhttps.protocols=TLSv1 -Dweblogic.security.SSL.protocolVersion=TLS1 -Dweblogic.ssl.JSSEEnabled=true -Dweblogic.security.SSL.enableJSSE=true -Djavax.net.ssl.trustStore=${SSL_STORE} ${SSL_DEBUG}";;
    *)  SSL_OPTIONS=" -Dweblogic.security.SSL.ignoreHostnameVerification=true -Dweblogic.security.CustomTrustKeyStoreType="jks" -Dweblogic.security.TrustKeyStore=CustomTrust -Dweblogic.security.CustomTrustKeyStoreFileName=${SSL_STORE} -Dweblogic.security.allowCryptoJDefaultJCEVerification=true -Dweblogic.security.allowCryptoJDefaultPRNG=true ${SSL_DEBUG}";;
esac

# ------- oracle_common (required for SOA12c domain creation) -----------------

if [ "${COMMON_COMPONENTS_HOME}" = "" ]; then
  export COMMON_COMPONENTS_HOME=${WL_HOME}/../oracle_common
fi

# -----------------------------------------------------------------------------
# Define JAVA_OPTION
#JAVA_OPTION="${JAVA_OPTION} ${WLST_PROPERTIES}"
 
JAVA_CMD="${JAVA_HOME}/bin/java"
unset JAVA_VERSION
 
# Check if JDK8
JAVA_VERSION=$($JAVA_CMD -Xms8m -Xmx8m -XX:MaxMetaspaceSize=8m -version 2>&1 | awk -F\" 'NR == 1 && $1 ~ /^java version/ { print $2 }')
 
# If not JDK8
if [ -z "${JAVA_VERSION}" ]; then
    JAVA_OPTION="-Xms1536m -Xmx1536m -XX:PermSize=512m -XX:MaxPermSize=512m ${JAVA_OPTION} ${WLST_PROPERTIES}"
else
    JAVA_OPTION="-Xms1536m -Xmx1536m -XX:MetaspaceSize=512m ${JAVA_OPTION} ${WLST_PROPERTIES}"
fi
# -----------------------------------------------------------------------------

#echo
#echo CLASSPATH=${CLASSPATH}

JYTHON_OPTION="${JYTHON_OPTION} -Dpython.path=${WL_HOME}/common/wlst/modules:${WSCRIPTS_HOME}/lib/wlstscripts.jar:${WL_HOME}/common/wlst/modules/jython-modules.jar/Lib"
export WL_HOME WSCRIPTS_HOME CLASSPATH JYTHON_OPTION

#echo
#echo JAVA_OPTION=${JAVA_OPTION}
#echo JYTHON_OPTION=${JYTHON_OPTION}

${JAVA_CMD} ${JAVA_OPTION} ${JYTHON_OPTION} ${SSL_OPTIONS} weblogic.management.scripting.WLST $*
