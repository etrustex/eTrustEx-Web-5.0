package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.exchange.model.EntityItem;
import eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckEntityIsEmpty;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckIsBusiness;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUniqueGroupNameForUpdate;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface GroupService {

    Group getRoot();

    Group findByIdentifierAndParentIdentifier(String entityIdentifier, String businessIdentifier);

    Group findById(Long id);

    Group findByRecipientPreferencesId(Long recipientPreferencesId);

    Group create(GroupSpec spec);

    Group findLastUpdatedGroup(String user);

    @CheckUniqueGroupNameForUpdate
    Group update(Long groupId, GroupSpec group);

    Page<Group> findByTypeAndParent(GroupType type, Long parentId, Collection<GrantedAuthority> grantedAuthorities, Pageable pageable, String filterBy, String filterValue);

    Page<Group> findByIds(Pageable pageable, String filterValue, Long... groupIds);

    List<GroupSearchItem> findByIdentifierOrNameLike(Long businessId, String text);

    List<Group> findByType(GroupType type);

    List<Group> findByParentId(Long parentId);

    boolean hasChildrenOrChannelsOrUsersOrMessages(Long groupId);
    boolean hasMessages(Long groupId);

    void deleteGroup(@CheckEntityIsEmpty Group group);

    void deleteBusiness(Group group);

    void cancelBusinessDeletion(@CheckIsBusiness Long groupId);

    void confirmBusinessDeletion(@CheckIsBusiness Long groupId);

    boolean existsByNameAndParentId(String name, Long parentId);
    boolean existsByIdAndParentId(Long id, Long parentId);
    Stream<Group> findByParentIdAndType(Long parentId, GroupType type);

    List<String> bulkAddGroups(byte[] fileContent, Long businessIdentifier);

    void disableEncryption(Long businessId);

    Page<EntityItem> findAllConfiguredGroups(Pageable pageable, String filterValue);

    List<SearchItem> searchAllConfiguredGroups(String filterValue);
}
