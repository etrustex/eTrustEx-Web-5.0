package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.SysDeleteUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface UserProfileService {
    @Transactional(readOnly = true)
    UserProfile findUserProfileByUserAndGroupId(User user, Long groupId);

    @Transactional(readOnly = true)
    UserProfile findByUserAndGroupIdentifierAndBusinessIdentifier(User user, EntitySpec entitySpec);

    UserProfile findUserProfileByUserAndGroup(User user, Group group);

    List<SearchItem> search(Long groupId, String filterValue, Boolean isRegistrationRequest, RoleName role);

    Stream<UserProfile> findByUserEcasId(String ecasId);

    List<SearchItem> searchSysAdmins(String filterValue);

    UserProfile create(CreateUserProfileSpec userProfileSpec);

    UserProfile update(UserProfileSpec userProfileSpec);

    Page<UserListItem> getByRoleNameAndGroupId(RoleName role, Long groupId, String filterValue, PageRequest pageRequest);

    Page<UserListItem> findSysAdmins(String filterValue, PageRequest pageRequest);

    Page<UserListItem> findOfficialsInCharge(String filterValue, PageRequest pageRequest);

    Page<UserListItem> getByGroupId(Long groupId, String filterValue, PageRequest pageRequest, Boolean isRegistrationRequest);

    void delete(String ecasId, Long groupId);

    void delete(SysDeleteUserProfileSpec user);

    Set<String> retrieveEmailsForNotification(Long businessId);

    List<UserProfile> findByNewMessageNotificationsIsTrueAndGroup(Group recipient);

    List<UserProfile> findByMessageStatusForSenderNotificationsIsTrueAndGroup(Group group);

    List<UserProfile> findByRetentionWarningNotificationsIsTrueAndGroup(Group recipient);

    List<String> bulkAddUserProfiles(byte[] fileContent, Long businessId);

    Page<UserListItem> getUsers(String filterValue, PageRequest pageRequest);
    Page<UserListItem> getUsers(String filterValue, String role, Boolean isAllUsers, PageRequest pageRequest);
    List<SearchItem> searchAllUsers(String filterValue);
    List<SearchItem> preSearchUsers(String filterValue, Boolean isAllUsers);

    String cleanFunctionalUserName(LdapUserDto user);
}
