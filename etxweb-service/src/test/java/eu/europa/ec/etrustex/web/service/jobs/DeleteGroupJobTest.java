package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"java:S112", "java:S100"})
@ExtendWith(MockitoExtension.class)
public class DeleteGroupJobTest {

    @Spy
    private GroupRepository groupRepository;
    @Spy
    private DeleteGroupService deleteGroupService;
    @Spy
    private GroupService groupService;
    private DeleteGroupJob deleteGroupJob;

    private Group business;
    private Group entity;

    @BeforeEach
    public void setUp() {
        Group businessRoot = Group.builder()
                .identifier("ABusinessId")
                .name("ABusinessName")
                .type(GroupType.ROOT)
                .build();

        business = Group.builder()
                .identifier("ABusinessId2")
                .name("ABusinessName2")
                .parent(businessRoot)
                .type(GroupType.BUSINESS)
                .build();

        entity = Group.builder()
                .identifier("AEntity")
                .name("AEntityName")
                .parent(business)
                .type(GroupType.ENTITY)
                .build();

        deleteGroupJob = new DeleteGroupJob(groupRepository, deleteGroupService, groupService);
    }

    @Test
    void should_run_delete_business() {

        given(groupRepository.findByIsPendingDeletionIsTrueAndRemovedDateIsNotNullAndType(any())).willReturn(Collections.singletonList(business));
        Mockito.doNothing().when(deleteGroupService).deleteBusiness(any());

        deleteGroupJob.runDeleteBusinessTask();

        verify(deleteGroupService, Mockito.times(1)).deleteBusiness(business);
    }

    @Test
    void should_run_delete_pending_group() {
        given(groupRepository.findByIsPendingDeletionIsTrueAndIsActiveIsFalseAndType(any())).willReturn(Collections.singletonList(entity));
        given(groupService.hasMessages(any())).willReturn(false);

        deleteGroupJob.runDeleteGroupTask();
        verify(groupService, Mockito.times(1)).deleteGroup(entity);
    }
}
