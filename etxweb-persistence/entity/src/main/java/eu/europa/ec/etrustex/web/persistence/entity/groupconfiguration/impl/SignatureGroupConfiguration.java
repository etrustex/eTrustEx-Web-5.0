package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.BooleanGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@SuperBuilder
public class SignatureGroupConfiguration extends BooleanGroupConfiguration {
    public static final String URL = GroupConfiguration.URL_TEMPLATE + "SignatureGroupConfiguration/";
}
