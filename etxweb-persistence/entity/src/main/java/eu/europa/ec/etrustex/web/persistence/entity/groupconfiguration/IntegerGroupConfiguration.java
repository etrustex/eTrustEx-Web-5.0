package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration;


import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.IntegerGroupConfigurationSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class IntegerGroupConfiguration extends GroupConfiguration<Integer> {

    @NotNull
    protected Integer integerValue;

    @Override
    public void setValue(Integer value) {
        this.setIntegerValue(value);
    }

    @Override
    public Class<?> specClass() {
        return IntegerGroupConfigurationSpec.class;
    }
}
