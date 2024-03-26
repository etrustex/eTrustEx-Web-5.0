package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;

public interface DeleteGroupService {
    void deleteBusiness(Group business);
    void removeGroupConfigurations(Group group);
}
