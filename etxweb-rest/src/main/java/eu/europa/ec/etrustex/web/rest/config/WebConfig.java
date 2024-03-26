package eu.europa.ec.etrustex.web.rest.config;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.rest.logging.LoggingInterceptor;
import eu.europa.ec.etrustex.web.rest.resolver.UserDetailsHandlerMethodArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
@EnableConfigurationProperties(EtrustexWebProperties.class)
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    private final EtrustexWebProperties etrustexWebProperties;
    private final AsyncTaskExecutor asyncTaskExecutor;
    private final CallableProcessingInterceptor callableProcessingInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    public WebConfig(EtrustexWebProperties etrustexWebProperties, @Qualifier("taskExecutor") AsyncTaskExecutor asyncTaskExecutor, CallableProcessingInterceptor callableProcessingInterceptor) {
        this.etrustexWebProperties = etrustexWebProperties;
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.callableProcessingInterceptor = callableProcessingInterceptor;
        creteFSRepoIfNotExist();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH")
                .allowedHeaders("Accept", "Accept-Encoding", "Accept-Language", "Authorization", "Cache-Control", "Connection", "Content-Type", "Cookie", "DNT", "Host", "Origin", "Referer", "User-Agent", "X-Requested-With")
                .allowCredentials(false);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserDetailsHandlerMethodArgumentResolver());
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(360000).setTaskExecutor(asyncTaskExecutor);
        configurer.registerCallableInterceptors(callableProcessingInterceptor);
        WebMvcConfigurer.super.configureAsyncSupport(configurer);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**");
    }

    private void creteFSRepoIfNotExist() {
        Path fileUploadDirPath = etrustexWebProperties.getFileUploadDirPath();

        Assert.notNull(fileUploadDirPath, "File repository path is null!");

        if (!fileUploadDirPath.toFile().exists()) {
            try {
                Files.createDirectory(fileUploadDirPath);
            } catch (IOException e) {
                log.error("Error creating File repository", e);
            }
        }

        Assert.isTrue(fileUploadDirPath.toFile().exists(), "File repository not found or unable to create it. Configured path: " + etrustexWebProperties.getFileUploadDir());
    }
}
