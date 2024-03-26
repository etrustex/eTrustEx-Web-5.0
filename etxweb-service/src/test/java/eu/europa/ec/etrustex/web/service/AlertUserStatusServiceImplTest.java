package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.exchange.model.AlertUserStatusSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.persistence.repository.AlertUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AlertUserStatusServiceImplTest {
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private AlertUserStatusRepository alertUserStatusRepository;
    @Mock
    private  UserRepository userRepository;

    private AlertUserStatusService alertUserStatusService;

    private Group business;
    private final User user = EntityTestUtils.mockUser();

    @BeforeEach
    public void init() {
        business = Group.builder()
                .id(1L)
                .identifier("ABusinessId")
                .name("ABusinessName")
                .build();

        alertUserStatusService = new AlertUserStatusServiceImpl(alertUserStatusRepository, alertRepository, userRepository);
    }

    @Test
    void should_create_an_alert() {


        AlertUserStatus check = getCheckAlertUserStatus(mockAlerUserStatustSpec());
        given(alertRepository.findById(anyLong())).willReturn(Optional.of(mockAlert()));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(alertUserStatusRepository.save(any())).willReturn(check);

        assertEquals(check, alertUserStatusService.create(mockAlerUserStatustSpec()));
    }

    @Test
    void should_get_the_alert() {
        AlertUserStatusSpec alertUserStatusSpec = mockAlerUserStatustSpec();
        AlertUserStatus check = getCheckAlertUserStatus(alertUserStatusSpec);
        given(alertUserStatusRepository.findByUserIdAndAlertIdAndGroupId(alertUserStatusSpec.getUserId(), alertUserStatusSpec.getAlertId(), alertUserStatusSpec.getGroupId()))
                .willReturn(Optional.of(check));

        AlertUserStatus alertUserStatus = alertUserStatusService.findByUserIdAndAlertIdAndGroupId(alertUserStatusSpec.getUserId(), alertUserStatusSpec.getAlertId(), alertUserStatusSpec.getGroupId());

        assertEquals(check, alertUserStatus);
    }

    @Test
    void should_return_null() {
        assertThat(alertUserStatusService.findByUserIdAndAlertIdAndGroupId(4321L, 256L, 10L)).isNull();
    }



    @Test
    void should_update_an_existing_alert_user_status() {
        AlertUserStatusSpec alertUserStatusSpec = mockAlerUserStatustSpec();
        AlertUserStatus check = getCheckAlertUserStatus(mockAlerUserStatustSpec());
        given(alertUserStatusRepository.findByUserIdAndAlertIdAndGroupId(alertUserStatusSpec.getUserId(), alertUserStatusSpec.getAlertId(), alertUserStatusSpec.getGroupId()))
                .willReturn(Optional.of(check));

        AlertUserStatus updated = getCheckAlertUserStatus(mockAlerUserStatustSpec());
        updated.setStatus(AlertUserStatus.AlertUserStatusType.READ);
        given(alertUserStatusRepository.save(any()))
                .willReturn(updated);

        assertEquals(updated, alertUserStatusService.update(mockAlerUserStatustSpec()));

    }

    private AlertUserStatusSpec mockAlerUserStatustSpec() {
        return AlertUserStatusSpec.builder()
                .groupId(business.getId())
                .alertId(mockAlert().getId())
                .status(AlertUserStatus.AlertUserStatusType.UNREAD.name())
                .userId(user.getId())
                .build();
    }

    private AlertUserStatus getCheckAlertUserStatus(AlertUserStatusSpec alertUserStatusSpec) {

        return AlertUserStatus.builder()
                .id(123L)
                .groupId(alertUserStatusSpec.getGroupId())
                .status(AlertUserStatus.AlertUserStatusType.UNREAD)
                .user(user)
                .alert(mockAlert())
                .build();
    }

    private Alert mockAlert() {
        Date now = new Date();
        Date tomorrow = Date.from(now.toInstant().plus(1, ChronoUnit.DAYS));

        return Alert.builder()
                .id(10L)
                .group(business)
                .isActive(true)
                .startDate(now)
                .endDate(tomorrow)
                .type(AlertType.SUCCESS)
                .title("aTitle")
                .content("Some content")
                .build();
    }
}

