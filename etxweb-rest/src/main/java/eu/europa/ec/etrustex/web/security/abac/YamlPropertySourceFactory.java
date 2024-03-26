package eu.europa.ec.etrustex.web.security.abac;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        Properties properties = Optional.ofNullable(factory.getObject())
                .orElseThrow(() -> new EtxWebException("factory.getObject() is null"));
        String fileName = Optional.ofNullable(encodedResource.getResource().getFilename())
                .orElseThrow(() -> new EtxWebException("encodedResource.getResource().getFilename() is null"));

        return new PropertiesPropertySource(fileName, properties);
    }
}
