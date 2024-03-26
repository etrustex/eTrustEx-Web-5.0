package eu.europa.ec.etrustex.web.common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Configuration
@PropertySource({"classpath:application.properties", "classpath:etrustex.web.properties", "classpath:openid.properties"})
@ConfigurationProperties("etrustexweb")
@Slf4j
@Getter
@Setter
@SuppressWarnings("java:S1068")
public class EtrustexWebProperties {
    private String contextPath;
    private String targetServerContextRoot;
    private String fileUploadDir;
    private String environment;
    private String applicationUrl;
    private String redirectUrl;
    private String ldapUrls;
    private String ldapBase;
    private String ldapUserAlias;
    private String ldapUsername;

    private Resource serverCertificate;
    private String serverCertificatePassword;
    private String serverCertificateType;
    private String serverCertificateAlias;

    private Resource oldServerCertificate;
    private String oldServerCertificatePassword;
    private String oldServerCertificateType;
    private String oldServerCertificateAlias;

    private URL tsaUrl;
    private String proxyHost;
    private Integer proxyPort;
    private String proxyUser;
    private String proxyPassword;

    private String functionalMailbox;

    private int newCertificateJobCorePoolSize;
    private int newCertificateJobMaxPoolSize;
    private int newCertificateJobQueueSize;
    private int newCertificateJobJpaPageSize;
    private int newCertificateJobStepChunkSize;
    private int newCertificateJobFirstNotificationDays;
    private int newCertificateJobSecondNotificationDays;

    private String credentialsAesKey;
    private String clientId;
    private String clientSecret;
    private String clientOidId;
    private String clientOidSecret;
    @SuppressWarnings("java:S1068") // Sonar false positive, the generated method is 'is...' and not 'get...'
    private boolean pdfSignatureValidateTimestamp;

    private String euloginAccessTokenUrlIntrospect;

    public Path getFileUploadDirPath() {
        return Paths.get(this.getFileUploadDir());
    }

    public boolean isDevEnvironment() {
        return Objects.equals("dev", environment);
    }

    public boolean isTestEnvironment() {
        return Objects.equals("test", environment);
    }

    public boolean isAccEnvironment() {
        return Objects.equals("acc", environment);
    }

    public boolean isProdEnvironment() {
        return Objects.equals("prod", environment);
    }

}
