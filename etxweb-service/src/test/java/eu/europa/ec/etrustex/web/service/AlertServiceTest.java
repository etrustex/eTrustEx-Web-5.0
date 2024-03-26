package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private GroupRepository groupRepository;

    private AlertService alertService;

    private Group business;

    @BeforeEach
    public void init() {
        business = Group.builder()
                .id(1L)
                .identifier("ABusinessId")
                .name("ABusinessName")
                .build();

        alertService = new AlertServiceImpl(alertRepository, groupRepository);
    }

    @Test
    void should_get_the_alert() {

        AlertSpec alertSpec = mockAlertSpec();
        Alert check = getCheckAlert(alertSpec);
        given(alertRepository.findActiveByGroupIdAndDate(anyLong(), any(Date.class)))
                .willReturn(Collections.singletonList(check));

        Alert alert = alertService.findActiveByGroupIdAndDate(alertSpec.getGroupId(), new Date()).get(0);

        assertEquals(check, alert);
    }

    @Test
    void should_return_empty_optional() {
        assertThat(alertService.findActiveByGroupIdAndDate(4321L, new Date())).isEmpty();
    }

    @Test
    void should_create_an_alert() {

        initGroupRepositoryAndAlertRepository();

        AlertSpec alertSpec = mockAlertSpec();

        Alert check = getCheckAlert(alertSpec);

        assertEquals(check, alertService.save(alertSpec));
    }

    private AlertSpec mockAlertSpec() {
        Date now = new Date();
        Date tomorrow = Date.from(now.toInstant().plus(1, ChronoUnit.DAYS));

        return AlertSpec.builder()
                .groupId(business.getId())
                .isActive(true)
                .startDate(new Date())
                .endDate(tomorrow)
                .type(AlertType.INFO)
                .title("aTitle")
                .content("Some content")
                .build();
    }

    private Alert getCheckAlert(AlertSpec alertSpec) {
        return Alert.builder()
                .group(business)
                .isActive(alertSpec.isActive())
                .startDate(alertSpec.getStartDate())
                .endDate(alertSpec.getEndDate())
                .type(alertSpec.getType())
                .title(alertSpec.getTitle())
                .content(alertSpec.getContent())
                .build();
    }

    private void initGroupRepositoryAndAlertRepository() {
        when(groupRepository.findById(business.getId())).thenReturn(Optional.of(business));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}

