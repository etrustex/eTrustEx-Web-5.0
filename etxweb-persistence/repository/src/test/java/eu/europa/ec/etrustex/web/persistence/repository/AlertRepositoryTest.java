package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AlertRepositoryTest {
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Group business1;

    private Date now;
    private Date yesterday;
    private Date tomorrow;

    @BeforeEach
    public void init() {
        business1 = groupRepository.save(EntityTestUtils.mockGroup("B1", "TEST_BUSINESS_1", null));
        Group business2 = groupRepository.save(EntityTestUtils.mockGroup("B2", "TEST_BUSINESS_2", null));

        now = new Date();
        tomorrow = Date.from(now.toInstant().plus(1, ChronoUnit.DAYS));
        yesterday = Date.from(now.toInstant().minus(1, ChronoUnit.DAYS));

        // saving an active alert for another business just to check it is never retrieved!
        alertRepository.save(Alert.builder()
                .group(business2)
                .startDate(now)
                .content("content6")
                .isActive(true)
                .build()
        );
    }

    @Test
    void should_find_the_always_active_alert() {
        Alert alwaysActive = alertRepository.save(Alert.builder()
                .group(business1)
                .startDate(now)
                .title("aTitle")
                .type(AlertType.INFO)
                .content("content1")
                .isActive(true)
                .build()
        );

        runFindTest(alwaysActive);
    }

    @Test
    void should_find_the_currently_active_alert() {
        Alert active = alertRepository.save(Alert.builder()
                .group(business1)
                .title("anotherTitle")
                .type(AlertType.WARNING)
                .content("content2")
                .startDate(yesterday)
                .endDate(tomorrow)
                .isActive(true)
                .build()
        );

        runFindTest(active);
    }

    @Test
    void should_not_find_the_inactive_alert() {
        alertRepository.save(Alert.builder()
                .group(business1)
                .title("aThirdTitle")
                .type(AlertType.DANGER)
                .content("content3")
                .startDate(yesterday)
                .isActive(false)
                .build()
        );

        runReturnEmptyTest();
    }

    @Test
    void should_not_find_the_not_yet_active_alert() {
        alertRepository.save(Alert.builder()
                .group(business1)
                .title("aFourthTitle")
                .type(AlertType.INFO)
                .content("content4")
                .isActive(true)
                .startDate(tomorrow)
                .build()
        );

        runReturnEmptyTest();
    }

    @Test
    void should_not_find_the_expired_alert() {
        alertRepository.save(Alert.builder()
                .group(business1)
                .title("aFifthTitle")
                .type(AlertType.SUCCESS)
                .content("content5")
                .isActive(true)
                .startDate(yesterday)
                .endDate(Date.from(now.toInstant().minus(1, ChronoUnit.SECONDS)))
                .build()
        );

        runReturnEmptyTest();
    }

    private void runFindTest(Alert alert) {
        Alert retrieved = alertRepository.findActiveByGroupIdAndDate(business1.getId(), new Date()).get(0);
        assertEquals(alert, retrieved);
    }

    private void runReturnEmptyTest() {
        if (!alertRepository.findActiveByGroupIdAndDate(business1.getId(), new Date()).isEmpty()) {
            throw new EtxWebException("The query returned the inactive alert ");
        }
    }
}
