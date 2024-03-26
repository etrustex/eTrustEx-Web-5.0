package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserGuideSpec;
import eu.europa.ec.etrustex.web.persistence.entity.UserGuide;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;

import java.util.List;

public interface UserGuideService {
    UserGuide findByBusinessAndRoleNameAndGroupType(Long businessId, RoleName roleName, GroupType groupType);
    List<UserGuideSpec> findAllByBusinessId(Long businessOrRootId);
    void saveOrUpdate(byte[] fileContent, String fileName, Group business, RoleName roleName, GroupType groupType);
    void delete(UserGuide userGuide);
}
