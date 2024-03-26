package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class StringGroupConfiguration extends GroupConfiguration<String> {

    @Lob
    @NotNull
    protected String stringValue;

    @Override
    public void setValue(String value) {
        this.setStringValue(value);
    }
}
