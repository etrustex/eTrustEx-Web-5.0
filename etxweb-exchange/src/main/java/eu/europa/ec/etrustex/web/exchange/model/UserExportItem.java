package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserExportItem implements Serializable {
    private String entityIdentifier;
    private String entityName;
    private GroupType groupType;
    private String name;
    private RoleName roleName;
    private String euLoginId;
    private String euLoginEmail;
    private String alternativeEmail;
    private Boolean useAlternativeEmail;
    private Boolean messageNotification;
    private Boolean statusNotification;
    private Boolean retentionWarningNotification;
    private Boolean status;
}
