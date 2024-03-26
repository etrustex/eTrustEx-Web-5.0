package eu.europa.ec.etrustex.web;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.WebApplicationInitializer;


@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@Slf4j
@EnableRetry
@EnableScheduling
public class AppInitializer extends SpringBootServletInitializer implements WebApplicationInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AppInitializer.class);
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AppInitializer.class);
        springApplication.run(args);
    }
}
