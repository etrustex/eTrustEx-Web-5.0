package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.exchange.model.EntityItem;
import eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface GroupRepository extends CrudRepository<Group, Long> {
    List<Group> findByIdIn(Collection<Long> ids);

    Stream<Group> findByParentId(Long parentId);

    List<Group> findListByParentId(Long parentId);

    int countByParentId(Long parentId);

    Stream<Group> findByParentIdAndType(Long parentId, GroupType type);

    Optional<Group> findByIdAndType(Long groupId, GroupType type);

    Optional<Group> findByIsActiveIsTrueAndIdentifierAndType(String groupId, GroupType type);

    int countByIdentifier(String groupId);

    Optional<Group> findByRecipientPreferencesId(Long recipientPreferencesId);

    boolean existsByNameAndParentId(String name, Long parentId);

    List<Group> findByNameAndParentId(String name, Long parentId);

    boolean existsByIdentifierAndParentId(String identifier, Long parentId);

    boolean existsByIdAndParentId(Long id, Long parentId);

    Group findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(String modifiedBy);

    @Query("select distinct new eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem(g.id, g.identifier, g.name, rp.publicKeyFileName, rp.confidentiality) from Group g " +
            " left join RecipientPreferences rp on rp = g.recipientPreferences " +
            " where g.parent.id = :parentId and g.type = :groupType and g.isActive = true and " +
            "(lower(g.identifier) like lower(concat('%', :text,'%') ) or lower(g.name) like lower(concat('%', :text,'%') )) order by g.name asc")
    List<GroupSearchItem> findByTypeAndParentIdAndIdentifierOrNameLike(GroupType groupType, Long parentId, String text);

    @Query("select distinct new eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem(g.id, g.identifier, g.name, rp.publicKeyFileName, rp.confidentiality) from Group g " +
            " left join RecipientPreferences rp on rp = g.recipientPreferences " +
            " where g.type = :groupType and g.isActive = true and " +
            "(lower(g.identifier) like lower(concat('%', :text,'%') ) or lower(g.name) like lower(concat('%', :text,'%') )) order by g.name asc")
    List<GroupSearchItem> findByTypeAndIdentifierOrNameLike(GroupType groupType, String text);

    List<Group> findByType(GroupType groupType);

    List<Group> findByIsPendingDeletionIsTrueAndRemovedDateIsNotNullAndType(GroupType groupType);

    List<Group> findByIsPendingDeletionIsTrueAndIsActiveIsFalseAndType(GroupType groupType);


    @SuppressWarnings("SqlDialectInspection")
    @Query(value = "SELECT SUM(total_rows) FROM " +
            "(SELECT count(*) AS total_rows FROM EW_MESSAGE em WHERE em.SENDER_GROUP_ID = :groupId and em.ATTACHMENT_TOTAL_NUMBER IS NOT NULL " +
            "UNION ALL " +
            "SELECT count(*) FROM EW_MESSAGE_SUMMARY ems WHERE ems.RECIPIENT_ID = :groupId " +
            "UNION ALL " +
            "SELECT count(*) FROM EW_GRANTED_AUTHORITY ega WHERE GROUP_ID = :groupId) ", nativeQuery = true)
    int getTotalRecordsFromGrantedAuthorityAndMessageAndMessageSummary(Long groupId);

    @Query(value = "select g from Group g " +
            " where g.parent = :parent and g.type = :groupType and ((g.isActive = true and g.isPendingDeletion = false and g.type = eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY) or g.type = eu.europa.ec.etrustex.web.util.exchange.model.GroupType.BUSINESS) and " +
            "(" +
            "lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%') )" +
            ")",
            countQuery = "select count(g) from Group g " +
                    " where g.parent = :parent and g.type = :groupType and g.isActive = true and g.isPendingDeletion = false and " +
                    "(lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<Group> findByGroupTypeAndParentAndGroupIdOrGroupName(GroupType groupType, Group parent, String filterValue, Pageable pageable);

    @Query(value = "select distinct g from Group g " +
            " where g.parent = :parent and g.type = :groupType and g.identifier in (:groupIds) and " +
            "(lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count(g.identifier) from Group g " +
                    " where g.parent = :parent and g.type = :groupType and g.identifier in (:groupIds) and " +
                    "(lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<Group> findByGroupTypeAndParentAndGroupIdsAndGroupIdOrGroupName(GroupType groupType, Group parent, Collection<String> groupIds, String filterValue, Pageable pageable);


    @Query(value = "select g from Group g " +
            " where g.id in :groupIds and " +
            "(" +
            "lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%') )" +
            ")",
            countQuery = "select count(g) from Group g " +
                    " where g.id in :groupIds and " +
                    "(lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<Group> findByGroupIdInAndGroupIdOrGroupNameLike(String filterValue, Pageable pageable, Long... groupIds);


    Group findFirstByIdentifierAndParentIdentifier(String identifier, String parentIdentifier);

    Optional<Group> findByIdentifierAndParentId(String identifier, Long parentId);

    @Query(value = "select new eu.europa.ec.etrustex.web.exchange.model.EntityItem(g.parent.identifier, g.parent.name, g.identifier, g.name, c.name, er.exchangeMode, g.isActive, rp.confidentiality, (CASE WHEN rp.publicKey is not null THEN true ELSE false END)) " +
            "from Group g " +
            "left join ExchangeRule er on g = er.member " +
            "left join Channel c on c = er.channel " +
            "left join RecipientPreferences rp on rp = g.recipientPreferences " +
            "where g.type = 'ENTITY' and " +
            "(lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%'))) ",
            countQuery = "select count(g)" +
                    "from Group g " +
                    "left join ExchangeRule er on g = er.member " +
                    "left join Channel c on c = er.channel " +
                    "left join RecipientPreferences rp on rp = g.recipientPreferences " +
                    "where g.type = 'ENTITY' and " +
                    "(lower(g.identifier) like lower(concat('%', :filterValue,'%') ) or lower(g.name) like lower(concat('%', :filterValue,'%'))) ")
    Page<EntityItem> findAllConfiguredGroups(String filterValue, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EW_GROUP g SET g.CREATED_DATE = :date WHERE g.ID = :groupId", nativeQuery = true)
    void updateCreatedDate(Long groupId, Date date);

    @Query(value = "SELECT SUM(total_rows) FROM " +
            "(SELECT count(*) AS total_rows FROM EW_MESSAGE em WHERE em.SENDER_GROUP_ID = :groupId and em.ATTACHMENT_TOTAL_NUMBER IS NOT NULL " +
            "UNION ALL " +
            "SELECT count(*) FROM EW_MESSAGE_SUMMARY ems WHERE ems.RECIPIENT_ID = :groupId)" , nativeQuery = true)
    int getTotalRecordsFromMessageAndMessageSummaryByGroupId(Long groupId);
}
