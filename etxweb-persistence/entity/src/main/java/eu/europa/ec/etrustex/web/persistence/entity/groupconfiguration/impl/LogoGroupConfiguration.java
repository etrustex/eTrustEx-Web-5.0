package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.StringGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.StringGroupConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class LogoGroupConfiguration extends StringGroupConfiguration {
    public static final String URL = GroupConfiguration.URL_TEMPLATE + "LogoGroupConfiguration/";

    @Override
    public Class<?> specClass() {
        return StringGroupConfigurationSpec.class;
    }

}
