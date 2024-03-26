package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserGuideSpec;
import eu.europa.ec.etrustex.web.persistence.entity.UserGuide;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.UserGuideRepository;
import eu.europa.ec.etrustex.web.service.security.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserGuideServiceImpl implements UserGuideService {
    private final UserGuideRepository userGuideRepository;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public UserGuide findByBusinessAndRoleNameAndGroupType(Long businessId, RoleName roleName, GroupType groupType) {

        return userGuideRepository.findByBusinessIdAndRoleNameAndGroupType(businessId, roleName, groupType)
                .orElseGet(() ->
                        userGuideRepository.findByBusinessIdentifierAndRoleNameAndGroupType(GroupType.ROOT.toString(), roleName, groupType)
                                .orElseThrow(() ->
                                        new EtxWebException(String.format("User Guide not found for for business %s and role %s and Group Type %s ", businessId, roleName, groupType))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGuideSpec> findAllByBusinessId(Long businessId ) {

        return userGuideRepository.findAllByBusinessId(businessId)
                .stream().map(userGuide -> UserGuideSpec.builder()
                        .businessId(userGuide.getBusiness().getId())
                        .filename(userGuide.getFilename())
                        .role(userGuide.getRole().getName())
                        .groupType(userGuide.getGroupType())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdate(byte[] fileContent,String fileName, Group business, RoleName roleName, GroupType groupType) {
        Optional<UserGuide> userGuide = userGuideRepository.findByBusinessIdAndRoleNameAndGroupType(business.getId(), roleName, groupType);

        if (userGuide.isPresent()) {
            userGuide.get().setBinary(fileContent);
            userGuide.get().setFilename(fileName);
            userGuideRepository.save(userGuide.get());
        } else {
            userGuideRepository.save(
                    UserGuide.builder()
                            .filename(fileName)
                            .business(business)
                            .role(roleService.getRole(roleName))
                            .groupType(groupType)
                            .binary(fileContent)
                            .build()
            );
        }
    }

    @Override
    public void delete(UserGuide userGuide) {
        userGuideRepository.delete(userGuide);
    }

}
