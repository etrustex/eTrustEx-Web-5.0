package eu.europa.ec.etrustex.web.service.config;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Credentials;
import eu.europa.ec.etrustex.web.service.CredentialServiceImpl;
import eu.europa.ec.etrustex.web.service.EncryptionServiceImpl;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import eu.europa.ec.etrustex.web.service.template.ThymeleafTemplateResolver;
import eu.europa.ec.etrustex.web.service.template.dialect.AttachmentDialect;
import eu.europa.ec.etrustex.web.service.template.dialect.MessageDialect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Slf4j
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class ServicesConfig {
    private final ThymeleafTemplateResolver templateResolver;
    private final RedirectService redirectService;
    private final EtrustexWebProperties etrustexWebProperties;

    @Bean
    public TemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true); // Compiled SpringEL should speed up executions
        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }

    @Bean
    public AttachmentDialect conditionalAttachmentDialect() {
        return new AttachmentDialect(redirectService);
    }

    @Bean
    public MessageDialect conditionalMessageDialect() {
        return new MessageDialect(redirectService);
    }

    @Bean
    LdapContextSource contextSource(CredentialServiceImpl credentialEncryptionService, EncryptionServiceImpl encryptionService) {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(etrustexWebProperties.getLdapUrls());
        ldapContextSource.setBase(etrustexWebProperties.getLdapBase());
        ldapContextSource.setUserDn(etrustexWebProperties.getLdapUsername());

        Credentials credentials = credentialEncryptionService.findCredentialsByUserName(etrustexWebProperties.getLdapUserAlias());
        if (credentials == null) {
            log.error("----->Failed to load ldap credentials");
        } else {
            String password = credentials.getEncryptedPassword().isRsaEncrypted() ?
                    new String(encryptionService.decryptWithServerPrivateKey(credentials.getEncryptedPassword().getPasswordB64())) :
                    credentialEncryptionService.decipherPassword(credentials.getEncryptedPassword());
            ldapContextSource.setPassword(password);
        }

        return ldapContextSource;
    }
}
