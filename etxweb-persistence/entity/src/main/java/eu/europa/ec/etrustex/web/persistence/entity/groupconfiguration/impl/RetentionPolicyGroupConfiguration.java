package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.RetentionPolicyGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.IntegerGroupConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class RetentionPolicyGroupConfiguration extends IntegerGroupConfiguration {
    public static final String URL = GroupConfiguration.URL_TEMPLATE + "RetentionPolicyGroupConfiguration/";

    @PrePersist
    @PreUpdate
    private void enable() {
        this.setActive(true);
    }

    @Override
    public Class<?> specClass() {
        return RetentionPolicyGroupConfigurationSpec.class;
    }

}
