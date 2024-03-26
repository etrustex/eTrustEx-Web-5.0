var proprefData = [ {
  "name" : "soa.home",
  "ptype" : "string",
  "description" : "Path to the SOA home. Variable used in SOA/BPM domain templates.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396450660592
}, {
  "name" : "admin.server.host",
  "ptype" : "string",
  "description" : "Administration server listen address (either IP address or DNS name).",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#ListenAddress"
  } ],
  "lastUpdated" : 1396450660592
}, {
  "name" : "admin.server.log.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether to configure logging on the administration server.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396529975816
}, {
  "name" : "admin.server.log.file.name",
  "ptype" : "string",
  "description" : "The name of the file that stores current log messages.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileName"
  } ],
  "lastUpdated" : 1396530540690
}, {
  "name" : "admin.server.log.rotation.data",
  "ptype" : "string",
  "description" : "Depending on the rotation type specified (TIME or SIZE), this property determines either the time at which rotation is performed or the size that triggers the rotation.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationTime"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileMinSize"
  } ],
  "lastUpdated" : 1396539649924
}, {
  "name" : "admin.server.log.rotation.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether to rotate log files.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#NumberOfFilesLimited"
  } ],
  "lastUpdated" : 1396537993645
}, {
  "name" : "admin.server.log.rotation.filecount",
  "ptype" : "integer",
  "description" : "The maximum number of log files that the server creates when it rotates the log.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileCount"
  } ],
  "lastUpdated" : 1396539951000
}, {
  "name" : "admin.server.log.rotation.onstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether a server rotates its log file during its startup cycle. The default value in production mode is false.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotateLogOnStartup"
  } ],
  "lastUpdated" : 1396538316850
}, {
  "name" : "admin.server.log.rotation.type",
  "ptype" : "string",
  "description" : "The log rotation type. Either no rotation (NONE), by time (TIME) or by size (SIZE). Use the admin.server.log.rotation.data property to specify the time or size threshold.",
  "legalValues" : [ "NONE", "TIME", "SIZE" ],
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationType"
  } ],
  "lastUpdated" : 1396538826778
}, {
  "name" : "admin.server.log.severity",
  "ptype" : "string",
  "description" : "Admin server logger severity level.",
  "legalValues" : [ "Trace", "Debug", "Info", "Notice", "Warning" ],
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#LoggerSeverity"
  } ],
  "lastUpdated" : 1400509653373
}, {
  "name" : "admin.server.name",
  "ptype" : "string",
  "description" : "Name of the administration server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396450115963
}, {
  "name" : "admin.server.overload.maxstuckthreadtime",
  "ptype" : "integer",
  "description" : "The number of seconds that a thread must be continually working before this server diagnoses the thread as being stuck.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerFailureTriggerMBean.html#MaxStuckThreadTime"
  } ],
  "lastUpdated" : 1439802142988
}, {
  "name" : "admin.server.overload.stuckthreadcount",
  "ptype" : "integer",
  "description" : "The number of stuck threads after which the server is transitioned into FAILED state.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerFailureTriggerMBean.html#StuckThreadCount"
  } ],
  "lastUpdated" : 1439802142988
}, {
  "name" : "admin.server.port",
  "ptype" : "integer",
  "description" : "Administration server listen port.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#ListenPort"
  } ],
  "lastUpdated" : 1396450627287
}, {
  "name" : "admin.server.security.password",
  "ptype" : "string",
  "description" : "Set the administrator password.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396511988100
}, {
  "name" : "admin.server.security.username",
  "ptype" : "string",
  "description" : "Set the administrator username.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396511810303
}, {
  "name" : "admin.server.serverstart.arguments",
  "ptype" : "string",
  "description" : "The arguments to use when starting this server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#Arguments"
  } ],
  "lastUpdated" : 1400487264316
}, {
  "name" : "admin.server.serverstart.classpath",
  "ptype" : "string",
  "description" : "The classpath (path on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#ClassPath"
  } ],
  "lastUpdated" : 1400487076548
}, {
  "name" : "admin.server.serverstart.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether server start will be configured.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1400485629608
}, {
  "name" : "admin.server.serverstart.javahome",
  "ptype" : "string",
  "description" : "The Java home directory (path on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#JavaHome"
  } ],
  "lastUpdated" : 1400487147718
}, {
  "name" : "admin.server.serverstart.javavendor",
  "ptype" : "string",
  "description" : "The Java Vendor value to use when starting this server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#JavaVendor"
  } ],
  "lastUpdated" : 1400508695919
}, {
  "name" : "admin.server.serverstart.password",
  "ptype" : "string",
  "description" : "The password for the user to boot the remote server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#PasswordEncrypted"
  } ],
  "lastUpdated" : 1400486860223
}, {
  "name" : "admin.server.serverstart.rootdir",
  "ptype" : "string",
  "description" : "The directory that this server uses as its root directory.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#RootDirectory"
  } ],
  "lastUpdated" : 1400487405224
}, {
  "name" : "admin.server.serverstart.username",
  "ptype" : "string",
  "description" : "The user name to use when booting the server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#Username"
  } ],
  "lastUpdated" : 1400486257693
}, {
  "name" : "admin.server.serverstartpolicyfile",
  "ptype" : "string",
  "description" : "The security policy file (directory and filename on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#SecurityPolicyFile"
  } ],
  "lastUpdated" : 1400486976663
}, {
  "name" : "admin.server.ssl.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether the SSL server will be enabled.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396510288294
}, {
  "name" : "admin.server.ssl.listen.port",
  "ptype" : "integer",
  "description" : "The administration server SSL listen port",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396510516420
}, {
  "name" : "admin.server.webserver.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether to configure the web server.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396540417203
}, {
  "name" : "admin.server.webserver.log.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether web server logging must be configured.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396540505163
}, {
  "name" : "admin.server.webserver.log.file.name",
  "ptype" : "string",
  "description" : "The name of the file that stores current log messages.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileName"
  } ],
  "lastUpdated" : 1396540750459
}, {
  "name" : "admin.server.webserver.log.rotation.data",
  "ptype" : "string",
  "description" : "Depending on the rotation type specified (TIME or SIZE), this property determines either the time at which rotation is performed or the size that triggers the rotation.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationTime"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileMinSize"
  } ],
  "lastUpdated" : 1396541410357
}, {
  "name" : "admin.server.webserver.log.rotation.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether to rotate log files.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#NumberOfFilesLimited"
  } ],
  "lastUpdated" : 1396540962468
}, {
  "name" : "admin.server.webserver.log.rotation.filecount",
  "ptype" : "integer",
  "description" : "The maximum number of log files that the server creates when it rotates the log.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396541561273
}, {
  "name" : "admin.server.webserver.log.rotation.onstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether a server rotates its log file during its startup cycle. The default value in production mode is false.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotateLogOnStartup"
  } ],
  "lastUpdated" : 1396541129179
}, {
  "name" : "admin.server.webserver.log.rotation.type",
  "ptype" : "string",
  "description" : "The log rotation type. Either no rotation (NONE), by time (TIME) or by size (SIZE). Use the admin.server.webserver.log.rotation.data property to specify the time or size threshold.",
  "legalValues" : [ "NONE", "TIME", "SIZE" ],
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationType"
  } ],
  "lastUpdated" : 1396541296961
}, {
  "name" : "bea.home",
  "ptype" : "string",
  "description" : "The BEA home directory (path on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#BeaHome"
  } ],
  "lastUpdated" : 1400487592864
}, {
  "name" : "cluster.0.frontend.address",
  "ptype" : "string",
  "description" : "The name of the host to which all redirected URLs will be sent.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#FrontendHost"
  } ],
  "lastUpdated" : 1400574254551
}, {
  "name" : "cluster.0.frontend.port",
  "ptype" : "integer",
  "description" : "The name of the HTTP port to which all redirected URLs will be sent.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#FrontendHTTPPort"
  } ],
  "lastUpdated" : 1400574301612
}, {
  "name" : "cluster.0.frontend.sslport",
  "ptype" : "integer",
  "description" : "The name of the secure HTTP port to which all redirected URLs will be sent.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#FrontendHTTPSPort"
  } ],
  "lastUpdated" : 1400574377717
}, {
  "name" : "cluster.0.listen.address",
  "ptype" : "string",
  "description" : "The address that forms a portion of the URL a client uses to connect to this cluster, and that is used for generating EJB handles and entity EJB failover addresses. (This address may be either a DNS host name that maps to multiple IP addresses or a comma-separated list of single address host names or IP addresses.)",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#ClusterAddress"
  } ],
  "lastUpdated" : 1400514546305
}, {
  "name" : "cluster.0.managed.servers",
  "ptype" : "list[string]",
  "description" : "The list of servers that are part of this cluster.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#Cluster"
  } ],
  "lastUpdated" : 1400514664587
}, {
  "name" : "cluster.0.messaging.mode",
  "ptype" : "string",
  "description" : "The cluster messaging mode: either unicast or multicast.\nMulticast messaging is provided for backwards compatibility. Unicast, the default, is recommended for new clusters.",
  "legalValues" : [ "unicast", "multicast" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#ClusterMessagingMode"
  } ],
  "lastUpdated" : 1400514887556
}, {
  "name" : "cluster.0.multicast.address",
  "ptype" : "string",
  "description" : "The multicast address used by cluster members to communicate with each other.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#MulticastAddress"
  } ],
  "lastUpdated" : 1400515184058
}, {
  "name" : "cluster.0.multicast.buffersize",
  "ptype" : "integer",
  "description" : "The multicast socket send/receive buffer size.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#MulticastBufferSize"
  } ],
  "lastUpdated" : 1400515333229
}, {
  "name" : "cluster.0.multicast.dataencrypted",
  "ptype" : "boolean",
  "description" : "Enables multicast data to be encrypted. Only the multicast data is encrypted. Multicast header information is not encrypted.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#MulticastDataEncryption"
  } ],
  "lastUpdated" : 1400515510124
}, {
  "name" : "cluster.0.multicast.delay",
  "ptype" : "integer",
  "description" : "The amount of time (between 0 and 250 milliseconds) to delay sending message fragments over multicast in order to avoid OS-level buffer overflow.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#MulticastSendDelay"
  } ],
  "lastUpdated" : 1400515402665
}, {
  "name" : "cluster.0.multicast.port",
  "ptype" : "integer",
  "description" : "The multicast port used by cluster members to communicate with each other.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#MulticastPort"
  } ],
  "lastUpdated" : 1400515239436
}, {
  "name" : "cluster.0.multicast.ttl",
  "ptype" : "integer",
  "description" : "The number of network hops (between 1 and 255) that a cluster multicast message is allowed to travel.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#MulticastTTL"
  } ],
  "lastUpdated" : 1400515449526
}, {
  "name" : "cluster.0.name",
  "ptype" : "string",
  "description" : "The cluster name.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#Name"
  } ],
  "lastUpdated" : 1400514126366
}, {
  "name" : "cluster.0.overload.maxstuckthreadtime",
  "ptype" : "integer",
  "description" : "The number of seconds that a thread must be continually working before this server diagnoses the thread as being stuck.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerFailureTriggerMBean.html#MaxStuckThreadTime"
  } ],
  "lastUpdated" : 1439802142988
}, {
  "name" : "cluster.0.overload.stuckthreadcount",
  "ptype" : "integer",
  "description" : "The number of stuck threads after which the server is transitioned into FAILED state.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerFailureTriggerMBean.html#StuckThreadCount"
  } ],
  "lastUpdated" : 1439802154684
}, {
  "name" : "cluster.0.unicast.broadcast.channel",
  "ptype" : "string",
  "description" : "Specifies the channel used to handle communications within a cluster. If no channel is specified the default channel is used.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ClusterMBean.html#ClusterBroadcastChannel"
  } ],
  "lastUpdated" : 1400515071046
}, {
  "name" : "cluster.items",
  "ptype" : "integer",
  "description" : "Number of clusters to process.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400513756186
}, {
  "name" : "domain.administration.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether or not domain administration should be configured.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394096894878
}, {
  "name" : "domain.administration.mbean.auditing.enable",
  "ptype" : "boolean",
  "description" : "Specify whether the admin server will generate audit information when modifying MBeans.",
  "deprecated" : false,
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DomainMBean.html?skipReload=true#AdministrationMBeanAuditingEnabled"
  } ],
  "lastUpdated" : 1394210280609
}, {
  "name" : "domain.administration.port",
  "ptype" : "integer",
  "description" : "The domain administration port.",
  "scripts" : [ "domainCreator.py", "Importer.py" ],
  "files" : [ "domainCreator.py", "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DomainMBean.html#AdministrationPort"
  } ],
  "lastUpdated" : 1396513040540
}, {
  "name" : "domain.administration.port.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether the domain-wide administration port should be enabled for this domain.",
  "scripts" : [ "domainCreator.py", "Importer.py" ],
  "files" : [ "domainCreator.py", "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DomainMBean.html#AdministrationPortEnabled"
  } ],
  "lastUpdated" : 1396512876694
}, {
  "name" : "domain.administration.protocol",
  "ptype" : "string",
  "description" : "The default protocol for communicating through the administration port or administration channels.",
  "legalValues" : [ "t3s", "https", "iiops" ],
  "scripts" : [ "domainCreator.py", "Importer.py" ],
  "files" : [ "domainCreator.py", "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DomainMBean.html#AdministrationProtocol"
  } ],
  "lastUpdated" : 1396513172580
}, {
  "name" : "domain.backup.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether a backup of the domain should be performed prior to (re-)creating it.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1400499315733
}, {
  "name" : "domain.backup.file.prefix",
  "ptype" : "string",
  "description" : "Prefix of the domain backup archive.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1400499394771
}, {
  "name" : "domain.backup.location",
  "ptype" : "string",
  "description" : "Path where the domain backup will be created.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1400499369310
}, {
  "name" : "domain.connect.password",
  "ptype" : "string",
  "description" : "The password for the user that connects to the Weblogic server.",
  "scripts" : [ "Importer.py", "Exporter.py", "Remover.py" ],
  "files" : [ "ESBExporter.py", "ESBImporter.py", "Exporter.py", "Importer.py", "Remover.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1400488461783
}, {
  "name" : "domain.connect.propertyfile.path",
  "ptype" : "string",
  "description" : "Path of the boot.properties file to use to connect to the server.",
  "scripts" : [ "Importer.py", "Exporter.py", "Remover.py" ],
  "files" : [ "ServerOperation.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1400498938718
}, {
  "name" : "domain.connect.url",
  "ptype" : "string",
  "description" : "URL of the server to connect to.",
  "scripts" : [ "Importer.py", "Exporter.py", "Remover.py" ],
  "files" : [ "ESBExporter.py", "ESBImporter.py", "Exporter.py", "Importer.py", "Remover.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1400498796322
}, {
  "name" : "domain.connect.username",
  "ptype" : "string",
  "description" : "The user name used to connect to the Weblogic server.",
  "scripts" : [ "Importer.py", "Exporter.py", "Remover.py" ],
  "files" : [ "ESBExporter.py", "ESBImporter.py", "Exporter.py", "Importer.py", "Remover.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1400488406577
}, {
  "name" : "domain.jdbc.script.loader.dbversion",
  "ptype" : "string",
  "description" : "The database version to use in order to load database scripts. [legacy property only used with additional schemes. this one might be dropped in future releases as no more db scripts are still used today].",
  "deprecated" : true,
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394039969786
}, {
  "name" : "domain.jta.timeout",
  "ptype" : "integer",
  "description" : "Specifies the maximum amount of time, in seconds, an active transaction is allowed to be in the first phase of a two-phase commit transaction. If the specified amount of time expires, the transaction is automatically rolled back.",
  "defaultValue" : "30",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/JTAMBean.html#TimeoutSeconds"
  } ],
  "lastUpdated" : 1432024436215
}, {
  "name" : "domain.loading.type",
  "ptype" : "string",
  "description" : "Specifies how to run WLST-API: either in online mode ('connect') or in offline mode ('read'). Except for domain creation, it is highly advised to run WSLT-API in online mode.",
  "legalValues" : [ "connect", "read" ],
  "scripts" : [ "Importer.py", "Exporter.py", "Remover.py" ],
  "files" : [ "Exporter.py", "Importer.py", "Remover.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1400488148698
}, {
  "name" : "domain.location",
  "ptype" : "string",
  "description" : "Path to the domain",
  "scripts" : [ "Importer.py", "domainCreator.py", "Exporter.py", "Remover.py" ],
  "files" : [ "ServerOperation.py", "Importer.py", "Remover.py", "domainCreator.py", "Exporter.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394035504589
}, {
  "name" : "domain.logging.filename",
  "ptype" : "string",
  "description" : "The name of the file that stores current log messages.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileName"
  } ],
  "lastUpdated" : 1400499770171
}, {
  "name" : "domain.logging.implementation",
  "ptype" : "string",
  "description" : "Name of the logging implementation. Deprecated in 12.1.3.0.",
  "deprecated" : true,
  "legalValues" : [ "jdk", "log4j" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#Log4jLoggingEnabled"
  } ],
  "lastUpdated" : 1400501730779
}, {
  "name" : "domain.logging.rotation.directory",
  "ptype" : "string",
  "description" : "The directory where the rotated log files will be stored.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#LogFileRotationDir"
  } ],
  "lastUpdated" : 1400500571896
}, {
  "name" : "domain.logging.rotation.file.limit",
  "ptype" : "boolean",
  "description" : "Indicates whether to limit the number of log files that this server instance creates to store old messages. (Requires that you specify a file rotation type of SIZE or TIME.)",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#NumberOfFilesLimited"
  } ],
  "lastUpdated" : 1400500434310
}, {
  "name" : "domain.logging.rotation.file.size",
  "ptype" : "integer",
  "description" : "The size that triggers the server to move log messages to a separate file.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileMinSize"
  } ],
  "lastUpdated" : 1400500045443
}, {
  "name" : "domain.logging.rotation.file.toretain",
  "ptype" : "integer",
  "description" : "The maximum number of log files that the server creates when it rotates the log.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileCount"
  } ],
  "lastUpdated" : 1400500499379
}, {
  "name" : "domain.logging.rotation.interval",
  "ptype" : "integer",
  "description" : "The interval (in hours) at which the server saves old log messages to another file. (Requires that you specify a file rotation type of TIME.)",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileTimeSpan"
  } ],
  "lastUpdated" : 1400500240341
}, {
  "name" : "domain.logging.rotation.startup",
  "ptype" : "boolean",
  "description" : "Specifies whether a server rotates its log file during its startup cycle.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotateLogOnStartup"
  } ],
  "lastUpdated" : 1400500644340
}, {
  "name" : "domain.logging.rotation.time",
  "ptype" : "string",
  "description" : "Determines the start time (hour and minute) for a time-based rotation sequence.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationTime"
  } ],
  "lastUpdated" : 1400500138854
}, {
  "name" : "domain.logging.rotation.type",
  "ptype" : "string",
  "description" : "Criteria for moving old log messages to a separate file.",
  "legalValues" : [ "bySize", "byTime", "none" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationType"
  } ],
  "lastUpdated" : 1400499931702
}, {
  "name" : "domain.name",
  "ptype" : "string",
  "description" : "Name of the domain.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394034706651
}, {
  "name" : "domain.nodemanager.security.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether the node manager username and password should be configured.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1396279679689
}, {
  "name" : "domain.nodemanager.security.password",
  "ptype" : "string",
  "description" : "The password that the Administration Server uses to communicate with Node Manager when starting, stopping, or restarting Managed Servers.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SecurityConfigurationMBean.html#NodeManagerPassword"
  } ],
  "lastUpdated" : 1396279807339
}, {
  "name" : "domain.nodemanager.security.username",
  "ptype" : "string",
  "description" : "The user name that the Administration Server uses to communicate with Node Manager when starting, stopping, or restarting Managed Servers.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SecurityConfigurationMBean.html#NodeManagerUsername"
  } ],
  "lastUpdated" : 1396279761713
}, {
  "name" : "domain.production.mode",
  "ptype" : "boolean",
  "description" : "Indicates whether the server will be configured for development or for production mode.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394037359469
}, {
  "name" : "domain.security.enforcebasicauth",
  "ptype" : "boolean",
  "description" : "Whether or not the system should allow requests with invalid Basic Authentication credentials to access unsecure resources.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/SecurityConfigurationMBean.html?#EnforceValidBasicAuthCredentials"
  } ],
  "lastUpdated" : 1438851169264
}, 
{
  "name" : "domain.shutdown.class.items",
  "ptype" : "integer",
  "description" : "Number of ShutdownClass items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480913313
},
{
  "name" : "domain.shutdown.class.0.name",
  "ptype" : "string",
  "description" : "The user-specified name of this MBean instance.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ShutdownClassMBean.html#Name"
  } ],
  "lastUpdated" : 1442480913317
},
{
  "name" : "domain.shutdown.class.0.arguments",
  "ptype" : "string",
  "description" : "Arguments that a server uses to initialize a class.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ShutdownClassMBean.html#Arguments"
  } ],
  "lastUpdated" : 1442480913319
},
{
  "name" : "domain.shutdown.class.0.class.name",
  "ptype" : "string",
  "description" : "The fully qualified name of a class to load and run. The class must be on the server's classpath.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ShutdownClassMBean.html#ClassName"
  } ],
  "lastUpdated" : 1442480913322
},
{
  "name" : "domain.shutdown.class.0.targets",
  "ptype" : "list[string]",
  "description" : "ShutdownClass target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480913326
},
{
  "name" : "domain.startup.class.items",
  "ptype" : "integer",
  "description" : "Number of StartupClass items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480703865
},
{
  "name" : "domain.startup.class.0.name",
  "ptype" : "string",
  "description" : "The user-specified name of this MBean instance.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#Name"
  } ],
  "lastUpdated" : 1442480703868
},
{
  "name" : "domain.startup.class.0.arguments",
  "ptype" : "string",
  "description" : "Arguments that a server uses to initialize a class.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#Arguments"
  } ],
  "lastUpdated" : 1442480703871
},
{
  "name" : "domain.startup.class.0.class.name",
  "ptype" : "string",
  "description" : "The fully qualified name of a class to load and run. The class must be on the server's classpath.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#ClassName"
  } ],
  "lastUpdated" : 1442480703873
},
{
  "name" : "domain.startup.class.0.failure.is.fatal",
  "ptype" : "boolean",
  "description" : "Specifies whether a failure in this startup class prevents the targeted server(s) from starting.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#FailureIsFatal"
  } ],
  "lastUpdated" : 1442480703878
},
{
  "name" : "domain.startup.class.0.load.after.apps.running",
  "ptype" : "boolean",
  "description" : "Specifies whether the targeted servers load and run this startup class after applications and EJBs are running.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#LoadAfterAppsRunning"
  } ],
  "lastUpdated" : 1442480703881
},
{
  "name" : "domain.startup.class.0.load.before.app.activation",
  "ptype" : "boolean",
  "description" : "Specifies whether the targeted servers load and run this startup class after activating JMS and JDBC services and before activating applications and EJBs.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#LoadBeforeAppActivation"
  } ],
  "lastUpdated" : 1442480703883
},
{
  "name" : "domain.startup.class.0.load.before.app.deployments",
  "ptype" : "boolean",
  "description" : "Specifies whether the targeted servers load and run this startup class before activating JMS and JDBC services and before starting deployment for applications and EJBs.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/StartupClassMBean.html#LoadBeforeAppDeployments"
  } ],
  "lastUpdated" : 1442480703887
},{
  "name" : "domain.startup.class.0.targets",
  "ptype" : "list[string]",
  "description" : "StartupClass target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480703888
},
{
  "name" : "domain.template.additional.scheme",
  "ptype" : "list[string]",
  "description" : "Optional comma-separated list of additional templates to add during domain creation.\nThis property should only be used when creating non-basic weblogic domains such as OSB or SOA domains or when adding other templates to the domain (ex. JRF,  JAX-WS WebServices).\nThe schemes are defined in the etc/templates.properties file.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394036418946
}, {
  "name" : "domain.template.appdir",
  "ptype" : "string",
  "description" : "Application directory to be used when a separate directory is desired for applications, as specified by the template. This option defaults to WL_HOME/user_projects/applications/domainname, where WL_HOME specifies the WebLogic Server home directory and domainname specifies the name of the WebLogic domain.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLSTC/reference.htm#WLSTC314"
  } ],
  "lastUpdated" : 1396276585500
}, {
  "name" : "domain.template.location",
  "ptype" : "string",
  "description" : "Path to the template for the creation of a weblogic domain. \nVersions prior to 12.1.2 should be '${weblogic.home}/common/templates/domains/wls.jar'. \nAs of version 12.1.2, the path should be '${weblogic.home}/common/templates/wls/wls.jar'.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1394035727704
}, {
  "name" : "domain.template.replaceduplicate",
  "ptype" : "boolean",
  "description" : "Specifies whether to keep original configuration elements in the WebLogic domain or replace the elements with corresponding ones from an extension template when there is a conflict.",
  "defaultValue" : "false",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLSTC/reference.htm#WLSTC314"
  } ],
  "lastUpdated" : 1396276436090
}, {
  "name" : "domain.template.topology.profile",
  "ptype" : "string",
  "description" : "Specifies the topology profile which can be either 'Expanded' or 'Compact'.",
  "legalValues" : [ "Expanded", "Compact" ],
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLDTR/intro.htm#WLDTR443"
  } ],
  "lastUpdated" : 1396276436090
}, {
  "name" : "export.admin.server.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether the admin server should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350828093
}, 
{
  "name" : "export.domain.shutdown.class.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether resources of type ShutdownClass should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480913326
},{
  "name" : "export.domain.shutdown.class.list",
  "ptype" : "list[string]",
  "description" : "List of ShutdownClass to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480913326
},
{
  "name" : "export.domain.startup.class.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether resources of type StartupClass should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480703888
},{
  "name" : "export.domain.startup.class.list",
  "ptype" : "list[string]",
  "description" : "List of StartupClass to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480703888
},
{
  "name" : "export.cluster.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether clusters should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350913833
}, {
  "name" : "export.cluster.list",
  "ptype" : "list[string]",
  "description" : "List of clusters to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350948659
}, {
  "name" : "export.jdbc.datasource.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JDBC data sources should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1405351021272
}, {
  "name" : "export.jdbc.datasource.list",
  "ptype" : "list[string]",
  "description" : "List of JDBC data sources to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1405351049898
}, {
  "name" : "export.jms.bridge.destination.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS bridge destinations should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348456320
}, {
  "name" : "export.jms.bridge.destination.list",
  "ptype" : "list[string]",
  "description" : "List of JMS bridge destinations to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348566496
}, {
  "name" : "export.jms.bridge.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS bridges should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348423900
}, {
  "name" : "export.jms.bridge.list",
  "ptype" : "list[string]",
  "description" : "List of JMS bridges to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348487981
}, {
  "name" : "export.jms.connection.factory.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS connection factories should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349208479
}, {
  "name" : "export.jms.connection.factory.list",
  "ptype" : "list[string]",
  "description" : "List of JMS connection factories to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349236094
}, {
  "name" : "export.jms.distriqueue.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS distributed queues should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349546058
}, {
  "name" : "export.jms.distriqueue.list",
  "ptype" : "list[string]",
  "description" : "List of JMS distributed queues to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349570423
}, {
  "name" : "export.jms.distritopic.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS distributed topics should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349653635
}, {
  "name" : "export.jms.distritopic.list",
  "ptype" : "list[string]",
  "description" : "List of JMS distributed topics to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349684319
}, {
  "name" : "export.jms.foreign.server.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS foreign servers should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1407342243859
}, {
  "name" : "export.jms.foreign.server.list",
  "ptype" : "list[string]",
  "description" : "List of JMS foreign servers to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1407342267266
}, {
  "name" : "export.jms.interop.module.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS interop modules should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348260789
}, {
  "name" : "export.jms.interop.module.list",
  "ptype" : "list[string]",
  "description" : "List of JMS interop modules to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348339422
}, {
  "name" : "export.jms.module.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS modules should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348819379
}, {
  "name" : "export.jms.module.list",
  "ptype" : "list[string]",
  "description" : "List of JMS modules to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348847980
}, {
  "name" : "export.jms.queue.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS queues should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349326902
}, {
  "name" : "export.jms.queue.list",
  "ptype" : "list[string]",
  "description" : "List of JMS queues to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349351839
}, {
  "name" : "export.jms.saf.agent.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS SAF agents should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350142097
}, {
  "name" : "export.jms.saf.agent.list",
  "ptype" : "list[string]",
  "description" : "List of JMS SAF agents to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350158511
}, {
  "name" : "export.jms.saf.error.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS SAF error handling items should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350312662
}, {
  "name" : "export.jms.saf.error.list",
  "ptype" : "list[string]",
  "description" : "List of JMS SAF error handling items to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350330703
}, {
  "name" : "export.jms.saf.imported.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS SAF imported destinations should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350369797
}, {
  "name" : "export.jms.saf.imported.list",
  "ptype" : "list[string]",
  "description" : "List of JMS SAF imported destinations to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350407716
}, {
  "name" : "export.jms.saf.queue.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS SAF queues should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350207319
}, {
  "name" : "export.jms.saf.queue.list",
  "ptype" : "list[string]",
  "description" : "List of JMS SAF queues to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350221437
}, {
  "name" : "export.jms.saf.remote.context.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS SAF remote contexts should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350237031
}, {
  "name" : "export.jms.saf.remote.context.list",
  "ptype" : "list[string]",
  "description" : "List of JMS SAF remote contexts to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350254143
}, {
  "name" : "export.jms.saf.topic.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS SAF topics should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350173151
}, {
  "name" : "export.jms.saf.topic.list",
  "ptype" : "list[string]",
  "description" : "List of JMS SAF topics to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350193151
}, {
  "name" : "export.jms.server.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS servers should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348661062
}, {
  "name" : "export.jms.server.list",
  "ptype" : "list[string]",
  "description" : "List of JMS servers to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405348786201
}, {
  "name" : "export.jms.subdeployment.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS sub deployments should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350025865
}, {
  "name" : "export.jms.subdeployment.list",
  "ptype" : "list[string]",
  "description" : "List of JMS sub deployments to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405350056193
}, {
  "name" : "export.jms.topic.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS topics should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349393873
}, {
  "name" : "export.jms.topic.list",
  "ptype" : "list[string]",
  "description" : "List of JMS topics to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349412669
}, {
  "name" : "export.jms.uniform.distriqueue.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS uniform distributed queues should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349753762
}, {
  "name" : "export.jms.uniform.distriqueue.list",
  "ptype" : "list[string]",
  "description" : "List of JMS uniform distributed queues to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349778906
}, {
  "name" : "export.jms.uniform.distritopic.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether JMS uniform distributed topics should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349880887
}, {
  "name" : "export.jms.uniform.distritopic.list",
  "ptype" : "list[string]",
  "description" : "List of JMS uniform distributed topics to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1405349910921
}, {
  "name" : "export.machine.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether machines should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350494073
}, {
  "name" : "export.machine.list",
  "ptype" : "list[string]",
  "description" : "List of machines to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350519118
}, {
  "name" : "export.mail.javaMail.dir",
  "ptype" : "string",
  "description" : "Path where the javaMail properties file will be exported. If no path is specified, it will be exported in the same directory as the exported properties file.",
  "defaultValue" : "",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "mail" ],
  "links" : [ ],
  "lastUpdated" : 1405351413854
}, {
  "name" : "export.mail.session.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether mail sessions should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "mail" ],
  "links" : [ ],
  "lastUpdated" : 1405351194967
}, {
  "name" : "export.mail.session.list",
  "ptype" : "list[string]",
  "description" : "List of mail sessions to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "mail" ],
  "links" : [ ],
  "lastUpdated" : 1405351264795
}, {
  "name" : "export.managed.server.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether managed servers should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350571584
}, {
  "name" : "export.managed.server.list",
  "ptype" : "list[string]",
  "description" : "List of managed servers to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350585898
}, {
  "name" : "export.output.filename",
  "ptype" : "string",
  "description" : "Path of the exported properties file.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1405351860925
}, {
  "name" : "export.persitent.store.file.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether persistent file stores should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1405351758708
}, {
  "name" : "export.persitent.store.file.list",
  "ptype" : "list[string]",
  "description" : "List of persistent file stores to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1405351782937
}, {
  "name" : "export.persitent.store.jdbc.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether persistent JDBC stores should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1405351687815
}, {
  "name" : "export.persitent.store.jdbc.list",
  "ptype" : "list[string]",
  "description" : "List of persistent JDBC stores to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1405351736793
}, {
  "name" : "export.security.credentials.cleartext",
  "ptype" : "boolean",
  "description" : "Indicates whether passwords should be exported in clear text.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405356063790
}, {
  "name" : "export.security.credentials.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether to export credentials to a XACML formatted file. If enabled, the export script will create one file for each realm and credential mapper specified in a directory named \"export_security\".",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405355330157
}, {
  "name" : "export.security.credentials.mapper.list",
  "ptype" : "list[string]",
  "description" : "The list of credential mappers for which credentials must be exported.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405355428356
}, {
  "name" : "export.security.credentials.realm.list",
  "ptype" : "list[string]",
  "description" : "The list of security realms hosting credentials to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405355370779
}, {
  "name" : "export.security.policies.authorizer.list",
  "ptype" : "list[string]",
  "description" : "The list of authorization providers for the policies to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354023193
}, {
  "name" : "export.security.policies.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether security policies should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405353322661
}, {
  "name" : "export.security.policies.realm.list",
  "ptype" : "list[string]",
  "description" : "The list of security realms hosting policies to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405353915614
}, {
  "name" : "export.security.policies.resource",
  "ptype" : "string",
  "description" : "The resource identifier for the policy to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405353833913
}, {
  "name" : "export.security.policies.xacml.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether to export policies to a XACML formatted file. If enabled, the export script will create one file for each realm and authorization provider specified in a directory named \"export_security\".",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354231300
}, {
  "name" : "export.security.roles.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether security roles should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354515158
}, {
  "name" : "export.security.roles.mapper.list",
  "ptype" : "list[string]",
  "description" : "The list of role mappers for which roles must be exported.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354575367
}, {
  "name" : "export.security.roles.name",
  "ptype" : "string",
  "description" : "The role name for the role to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354908056
}, {
  "name" : "export.security.roles.realm.list",
  "ptype" : "list[string]",
  "description" : "The list of security realms hosting roles to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405355029266
}, {
  "name" : "export.security.roles.resource",
  "ptype" : "string",
  "description" : "The resource identifier for the role to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354951097
}, {
  "name" : "export.security.roles.xacml.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether to export roles to a XACML formatted file. If enabled, the export script will create one file for each realm and role mapper specified in a directory named \"export_security\".",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405354630402
}, {
  "name" : "export.security.usergroup.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether users, groups and groups membership should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1405355118702
}, 
{
  "name" : "export.server.network.channel.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether resources of type NetworkAccessPoint should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1442828254384
},{
  "name" : "export.server.network.channel.list",
  "ptype" : "list[string]",
  "description" : "List of NetworkAccessPoint to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1442828254384
},
{
  "name" : "jdbc.datasource.0.algorithm.type",
  "ptype" : "string",
  "description" : "Datasource algorithm type.",
  "legalValues" : [ "Failover", "Load-Balancing" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#AlgorithmType"
  } ],
  "lastUpdated" : 1400162704446
}, {
  "name" : "jdbc.datasource.0.driver.name",
  "ptype" : "string",
  "description" : "Datasource driver name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400161563841
}, {
  "name" : "jdbc.datasource.0.driver.password",
  "ptype" : "string",
  "description" : "Datasource (clear text) password.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400162198238
}, {
  "name" : "jdbc.datasource.0.driver.properties.items",
  "ptype" : "integer",
  "description" : "Number of property items to set for this JDBC datasource.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400166944948
}, {
  "name" : "jdbc.datasource.0.driver.property.0.key",
  "ptype" : "string",
  "description" : "Name of the datasource driver property to set.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCPropertyBean.html#Name"
  } ],
  "lastUpdated" : 1400167137052
}, {
  "name" : "jdbc.datasource.0.driver.property.0.value",
  "ptype" : "string",
  "description" : "Value of the datasource driver property to set.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCPropertyBean.html#Value"
  } ],
  "lastUpdated" : 1400167178507
}, {
  "name" : "jdbc.datasource.0.driver.url",
  "ptype" : "string",
  "description" : "Datasource JDBC url.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400161635071
}, {
  "name" : "jdbc.datasource.0.driver.username",
  "ptype" : "string",
  "description" : "Datasource user name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400162062296
}, {
  "name" : "jdbc.datasource.0.driver.xa.interface",
  "ptype" : "boolean",
  "description" : "Specifies that WebLogic Server should use the XA interface of the JDBC driver.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDriverParamsBean.html#UseXaDataSourceInterface"
  } ],
  "lastUpdated" : 1400166844220
}, {
  "name" : "jdbc.datasource.0.jndi.name",
  "ptype" : "string",
  "description" : "Datasource JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400161353751
}, {
  "name" : "jdbc.datasource.0.list",
  "ptype" : "string",
  "description" : "The list of data sources to which the multi data source will route connection requests. The order of data sources in the list determines the failover order.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#DataSourceList"
  } ],
  "lastUpdated" : 1400167502123
}, {
  "name" : "jdbc.datasource.0.name",
  "ptype" : "string",
  "description" : "Datasource name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400160660905
}, {
  "name" : "jdbc.datasource.0.pool.capacity.increment",
  "ptype" : "integer",
  "description" : "Capacity increment for the connection pool. (In WebLogic Server 10.3.1 and higher releases, the capacityIncrement is no longer configurable and is set to a value of 1.)",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#CapacityIncrement"
  } ],
  "lastUpdated" : 1400164184175
}, {
  "name" : "jdbc.datasource.0.pool.capacity.initial",
  "ptype" : "integer",
  "description" : "The datasource connection pool initial capacity.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#InitialCapacity"
  } ],
  "lastUpdated" : 1400164302042
}, {
  "name" : "jdbc.datasource.0.pool.capacity.max",
  "ptype" : "integer",
  "description" : "Maximum number of connections in the pool.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#MaxCapacity"
  } ],
  "lastUpdated" : 1400164446577
}, {
  "name" : "jdbc.datasource.0.pool.capacity.min",
  "ptype" : "integer",
  "description" : "The minimum number of physical connections that this connection pool can contain after it is initialized.",
  "defaultValue" : "1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#MinCapacity"
  } ],
  "lastUpdated" : 1400166391834
}, {
  "name" : "jdbc.datasource.0.pool.connection.initsql",
  "ptype" : "string",
  "description" : "SQL statement to execute that will initialize newly created physical database connections. Start the statement with SQL followed by a space.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#InitSql"
  } ],
  "lastUpdated" : 1400166280633
}, {
  "name" : "jdbc.datasource.0.pool.connection.removeinfected",
  "ptype" : "boolean",
  "description" : "Specifies whether a connection will be removed from the connection pool after the application uses the underlying vendor connection object.\n\nIf you disable removing infected connections, you must make sure that the database connection is suitable for reuse by other applications.\n\nWhen set to true (the default), the physical connection is not returned to the connection pool after the application closes the logical connection. Instead, the physical connection is closed and recreated.\n\nWhen set to false, when the application closes the logical connection, the physical connection is returned to the connection pool and can be reused by the application or by another application.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#RemoveInfectedConnections"
  } ],
  "lastUpdated" : 1406792841348
}, {
  "name" : "jdbc.datasource.0.pool.connection.test.onreserv.connectionretryfrequency",
  "ptype" : "integer",
  "description" : "The number of seconds between attempts to establish connections to the database.\nWhen set to 0, connection retry is disabled.",
  "defaultValue" : "10",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#ConnectionCreationRetryFrequencySeconds"
  } ],
  "lastUpdated" : 1400165854337
}, {
  "name" : "jdbc.datasource.0.pool.connection.test.onreserv.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether or not to test connections before serving them.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400164999703
}, {
  "name" : "jdbc.datasource.0.pool.connection.test.onreserv.sql",
  "ptype" : "string",
  "description" : "The name of the database table to use when testing physical database connections. Can be a SQL statement (prefixed with the \"SQL \" string. ex: \"SQL SELECT 1 FROM DUAL\")",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#TestTableName"
  } ],
  "lastUpdated" : 1400165393072
}, {
  "name" : "jdbc.datasource.0.pool.connection.test.onreserv.testfrequency",
  "ptype" : "integer",
  "description" : "The number of seconds a WebLogic Server instance waits between attempts when testing unused connections. (Requires that you specify a Test Table Name.) Connections that fail the test are closed and reopened to re-establish a valid physical connection. If the test fails again, the connection is closed.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#TestFrequencySeconds"
  } ],
  "lastUpdated" : 1406792722961
}, {
  "name" : "jdbc.datasource.0.pool.connection.test.onreserv.timeout",
  "ptype" : "integer",
  "description" : "The number of seconds after which a call to reserve a connection from the connection pool will timeout.\nWhen set to 0, a call will never timeout.\nWhen set to -1, a call will timeout immediately.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#ConnectionReserveTimeoutSeconds"
  } ],
  "lastUpdated" : 1400165594600
}, {
  "name" : "jdbc.datasource.0.pool.connection.test.secondstotrust",
  "ptype" : "integer",
  "description" : "The number of seconds within a connection use that WebLogic Server trusts that the connection is still viable and will skip the connection test, either before delivering it to an application or during the periodic connection testing process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#SecondsToTrustAnIdlePoolConnection"
  } ],
  "lastUpdated" : 1400166146841
}, {
  "name" : "jdbc.datasource.0.pool.inactive.connection.timeout",
  "ptype" : "integer",
  "description" : "The number of inactive seconds on a reserved connection before WebLogic Server reclaims the connection and releases it back into the connection pool.\nWhen set to 0, the feature is disabled.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#InactiveConnectionTimeoutSeconds"
  } ],
  "lastUpdated" : 1400164639670
}, {
  "name" : "jdbc.datasource.0.pool.statement.cache.size",
  "ptype" : "integer",
  "description" : "The number of prepared and callable statements stored in the cache.",
  "defaultValue" : "10",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#StatementCacheSize"
  } ],
  "lastUpdated" : 1400166391834
}, {
  "name" : "jdbc.datasource.0.pool.statement.cache.type",
  "ptype" : "string",
  "description" : "The algorithm used for maintaining the prepared statements stored in the statement cache.",
  "defaultValue" : "LRU",
  "legalValues" : [ "LRU", "FIXED" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#StatementCacheType"
  } ],
  "lastUpdated" : 1400166488163
}, {
  "name" : "jdbc.datasource.0.pool.statement.timeout",
  "ptype" : "integer",
  "description" : "The time after which a statement currently being executed will time out.",
  "defaultValue" : "-1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCConnectionPoolParamsBean.html#StatementTimeout"
  } ],
  "lastUpdated" : 1400166579475
}, {
  "name" : "jdbc.datasource.0.prefetch.row.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether row prefetching is enabled for the datasource.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#RowPrefetch"
  } ],
  "lastUpdated" : 1400163355927
}, {
  "name" : "jdbc.datasource.0.prefetch.row.size",
  "ptype" : "integer",
  "description" : "When prefetching is enabled, the number of rows to prefetch (between 2 and 65536).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#RowPrefetchSize"
  } ],
  "lastUpdated" : 1400163547498
}, {
  "name" : "jdbc.datasource.0.scope",
  "ptype" : "string",
  "description" : "Datasource scope.",
  "legalValues" : [ "Global", "Application" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#Scope"
  } ],
  "lastUpdated" : 1400163150627
}, {
  "name" : "jdbc.datasource.0.stream.chunksize",
  "ptype" : "integer",
  "description" : "The data chunk size for steaming data types.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#StreamChunkSize"
  } ],
  "lastUpdated" : 1400163732700
}, {
  "name" : "jdbc.datasource.0.targets",
  "ptype" : "list[string]",
  "description" : "Datasource target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400160911495
}, {
  "name" : "jdbc.datasource.0.transaction.protocol",
  "ptype" : "string",
  "description" : "Datasource transaction protocol.",
  "legalValues" : [ "TwoPhaseCommit", "LoggingLastResource", "EmulateTwoPhaseCommit", "OnePhaseCommit", "None" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCDataSourceParamsBean.html#GlobalTransactionsProtocol"
  } ],
  "lastUpdated" : 1400162975079
}, {
  "name" : "jdbc.datasource.0.xa.retry.duration",
  "ptype" : "integer",
  "description" : "Determines the duration in seconds for which the transaction manager will perform recover operations on the resource. A value of zero indicates that no retries will be performed.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCXAParamsBean.html#XaRetryDurationSeconds"
  } ],
  "lastUpdated" : 1400168290675
}, {
  "name" : "jdbc.datasource.0.xa.retry.interval",
  "ptype" : "integer",
  "description" : "The number of seconds between XA retry operations if XARetryDurationSeconds is set to a positive value.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCXAParamsBean.html#XaRetryIntervalSeconds"
  } ],
  "lastUpdated" : 1400168223496
}, {
  "name" : "jdbc.datasource.0.xa.transaction.timeout.branch",
  "ptype" : "boolean",
  "description" : "Enables WebLogic Server to set a transaction branch timeout based on the value for XaTransactionTimeout.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCXAParamsBean.html#XaSetTransactionTimeout"
  } ],
  "lastUpdated" : 1400168113265
}, {
  "name" : "jdbc.datasource.0.xa.transaction.timeout.value",
  "ptype" : "integer",
  "description" : "The number of seconds to set as the transaction branch timeout.\nIf set, this value is passed as the transaction timeout value in the XAResource.setTransactionTimeout() call on the XA resource manager, typically the JDBC driver.\nWhen this value is set to 0, the WebLogic Server Transaction Manager passes the global WebLogic Server transaction timeout in seconds in the method.\nIf set, this value should be greater than or equal to the global WebLogic Server transaction timeout.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCXAParamsBean.html#XaTransactionTimeout"
  } ],
  "lastUpdated" : 1400168008287
}, {
  "name" : "jdbc.datasource.items",
  "ptype" : "integer",
  "description" : "Number of datasources to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1400160479073
}, {
  "name" : "jms.auto.distributed.queue.0.jmsserver.list",
  "ptype" : "list[string]",
  "description" : "The list of JMS servers which will be hosting the distributed queue members.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1402388003536
}, {
  "name" : "jms.auto.distributed.queue.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS distributed queue JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#JNDIName"
  } ],
  "lastUpdated" : 1402387437668
}, {
  "name" : "jms.auto.distributed.queue.0.loadbalancing.ramdom",
  "ptype" : "boolean",
  "description" : "Determines the load balancing policy for producers sending messages to a distributed destination in order to balance the message load across the members of the distributed set. A value of 'false' means the Round-Robin (default) policy will be used while a value of 'true' means the Random policy will be used.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#LoadBalancingPolicy"
  } ],
  "lastUpdated" : 1402387875792
}, {
  "name" : "jms.auto.distributed.queue.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this distributed queue.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#DistributedQueues"
  } ],
  "lastUpdated" : 1402387186644
}, {
  "name" : "jms.auto.distributed.queue.0.name",
  "ptype" : "string",
  "description" : "JMS distributed queue name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#Name"
  } ],
  "lastUpdated" : 1402387278332
}, {
  "name" : "jms.auto.distributed.queue.0.number",
  "ptype" : "integer",
  "description" : "The number of JMS distributed queues to create.",
  "defaultValue" : "1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#DistributedQueueMembers"
  } ],
  "lastUpdated" : 1402387624283
}, {
  "name" : "jms.auto.distributed.queue.items",
  "ptype" : "integer",
  "description" : "Number of automatic distributed queues to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1402387049346
}, {
  "name" : "jms.auto.distributed.topic.0.jmsserver.list",
  "ptype" : "list[string]",
  "description" : "The list of JMS servers which will be hosting the distributed topic members.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1402388844977
}, {
  "name" : "jms.auto.distributed.topic.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS distributed topic JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#JNDIName"
  } ],
  "lastUpdated" : 1402388825055
}, {
  "name" : "jms.auto.distributed.topic.0.loadbalancing.ramdom",
  "ptype" : "boolean",
  "description" : "Determines the load balancing policy for producers sending messages to a distributed destination in order to balance the message load across the members of the distributed set. A value of 'false' means the Round-Robin (default) policy will be used while a value of 'true' means the Random policy will be used.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#LoadBalancingPolicy"
  } ],
  "lastUpdated" : 1402388775556
}, {
  "name" : "jms.auto.distributed.topic.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this distributed topic.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#DistributedTopics"
  } ],
  "lastUpdated" : 1402388713159
}, {
  "name" : "jms.auto.distributed.topic.0.name",
  "ptype" : "string",
  "description" : "JMS distributed topic name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#Name"
  } ],
  "lastUpdated" : 1402388165939
}, {
  "name" : "jms.auto.distributed.topic.0.number",
  "ptype" : "integer",
  "description" : "The number of JMS distributed topics to create.",
  "defaultValue" : "1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#DistributedTopicMembers"
  } ],
  "lastUpdated" : 1402388123126
}, {
  "name" : "jms.auto.distributed.topic.items",
  "ptype" : "integer",
  "description" : "Number of automatic distributed topics to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1402388031155
}, {
  "name" : "jms.bridge.0.XAenable",
  "ptype" : "boolean",
  "description" : "Indicates wether to deploy the transactional or non-transactional resource adapter for the messaging bridge.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/BRDGE/basics.htm#BRDGE120"
  } ],
  "lastUpdated" : 1401958389485
}, {
  "name" : "jms.bridge.0.destination.source",
  "ptype" : "string",
  "description" : "The source destination from which this messaging bridge instance reads messages.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#SourceDestination"
  } ],
  "lastUpdated" : 1401956065537
}, {
  "name" : "jms.bridge.0.destination.target",
  "ptype" : "string",
  "description" : "The target destination where a messaging bridge instance sends the messages it receives from the source destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#TargetDestination"
  } ],
  "lastUpdated" : 1401955883134
}, {
  "name" : "jms.bridge.0.name",
  "ptype" : "string",
  "description" : "JMS bridge name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#Name"
  } ],
  "lastUpdated" : 1401955666343
}, {
  "name" : "jms.bridge.0.preserve.message",
  "ptype" : "boolean",
  "description" : "Specifies if message properties are preserved when messages are forwarded by a bridge instance.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#PreserveMsgProperty"
  } ],
  "lastUpdated" : 1401957123807
}, {
  "name" : "jms.bridge.0.qos",
  "ptype" : "string",
  "description" : "The QOS (quality of service) for this messaging bridge instance.",
  "legalValues" : [ "Exactly-once", "Atmost-once", "Duplicate-okay" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#QualityOfService"
  } ],
  "lastUpdated" : 1401956944227
}, {
  "name" : "jms.bridge.0.qos.degradation",
  "ptype" : "boolean",
  "description" : "Specifies if this messaging bridge instance allows the degradation of its QOS (quality of service) when the configured QOS is not available.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#QOSDegradationAllowed"
  } ],
  "lastUpdated" : 1401957059224
}, {
  "name" : "jms.bridge.0.selector",
  "ptype" : "string",
  "description" : "The filter for messages that are sent across the messaging bridge instance.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#Selector"
  } ],
  "lastUpdated" : 1401956476790
}, {
  "name" : "jms.bridge.0.startmode.auto",
  "ptype" : "boolean",
  "description" : "Specifies the initial operating state of a targeted messaging bridge instance.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#Started"
  } ],
  "lastUpdated" : 1401956381550
}, {
  "name" : "jms.bridge.0.targets",
  "ptype" : "list[string]",
  "description" : "JMS bridge targets.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessagingBridgeMBean.html#Targets"
  } ],
  "lastUpdated" : 1401955753111
}, {
  "name" : "jms.bridge.destination.0.classpath",
  "ptype" : "string",
  "description" : "The CLASSPATH of the bridge destination.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#Classpath"
  } ],
  "lastUpdated" : 1401960268659
}, {
  "name" : "jms.bridge.destination.0.connection.credential",
  "ptype" : "string",
  "description" : "The user password that the adapter uses to access the bridge destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#UserPassword"
  } ],
  "lastUpdated" : 1401959902807
}, {
  "name" : "jms.bridge.destination.0.connection.principal",
  "ptype" : "string",
  "description" : "The optional user name the adapter uses to access the bridge destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#UserName"
  } ],
  "lastUpdated" : 1401959831762
}, {
  "name" : "jms.bridge.destination.0.connection.url",
  "ptype" : "string",
  "description" : "The connection URL for this JMS bridge destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#ConnectionURL"
  } ],
  "lastUpdated" : 1401959782200
}, {
  "name" : "jms.bridge.destination.0.connectionfactory.jndi",
  "ptype" : "string",
  "description" : "The connection factory's JNDI name for this JMS bridge destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#ConnectionFactoryJNDIName"
  } ],
  "lastUpdated" : 1401959975836
}, {
  "name" : "jms.bridge.destination.0.destination.jndi",
  "ptype" : "string",
  "description" : "The destination JNDI name for this JMS bridge destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#DestinationJNDIName"
  } ],
  "lastUpdated" : 1401960092948
}, {
  "name" : "jms.bridge.destination.0.jndiadapter",
  "ptype" : "string",
  "description" : "The JNDI name of the adapter used to communicate with the specified destination. This name is specified in the adapter's deployment descriptor file and is used by the WebLogic Server Connector container to bind the adapter in WebLogic Server JNDI.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#AdapterJNDIName"
  } ],
  "lastUpdated" : 1401959632229
}, {
  "name" : "jms.bridge.destination.0.name",
  "ptype" : "string",
  "description" : "The JMS bridge destination name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBridgeDestinationMBean.html#Name"
  } ],
  "lastUpdated" : 1401959368013
}, {
  "name" : "jms.bridge.destination.items",
  "ptype" : "integer",
  "description" : "Number of JMS bridge destinations to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401959154602
}, {
  "name" : "jms.bridge.items",
  "ptype" : "integer",
  "description" : "Number of JMS bridges to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401955101480
}, {
  "name" : "jms.connection.factory.0.default.target.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether this JMS resource defaults to the parent module's targeting or uses the subdeployment targeting mechanism.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSConnectionFactoryBean.html#DefaultTargetingEnabled"
  } ],
  "lastUpdated" : 1401183187455
}, {
  "name" : "jms.connection.factory.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS connection factory JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSConnectionFactoryBean.html#JNDIName"
  } ],
  "lastUpdated" : 1401182413493
}, {
  "name" : "jms.connection.factory.0.module.name",
  "ptype" : "string",
  "description" : "JMS connection factory module.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401182508248
}, {
  "name" : "jms.connection.factory.0.name",
  "ptype" : "string",
  "description" : "JMS connection factory name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSConnectionFactoryBean.html#Name"
  } ],
  "lastUpdated" : 1401182036085
}, {
  "name" : "jms.connection.factory.0.subdeployment.name",
  "ptype" : "string",
  "description" : "JMS connection factory sub deployment.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSConnectionFactoryBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1401182558259
}, {
  "name" : "jms.connection.factory.0.xa.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether a XA queue or XA topic connection factory is returned, instead of a queue or topic connection factory.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TransactionParamsBean.html#XAConnectionFactoryEnabled"
  } ],
  "lastUpdated" : 1401183036561
}, {
  "name" : "jms.connection.factory.0.xa.timeout",
  "ptype" : "integer",
  "description" : "The timeout value (in seconds) for all transactions on connections created with this connection factory.",
  "defaultValue" : "3600",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TransactionParamsBean.html#TransactionTimeout"
  } ],
  "lastUpdated" : 1401182881887
}, {
  "name" : "jms.connection.factory.items",
  "ptype" : "integer",
  "description" : "Number of JMS connection factory items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401179726314
}, {
  "name" : "jms.distributed.queue.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS distributed queue JNDI name.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#JNDIName"
  } ],
  "lastUpdated" : 1402995636021
}, {
  "name" : "jms.distributed.queue.0.loadbalancing.ramdom",
  "ptype" : "boolean",
  "description" : "Determines the load balancing policy for producers sending messages to a distributed destination in order to balance the message load across the members of the distributed set. A value of 'false' means the Round-Robin (default) policy will be used while a value of 'true' means the Random policy will be used.",
  "defaultValue" : "false",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#LoadBalancingPolicy"
  } ],
  "lastUpdated" : 1402995630220
}, {
  "name" : "jms.distributed.queue.0.members",
  "ptype" : "list[string]",
  "description" : "The list of distributed queue members.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#DistributedQueueMembers"
  } ],
  "lastUpdated" : 1402995626324
}, {
  "name" : "jms.distributed.queue.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this distributed queue.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#DistributedQueues"
  } ],
  "lastUpdated" : 1402995622472
}, {
  "name" : "jms.distributed.queue.0.name",
  "ptype" : "string",
  "description" : "JMS distributed queue name.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#Name"
  } ],
  "lastUpdated" : 1402995618376
}, {
  "name" : "jms.distributed.queue.items",
  "ptype" : "integer",
  "description" : "Number of JMS (weighted) distributed queues to process.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1402995613899
}, {
  "name" : "jms.distributed.topic.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS distributed topic JNDI name.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#JNDIName"
  } ],
  "lastUpdated" : 1402998313853
}, {
  "name" : "jms.distributed.topic.0.loadbalancing.ramdom",
  "ptype" : "boolean",
  "description" : "Determines the load balancing policy for producers sending messages to a distributed destination in order to balance the message load across the members of the distributed set. A value of 'false' means the Round-Robin (default) policy will be used while a value of 'true' means the Random policy will be used.",
  "defaultValue" : "false",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#LoadBalancingPolicy"
  } ],
  "lastUpdated" : 1402998170911
}, {
  "name" : "jms.distributed.topic.0.members",
  "ptype" : "list[string]",
  "description" : "The list of distributed topic members.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedQueueBean.html#DistributedTopicMembers"
  } ],
  "lastUpdated" : 1402998100857
}, {
  "name" : "jms.distributed.topic.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this distributed topic.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#DistributedTopics"
  } ],
  "lastUpdated" : 1402998074590
}, {
  "name" : "jms.distributed.topic.0.name",
  "ptype" : "string",
  "description" : "JMS distributed topic name.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DistributedTopicBean.html#Name"
  } ],
  "lastUpdated" : 1402997150008
}, {
  "name" : "jms.distributed.topic.items",
  "ptype" : "integer",
  "description" : "Number of JMS (weighted) distributed topics to process.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1402997073364
}, {
  "name" : "jms.foreign.connection.factory.0.jndi.local",
  "ptype" : "string",
  "description" : "The local JNDI name of the foreign connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignConnectionFactoryBean.html#LocalJNDIName"
  } ],
  "lastUpdated" : 1407331669996
}, {
  "name" : "jms.foreign.connection.factory.0.jndi.remote",
  "ptype" : "string",
  "description" : "The remote JNDI name of the foreign connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignConnectionFactoryBean.html#RemoteJNDIName"
  } ],
  "lastUpdated" : 1407331738178
}, {
  "name" : "jms.foreign.connection.factory.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting the foreign server hosting this foreign connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ForeignConnectionFactories"
  } ],
  "lastUpdated" : 1407331140419
}, {
  "name" : "jms.foreign.connection.factory.0.name",
  "ptype" : "string",
  "description" : "The JMS foreign connection factory name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignConnectionFactoryBean.html#Name"
  } ],
  "lastUpdated" : 1407331058711
}, {
  "name" : "jms.foreign.connection.factory.0.server",
  "ptype" : "string",
  "description" : "The JMS foreign server hosting this foreign connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ForeignConnectionFactories"
  } ],
  "lastUpdated" : 1407331228659
}, {
  "name" : "jms.foreign.connection.factory.items",
  "ptype" : "integer",
  "description" : "Number of JMS foreign connection factory items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ForeignConnectionFactories"
  } ],
  "lastUpdated" : 1407330982657
}, {
  "name" : "jms.foreign.destination.0.jndi.local",
  "ptype" : "string",
  "description" : "The local JNDI name of the foreign destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignDestinationBean.html#LocalJNDIName"
  } ],
  "lastUpdated" : 1407330755861
}, {
  "name" : "jms.foreign.destination.0.jndi.remote",
  "ptype" : "string",
  "description" : "The remote JNDI name of the foreign destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignDestinationBean.html#RemoteJNDIName"
  } ],
  "lastUpdated" : 1407330810879
}, {
  "name" : "jms.foreign.destination.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting the foreign server hosting this destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ForeignDestinations"
  } ],
  "lastUpdated" : 1407330572078
}, {
  "name" : "jms.foreign.destination.0.name",
  "ptype" : "string",
  "description" : "The JMS foreign destination name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignDestinationBean.html#Name"
  } ],
  "lastUpdated" : 1407330367683
}, {
  "name" : "jms.foreign.destination.0.server",
  "ptype" : "string",
  "description" : "The JMS foreign server hosting this destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ForeignDestinations"
  } ],
  "lastUpdated" : 1407330663029
}, {
  "name" : "jms.foreign.destination.items",
  "ptype" : "integer",
  "description" : "Number of JMS foreign destinations to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ForeignDestinations"
  } ],
  "lastUpdated" : 1407330035623
}, {
  "name" : "jms.foreign.server.0.connection.url",
  "ptype" : "string",
  "description" : "The URL that WebLogic Server will use to contact the JNDI provider. The syntax of this URL depends on which JNDI provider is being used. For WebLogic JMS, leave this field blank if you are referencing WebLogic JMS objects within the same cluster.\nThis value corresponds to the standard JNDI property, java.naming.provider.url.\nNote: If this value is not specified, look-ups will be performed on the JNDI server within the WebLogic Server instance where this connection factory is deployed.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#ConnectionURL"
  } ],
  "lastUpdated" : 1407328877762
}, {
  "name" : "jms.foreign.server.0.default.target.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether this JMS resource defaults to the parent module's targeting or uses the subdeployment targeting mechanism.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#DefaultTargetingEnabled"
  } ],
  "lastUpdated" : 1407328530969
}, {
  "name" : "jms.foreign.server.0.initialcontextfactory",
  "ptype" : "string",
  "description" : "The name of the class that must be instantiated to access the JNDI provider. This class name depends on the JNDI provider and the vendor that are being used.\nThis value corresponds to the standard JNDI property, java.naming.factory.initial.\nNote: This value defaults to Weblogic.jndi.WLInitialContextFactory, which is the correct value for WebLogic Server.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#InitialContextFactory"
  } ],
  "lastUpdated" : 1407328784412
}, {
  "name" : "jms.foreign.server.0.jndi.properties.credential",
  "ptype" : "string",
  "description" : "Any Credentials that must be set for the JNDI provider. These Credentials will be part of the properties will be passed directly to the constructor for the JNDI provider's InitialContext class.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#JNDIPropertiesCredential"
  } ],
  "lastUpdated" : 1407328965765
}, {
  "name" : "jms.foreign.server.0.jndi.properties.items",
  "ptype" : "integer",
  "description" : "Number of property items to set for this JMS foreign server.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#JNDIProperties"
  } ],
  "lastUpdated" : 1407329152383
}, {
  "name" : "jms.foreign.server.0.jndi.property.0.key",
  "ptype" : "string",
  "description" : "Name of the JMS foreign server property to set.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/PropertyBean.html#Key"
  } ],
  "lastUpdated" : 1407329488456
}, {
  "name" : "jms.foreign.server.0.jndi.property.0.value",
  "ptype" : "string",
  "description" : "Value of the JMS foreign server property to set.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/PropertyBean.html#Value"
  } ],
  "lastUpdated" : 1407329552478
}, {
  "name" : "jms.foreign.server.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this foreign server.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#ForeignServers"
  } ],
  "lastUpdated" : 1407328429202
}, {
  "name" : "jms.foreign.server.0.name",
  "ptype" : "string",
  "description" : "JMS foreign server name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#Name"
  } ],
  "lastUpdated" : 1407328252344
}, {
  "name" : "jms.foreign.server.0.subdeployment.name",
  "ptype" : "string",
  "description" : "JMS foreign server sub deployment.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ForeignServerBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1407328672435
}, {
  "name" : "jms.foreign.server.items",
  "ptype" : "integer",
  "description" : "Number of JMS Foreign servers to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1407317377509
}, {
  "name" : "jms.module.0.name",
  "ptype" : "string",
  "description" : "JMS module name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSSystemResourceMBean.html#Name"
  } ],
  "lastUpdated" : 1401115378401
}, {
  "name" : "jms.module.0.targets",
  "ptype" : "list[string]",
  "description" : "JMS module target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSSystemResourceMBean.html#Targets"
  } ],
  "lastUpdated" : 1401115353679
}, {
  "name" : "jms.module.items",
  "ptype" : "integer",
  "description" : "Number of JMS module(s) to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401115154008
}, {
  "name" : "jms.queue.0.attachsender",
  "ptype" : "string",
  "description" : "Specifies whether messages landing on this destination should attach the credential of the sending user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#AttachSender"
  } ],
  "lastUpdated" : 1401263692093
}, {
  "name" : "jms.queue.0.consumptionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether consumption is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#ConsumptionPausedAtStartup"
  } ],
  "lastUpdated" : 1401263903378
}, {
  "name" : "jms.queue.0.delivery.failure.expiration.data",
  "ptype" : "string",
  "description" : "Depending on the expiration policy, this property contains either the error destination name for the 'Redirect' policy or the logging policy for the 'Log' policy.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ErrorDestination"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationLoggingPolicy"
  } ],
  "lastUpdated" : 1401262875721
}, {
  "name" : "jms.queue.0.delivery.failure.expiration.policy",
  "ptype" : "string",
  "description" : "The message Expiration Policy to use when an expired message is encountered on a destination. The value of property jms.queue.0.delivery.failure.expiration.data determines the data for the policy: either the error message destination for 'Redirect' or the logging policy for 'Log'.",
  "legalValues" : [ "Redirect", "Log" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationPolicy"
  } ],
  "lastUpdated" : 1401262723919
}, {
  "name" : "jms.queue.0.delivery.failure.redelivery.limit",
  "ptype" : "integer",
  "description" : "The number of redelivery tries a message can have before it is moved to the error destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#RedeliveryLimit"
  } ],
  "lastUpdated" : 1401263074170
}, {
  "name" : "jms.queue.0.delivery.params.deliverymode",
  "ptype" : "string",
  "description" : "The delivery mode assigned to all messages that arrive at the destination regardless of the DeliveryMode specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#DeliveryMode"
  } ],
  "lastUpdated" : 1401264176683
}, {
  "name" : "jms.queue.0.delivery.params.priority",
  "ptype" : "integer",
  "description" : "The priority assigned to all messages that arrive at this destination, regardless of the Priority specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#Priority"
  } ],
  "lastUpdated" : 1401264223153
}, {
  "name" : "jms.queue.0.delivery.params.redeliverydelay",
  "ptype" : "integer",
  "description" : "The delay, in milliseconds, before rolled back or recovered messages are redelivered, regardless of the RedeliveryDelay specified by the consumer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#RedeliveryDelay"
  } ],
  "lastUpdated" : 1401264297240
}, {
  "name" : "jms.queue.0.delivery.params.timetodeliver",
  "ptype" : "string",
  "description" : "The default delay, either in milliseconds or as a schedule, between when a message is produced and when it is made visible on its target destination, regardless of the delivery time specified by the producer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToDeliver"
  } ],
  "lastUpdated" : 1401264480932
}, {
  "name" : "jms.queue.0.delivery.params.timetolive",
  "ptype" : "integer",
  "description" : "The time-to-live assigned to all messages that arrive at this destination, regardless of the TimeToLive value specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToLive"
  } ],
  "lastUpdated" : 1401264540343
}, {
  "name" : "jms.queue.0.insertionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message insertion is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#InsertionPausedAtStartup"
  } ],
  "lastUpdated" : 1401263961705
}, {
  "name" : "jms.queue.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS queue JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#JNDIName"
  } ],
  "lastUpdated" : 1401201433813
}, {
  "name" : "jms.queue.0.message.logging.enabled",
  "ptype" : "boolean",
  "description" : "Specifies whether the module logs information about the message life cycle.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingEnabled"
  } ],
  "lastUpdated" : 1401266272600
}, {
  "name" : "jms.queue.0.message.logging.format",
  "ptype" : "string",
  "description" : "Defines which information about the message is logged.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingFormat"
  } ],
  "lastUpdated" : 1401266325583
}, {
  "name" : "jms.queue.0.module.name",
  "ptype" : "string",
  "description" : "JMS queue module.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401201391178
}, {
  "name" : "jms.queue.0.name",
  "ptype" : "string",
  "description" : "JMS queue name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#Name"
  } ],
  "lastUpdated" : 1401201371198
}, {
  "name" : "jms.queue.0.productionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message production is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#ProductionPausedAtStartup"
  } ],
  "lastUpdated" : 1401263814488
}, {
  "name" : "jms.queue.0.subdeployment.name",
  "ptype" : "string",
  "description" : "JMS queue sub deployment.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/QueueBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1401202572807
}, {
  "name" : "jms.queue.items",
  "ptype" : "integer",
  "description" : "Number of JMS queues to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401193999031
}, {
  "name" : "jms.saf.agent.0.name",
  "ptype" : "string",
  "description" : "The SAF agent name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFAgentMBean.html#Name"
  } ],
  "lastUpdated" : 1401696395572
}, {
  "name" : "jms.saf.agent.0.store.name",
  "ptype" : "string",
  "description" : "The persistent store for the SAF agent.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFAgentMBean.html#Store"
  } ],
  "lastUpdated" : 1401696833343
}, {
  "name" : "jms.saf.agent.0.target.name",
  "ptype" : "list[string]",
  "description" : "The SAF agent target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFAgentMBean.html#Targets"
  } ],
  "lastUpdated" : 1401696684934
}, {
  "name" : "jms.saf.agent.0.type",
  "ptype" : "string",
  "description" : "The type of service that this SAF agent provides. JMS requires only a Sending agent on the sending side for messages. Whereas, Web Services Reliable Messaging requires both a Sending and Receiving agent for messages.",
  "defaultValue" : "Both",
  "legalValues" : [ "Sending-only", "Receiving-only", "Both" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFAgentMBean.html#ServiceType"
  } ],
  "lastUpdated" : 1401696960960
}, {
  "name" : "jms.saf.agent.items",
  "ptype" : "integer",
  "description" : "Number of SAF agents to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401696269105
}, {
  "name" : "jms.saf.error.handling.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this SAF error handling.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFErrorHandlingBean.html#Name"
  } ],
  "lastUpdated" : 1401697591347
}, {
  "name" : "jms.saf.error.handling.0.name",
  "ptype" : "string",
  "description" : "The SAF error handling name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFErrorHandlingBean.html#Name"
  } ],
  "lastUpdated" : 1401697631685
}, {
  "name" : "jms.saf.error.handling.0.policy.data",
  "ptype" : "string",
  "description" : "Depending on the SAF error handling policy, this property contains either the log format for the 'Log' policy or the SAF error destination for the 'Redirect' policy.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFErrorHandlingBean.html#LogFormat"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFErrorHandlingBean.html#SAFErrorDestination"
  } ],
  "lastUpdated" : 1401698191992
}, {
  "name" : "jms.saf.error.handling.0.policy.saf.destination.name",
  "ptype" : "string",
  "description" : "Name of the imported destination when the SAF error handling policy is set to 'Redirect'.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401698474043
}, {
  "name" : "jms.saf.error.handling.0.policy.type",
  "ptype" : "string",
  "description" : "The error handling policy for this SAF error handling resource.",
  "defaultValue" : "Discard",
  "legalValues" : [ "Discard", "Log", "Redirect", "Always-Forward" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFErrorHandlingBean.html#Policy"
  } ],
  "lastUpdated" : 1401697953842
}, {
  "name" : "jms.saf.error.handling.items",
  "ptype" : "integer",
  "description" : "The number of SAF error handlers to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401697341904
}, {
  "name" : "jms.saf.imported.destination.0.error.handling",
  "ptype" : "string",
  "description" : "Specifies the error handling configuration used for the imported destinations.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#SAFErrorHandling"
  } ],
  "lastUpdated" : 1401715324390
}, {
  "name" : "jms.saf.imported.destination.0.jndi.prefix",
  "ptype" : "string",
  "description" : "Specifies the string that will prefix the local JNDI name of a remote destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#JNDIPrefix"
  } ],
  "lastUpdated" : 1401714397282
}, {
  "name" : "jms.saf.imported.destination.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this SAF imported destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#SAFImportedDestinations"
  } ],
  "lastUpdated" : 1401713855300
}, {
  "name" : "jms.saf.imported.destination.0.name",
  "ptype" : "string",
  "description" : "The SAF imported destination name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#Name"
  } ],
  "lastUpdated" : 1401714009545
}, {
  "name" : "jms.saf.imported.destination.0.remote.context",
  "ptype" : "string",
  "description" : "The remote context used for the SAF imported destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#SAFRemoteContext"
  } ],
  "lastUpdated" : 1401714272925
}, {
  "name" : "jms.saf.imported.destination.0.target.default.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether this JMS resource defaults to the parent module's targeting or uses the subdeployment targeting mechanism.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#DefaultTargetingEnabled"
  } ],
  "lastUpdated" : 1401714925115
}, {
  "name" : "jms.saf.imported.destination.0.target.subdeployment.name",
  "ptype" : "string",
  "description" : "The name of the sub-deployment to use when targeting this entity.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1401715176414
}, {
  "name" : "jms.saf.imported.destination.0.timetolive.value",
  "ptype" : "integer",
  "description" : "Specifies the default Time-to-Live value (expiration time), in milliseconds, for imported JMS messages.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#TimeToLiveDefault"
  } ],
  "lastUpdated" : 1401714786061
}, {
  "name" : "jms.saf.imported.destination.0.timetolivedefault.enable",
  "ptype" : "boolean",
  "description" : "Controls whether the Time-to-Live (expiration time) value set on imported JMS messages will be overridden by the value specified in the property 'jms.saf.imported.destination.0.timetolive.value'.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#UseSAFTimeToLiveDefault"
  } ],
  "lastUpdated" : 1401714662116
}, {
  "name" : "jms.saf.imported.destination.items",
  "ptype" : "integer",
  "description" : "Number of SAF imported destinations to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401713617638
}, {
  "name" : "jms.saf.queue.0.error.handling.name",
  "ptype" : "string",
  "description" : "Specifies the error handling configuration used by this SAF destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#SAFErrorHandling"
  } ],
  "lastUpdated" : 1401717471545
}, {
  "name" : "jms.saf.queue.0.imported.destination",
  "ptype" : "string",
  "description" : "The SAF imported destination defining this SAF queue.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#SAFQueues"
  } ],
  "lastUpdated" : 1401716647371
}, {
  "name" : "jms.saf.queue.0.jndi.local",
  "ptype" : "string",
  "description" : "The local JNDI name of the remote destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#LocalJNDIName"
  } ],
  "lastUpdated" : 1401716847173
}, {
  "name" : "jms.saf.queue.0.jndi.remote",
  "ptype" : "string",
  "description" : "The remote JNDI name of the remote destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#RemoteJNDIName"
  } ],
  "lastUpdated" : 1401716913697
}, {
  "name" : "jms.saf.queue.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this SAF queue.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#SAFImportedDestinations"
  } ],
  "lastUpdated" : 1401716093614
}, {
  "name" : "jms.saf.queue.0.name",
  "ptype" : "string",
  "description" : "The SAF queue name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#Name"
  } ],
  "lastUpdated" : 1401716729438
}, {
  "name" : "jms.saf.queue.0.nonpersistentQOS",
  "ptype" : "string",
  "description" : "Specifies the quality-of-service for non-persistent messages.",
  "defaultValue" : "At-Least-Once",
  "legalValues" : [ "At-Most-Once", "At-Least-Once", "Exactly-Once" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#NonPersistentQos"
  } ],
  "lastUpdated" : 1401717347347
}, {
  "name" : "jms.saf.queue.0.timetolive.default.enable",
  "ptype" : "boolean",
  "description" : "Controls whether the Time-to-Live (expiration time) value set on imported JMS messages will be overridden by the value specified in the property 'jms.saf.queue.0.timetolive.value'.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#UseSAFTimeToLiveDefault"
  } ],
  "lastUpdated" : 1401717069595
}, {
  "name" : "jms.saf.queue.0.timetolive.value",
  "ptype" : "integer",
  "description" : "Specifies the default Time-to-Live value (expiration time), in milliseconds, for imported JMS messages.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFQueueBean.html#TimeToLiveDefault"
  } ],
  "lastUpdated" : 1401717169420
}, {
  "name" : "jms.saf.queue.items",
  "ptype" : "integer",
  "description" : "Number of SAF queues to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401715724029
}, {
  "name" : "jms.saf.remote.context.0.login.password",
  "ptype" : "string",
  "description" : "The password for the username used to log into the remote URL.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFLoginContextBean.html#Password"
  } ],
  "lastUpdated" : 1401701840855
}, {
  "name" : "jms.saf.remote.context.0.login.url",
  "ptype" : "string",
  "description" : "Specifies the URL to connect to when using this SAF Login Context.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFLoginContextBean.html#LoginURL"
  } ],
  "lastUpdated" : 1401701753123
}, {
  "name" : "jms.saf.remote.context.0.login.username",
  "ptype" : "string",
  "description" : "The name used to log into the remote URL.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFLoginContextBean.html#Username"
  } ],
  "lastUpdated" : 1401701793657
}, {
  "name" : "jms.saf.remote.context.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this SAF remote context.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#SAFRemoteContexts"
  } ],
  "lastUpdated" : 1401701530439
}, {
  "name" : "jms.saf.remote.context.0.name",
  "ptype" : "string",
  "description" : "The SAF remote context name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFRemoteContextBean.html#Name"
  } ],
  "lastUpdated" : 1401701614532
}, {
  "name" : "jms.saf.remote.context.items",
  "ptype" : "integer",
  "description" : "Number of SAF remote contexts to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401701348905
}, {
  "name" : "jms.saf.topic.0.error.handling.name",
  "ptype" : "string",
  "description" : "Specifies the error handling configuration used by this SAF destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#SAFErrorHandling"
  } ],
  "lastUpdated" : 1401781953250
}, {
  "name" : "jms.saf.topic.0.imported.destination",
  "ptype" : "string",
  "description" : "The SAF imported destination defining this SAF topic.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFImportedDestinationsBean.html#SAFTopics"
  } ],
  "lastUpdated" : 1401781815444
}, {
  "name" : "jms.saf.topic.0.jndi.local",
  "ptype" : "string",
  "description" : "The local JNDI name of the remote destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#LocalJNDIName"
  } ],
  "lastUpdated" : 1401781880534
}, {
  "name" : "jms.saf.topic.0.jndi.remote",
  "ptype" : "string",
  "description" : "The remote JNDI name of the remote destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#RemoteJNDIName"
  } ],
  "lastUpdated" : 1401781911452
}, {
  "name" : "jms.saf.topic.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this SAF topic.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#SAFImportedDestinations"
  } ],
  "lastUpdated" : 1401781756915
}, {
  "name" : "jms.saf.topic.0.name",
  "ptype" : "string",
  "description" : "The SAF topic name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#Name"
  } ],
  "lastUpdated" : 1401781713148
}, {
  "name" : "jms.saf.topic.0.nonpersistentQOS",
  "ptype" : "string",
  "description" : "Specifies the quality-of-service for non-persistent messages.",
  "defaultValue" : "At-Least-Once",
  "legalValues" : [ "At-Most-Once", "At-Least-Once", "Exactly-Once" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#NonPersistentQos"
  } ],
  "lastUpdated" : 1401782011889
}, {
  "name" : "jms.saf.topic.0.timetolive.default.enable",
  "ptype" : "boolean",
  "description" : "Controls whether the Time-to-Live (expiration time) value set on imported JMS messages will be overridden by the value specified in the property 'jms.saf.topic.0.timetolive.value'.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#UseSAFTimeToLiveDefault"
  } ],
  "lastUpdated" : 1401782065013
}, {
  "name" : "jms.saf.topic.0.timetolive.value",
  "ptype" : "integer",
  "description" : "Specifies the default Time-to-Live value (expiration time), in milliseconds, for imported JMS messages.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SAFTopicBean.html#TimeToLiveDefault"
  } ],
  "lastUpdated" : 1401782106396
}, {
  "name" : "jms.saf.topic.items",
  "ptype" : "integer",
  "description" : "Number of SAF topics to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401781272829
}, {
  "name" : "jms.server.0.consumptionpaused.atstartup",
  "ptype" : "string",
  "description" : "Indicates whether consumption is paused at startup on destinations targeted to this JMS server at startup. A destination cannot receive any new messages while it is paused.",
  "legalValues" : [ "default", "true", "false" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSServerMBean.html#ConsumptionPausedAtStartup"
  } ],
  "lastUpdated" : 1400833431110
}, {
  "name" : "jms.server.0.insertionpaused.atstartup",
  "ptype" : "string",
  "description" : "Indicates whether insertion is paused at startup on destinations targeted to this JMS server. A destination cannot receive any new messages while it is paused.",
  "legalValues" : [ "default", "true", "false" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSServerMBean.html#InsertionPausedAtStartup"
  } ],
  "lastUpdated" : 1400833502519
}, {
  "name" : "jms.server.0.log.dateformatpattern",
  "ptype" : "string",
  "description" : "The date format pattern used for rendering dates in the log. The DateFormatPattern string conforms to the specification of the java.text.SimpleDateFormat class.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#DateFormatPattern"
  } ],
  "lastUpdated" : 1400834021264
}, {
  "name" : "jms.server.0.log.file.count",
  "ptype" : "integer",
  "description" : "The maximum number of log files that the server creates when it rotates the log.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#FileCount"
  } ],
  "lastUpdated" : 1400835473572
}, {
  "name" : "jms.server.0.log.file.minsize",
  "ptype" : "integer",
  "description" : "The size that triggers the server to move log messages to a separate file.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#FileMinSize"
  } ],
  "lastUpdated" : 1400835565528
}, {
  "name" : "jms.server.0.log.file.name",
  "ptype" : "string",
  "description" : "The name of the file that stores current JMS server log messages.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#FileName"
  } ],
  "lastUpdated" : 1400835620959
}, {
  "name" : "jms.server.0.log.file.number.limited",
  "ptype" : "boolean",
  "description" : "Indicates whether to limit the number of log files that this server instance creates to store old messages. (Requires that you specify a file rotation type of SIZE or TIME.)",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#NumberOfFilesLimited"
  } ],
  "lastUpdated" : 1400837253261
}, {
  "name" : "jms.server.0.log.file.rotation.dir",
  "ptype" : "string",
  "description" : "The directory where the rotated log files will be stored.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#LogFileRotationDir"
  } ],
  "lastUpdated" : 1400835809601
}, {
  "name" : "jms.server.0.log.file.rotation.logonstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether a server rotates its log file during its startup cycle. The default value in production mode is false.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#RotateLogOnStartup"
  } ],
  "lastUpdated" : 1400836185975
}, {
  "name" : "jms.server.0.log.file.rotation.time",
  "ptype" : "string",
  "description" : "Determines the start time (hour and minute) for a time-based rotation sequence.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#RotationTime"
  } ],
  "lastUpdated" : 1400835920838
}, {
  "name" : "jms.server.0.log.file.rotation.type",
  "ptype" : "string",
  "description" : "Criteria for moving old log messages to a separate file.",
  "legalValues" : [ "NONE", "TIME", "SIZE" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#RotationType"
  } ],
  "lastUpdated" : 1400836056763
}, {
  "name" : "jms.server.0.log.file.timespan",
  "ptype" : "integer",
  "description" : "The interval (in hours) at which the server saves old log messages to another file.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSMessageLogFileMBean.html#FileTimeSpan"
  } ],
  "lastUpdated" : 1400835733233
}, {
  "name" : "jms.server.0.name",
  "ptype" : "string",
  "description" : "JMS server name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSServerMBean.html#Name"
  } ],
  "lastUpdated" : 1400832745732
}, {
  "name" : "jms.server.0.paging.directory",
  "ptype" : "string",
  "description" : "Specifies where message bodies are written when the size of the message bodies in the JMS server exceeds the message buffer size.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/JMSServerMBean.html#PagingDirectory"
  } ],
  "lastUpdated" : 1441012077157
}, {
  "name" : "jms.server.0.productionpaused.atstartup",
  "ptype" : "string",
  "description" : "Specifies whether production is paused at server startup on destinations targeted to this JMS server. A destination cannot receive any new messages while it is paused.",
  "legalValues" : [ "default", "true", "false" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSServerMBean.html#ProductionPausedAtStartup"
  } ],
  "lastUpdated" : 1400833313590
}, {
  "name" : "jms.server.0.store",
  "ptype" : "string",
  "description" : "JMS server persistent store (either a FileStore or a JDBCStore).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSServerMBean.html#PersistentStore"
  } ],
  "lastUpdated" : 1400832931751
}, {
  "name" : "jms.server.0.target",
  "ptype" : "string",
  "description" : "JMS server target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSServerMBean.html#Targets"
  } ],
  "lastUpdated" : 1400833074933
}, {
  "name" : "jms.server.items",
  "ptype" : "integer",
  "description" : "Number of JMS servers to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1400832553694
}, {
  "name" : "jms.subdeployment.0.module.name",
  "ptype" : "string",
  "description" : "JMS sub deployment module.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401179130380
}, {
  "name" : "jms.subdeployment.0.name",
  "ptype" : "string",
  "description" : "JMS sub deployment name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SubDeploymentMBean.html#Name"
  } ],
  "lastUpdated" : 1401178943740
}, {
  "name" : "jms.subdeployment.0.targets",
  "ptype" : "list[string]",
  "description" : "JMS sub deployment targets.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SubDeploymentMBean.html#Targets"
  } ],
  "lastUpdated" : 1401178984685
}, {
  "name" : "jms.subdeployment.items",
  "ptype" : "integer",
  "description" : "Number of sub deployments to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401116975124
}, {
  "name" : "jms.topic.0.attachsender",
  "ptype" : "string",
  "description" : "Specifies whether messages landing on this destination should attach the credential of the sending user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#AttachSender"
  } ],
  "lastUpdated" : 1401287798477
}, {
  "name" : "jms.topic.0.consumptionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether consumption is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#ConsumptionPausedAtStartup"
  } ],
  "lastUpdated" : 1401288081489
}, {
  "name" : "jms.topic.0.delivery.failure.expiration.data",
  "ptype" : "string",
  "description" : "Depending on the expiration policy, this property contains either the error destination name for the 'Redirect' policy or the logging policy for the 'Log' policy.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ErrorDestination"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationLoggingPolicy"
  } ],
  "lastUpdated" : 1401287674468
}, {
  "name" : "jms.topic.0.delivery.failure.expiration.policy",
  "ptype" : "string",
  "description" : "The message Expiration Policy to use when an expired message is encountered on a destination. The value of property jms.topic.0.delivery.failure.expiration.data determines the data for the policy: either the error message destination for 'Redirect' or the logging policy for 'Log'.",
  "legalValues" : [ "Redirect", "Log" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationPolicy"
  } ],
  "lastUpdated" : 1401287599584
}, {
  "name" : "jms.topic.0.delivery.failure.redelivery.limit",
  "ptype" : "integer",
  "description" : "The number of redelivery tries a message can have before it is moved to the error destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#RedeliveryLimit"
  } ],
  "lastUpdated" : 1401287731603
}, {
  "name" : "jms.topic.0.delivery.params.deliverymode",
  "ptype" : "string",
  "description" : "The delivery mode assigned to all messages that arrive at the destination regardless of the DeliveryMode specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#DeliveryMode"
  } ],
  "lastUpdated" : 1401288226396
}, {
  "name" : "jms.topic.0.delivery.params.priority",
  "ptype" : "integer",
  "description" : "The priority assigned to all messages that arrive at this destination, regardless of the Priority specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#Priority"
  } ],
  "lastUpdated" : 1401288293540
}, {
  "name" : "jms.topic.0.delivery.params.redeliverydelay",
  "ptype" : "integer",
  "description" : "The delay, in milliseconds, before rolled back or recovered messages are redelivered, regardless of the RedeliveryDelay specified by the consumer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#RedeliveryDelay"
  } ],
  "lastUpdated" : 1401288278142
}, {
  "name" : "jms.topic.0.delivery.params.timetodeliver",
  "ptype" : "string",
  "description" : "The default delay, either in milliseconds or as a schedule, between when a message is produced and when it is made visible on its target destination, regardless of the delivery time specified by the producer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToDeliver"
  } ],
  "lastUpdated" : 1401288262616
}, {
  "name" : "jms.topic.0.delivery.params.timetolive",
  "ptype" : "integer",
  "description" : "The time-to-live assigned to all messages that arrive at this destination, regardless of the TimeToLive value specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToLive"
  } ],
  "lastUpdated" : 1401288244632
}, {
  "name" : "jms.topic.0.insertionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message insertion is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#InsertionPausedAtStartup"
  } ],
  "lastUpdated" : 1401288048007
}, {
  "name" : "jms.topic.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS topic JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#JNDIName"
  } ],
  "lastUpdated" : 1401287384051
}, {
  "name" : "jms.topic.0.message.logging.enabled",
  "ptype" : "boolean",
  "description" : "Specifies whether the module logs information about the message life cycle.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingEnabled"
  } ],
  "lastUpdated" : 1401288338828
}, {
  "name" : "jms.topic.0.message.logging.format",
  "ptype" : "string",
  "description" : "Defines which information about the message is logged.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingFormat"
  } ],
  "lastUpdated" : 1401288363644
}, {
  "name" : "jms.topic.0.module.name",
  "ptype" : "string",
  "description" : "JMS topic module.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401286428320
}, {
  "name" : "jms.topic.0.name",
  "ptype" : "string",
  "description" : "JMS topic name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#Name"
  } ],
  "lastUpdated" : 1401286392085
}, {
  "name" : "jms.topic.0.productionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message production is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#ProductionPausedAtStartup"
  } ],
  "lastUpdated" : 1401288006477
}, {
  "name" : "jms.topic.0.subdeployment.name",
  "ptype" : "string",
  "description" : "JMS topic sub deployment.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/TopicBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1401286528984
}, {
  "name" : "jms.topic.items",
  "ptype" : "integer",
  "description" : "Number of JMS topics to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1401266483132
}, {
  "name" : "jms.uniform.distributed.queue.0.attachsender",
  "ptype" : "string",
  "description" : "Specifies whether messages landing on this destination should attach the credential of the sending user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#AttachSender"
  } ],
  "lastUpdated" : 1403251389703
}, {
  "name" : "jms.uniform.distributed.queue.0.consumptionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether consumption is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#ConsumptionPausedAtStartup"
  } ],
  "lastUpdated" : 1403251521660
}, {
  "name" : "jms.uniform.distributed.queue.0.default.target.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether this JMS resource defaults to the parent module's targeting or uses the subdeployment targeting mechanism.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#DefaultTargetingEnabled"
  } ],
  "lastUpdated" : 1403249553333
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.failure.expiration.data",
  "ptype" : "string",
  "description" : "The message Expiration Policy to use when an expired message is encountered on a destination. The value of property jms.uniform.distributed.queue.0.delivery.failure.expiration.data determines the data for the policy: either the error message destination for 'Redirect' or the logging policy for 'Log'.",
  "legalValues" : [ "Redirect", "Log" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationPolicy"
  } ],
  "lastUpdated" : 1403250964234
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.failure.expiration.policy",
  "ptype" : "string",
  "description" : "The message Expiration Policy to use when an expired message is encountered on a destination. The value of property jms.uniform.distributed.queue.0.delivery.failure.expiration.data determines the data for the policy: either the error message destination for 'Redirect' or the logging policy for 'Log'.",
  "legalValues" : [ "Redirect", "Log" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationPolicy"
  } ],
  "lastUpdated" : 1403250926899
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.failure.redelivery.limit",
  "ptype" : "integer",
  "description" : "The number of redelivery tries a message can have before it is moved to the error destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#RedeliveryLimit"
  } ],
  "lastUpdated" : 1403251260646
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.params.deliverymode",
  "ptype" : "string",
  "description" : "The delivery mode assigned to all messages that arrive at the destination regardless of the DeliveryMode specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#DeliveryMode"
  } ],
  "lastUpdated" : 1403251677781
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.params.priority",
  "ptype" : "integer",
  "description" : "The priority assigned to all messages that arrive at this destination, regardless of the Priority specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#Priority"
  } ],
  "lastUpdated" : 1403251703255
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.params.redeliverydelay",
  "ptype" : "integer",
  "description" : "The delay, in milliseconds, before rolled back or recovered messages are redelivered, regardless of the RedeliveryDelay specified by the consumer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#RedeliveryDelay"
  } ],
  "lastUpdated" : 1403251730723
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.params.timetodeliver",
  "ptype" : "string",
  "description" : "The default delay, either in milliseconds or as a schedule, between when a message is produced and when it is made visible on its target destination, regardless of the delivery time specified by the producer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToDeliver"
  } ],
  "lastUpdated" : 1403251760977
}, {
  "name" : "jms.uniform.distributed.queue.0.delivery.params.timetolive",
  "ptype" : "integer",
  "description" : "The time-to-live assigned to all messages that arrive at this destination, regardless of the TimeToLive value specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToLive"
  } ],
  "lastUpdated" : 1403251796437
}, {
  "name" : "jms.uniform.distributed.queue.0.insertionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message insertion is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#InsertionPausedAtStartup"
  } ],
  "lastUpdated" : 1403251558971
}, {
  "name" : "jms.uniform.distributed.queue.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS uniform distributed queue JNDI name.",
  "deprecated" : false,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#JNDIName"
  } ],
  "lastUpdated" : 1403249424368
}, {
  "name" : "jms.uniform.distributed.queue.0.loadbalancing.ramdom",
  "ptype" : "boolean",
  "description" : "Determines how messages are distributed to the members of this destination. A value of 'false' means the Round-Robin (default) policy will be used while a value of 'true' means the Random policy will be used.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#LoadBalancingPolicy"
  } ],
  "lastUpdated" : 1403249787888
}, {
  "name" : "jms.uniform.distributed.queue.0.message.logging.enabled",
  "ptype" : "boolean",
  "description" : "Specifies whether the module logs information about the message life cycle.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingEnabled"
  } ],
  "lastUpdated" : 1403251881573
}, {
  "name" : "jms.uniform.distributed.queue.0.message.logging.format",
  "ptype" : "string",
  "description" : "Defines which information about the message is logged.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingFormat"
  } ],
  "lastUpdated" : 1403251914566
}, {
  "name" : "jms.uniform.distributed.queue.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this uniform distributed queue.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#UniformDistributedQueues"
  } ],
  "lastUpdated" : 1403249305850
}, {
  "name" : "jms.uniform.distributed.queue.0.name",
  "ptype" : "string",
  "description" : "JMS uniform distributed queue name.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#Name"
  } ],
  "lastUpdated" : 1403249372486
}, {
  "name" : "jms.uniform.distributed.queue.0.productionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message production is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#ProductionPausedAtStartup"
  } ],
  "lastUpdated" : 1403251466620
}, {
  "name" : "jms.uniform.distributed.queue.0.subdeployment.name",
  "ptype" : "string",
  "description" : "JMS uniform distributed queue sub deployment.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedQueueBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1403249666167
}, {
  "name" : "jms.uniform.distributed.queue.items",
  "ptype" : "integer",
  "description" : "Number of JMS uniform distributed queues to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403162946694
}, {
  "name" : "jms.uniform.distributed.topic.0.attachsender",
  "ptype" : "string",
  "description" : "Specifies whether messages landing on this destination should attach the credential of the sending user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#AttachSender"
  } ],
  "lastUpdated" : 1403252474600
}, {
  "name" : "jms.uniform.distributed.topic.0.consumptionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether consumption is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#ConsumptionPausedAtStartup"
  } ],
  "lastUpdated" : 1403252447248
}, {
  "name" : "jms.uniform.distributed.topic.0.default.target.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether this JMS resource defaults to the parent module's targeting or uses the subdeployment targeting mechanism.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#DefaultTargetingEnabled"
  } ],
  "lastUpdated" : 1403252412869
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.failure.expiration.data",
  "ptype" : "string",
  "description" : "The message Expiration Policy to use when an expired message is encountered on a destination. The value of property jms.uniform.distributed.topic.0.delivery.failure.expiration.data determines the data for the policy: either the error message destination for 'Redirect' or the logging policy for 'Log'.",
  "legalValues" : [ "Redirect", "Log" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationPolicy"
  } ],
  "lastUpdated" : 1403252382764
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.failure.expiration.policy",
  "ptype" : "string",
  "description" : "The message Expiration Policy to use when an expired message is encountered on a destination. The value of property jms.uniform.distributed.topic.0.delivery.failure.expiration.data determines the data for the policy: either the error message destination for 'Redirect' or the logging policy for 'Log'.",
  "legalValues" : [ "Redirect", "Log" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#ExpirationPolicy"
  } ],
  "lastUpdated" : 1403252348887
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.failure.redelivery.limit",
  "ptype" : "integer",
  "description" : "The number of redelivery tries a message can have before it is moved to the error destination.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryFailureParamsBean.html#RedeliveryLimit"
  } ],
  "lastUpdated" : 1403252324774
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.params.deliverymode",
  "ptype" : "string",
  "description" : "The delivery mode assigned to all messages that arrive at the destination regardless of the DeliveryMode specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#DeliveryMode"
  } ],
  "lastUpdated" : 1403252297912
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.params.priority",
  "ptype" : "integer",
  "description" : "The priority assigned to all messages that arrive at this destination, regardless of the Priority specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#Priority"
  } ],
  "lastUpdated" : 1403252281955
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.params.redeliverydelay",
  "ptype" : "integer",
  "description" : "The delay, in milliseconds, before rolled back or recovered messages are redelivered, regardless of the RedeliveryDelay specified by the consumer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#RedeliveryDelay"
  } ],
  "lastUpdated" : 1403252264245
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.params.timetodeliver",
  "ptype" : "string",
  "description" : "The default delay, either in milliseconds or as a schedule, between when a message is produced and when it is made visible on its target destination, regardless of the delivery time specified by the producer and/or connection factory.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToDeliver"
  } ],
  "lastUpdated" : 1403252242214
}, {
  "name" : "jms.uniform.distributed.topic.0.delivery.params.timetolive",
  "ptype" : "integer",
  "description" : "The time-to-live assigned to all messages that arrive at this destination, regardless of the TimeToLive value specified by the message producer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/DeliveryParamsOverridesBean.html#TimeToLive"
  } ],
  "lastUpdated" : 1403252225782
}, {
  "name" : "jms.uniform.distributed.topic.0.insertionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message insertion is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#InsertionPausedAtStartup"
  } ],
  "lastUpdated" : 1403252208121
}, {
  "name" : "jms.uniform.distributed.topic.0.jndi.name",
  "ptype" : "string",
  "description" : "JMS uniform distributed topic JNDI name.",
  "deprecated" : false,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#JNDIName"
  } ],
  "lastUpdated" : 1403252172157
}, {
  "name" : "jms.uniform.distributed.topic.0.loadbalancing.ramdom",
  "ptype" : "boolean",
  "description" : "Determines how messages are distributed to the members of this destination. A value of 'false' means the Round-Robin (default) policy will be used while a value of 'true' means the Random policy will be used.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#LoadBalancingPolicy"
  } ],
  "lastUpdated" : 1403252148184
}, {
  "name" : "jms.uniform.distributed.topic.0.message.logging.enabled",
  "ptype" : "boolean",
  "description" : "Specifies whether the module logs information about the message life cycle.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingEnabled"
  } ],
  "lastUpdated" : 1403252124595
}, {
  "name" : "jms.uniform.distributed.topic.0.message.logging.format",
  "ptype" : "string",
  "description" : "Defines which information about the message is logged.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MessageLoggingParamsBean.html#MessageLoggingFormat"
  } ],
  "lastUpdated" : 1403252106576
}, {
  "name" : "jms.uniform.distributed.topic.0.module.name",
  "ptype" : "string",
  "description" : "The JMS module hosting this uniform distributed topic.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JMSBean.html#UniformDistributedTopics"
  } ],
  "lastUpdated" : 1403252092470
}, {
  "name" : "jms.uniform.distributed.topic.0.name",
  "ptype" : "string",
  "description" : "JMS uniform distributed topic name.",
  "deprecated" : true,
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#Name"
  } ],
  "lastUpdated" : 1403252049776
}, {
  "name" : "jms.uniform.distributed.topic.0.productionpaused.atstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether new message production is paused on a destination at startup.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#ProductionPausedAtStartup"
  } ],
  "lastUpdated" : 1403252021142
}, {
  "name" : "jms.uniform.distributed.topic.0.subdeployment.name",
  "ptype" : "string",
  "description" : "JMS uniform distributed topic sub deployment.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/UniformDistributedTopicBean.html#SubDeploymentName"
  } ],
  "lastUpdated" : 1403251987130
}, {
  "name" : "jms.uniform.distributed.topic.items",
  "ptype" : "integer",
  "description" : "Number of JMS uniform distributed topics to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403251947262
}, {
  "name" : "machine.0.name",
  "ptype" : "string",
  "description" : "Machine name.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain", "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MachineMBean.html#Name"
  } ],
  "lastUpdated" : 1400232155180
}, {
  "name" : "machine.0.node.manager.listen.address",
  "ptype" : "string",
  "description" : "The listen address for the node manager instance running on this machine.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain", "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/NodeManagerMBean.html#ListenAddress"
  } ],
  "lastUpdated" : 1400232637965
}, {
  "name" : "machine.0.node.manager.listen.port",
  "ptype" : "integer",
  "description" : "The listen port for the node manager instance running on this machine.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain", "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/NodeManagerMBean.html#ListenPort"
  } ],
  "lastUpdated" : 1400232725042
}, {
  "name" : "machine.0.rename.from",
  "ptype" : "string",
  "description" : "This property is used for renaming existing machines. It should contain the name of an existing machine to rename.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain", "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400232909375
}, {
  "name" : "machine.0.type.unix",
  "ptype" : "boolean",
  "description" : "Determines whether the machine is a Unix machine.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain", "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400232346246
}, {
  "name" : "machine.items",
  "ptype" : "integer",
  "description" : "Number of machines to process.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "domain", "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400228028848
}, {
  "name" : "mail.session.0.jndi.name",
  "ptype" : "string",
  "description" : "Mail session JNDI name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "mail" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MailSessionMBean.html#JNDIName"
  } ],
  "lastUpdated" : 1400251055857
}, {
  "name" : "mail.session.0.name",
  "ptype" : "string",
  "description" : "Mail session name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "mail" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MailSessionMBean.html#Name"
  } ],
  "lastUpdated" : 1400251000582
}, {
  "name" : "mail.session.0.property.file",
  "ptype" : "string",
  "description" : "Path to the properties file containing the JavaMail properties. See the JavaMail api for more information.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "mail" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/MailSessionMBean.html#Properties"
  } ],
  "lastUpdated" : 1400251329705
}, {
  "name" : "mail.session.0.targets",
  "ptype" : "list[string]",
  "description" : "Mail session target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "mail" ],
  "links" : [ ],
  "lastUpdated" : 1400251536534
}, {
  "name" : "mail.session.items",
  "ptype" : "integer",
  "description" : "Number of mail sessions to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "mail" ],
  "links" : [ ],
  "lastUpdated" : 1400250553429
}, {
  "name" : "managed.server.0.listen.address",
  "ptype" : "string",
  "description" : "The IP address or DNS name this server uses to listen for incoming connections.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#ListenAddress"
  } ],
  "lastUpdated" : 1400504796662
}, {
  "name" : "managed.server.0.listen.port",
  "ptype" : "integer",
  "description" : "The default TCP port that this server uses to listen for regular (non-SSL) incoming connections.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#ListenPort"
  } ],
  "lastUpdated" : 1400504774194
}, {
  "name" : "managed.server.0.log.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether to configure logging on the managed server.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400509389541
}, {
  "name" : "managed.server.0.log.file.name",
  "ptype" : "string",
  "description" : "The name of the file that stores current log messages.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileName"
  } ],
  "lastUpdated" : 1400509701438
}, {
  "name" : "managed.server.0.log.rotation.data",
  "ptype" : "string",
  "description" : "Depending on the rotation type specified (TIME or SIZE), this property determines either the time at which rotation is performed or the size that triggers the rotation.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationTime"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileMinSize"
  } ],
  "lastUpdated" : 1400509839442
}, {
  "name" : "managed.server.0.log.rotation.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether to rotate log files.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#NumberOfFilesLimited"
  } ],
  "lastUpdated" : 1400509739378
}, {
  "name" : "managed.server.0.log.rotation.filecount",
  "ptype" : "integer",
  "description" : "The maximum number of log files that the server creates when it rotates the log.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#FileCount"
  } ],
  "lastUpdated" : 1400509878238
}, {
  "name" : "managed.server.0.log.rotation.onstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether a server rotates its log file during its startup cycle. The default value in production mode is false.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotateLogOnStartup"
  } ],
  "lastUpdated" : 1400509931686
}, {
  "name" : "managed.server.0.log.rotation.type",
  "ptype" : "string",
  "description" : "The log rotation type. Either no rotation (NONE), by time (TIME) or by size (SIZE). Use the admin.server.log.rotation.data property to specify the time or size threshold.",
  "legalValues" : [ "NONE", "TIME", "SIZE" ],
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#RotationType"
  } ],
  "lastUpdated" : 1400509791268
}, {
  "name" : "managed.server.0.log.severity",
  "ptype" : "string",
  "description" : "Managed server logger severity level.",
  "legalValues" : [ "Trace", "Debug", "Info", "Notice", "Warning" ],
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#LoggerSeverity"
  } ],
  "lastUpdated" : 1400509645527
}, {
  "name" : "managed.server.0.machine.name",
  "ptype" : "string",
  "description" : "Name of the WebLogic Server host computer (machine) on which this server is meant to run.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#Machine"
  } ],
  "lastUpdated" : 1400505509172
}, {
  "name" : "managed.server.0.name",
  "ptype" : "string",
  "description" : "Managed server name.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerMBean.html#Name"
  } ],
  "lastUpdated" : 1400504441661
}, {
  "name" : "managed.server.0.overload.maxstuckthreadtime",
  "ptype" : "integer",
  "description" : "The number of seconds that a thread must be continually working before this server diagnoses the thread as being stuck.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerFailureTriggerMBean.html#MaxStuckThreadTime"
  } ],
  "lastUpdated" : 1439797480103
}, {
  "name" : "managed.server.0.overload.stuckthreadcount",
  "ptype" : "integer",
  "description" : "The number of stuck threads after which the server is transitioned into FAILED state.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerFailureTriggerMBean.html#StuckThreadCount"
  } ],
  "lastUpdated" : 1439797489763
}, {
  "name" : "managed.server.0.rename.from",
  "ptype" : "string",
  "description" : "The name of the existing server instance to be renamed.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400505840654
}, {
  "name" : "managed.server.0.servergroups",
  "ptype" : "list[string]",
  "description" : "The server groups this server belongs to.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLSTC/reference.htm#WLSTC3485"
  } ],
  "lastUpdated" : 1400508752441
}, {
  "name" : "managed.server.0.serverstart.arguments",
  "ptype" : "string",
  "description" : "The arguments to use when starting this server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#Arguments"
  } ],
  "lastUpdated" : 1400508752441
}, {
  "name" : "managed.server.0.serverstart.classpath",
  "ptype" : "string",
  "description" : "The classpath (path on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#ClassPath"
  } ],
  "lastUpdated" : 1400508578273
}, {
  "name" : "managed.server.0.serverstart.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether server start will be configured.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400508318305
}, {
  "name" : "managed.server.0.serverstart.javahome",
  "ptype" : "string",
  "description" : "The Java home directory (path on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#JavaHome"
  } ],
  "lastUpdated" : 1400508626987
}, {
  "name" : "managed.server.0.serverstart.javavendor",
  "ptype" : "string",
  "description" : "The Java Vendor value to use when starting this server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#JavaVendor"
  } ],
  "lastUpdated" : 1400508703404
}, {
  "name" : "managed.server.0.serverstart.password",
  "ptype" : "string",
  "description" : "The password for the user to boot the remote server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#PasswordEncrypted"
  } ],
  "lastUpdated" : 1400508535375
}, {
  "name" : "managed.server.0.serverstart.policyfile",
  "ptype" : "string",
  "description" : "The security policy file (directory and filename on the machine running Node Manager) to use when starting this server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#SecurityPolicyFile"
  } ],
  "lastUpdated" : 1400508979434
}, {
  "name" : "managed.server.0.serverstart.rootdir",
  "ptype" : "string",
  "description" : "The directory that this server uses as its root directory.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#RootDirectory"
  } ],
  "lastUpdated" : 1400508785720
}, {
  "name" : "managed.server.0.serverstart.username",
  "ptype" : "string",
  "description" : "The user name to use when booting the server.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/ServerStartMBean.html#Username"
  } ],
  "lastUpdated" : 1400508489379
}, {
  "name" : "managed.server.0.ssl.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether SSL is enabled for this server.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400505046117
}, {
  "name" : "managed.server.0.ssl.listen.port",
  "ptype" : "integer",
  "description" : "The TCP/IP port at which this server listens for SSL connection requests.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/SSLMBean.html#ListenPort"
  } ],
  "lastUpdated" : 1400505223304
}, {
  "name" : "managed.server.0.webserver.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether the web server for this managed server should be configured.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400506009098
}, {
  "name" : "managed.server.0.webserver.log.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether logging for the web server should be enabled.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/WebServerMBean.html#WebServerLog"
  } ],
  "lastUpdated" : 1400506304105
}, {
  "name" : "managed.server.0.webserver.log.file.name",
  "ptype" : "string",
  "description" : "The name of the log file.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/WebServerLogMBean.html#FileName"
  } ],
  "lastUpdated" : 1400506455581
}, {
  "name" : "managed.server.0.webserver.log.rotation.data",
  "ptype" : "string",
  "description" : "Depending on the rotation type specified (TIME or SIZE), this property determines either the time at which rotation is performed or the size that triggers the rotation.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/WebServerLogMBean.html#FileMinSize"
  }, {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/WebServerLogMBean.html#RotationTime"
  } ],
  "lastUpdated" : 1400507144582
}, {
  "name" : "managed.server.0.webserver.log.rotation.enable",
  "ptype" : "boolean",
  "description" : "Specifies whether to rotate log files.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400506709009
}, {
  "name" : "managed.server.0.webserver.log.rotation.filecount",
  "ptype" : "integer",
  "description" : "The maximum number of log files that the server creates when it rotates the log.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/WebServerLogMBean.html#FileCount"
  } ],
  "lastUpdated" : 1400507258945
}, {
  "name" : "managed.server.0.webserver.log.rotation.onstartup",
  "ptype" : "boolean",
  "description" : "Specifies whether a server rotates its log file during its startup cycle. The default value in production mode is false.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/WebServerLogMBean.html#RotateLogOnStartup"
  } ],
  "lastUpdated" : 1400507394328
}, {
  "name" : "managed.server.0.webserver.log.rotation.type",
  "ptype" : "string",
  "description" : "The log rotation type. Either no rotation (NONE), by time (TIME) or by size (SIZE). Use the managed.server.webserver.log.rotation.data property to specify the time or size threshold.",
  "legalValues" : [ "NONE", "TIME", "SIZE" ],
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400506970341
}, {
  "name" : "managed.server.items",
  "ptype" : "integer",
  "description" : "Number of managed servers to process.",
  "scripts" : [ "Importer.py", "domainCreator.py" ],
  "files" : [ "Importer.py", "domainCreator.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1400504046502
}, {
  "name" : "managed.webserver.0.log.rotation.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether to limit the number of log files that this server instance creates to store old messages.",
  "scripts" : [ "domainCreator.py" ],
  "files" : [ "domainCreator.py" ],
  "tags" : [ "domain" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/LogMBean.html#NumberOfFilesLimited"
  } ],
  "lastUpdated" : 1405410623738
}, {
  "name" : "persistent.filestore.0.location",
  "ptype" : "string",
  "description" : "The path name to the file system directory where the file store maintains its data files.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/FileStoreMBean.html#Directory"
  } ],
  "lastUpdated" : 1400502883707
}, {
  "name" : "persistent.filestore.0.name",
  "ptype" : "string",
  "description" : "Persistent file store name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/FileStoreMBean.html#Name"
  } ],
  "lastUpdated" : 1400502718967
}, {
  "name" : "persistent.filestore.0.target",
  "ptype" : "list[string]",
  "description" : "Persistent file store targets.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1400502958793
}, {
  "name" : "persistent.filestore.items",
  "ptype" : "integer",
  "description" : "Number of file stores to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1400502640783
}, {
  "name" : "persistent.jdbcstore.0.datasource",
  "ptype" : "string",
  "description" : "Name of the JDBC data source used by this JDBC store to access its backing table.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCStoreMBean.html#DataSource"
  } ],
  "lastUpdated" : 1400503430444
}, {
  "name" : "persistent.jdbcstore.0.name",
  "ptype" : "string",
  "description" : "Persistent jdbc store name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCStoreMBean.html#Name"
  } ],
  "lastUpdated" : 1400503165014
}, {
  "name" : "persistent.jdbcstore.0.table.prefix",
  "ptype" : "string",
  "description" : "The prefix for the JDBC store's database table.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1212/wls/WLMBR/mbeans/JDBCStoreMBean.html#PrefixName"
  } ],
  "lastUpdated" : 1400503675153
}, {
  "name" : "persistent.jdbcstore.0.target",
  "ptype" : "list[string]",
  "description" : "Persistent jdbc store targets.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1400503233986
}, {
  "name" : "persistent.jdbcstore.items",
  "ptype" : "integer",
  "description" : "Number of persistent jdbc stores to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "store" ],
  "links" : [ ],
  "lastUpdated" : 1400503040602
}, 
{
  "name" : "remove.domain.shutdown.class.list",
  "ptype" : "list[string]",
  "description" : "The list of ShutdownClass name(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480913326
},
{
  "name" : "remove.domain.startup.class.list",
  "ptype" : "list[string]",
  "description" : "The list of StartupClass name(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "domain" ],
  "links" : [ ],
  "lastUpdated" : 1442480703889
}, {
  "name" : "remove.jdbc.resource.list",
  "ptype" : "list[string]",
  "description" : "The list of JDBC data sources to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jdbc" ],
  "links" : [ ],
  "lastUpdated" : 1403860616749
}, {
  "name" : "remove.jms.connectionfactory.0.list",
  "ptype" : "list[string]",
  "description" : "The list of connection factories to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861670363
}, {
  "name" : "remove.jms.connectionfactory.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the connection factories to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861654134
}, {
  "name" : "remove.jms.connectionfactory.items",
  "ptype" : "integer",
  "description" : "Number of JMS connection factory items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861621744
}, {
  "name" : "remove.jms.distriqueue.0.list",
  "ptype" : "list[string]",
  "description" : "The list of distributed queues to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403882843376
}, {
  "name" : "remove.jms.distriqueue.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the distributed queue(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403882825977
}, {
  "name" : "remove.jms.distriqueue.items",
  "ptype" : "integer",
  "description" : "Number of JMS distributed queue items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403882806769
}, {
  "name" : "remove.jms.distritopic.0.list",
  "ptype" : "list[string]",
  "description" : "The list of distributed topics to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403882922836
}, {
  "name" : "remove.jms.distritopic.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the distributed topic(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403882902072
}, {
  "name" : "remove.jms.distritopic.items",
  "ptype" : "integer",
  "description" : "Number of JMS distributed topic items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403882881925
}, {
  "name" : "remove.jms.foreign.server.0.list",
  "ptype" : "list[string]",
  "description" : "The list of JMS foreign servers to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1407334134394
}, {
  "name" : "remove.jms.foreign.server.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the foreign server to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1407334045748
}, {
  "name" : "remove.jms.foreign.server.items",
  "ptype" : "integer",
  "description" : "Number of JMS foreign server items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1407334000712
}, {
  "name" : "remove.jms.module.0.list",
  "ptype" : "list[string]",
  "description" : "The list of JMS modules to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861849518
}, {
  "name" : "remove.jms.module.items",
  "ptype" : "integer",
  "description" : "Number of JMS module items to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861827962
}, {
  "name" : "remove.jms.queue.0.list",
  "ptype" : "list[string]",
  "description" : "The list of queues to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861524150
}, {
  "name" : "remove.jms.queue.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the queue(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861499639
}, {
  "name" : "remove.jms.queue.items",
  "ptype" : "integer",
  "description" : "Number of JMS queue items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861440777
}, {
  "name" : "remove.jms.saf.agent.list",
  "ptype" : "list[string]",
  "description" : "The list of JMS SAF agents to remove.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884315897
}, {
  "name" : "remove.jms.saf.errorhandling.0.list",
  "ptype" : "list[string]",
  "description" : "The list of SAF error handlings to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884891883
}, {
  "name" : "remove.jms.saf.errorhandling.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the SAF error handling(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884855330
}, {
  "name" : "remove.jms.saf.errorhandling.items",
  "ptype" : "integer",
  "description" : "Number of JMS SAF error handling items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884835057
}, {
  "name" : "remove.jms.saf.importeddestination.0.list",
  "ptype" : "list[string]",
  "description" : "The list of SAF imported destinations to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884968557
}, {
  "name" : "remove.jms.saf.importeddestination.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the SAF imported destination(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884943241
}, {
  "name" : "remove.jms.saf.importeddestination.items",
  "ptype" : "integer",
  "description" : "Number of JMS SAF imported destination items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884926324
}, {
  "name" : "remove.jms.saf.queue.0.importeddestination",
  "ptype" : "string",
  "description" : "The JMS SAF imported destination owning the SAF queue(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884534696
}, {
  "name" : "remove.jms.saf.queue.0.list",
  "ptype" : "list[string]",
  "description" : "The list of SAF queues to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884508864
}, {
  "name" : "remove.jms.saf.queue.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the SAF queue(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884494819
}, {
  "name" : "remove.jms.saf.queue.items",
  "ptype" : "integer",
  "description" : "Number of JMS SAF queue items belonging to a specific module and imported destination to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884461477
}, {
  "name" : "remove.jms.saf.remotecontext.0.list",
  "ptype" : "list[string]",
  "description" : "The list of SAF remote contexts to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884768705
}, {
  "name" : "remove.jms.saf.remotecontext.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the SAF remote context(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884751931
}, {
  "name" : "remove.jms.saf.remotecontext.items",
  "ptype" : "integer",
  "description" : "Number of JMS SAF remote context items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884732251
}, {
  "name" : "remove.jms.saf.topic.0.importeddestination",
  "ptype" : "string",
  "description" : "The JMS SAF imported destination owning the SAF topic(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884598907
}, {
  "name" : "remove.jms.saf.topic.0.list",
  "ptype" : "list[string]",
  "description" : "The list of SAF topics to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884583080
}, {
  "name" : "remove.jms.saf.topic.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the SAF topic(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884572281
}, {
  "name" : "remove.jms.saf.topic.items",
  "ptype" : "integer",
  "description" : "Number of JMS SAF topic items belonging to a specific module and imported destination to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403884561837
}, {
  "name" : "remove.jms.server.list",
  "ptype" : "list[string]",
  "description" : "The list of JMS servers to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861748612
}, {
  "name" : "remove.jms.topic.0.list",
  "ptype" : "list[string]",
  "description" : "The list of topics to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861580496
}, {
  "name" : "remove.jms.topic.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the topic(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861564766
}, {
  "name" : "remove.jms.topic.items",
  "ptype" : "integer",
  "description" : "Number of JMS topic items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403861543623
}, {
  "name" : "remove.jms.unidistriqueue.0.list",
  "ptype" : "list[string]",
  "description" : "The list of uniform distributed queues to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403883453754
}, {
  "name" : "remove.jms.unidistriqueue.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the uniform distributed queue(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403883415841
}, {
  "name" : "remove.jms.unidistriqueue.items",
  "ptype" : "integer",
  "description" : "Number of JMS uniform distributed queue items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403883380653
}, {
  "name" : "remove.jms.unidistritopic.0.list",
  "ptype" : "list[string]",
  "description" : "The list of uniform distributed topics to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403883526719
}, {
  "name" : "remove.jms.unidistritopic.0.module",
  "ptype" : "string",
  "description" : "The JMS module owning the uniform distributed topic(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403883498461
}, {
  "name" : "remove.jms.unidistritopic.items",
  "ptype" : "integer",
  "description" : "Number of JMS uniform distributed topic items belonging to a specific module to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "jms" ],
  "links" : [ ],
  "lastUpdated" : 1403883484688
}, {
  "name" : "remove.mail.session.list",
  "ptype" : "list[string]",
  "description" : "The list of mail sessions to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "mail" ],
  "links" : [ ],
  "lastUpdated" : 1403860665909
}, {
  "name" : "remove.security.group.0.authenticator",
  "ptype" : "string",
  "description" : "The authentication provider owning the group(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861265461
}, {
  "name" : "remove.security.group.0.list",
  "ptype" : "list[string]",
  "description" : "The list of groups to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861287562
}, {
  "name" : "remove.security.group.0.realm",
  "ptype" : "string",
  "description" : "The security realm containing the group(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861246146
}, {
  "name" : "remove.security.group.items",
  "ptype" : "integer",
  "description" : "Number of group items belonging to a specific realm and authentication provider to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861227383
}, {
  "name" : "remove.security.user.0.authenticator",
  "ptype" : "string",
  "description" : "The authentication provider owning the user(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861176648
}, {
  "name" : "remove.security.user.0.list",
  "ptype" : "list[string]",
  "description" : "The list of users to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861206001
}, {
  "name" : "remove.security.user.0.realm",
  "ptype" : "string",
  "description" : "The security realm containing the user(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861114960
}, {
  "name" : "remove.security.user.items",
  "ptype" : "integer",
  "description" : "Number of user items belonging to a specific realm and authentication provider to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1403861087116
}, 
{
  "name" : "remove.server.network.channel.list",
  "ptype" : "list[string]",
  "description" : "The list of NetworkAccessPoint name(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1442828254385
},
{
  "name" : "script.log.file",
  "ptype" : "string",
  "description" : "Optional path to a log file.",
  "scripts" : [ "Importer.py", "domainCreator.py", "Exporter.py", "Remover.py" ],
  "files" : [ "Logger.py" ],
  "tags" : [ "logging" ],
  "links" : [ ],
  "lastUpdated" : 1394011892978
}, {
  "name" : "script.log.level",
  "ptype" : "string",
  "description" : "WLST-API logging level.",
  "defaultValue" : "INFO",
  "legalValues" : [ "DEBUG", "INFO", "WARNING", "ERROR" ],
  "scripts" : [ "Importer.py", "domainCreator.py", "Exporter.py", "Remover.py" ],
  "files" : [ "Logger.py" ],
  "tags" : [ "logging" ],
  "links" : [ ],
  "lastUpdated" : 1394098875186
}, {
  "name" : "security.credentials.0.filepath",
  "ptype" : "string",
  "description" : "Path to the file containing the credentials to import.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396444203425
}, {
  "name" : "security.credentials.0.mapper",
  "ptype" : "string",
  "description" : "The credential mapper to use. The default credential mapper in Weblogic is DefaultCredentialMapper.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396443683706
}, {
  "name" : "security.credentials.0.realm",
  "ptype" : "string",
  "description" : "The realm to be used.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396443528791
}, {
  "name" : "security.credentials.items",
  "ptype" : "integer",
  "description" : "Number of credential items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396443471394
}, {
  "name" : "security.group.member.0.authenticator",
  "ptype" : "string",
  "description" : "The authentication provider for the user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396275218286
}, {
  "name" : "security.group.member.0.groups",
  "ptype" : "list[string]",
  "description" : "Comma separated list of groups where the user/group will be added to.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396275273261
}, {
  "name" : "security.group.member.0.realm",
  "ptype" : "string",
  "description" : "The security realm where the user and group is defined.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396275186476
}, {
  "name" : "security.group.member.0.user",
  "ptype" : "string",
  "description" : "Name of the existing user (or group).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396275110313
}, {
  "name" : "security.group.member.items",
  "ptype" : "integer",
  "description" : "Number of group membership items to create.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396274701022
}, {
  "name" : "security.groups.0.authenticator",
  "ptype" : "string",
  "description" : "The authentication provider for the group.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396273207357
}, {
  "name" : "security.groups.0.description",
  "ptype" : "string",
  "description" : "Description of the group to create.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396273318166
}, {
  "name" : "security.groups.0.name",
  "ptype" : "string",
  "description" : "Name for the new group.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396273277943
}, {
  "name" : "security.groups.0.realm",
  "ptype" : "string",
  "description" : "The security realm where the group will be created.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396273166264
}, {
  "name" : "security.groups.items",
  "ptype" : "integer",
  "description" : "Number of groups to create.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396273123605
}, {
  "name" : "security.policies.0.authorizer",
  "ptype" : "string",
  "description" : "The authorization provider for the policies. The default one in Weblogic is XACMLAuthorizer.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396427320212
}, {
  "name" : "security.policies.0.bulk.filepath",
  "ptype" : "string",
  "description" : "Path to the (XACML) file containing the policies to import.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396364867372
}, {
  "name" : "security.policies.0.bulk.removeAll",
  "ptype" : "boolean",
  "description" : "Indicates whether to clear the current policies before importing them.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396364112381
}, {
  "name" : "security.policies.0.expression",
  "ptype" : "string",
  "description" : "The expression that determines the type of access this policy allows to the resource.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396362421253
}, {
  "name" : "security.policies.0.expression.update.mode",
  "ptype" : "string",
  "description" : "The mode used to determine how to update the expression.",
  "defaultValue" : "OR",
  "legalValues" : [ "OR", "AND", "REPLACE" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396363607984
}, {
  "name" : "security.policies.0.mode",
  "ptype" : "string",
  "description" : "The mode which determines the action on the policy: either CREATE, UPDATE, BULK or READ for listing the policies.",
  "legalValues" : [ "CREATE", "UPDATE", "READ", "BULK" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396361574199
}, {
  "name" : "security.policies.0.realm",
  "ptype" : "string",
  "description" : "The realm to be used.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396363308785
}, {
  "name" : "security.policies.0.resource",
  "ptype" : "string",
  "description" : "the resource identifier concerned by the policy.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396362277456
}, {
  "name" : "security.policies.items",
  "ptype" : "integer",
  "description" : "Number of policies to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396356065928
}, {
  "name" : "security.roles.0.bulk.filepath",
  "ptype" : "string",
  "description" : "Path to the (XACML) file containing the roles to import.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396430193219
}, {
  "name" : "security.roles.0.bulk.removeAll",
  "ptype" : "boolean",
  "description" : "Indicates whether to clear the current roles before importing them.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396430230843
}, {
  "name" : "security.roles.0.expression",
  "ptype" : "string",
  "description" : "The expression for this role.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396428395628
}, {
  "name" : "security.roles.0.expression.update.mode",
  "ptype" : "string",
  "description" : "The mode used to determine how to update the expression.",
  "defaultValue" : "OR",
  "legalValues" : [ "OR", "AND", "REPLACE" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396430116596
}, {
  "name" : "security.roles.0.mapper",
  "ptype" : "string",
  "description" : "The role mapper to use. The default role mapper in Weblogic is XACMLRoleMapper.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396427281811
}, {
  "name" : "security.roles.0.mode",
  "ptype" : "string",
  "description" : "The mode which determines the action on the role: either CREATE, UPDATE, BULK or READ.",
  "legalValues" : [ "CREATE", "UPDATE", "BULK", "READ" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396427030871
}, {
  "name" : "security.roles.0.name",
  "ptype" : "string",
  "description" : "The role name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396427088407
}, {
  "name" : "security.roles.0.realm",
  "ptype" : "string",
  "description" : "The realm to be used.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396427138731
}, {
  "name" : "security.roles.0.resource",
  "ptype" : "string",
  "description" : "The resource identifier targeted by the role.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396428346332
}, {
  "name" : "security.roles.items",
  "ptype" : "integer",
  "description" : "Number of roles to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396425642147
}, {
  "name" : "security.users.0.authenticator",
  "ptype" : "string",
  "description" : "The authentication provider for the user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396258401672
}, {
  "name" : "security.users.0.comment",
  "ptype" : "string",
  "description" : "Comment for the user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396258063741
}, {
  "name" : "security.users.0.name",
  "ptype" : "string",
  "description" : "Name of the user to create.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396257802111
}, {
  "name" : "security.users.0.password",
  "ptype" : "string",
  "description" : "The password to set for the new user.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396257787616
}, {
  "name" : "security.users.0.realm",
  "ptype" : "string",
  "description" : "The security realm where the user will be created.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396258364486
}, {
  "name" : "security.users.0.update",
  "ptype" : "boolean",
  "description" : "Indicates whether to update the user if it already exists.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396270127305
}, {
  "name" : "security.users.0.update.bootproperties",
  "ptype" : "boolean",
  "description" : "Indicate whether to update the boot.properties file(s).",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396272234104
}, {
  "name" : "security.users.items",
  "ptype" : "integer",
  "description" : "Number of users to create.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "security" ],
  "links" : [ ],
  "lastUpdated" : 1396272250344
}, 
{
  "name" : "server.network.channel.items",
  "ptype" : "integer",
  "description" : "Number of NetworkAccessPoint items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1442828254349
},
{
  "name" : "server.network.channel.0.name",
  "ptype" : "string",
  "description" : "The name of this network channel.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#Name"
  } ],
  "lastUpdated" : 1442828254358
},
{
  "name" : "server.network.channel.0.channel.identity.customized",
  "ptype" : "boolean",
  "description" : "Whether or not the channel's custom identity should be used. This setting only has an effect if the server is using a customized keystore. By default the channel's identity is inherited from the server's identity.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#ChannelIdentityCustomized"
  } ],
  "lastUpdated" : 1442828254360
},
{
  "name" : "server.network.channel.0.client.certificate.enforced",
  "ptype" : "boolean",
  "description" : "Specifies whether clients must present digital certificates from a trusted certificate authority to WebLogic Server on this channel.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#ClientCertificateEnforced"
  } ],
  "lastUpdated" : 1442828254363
},
{
  "name" : "server.network.channel.0.enabled",
  "ptype" : "boolean",
  "description" : "Specifies whether this channel should be started.",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#Enabled"
  } ],
  "lastUpdated" : 1442828254365
},
{
  "name" : "server.network.channel.0.listen.address",
  "ptype" : "string",
  "description" : "The IP address or DNS name this network channel uses to listen for incoming connections. A value of null indicates that the network channel should obtain this value from the server's configuration.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#ListenAddress"
  } ],
  "lastUpdated" : 1442828254368
},
{
  "name" : "server.network.channel.0.listen.port",
  "ptype" : "integer",
  "description" : "The default TCP port this network channel uses to listen for regular (non-SSL) incoming connections. A value of -1 indicates that the network channel should obtain this value from the server's configuration.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#ListenPort"
  } ],
  "lastUpdated" : 1442828254370
},
{
  "name" : "server.network.channel.0.protocol",
  "ptype" : "string",
  "description" : "The protocol this network channel should use for connections.",
  "defaultValue" : "t3",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#Protocol"
  } ],
  "lastUpdated" : 1442828254373
},
{
  "name" : "server.network.channel.0.public.address",
  "ptype" : "string",
  "description" : "The IP address or DNS name representing the external identity of this network channel. A value of null indicates that the network channel's Listen Address is also its external address. If the Listen Address is null,the network channel obtains its external identity from the server's configuration.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#PublicAddress"
  } ],
  "lastUpdated" : 1442828254376
},
{
  "name" : "server.network.channel.0.public.port",
  "ptype" : "integer",
  "description" : "The externally published listen port for this network channel. A value of -1 indicates that the network channel's Listen Port is also its public listen port. If the Listen Port is -1,the network channel obtains its public listen port from the server's configuration.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#PublicPort"
  } ],
  "lastUpdated" : 1442828254379
},
{
  "name" : "server.network.channel.0.server.name",
  "ptype" : "string",
  "description" : "The name of the Server to which this NetworkAccessPoint belongs.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1442828254381
},
{
  "name" : "server.network.channel.0.two.way.ssl.enabled",
  "ptype" : "boolean",
  "description" : "Specifies whether this network channel uses two way SSL.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/NetworkAccessPointMBean.html#TwoWaySSLEnabled"
  } ],
  "lastUpdated" : 1442828254384
},
{
  "name" : "weblogic.home",
  "ptype" : "string",
  "description" : "Special property set by the WLST-API to the value of the 'wlserver' directory under the weblogic (middleware) home directory. DO NOT OVERRIDE !",
  "scripts" : [ "Importer.py", "domainCreator.py", "Exporter.py", "Remover.py" ],
  "files" : [ "Importer.py", "PropertyManager.py" ],
  "tags" : [ ],
  "links" : [ ],
  "lastUpdated" : 1394012086898
},
{
  "name" : "domain.workmanager.items",
  "ptype" : "integer",
  "description" : "Number of WorkManager items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ ],
  "lastUpdated" : 1442997048065
},
{
  "name" : "domain.workmanager.0.name",
  "ptype" : "string",
  "description" : "The name of this WorkManager.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/WorkManagerMBean.html#Name"
  } ],
  "lastUpdated" : 1442997048074
},
{
  "name" : "domain.workmanager.0.ignore.stuck.threads",
  "ptype" : "boolean",
  "description" : "Specifies whether this Work Manager ignores \"stuck\" threads. Typically, stuck threads will cause the associated Work Manager to take some action: either switching the application to Admin mode, shutting down the server, or shutting down the Work Manager. If this flag is set, then no thread in this Work Manager is ever considered stuck.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/WorkManagerMBean.html#IgnoreStuckThreads"
  } ],
  "lastUpdated" : 1442997048076
},{
  "name" : "domain.workmanager.0.targets",
  "ptype" : "list[string]",
  "description" : "WorkManager target(s).",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ ],
  "lastUpdated" : 1442997048077
},
{
  "name" : "domain.workmanager.0.shutdown.trigger.max.stuck.thread.time",
  "ptype" : "integer",
  "description" : "Time after which a executing thread is declared as stuck.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/WorkManagerShutdownTriggerMBean.html#MaxStuckThreadTime"
  } ],
  "lastUpdated" : 1442997120642
},
{
  "name" : "domain.workmanager.0.shutdown.trigger.resume.when.unstuck",
  "ptype" : "boolean",
  "description" : "Whether to resume work manager once the stuck threads were cleared",
  "defaultValue" : "true",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/WorkManagerShutdownTriggerMBean.html#ResumeWhenUnstuck"
  } ],
  "lastUpdated" : 1442997120644
},
{
  "name" : "domain.workmanager.0.shutdown.trigger.stuck.thread.count",
  "ptype" : "integer",
  "description" : "Number of stuck threads after which the WorkManager is shutdown",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/WorkManagerShutdownTriggerMBean.html#StuckThreadCount"
  } ],
  "lastUpdated" : 1442997120647
},
{
  "name" : "domain.workmanager.0.capacity.name",
  "ptype" : "string",
  "description" : "The name of this Capacity.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/CapacityMBean.html#Name"
  } ],
  "lastUpdated" : 1443013321409
},
{
  "name" : "domain.workmanager.0.capacity.count",
  "ptype" : "integer",
  "description" : "Total number of requests that can be enqueued.",
  "defaultValue" : "-1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/CapacityMBean.html#Count"
  } ],
  "lastUpdated" : 1443013321412
},
{
  "name" : "domain.workmanager.0.context.request.class.name",
  "ptype" : "string",
  "description" : "The name of this ContextRequestClass.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ContextRequestClassMBean.html#Name"
  } ],
  "lastUpdated" : 1443085341171
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.items",
  "ptype" : "integer",
  "description" : "Number of ContextCase items to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ ],
  "lastUpdated" : 1443097408441
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.name",
  "ptype" : "string",
  "description" : "The name of this ContextCase.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ContextCaseMBean.html#Name"
  } ],
  "lastUpdated" : 1443097408448
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.group.name",
  "ptype" : "string",
  "description" : "The name of the user group whose requests are to be processed by the request class with the name specified in RequestClassName.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ContextCaseMBean.html#GroupName"
  } ],
  "lastUpdated" : 1443097408450
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.user.name",
  "ptype" : "string",
  "description" : "The name of the user whose requests are to be processed by the request class with the name specified in RequestClassName.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ContextCaseMBean.html#UserName"
  } ],
  "lastUpdated" : 1443097408455
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.fair.share.request.class.name",
  "ptype" : "string",
  "description" : "The name of this FairShareRequestClass.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/FairShareRequestClassMBean.html#Name"
  } ],
  "lastUpdated" : 1443166451844
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.fair.share.request.class.fair.share",
  "ptype" : "integer",
  "description" : "Fair share value",
  "defaultValue" : "50",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/FairShareRequestClassMBean.html#FairShare"
  } ],
  "lastUpdated" : 1443166451846
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.response.time.request.class.name",
  "ptype" : "string",
  "description" : "The name of this ResponseTimeRequestClass.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ResponseTimeRequestClassMBean.html#Name"
  } ],
  "lastUpdated" : 1443444373179
},
{
  "name" : "domain.workmanager.0.context.request.class.context.case.0.response.time.request.class.goal.ms",
  "ptype" : "integer",
  "description" : "A response time goal in milliseconds.",
  "defaultValue" : "-1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ResponseTimeRequestClassMBean.html#GoalMs"
  } ],
  "lastUpdated" : 1443444373181
},
{
  "name" : "domain.workmanager.0.fair.share.request.class.name",
  "ptype" : "string",
  "description" : "The name of this FairShareRequestClass.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/FairShareRequestClassMBean.html#Name"
  } ],
  "lastUpdated" : 1443166451844
},
{
  "name" : "domain.workmanager.0.fair.share.request.class.fair.share",
  "ptype" : "integer",
  "description" : "Fair share value",
  "defaultValue" : "50",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/FairShareRequestClassMBean.html#FairShare"
  } ],
  "lastUpdated" : 1443166451846
},
{
  "name" : "domain.workmanager.0.response.time.request.class.name",
  "ptype" : "string",
  "description" : "The name of this ResponseTimeRequestClass.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ResponseTimeRequestClassMBean.html#Name"
  } ],
  "lastUpdated" : 1443444373179
},
{
  "name" : "domain.workmanager.0.response.time.request.class.goal.ms",
  "ptype" : "integer",
  "description" : "A response time goal in milliseconds.",
  "defaultValue" : "-1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ResponseTimeRequestClassMBean.html#GoalMs"
  } ],
  "lastUpdated" : 1443444373181
},
{
  "name" : "domain.workmanager.0.max.threads.constraint.name",
  "ptype" : "string",
  "description" : "The name of this MaxThreadsConstraint.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/MaxThreadsConstraintMBean.html#Name"
  } ],
  "lastUpdated" : 1444031000623
},
{
  "name" : "domain.workmanager.0.max.threads.constraint.connection.pool.name",
  "ptype" : "string",
  "description" : "Name of the connection pool whose size is taken as the max constraint.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/MaxThreadsConstraintMBean.html#ConnectionPoolName"
  } ],
  "lastUpdated" : 1444031000626
},
{
  "name" : "domain.workmanager.0.max.threads.constraint.count",
  "ptype" : "integer",
  "description" : "Maximum number of concurrent threads that can execute requests sharing this constraint.",
  "defaultValue" : "-1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/MaxThreadsConstraintMBean.html#Count"
  } ],
  "lastUpdated" : 1444031000628
},
{
  "name" : "domain.workmanager.0.max.threads.constraint.queue.size",
  "ptype" : "integer",
  "description" : "Desired size of the MaxThreadsConstraint queue for requests pending execution.",
  "defaultValue" : "8192",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/MaxThreadsConstraintMBean.html#QueueSize"
  } ],
  "lastUpdated" : 1444031000631
},
{
  "name" : "domain.workmanager.0.min.threads.constraint.name",
  "ptype" : "string",
  "description" : "The name of this MinThreadsConstraint.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/MinThreadsConstraintMBean.html#Name"
  } ],
  "lastUpdated" : 1444117801905
},
{
  "name" : "domain.workmanager.0.min.threads.constraint.count",
  "ptype" : "integer",
  "description" : "Minimum number of concurrent threads executing requests that share this constraint.",
  "defaultValue" : "-1",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "tuning" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/MinThreadsConstraintMBean.html#Count"
  } ],
  "lastUpdated" : 1444117801908
},
{
  "name" : "export.domain.workmanager.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether resources of type WorkManager should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "tuning" ],
  "links" : [ ],
  "lastUpdated" : 1442997048077
},{
  "name" : "export.domain.workmanager.list",
  "ptype" : "list[string]",
  "description" : "List of WorkManager to export.",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "tuning" ],
  "links" : [ ],
  "lastUpdated" : 1442997048077
},
{
  "name" : "remove.domain.workmanager.list",
  "ptype" : "list[string]",
  "description" : "The list of WorkManager name(s) to be removed.",
  "scripts" : [ "Remover.py" ],
  "files" : [ "Remover.py" ],
  "tags" : [ "tuning" ],
  "links" : [ ],
  "lastUpdated" : 1442997048077
},
{
  "name" : "server.0.ssl.server.private.key.alias",
  "ptype" : "string",
  "description" : "The string alias used to store and retrieve the server's private key in the keystore. This private key is associated with the server's digital certificate.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/SSLMBean.html#ServerPrivateKeyAlias"
  } ],
  "lastUpdated" : 1449762170643
},
{
  "name" : "server.0.ssl.server.private.key.pass.phrase",
  "ptype" : "string",
  "description" : "The passphrase used to retrieve the server's private key from the keystore. This passphrase is assigned to the private key when it is generated.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/SSLMBean.html#ServerPrivateKeyPassPhrase"
  } ],
  "lastUpdated" : 1449762170645
},
{
  "name" : "server.0.custom.identity.key.store.file.name",
  "ptype" : "string",
  "description" : "The source of the identity keystore. For a JKS keystore, the source is the path and file name. For an Oracle Key Store Service (KSS) keystore, the source is the KSS URI.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#CustomIdentityKeyStoreFileName"
  } ],
  "lastUpdated" : 1450082773051
},
{
  "name" : "server.0.custom.identity.key.store.pass.phrase",
  "ptype" : "string",
  "description" : "The encrypted custom identity keystore's passphrase. If empty or null, then the keystore will be opened without a passphrase.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#CustomIdentityKeyStorePassPhrase"
  } ],
  "lastUpdated" : 1450082773053
},
{
  "name" : "server.0.custom.identity.key.store.type",
  "ptype" : "string",
  "description" : "The type of the keystore. Generally, this is JKS. If using the Oracle Key Store Service, this would be KSS",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#CustomIdentityKeyStoreType"
  } ],
  "lastUpdated" : 1450082773055
},
{
  "name" : "server.0.custom.trust.key.store.file.name",
  "ptype" : "string",
  "description" : "The source of the custom trust keystore. For a JKS keystore, the source is the path and file name. For an Oracle Key Store Service (KSS) keystore, the source is the KSS URI.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#CustomTrustKeyStoreFileName"
  } ],
  "lastUpdated" : 1450082773058
},
{
  "name" : "server.0.custom.trust.key.store.pass.phrase",
  "ptype" : "string",
  "description" : "The custom trust keystore's passphrase. If empty or null, then the keystore will be opened without a passphrase.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#CustomTrustKeyStorePassPhrase"
  } ],
  "lastUpdated" : 1450082773060
},
{
  "name" : "server.0.custom.trust.key.store.type",
  "ptype" : "string",
  "description" : "The type of the keystore. Generally, this is JKS. If using the Oracle Key Store Service, this would be KSS",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#CustomTrustKeyStoreType"
  } ],
  "lastUpdated" : 1450082773063
},
{
  "name" : "server.0.key.stores",
  "ptype" : "string",
  "description" : "Which configuration rules should be used for finding the server's identity and trust keystores?",
  "defaultValue" : "DemoIdentityAndDemoTrust",
  "legalValues" : [ "DemoIdentityAndDemoTrust", "CustomIdentityAndJavaStandardTrust", "CustomIdentityAndCustomTrust", "CustomIdentityAndCommandLineTrust" ],
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra", "security" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#KeyStores"
  } ],
  "lastUpdated" : 1450082773065
},
{
  "name" : "export.server.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether servers should be exported.",
  "defaultValue" : "false",
  "scripts" : [ "Exporter.py" ],
  "files" : [ "Exporter.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1405350571584
}, {
  "name" : "server.0.ssl.enable",
  "ptype" : "boolean",
  "description" : "Indicates whether SSL is enabled for this server.",
  "defaultValue" : "false",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1453887521735
}, {
  "name" : "server.0.ssl.listen.port",
  "ptype" : "integer",
  "description" : "The TCP/IP port at which this server listens for SSL connection requests.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/SSLMBean.html#ListenPort"
  } ],
  "lastUpdated" : 1453887529643
}, {
  "name" : "server.items",
  "ptype" : "integer",
  "description" : "Number of servers to process.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ ],
  "lastUpdated" : 1460552113125
}, {
  "name" : "server.0.name",
  "ptype" : "string",
  "description" : "Server name.",
  "scripts" : [ "Importer.py" ],
  "files" : [ "Importer.py" ],
  "tags" : [ "infra" ],
  "links" : [ {
    "url" : "http://docs.oracle.com/middleware/1213/wls/WLMBR/mbeans/ServerMBean.html#Name"
  } ],
  "lastUpdated" : 1460552294500
}

 ]