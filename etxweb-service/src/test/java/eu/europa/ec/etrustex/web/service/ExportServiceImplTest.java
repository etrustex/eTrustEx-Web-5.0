package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserExportItem;
import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {
    public static final String ENTITY_ID = "entityId";
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ExchangeRuleRepository exchangeRuleRepository;

    private ExportService exportService;

    private static final Long BUSINESS_ID = 1L;

    @BeforeEach
    void init() {
        exportService = new ExportServiceImpl(userProfileRepository, groupRepository, exchangeRuleRepository);
    }

    @Test
    void should_create_the_xlsx_file() throws IOException {

        given(userProfileRepository.exportByBusinessId(BUSINESS_ID)).willReturn(Stream.<UserExportItem>builder()
                .add(UserExportItem.builder()
                        .statusNotification(false)
                        .roleName(RoleName.OPERATOR)
                        .messageNotification(true)
                        .euLoginId("EULoginId")
                        .alternativeEmail("an@email.com")
                        .useAlternativeEmail(false)
                        .name("name")
                        .entityIdentifier(ENTITY_ID)
                        .status(true)
                        .build())
                .build());
        given(groupRepository.findByParentId(BUSINESS_ID)).willReturn(Stream.of(
                        EntityTestUtils.mockGroup(ENTITY_ID, "Entity", EntityTestUtils.mockBusiness(), GroupType.ENTITY)
                )
        );

        try (InputStream inputStream = exportService.exportUsersAndFunctionalMailboxes(BUSINESS_ID)) {

            assertThat(inputStream.read()).isPositive();
        }

    }

    @Test
    void should_create_the_xlsx_file_with_functional_mailboxes() throws IOException {

        given(userProfileRepository.exportByBusinessId(BUSINESS_ID)).willReturn(Stream.<UserExportItem>builder()
                .add(UserExportItem.builder()
                        .statusNotification(false)
                        .roleName(RoleName.OPERATOR)
                        .messageNotification(true)
                        .euLoginId("EULoginId")
                        .alternativeEmail("an@email.com")
                        .useAlternativeEmail(true)
                        .name("name")
                        .entityIdentifier(ENTITY_ID)
                        .build())
                .build());
        given(groupRepository.findByParentId(BUSINESS_ID)).willReturn(Stream.of(
                        EntityTestUtils.mockGroup(ENTITY_ID, "Entity", EntityTestUtils.mockBusiness(), GroupType.ENTITY, "testFunctional1@test.com,testFunctional2@test.com,testFunctional3@test.com", "testFunctional2@test.com", "testFunctional2@test.com")
                )
        );

        try (InputStream inputStream = exportService.exportUsersAndFunctionalMailboxes(BUSINESS_ID)) {


            verify(groupRepository, times(1)).findByParentId(any());

            assertThat(inputStream.read()).isPositive();
        }

    }

    @Test
    void should_return_the_export_file_name() {
        String exportFileName = exportService.getExportUsersFilename(GroupType.BUSINESS);
        Pattern pattern = Pattern.compile("export-users-fm-[1-9]\\d{3}-\\d{2}-\\d{2}\\.xlsx");
        assertTrue(pattern.matcher(exportFileName).matches());
    }
}
