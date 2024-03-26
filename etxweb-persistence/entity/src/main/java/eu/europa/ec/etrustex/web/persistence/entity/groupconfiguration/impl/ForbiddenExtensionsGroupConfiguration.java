package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import eu.europa.ec.etrustex.web.common.DbStringListsSeparators;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.ForbiddenExtensionsGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.StringGroupConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ForbiddenExtensionsGroupConfiguration extends StringGroupConfiguration {

    public static final String URL = GroupConfiguration.URL_TEMPLATE + "ForbiddenExtensionsGroupConfiguration/";


    @JsonIgnore
    @Transient
    public Set<String> getForbiddenExtensions() {
        if (getStringValue() == null || getStringValue().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(this.getStringValue().split(DbStringListsSeparators.DB_STRING_LIST_SEPARATOR.toString()))
                .collect(Collectors.toSet());
    }

    @Override
    public Class<?> specClass() {
        return ForbiddenExtensionsGroupConfigurationSpec.class;
    }

    /**
     * Used in typescript generated entities
     */
    public enum DefaultForbiddenExtensions {
        SEVEN_ZIP("7Z"),
        AR("AR"),
        ARC("ARC"),
        BZ("BZ"),
        BZ2("BZ2"),
        BZIP("BZIP"),
        BZIP2("BZIP2"),
        GZ("GZ"),
        GZ2("GZ2"),
        RAR("RAR"),
        ZIP("ZIP"),
        TAR("TAR"),
        Z("Z"),
        LZ("LZ");

        private final String value;

        DefaultForbiddenExtensions(String value) {
            this.value = value;
        }

        public static DefaultForbiddenExtensions getValue(String rel) {
            for (DefaultForbiddenExtensions c : DefaultForbiddenExtensions.values()) {
                if (c.toString().equalsIgnoreCase(rel)) {
                    return c;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return this.value;
        }
    }
}
