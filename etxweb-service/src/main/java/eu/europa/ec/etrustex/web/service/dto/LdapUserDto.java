package eu.europa.ec.etrustex.web.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
public class LdapUserDto {

    private String euLogin;
    private String fullName;
    private String euLoginEmail;
}
