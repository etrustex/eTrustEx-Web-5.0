package eu.europa.ec.etrustex.web.integration.utils;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;

public interface GroupUtils {
    Group createGroup(GroupSpec spec, SecurityUserDetails userDetails);

    Group createEntity(Long businessId, String entityIdentifier, SecurityUserDetails userDetails);

    Group createEntity(Long businessId, String entityIdentifier, SecurityUserDetails userDetails, Boolean isSystem);

    Group createBusiness(Long rootGroupId, String businessIdentifier, SecurityUserDetails userDetails);
}
