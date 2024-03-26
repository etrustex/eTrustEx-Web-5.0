package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckNotificationPreferences;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@CheckNotificationPreferences
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CreateUserProfileSpec extends UserProfileSpec {
    private List<RoleName> roleNames;
}
