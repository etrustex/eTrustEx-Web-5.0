package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserExportItem;
import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfileId;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserProfileRepository extends CrudRepository<UserProfile, UserProfileId> {

    UserProfile findByUserAndGroup(User user, Group group);

    Optional<UserProfile> findByUserEcasIdAndGroup(String ecasId, Group group);

    UserProfile findByUserEcasIdAndGroupId(String ecasId, Long groupId);

    void deleteByUserEcasIdAndGroupId(String ecasId, Long groupId);

    boolean existsByUserEcasId(String ecasId);

    boolean existsByUserEcasIdIgnoreCaseAndGroupId(String ecasId, Long groupId);

    @Query(value = "select up from UserProfile up JOIN GrantedAuthority ga on up.user.id = ga.user.id where ga.role.name = :roleName",
            countQuery = "select count(up) from UserProfile up JOIN GrantedAuthority ga on up.user.id = ga.user.id where ga.role.name = :roleName")
    List<UserProfile> findByRoleName(RoleName roleName);

    @Query(value = "select new eu.europa.ec.etrustex.web.exchange.model.UserListItem(up.user.ecasId, up.user.name, up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.group.id, up.group.identifier, up.group.name, up.group.type, " +
            "up.auditingEntity.createdDate, up.auditingEntity.createdBy, up.auditingEntity.modifiedDate, up.auditingEntity.modifiedBy, " +
            "up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, ga.enabled) " +
            "from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
            "where ga.role.name = :roleName and " +
            "( " +
            "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
            "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count(distinct up.concatedId) from UserProfile up " +
                    "join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
                    "where ga.role.name = :roleName and " +
                    "( " +
                    "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<UserListItem> getByRoleNameAndNameOrEcasIdContains(RoleName roleName, String filterValue, Pageable pageable);

    @Query(value = "select distinct new eu.europa.ec.etrustex.web.exchange.model.UserListItem(up.user.ecasId, up.user.name, up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.group.id, up.group.identifier, up.group.name, up.group.type, " +
            "up.auditingEntity.createdDate, up.auditingEntity.createdBy, up.auditingEntity.modifiedDate, up.auditingEntity.modifiedBy, " +
            "up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, true) " +
            "from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
            "where up.group.id = :groupId and " +
            "( " +
            "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
            "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count (distinct up.concatedId) from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
                    "where up.group.id = :groupId and " +
                    "( " +
                    "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<UserListItem> findByGroupIdAndNameOrEcasIdContains(Long groupId, String filterValue, Pageable pageable);

    @Query(value = "select distinct new eu.europa.ec.etrustex.web.exchange.model.UserListItem(up.user.ecasId, up.user.name, up.group.id, up.group.identifier, up.group.name, " +
            "up.group.type, up.group.parent.identifier, up.group.parent.name, up.auditingEntity.createdDate, up.auditingEntity.createdBy, up.auditingEntity.modifiedDate, up.auditingEntity.modifiedBy) " +
            "from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
            "where ga.role.name != 'SYS_ADMIN' and ga.role.name != 'OFFICIAL_IN_CHARGE' and " +
            "( " +
            "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
            "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count (distinct up.concatedId) from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
                    "where ga.role.name != 'SYS_ADMIN' and ga.role.name != 'OFFICIAL_IN_CHARGE' and " +
                    "( " +
                    "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<UserListItem> findByNameOrEcasIdContains(String filterValue, Pageable pageable);

    @Query(value = "select new eu.europa.ec.etrustex.web.exchange.model.UserListItem(up.user.ecasId, up.user.name, up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.group.id, up.group.identifier, up.group.name, up.group.type, " +
            "up.auditingEntity.createdDate, up.auditingEntity.createdBy, up.auditingEntity.modifiedDate, up.auditingEntity.modifiedBy, " +
            "up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, ga.enabled) " +
            "from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
            "where (ga.role.name = :roleName and ga.group.id = :groupId) and " +
            "( " +
            "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
            "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count(distinct up.concatedId) from UserProfile up " +
                    "join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
                    "where (ga.role.name = :roleName and ga.group.id = :groupId) and " +
                    "( " +
                    "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<UserListItem> getByRoleNameAndGroupIdAndNameOrEcasIdContains(RoleName roleName, Long groupId, String filterValue, Pageable pageable);

    @Query(value = "select distinct new eu.europa.ec.etrustex.web.exchange.model.UserListItem(up.user.ecasId, up.user.name, up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.group.id, up.group.identifier, up.group.name, up.group.type, " +
            "urr.auditingEntity.createdDate, urr.auditingEntity.createdBy, urr.auditingEntity.modifiedDate, urr.auditingEntity.modifiedBy, " +
            "up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, true, urr.isOperator, urr.isAdmin) " +
            "from UserRegistrationRequest urr left join UserProfile up on up.group.id = urr.group.id and up.user.id = urr.user.id " +
            "where (up.group.parent.id = :groupId or up.group.id = :groupId) and " +
            "( " +
            "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
            "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count (distinct up.concatedId) from UserRegistrationRequest urr left join UserProfile up on up.group.id = urr.group.id and up.user.id = urr.user.id " +
                    "where (up.group.parent.id = :groupId or up.group.id = :groupId) and " +
                    "( " +
                    "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<UserListItem> findByGroupIdOrParentGroupIdAndNameOrEcasIdContainsAndUserRegistration(Long groupId, String filterValue, Pageable pageable);

    @Query(value = "select distinct new eu.europa.ec.etrustex.web.exchange.model.UserListItem(up.user.ecasId, up.user.name, up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.group.id, up.group.identifier, up.group.name, up.group.type, " +
            "up.auditingEntity.createdDate, up.auditingEntity.createdBy, up.auditingEntity.modifiedDate, up.auditingEntity.modifiedBy, " +
            "up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, true) " +
            "from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
            "where (up.group.parent.id = :groupId or up.group.id = :groupId) and " +
            "( " +
            "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
            "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count (distinct up.concatedId) from UserProfile up join GrantedAuthority ga on up.user.id = ga.user.id and up.group.id = ga.group.id " +
                    "where (up.group.parent.id = :groupId or up.group.id = :groupId) and " +
                    "( " +
                    "   lower(up.user.name) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(up.user.ecasId) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<UserListItem> findByGroupIdOrParentGroupIdAndNameOrEcasIdContains(Long groupId, String filterValue, Pageable pageable);

    @EntityGraph(attributePaths = {"group"})
    Stream<UserProfile> findByUserEcasId(String ecasId);

    @Query("select distinct new eu.europa.ec.etrustex.web.exchange.model.UserExportItem(ga.group.identifier, ga.group.name, ga.group.type, up.user.name, ga.role.name, up.user.ecasId, " +
            "up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, ga.enabled) " +
            "from UserProfile up " +
            "   join GrantedAuthority ga on ga.user = up.user and ga.group = up.group " +
            "where up.group.id = :businessId or up.group.parent.id = :businessId")
    Stream<UserExportItem> exportByBusinessId(Long businessId);

    @Query("select distinct new eu.europa.ec.etrustex.web.exchange.model.UserExportItem(ga.group.identifier, ga.group.name, ga.group.type, up.user.name, ga.role.name, up.user.ecasId, " +
            "up.user.euLoginEmailAddress, up.alternativeEmail, up.alternativeEmailUsed, up.newMessageNotifications, up.messageStatusForSenderNotifications, up.retentionWarningNotifications, ga.enabled) " +
            "from UserProfile up " +
            "   join GrantedAuthority ga on ga.user = up.user and ga.group = up.group " +
            "where up.group.id = :entityId")
    Stream<UserExportItem> exportByEntityId(Long entityId);

    @Query("select distinct new java.lang.String(CASE WHEN (up.alternativeEmailUsed = true) THEN up.alternativeEmail ELSE up.user.euLoginEmailAddress END) " +
            "from UserProfile up " +
            "   join GrantedAuthority ga on ga.user = up.user and ga.group = up.group " +
            "where (up.group.id = :businessId or up.group.parent.id = :businessId) " +
            "and ((up.alternativeEmail is not null and up.alternativeEmailUsed = true) or up.user.euLoginEmailAddress is not null) " +
            "and ((up.newMessageNotifications = true) or (up.messageStatusForSenderNotifications = true) or (up.retentionWarningNotifications = true)) ")
    Stream<String> findEmailsByGroupIdOrBusinessId(Long businessId);

    List<UserProfile> findByNewMessageNotificationsIsTrueAndGroup(Group group);

    List<UserProfile> findByRetentionWarningNotificationsIsTrueAndGroup(Group group);

    List<UserProfile> findByMessageStatusForSenderNotificationsIsTrueAndGroup(Group group);

    List<UserProfile> findByGroupId(Long groupId);

    void deleteByGroupId(Long groupId);
}
