package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration;


import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.BooleanGroupConfigurationSpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public abstract class BooleanGroupConfiguration extends GroupConfiguration<Boolean> {
    @Override
    public void setValue(Boolean value) {
        this.setActive(value);
    }

    @Override
    public Class<?> specClass() {
        return BooleanGroupConfigurationSpec.class;
    }
}
