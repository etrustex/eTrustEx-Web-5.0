package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl;

import com.fasterxml.jackson.annotation.JsonValue;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.BooleanGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
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
public class WindowsCompatibleFilenamesGroupConfiguration extends BooleanGroupConfiguration {

    public static final String URL = GroupConfiguration.URL_TEMPLATE + "WindowsCompatibleFilenamesGroupConfiguration/";


    public enum ReservedFilenames {
        AUX, COM1, COM2, COM3, COM4, COM5, COM6, COM7, COM8, COM9, CON, LPT1, LPT2, LPT3, LPT4, LPT5, LPT6, LPT7, LPT8, LPT9, NUL, PRN
    }

    public enum ReservedCharacters {
        LESS_THAN("<"),
        GREATER_THAN(">"),
        COLON(":"),
        DOUBLE_QUOTE("\""),
        FORWARD_SLASH("/"),
        BACKSLASH("\\"),
        PIPE("|"),
        QUESTION_MARK("?"),
        ASTERISK("*");

        private final String value;

        ReservedCharacters(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {

            // this is needed to create the enum correctly in typescript
            if (this.equals(BACKSLASH)) {
                return "\\\\";
            }

            return this.value;
        }
    }
}
