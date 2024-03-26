package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails implements Serializable {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole[] roles;
    private UserPreferencesSpec userPreferencesSpec;
}
