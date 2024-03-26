package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
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
public class UserRole implements Serializable {
    private RoleName role;
    private Long groupId;
    private Long businessId;
    private String groupName;
    private String groupDescription;
    private GroupType groupType;
    private boolean groupIsActive;
}
