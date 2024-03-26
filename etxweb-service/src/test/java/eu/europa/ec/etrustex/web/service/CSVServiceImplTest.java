package eu.europa.ec.etrustex.web.service;


import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.service.CSVServiceImpl.FILE_NOT_VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* method names false positive. */
class CSVServiceImplTest {

    private static final String USERS = "user0104,test0101@test.com,TEST_PARTY,true, false, true, false, false";
    private static final String DUPLICATE_USERS = "user0104,test0101@test.com,TEST_PARTY,true, false, true, false, false\n" +
            "user0104,test0101@test.com,TEST_PARTY,true, false, true, false, false";
    private static final String USER_ERRORS = "user0104,test0101@test.com,TEST_PARTY,true, false, true, true";
    private static final String ENTITIES = "entity1,entityDisplayName1,entityDescription1\n" +
            "entity2;entityDisplayName2,entityDescription2";
    private static final String DUPLICATE_ENTITIES = "entity1,entityDisplayName1,entityDescription1\n" +
            "entity1;entityDisplayName2,entityDescription2";

    private static final String PARTICIPANTS = "entity1,SENDER\n" + "entity2, SENDER";

    private static final String CHANNELS_BULK = "channel1,channel1 for exchange,entity1, Sender\n" + "channel2,channel2for exchange,entity2, Recipient";

    private static final String DUPLICATE_CHANNELS_BULK = "channel1,channel1 for exchange,entity1, Sender\n" + "channel1,channel2for exchange,entity2, Recipient";

    private static final Long GROUP_BUSINESS_ID_1 = 12345L;

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserService userService;
    private CSVService csvService;

    @BeforeEach
    public void setUp() {
        this.csvService = new CSVServiceImpl(groupRepository, userService);
    }


    @Test
    void should_parse_CSV_file_to_user_profile() {
        LdapUserDto ldapUserDto = LdapUserDto.builder()
                .euLogin("eulogin")
                .euLoginEmail("test@test.com")
                .fullName("Fullname")
                .build();
        given(groupRepository.findById(any())).willReturn(Optional.of(mockBusiness()));
        given(userService.getEuLoginProfileFromLdap(any())).willReturn(ldapUserDto);
        given(groupRepository.findByIdentifierAndParentId(any(), any())).willReturn(Optional.of(mockGroup("user0104")));
        assertEquals(1, this.csvService.parseCSVFileToUserProfile(USERS.getBytes(StandardCharsets.UTF_8), GROUP_BUSINESS_ID_1).size());
    }

    @Test
    void should_parse_CSV_file_to_user_profile_with_ExceptionThrown() {
        try {
            given(groupRepository.findById(any())).willReturn(Optional.of(mockBusiness()));
            this.csvService.parseCSVFileToUserProfile(USER_ERRORS.getBytes(StandardCharsets.UTF_8), GROUP_BUSINESS_ID_1);
        } catch (EtxWebException e) {
            assertEquals(FILE_NOT_VALID, e.getMessage());
        }
    }

    @Test
    void should_parse_CSV_file_to_create_group_specs() {
        assertEquals(2, this.csvService.parseCSVFileToGroupSpecs(ENTITIES.getBytes(StandardCharsets.UTF_8), GROUP_BUSINESS_ID_1).size());
    }

    @Test
    void should_throw_an_exception_when_duplicate_users() {
        LdapUserDto ldapUserDto = LdapUserDto.builder()
                .euLogin("eulogin")
                .euLoginEmail("test@test.com")
                .fullName("Fullname")
                .build();

        try {
            given(userService.getEuLoginProfileFromLdap(any())).willReturn(ldapUserDto);
            given(groupRepository.findById(any())).willReturn(Optional.of(mockBusiness()));
            given(groupRepository.findByIdentifierAndParentId(any(), any())).willReturn(Optional.of(mockGroup("user0104")));
            this.csvService.parseCSVFileToUserProfile(DUPLICATE_USERS.getBytes(StandardCharsets.UTF_8), 1L);
        } catch (EtxWebException e) {
            assertEquals(FILE_NOT_VALID + CSVServiceImpl.DUPLICATE_USERS, e.getMessage());
        }
    }

    @Test
    void should_throw_an_exception_when_duplicate_entities() {
        try {
            this.csvService.parseCSVFileToGroupSpecs(DUPLICATE_ENTITIES.getBytes(StandardCharsets.UTF_8), GROUP_BUSINESS_ID_1);
        } catch (EtxWebException e) {
            assertEquals(FILE_NOT_VALID + CSVServiceImpl.DUPLICATE_ENTITIES, e.getMessage());
        }
    }

    @Test
    void should_parse_CSV_file_to_exchange_rule() {
        Channel channel = mockChannel();
        Group entity1 = mockGroup("Entity1");
        Group entity2 = mockGroup("Entity2");
        Group business = mockBusiness();

        given(groupRepository.findById(any())).willReturn(Optional.of(business));
        given(groupRepository.findByIdentifierAndParentId(any(), any())).willReturn(Optional.of(entity1), Optional.of(entity2));
        assertEquals(2,csvService.parseCSVFileToExchangeRules(PARTICIPANTS.getBytes(), 3L, channel).size());
    }

    @Test
    void should_not_parse_CSV_file_to_exchange_rule() {
        Channel channel = mockChannel();
        Group entity1 = mockGroup("Entity1");
        Group business = mockBusiness();

        given(groupRepository.findById(any())).willReturn(Optional.of(business));
        given(groupRepository.findByIdentifierAndParentId(any(), any())).willReturn(Optional.of(entity1));
        assertThrows(EtxWebException.class, () -> csvService.parseCSVFileToExchangeRules(PARTICIPANTS.getBytes(), 3L, channel));
    }

    @Test
    void should_parse_CSV_file_to_bulk_exchange_rule_specs() {
        assertEquals(2,csvService.parseCSVFileToBulkExchangeRuleSpecs(CHANNELS_BULK.getBytes(), 3L).size());
    }

    @Test
    void should_not_parse_CSV_file_to_bulk_exchange_rule_specs_duplicate_exception() {
        assertThrows(EtxWebException.class, () -> csvService.parseCSVFileToBulkExchangeRuleSpecs(DUPLICATE_CHANNELS_BULK.getBytes(), 3L));
    }

}
