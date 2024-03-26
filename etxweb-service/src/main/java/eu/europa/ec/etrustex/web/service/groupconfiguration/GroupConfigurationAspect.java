package eu.europa.ec.etrustex.web.service.groupconfiguration;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@AllArgsConstructor
@Slf4j
public class GroupConfigurationAspect {

    private final GroupConfigurationService groupConfigurationService;
    private final GroupService groupService;

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.security.GroupService.create(..)) && args(groupSpec)", returning = "group", argNames = "groupSpec,group")
    public void createDefaultGroupConfiguration(GroupSpec groupSpec, Group group) {
        log.info("Creating group configs for group " + group.getIdentifier());
        if (!groupSpec.getType().equals(GroupType.ROOT)) {
            Group createdGroup = groupService.findById(group.getId());
            groupConfigurationService.saveDefaults(createdGroup);
            log.info(String.format("Default group configuration has been created for group id: %s", createdGroup.getIdentifier()));
        }
    }
}
