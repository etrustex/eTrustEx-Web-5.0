package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.integration.utils.UserUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.*;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration.GroupConfigurationRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.*;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.security.*;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@AutoConfigureRestDocs("build/generated-snippets")
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
public abstract class AbstractControllerTest {
    protected static final String API_ERROR_INSTANCE_PREFIX = "uri=";
    private static final String NAME_SUFFIX = " name";
    private static final String EMAIL_SUFFIX = "@domain.eu";
    protected static final Random RANDOM = new SecureRandom();
    @Autowired
    public MockMvc mockMvc;
    protected SecurityUserDetails operatorUserDetails;
    protected SecurityUserDetails entityAdminUserDetails;
    protected SecurityUserDetails businessAdminUserDetails;
    protected SecurityUserDetails sysAdminUserDetails;
    protected SecurityUserDetails officialInChargeUserDetails;
    @Autowired
    protected RoleService roleService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected GroupService groupService;
    @Autowired
    protected GroupRepository groupRepository;
    @Autowired
    protected UserProfileService userProfileService;
    @Autowired
    protected UserProfileRepository userProfileRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected GrantedAuthorityRepository grantedAuthorityRepository;
    @Autowired
    protected GrantedAuthorityService grantedAuthorityService;
    @Autowired
    protected TemplateService templateService;
    @Autowired
    protected TemplateRepository templateRepository;
    @Autowired
    private GroupConfigurationRepository<?> groupConfigurationRepository;
    @Autowired
    protected GroupConfigurationService groupConfigurationService;
    @Autowired
    protected ExchangeRuleRepository exchangeRuleRepository;
    @Autowired
    protected ChannelRepository channelRepository;
    @Autowired
    protected MessageRepository messageRepository;
    @Autowired
    protected MessageUserStatusRepository messageUserStatusRepository;
    @Autowired
    protected MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    @Autowired
    UserGuideRepository userGuideRepository;
    @Autowired
    protected UserUtils userUtils;
    @Autowired
    UserRegistrationRequestRepository userRegistrationRequestRepository;

    @Autowired
    protected MessageUtils messageUtils;

    @MockBean
    protected MailService mailService;

    protected User otherUser;
    protected User abstractTestUser;
    protected Role operatorRole;
    protected Role groupAdminRole;
    protected Role sysAdminRole;
    protected Role officialRole;
    protected Group root;
    protected Group abstractTestBusiness;
    protected Group abstractTestEntity;

    /*
     * IMPORTANT
     * DO NOT @Autowire ObjectMapper!!!
     * Running a single test works but running all tests makes tests fail randomly by not properly serializing objects into Json.
     */
    protected ObjectMapper objectMapper;
    @Autowired
    private Jackson2ObjectMapperBuilder objectMapperBuilder;

    @BeforeEach
    public void setUp() {
        doNothing().when(mailService).send(any(NotificationEmailSpec.class));

        objectMapper = objectMapperBuilder.build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        populateDB();

        operatorUserDetails = userUtils.buildUserDetails(abstractTestUser, abstractTestEntity, RoleName.OPERATOR);
        entityAdminUserDetails = userUtils.buildUserDetails(abstractTestUser, abstractTestEntity, RoleName.GROUP_ADMIN);
        businessAdminUserDetails = userUtils.buildUserDetails(abstractTestUser, abstractTestBusiness, RoleName.GROUP_ADMIN);
        sysAdminUserDetails = userUtils.buildUserDetails(abstractTestUser, root, RoleName.SYS_ADMIN);
        officialInChargeUserDetails = userUtils.buildUserDetails(abstractTestUser, root, RoleName.OFFICIAL_IN_CHARGE);
    }

    @AfterEach
    public void tearDown() {
        userRegistrationRequestRepository.deleteAll();
        userGuideRepository.deleteAll();
        messageUserStatusRepository.deleteAll();
        userProfileRepository.deleteAll();
        grantedAuthorityRepository.deleteAll();
        userRepository.deleteAll();
        groupConfigurationRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        exchangeRuleRepository.deleteAll();
        channelRepository.deleteAll();
        messageRepository.deleteAll();
        groupRepository.deleteAll();
        templateRepository.deleteAll();
    }

    protected SecurityUserDetails createEntityAndOperatorUserDetails(Group business) {
        Group otherEntity = groupService.create(GroupSpec.builder()
                .parentGroupId(business.getId())
                .identifier(RandomStringUtils.random(12, true, false).toUpperCase())
                .displayName(RandomStringUtils.random(12, true, false))
                .type(GroupType.ENTITY)
                .isActive(true)
                .build());
        String ecasId = RandomStringUtils.random(7, true, false).toLowerCase();
        User otherEntityUser = userUtils.createUserProfile(ecasId, otherEntity.getId(), sysAdminUserDetails, mockMvc).getUser();

        return userUtils.buildUserDetails(otherEntityUser, otherEntity, RoleName.OPERATOR);
    }

    private void populateDB() {
        abstractTestUser = userService.create(TEST_USER_ID, "name", "abstractUser@test.com");
        otherUser = userService.create("otherUser", "otherUser", "otherUser@test.com");
        operatorRole = roleRepository.save(mockRole(RoleName.OPERATOR));
        groupAdminRole = roleRepository.save(mockRole(RoleName.GROUP_ADMIN));
        sysAdminRole = roleRepository.save(mockRole(RoleName.SYS_ADMIN));
        officialRole = roleRepository.save(mockRole(RoleName.OFFICIAL_IN_CHARGE));

        root = groupService.create(GroupSpec.builder()
                .identifier(TEST_ROOT_GROUP_IDENTIFIER)
                .displayName(TEST_ROOT_GROUP_IDENTIFIER)
                .type(GroupType.ROOT)
                .isActive(true)
                .build());

        try {
            saveDefaultTemplates();
        } catch (IOException e) {
            throw new EtxWebException("Error reading templates from files", e);
        }

        abstractTestBusiness = groupService.create(GroupSpec.builder()
                .parentGroupId(root.getId())
                .identifier(TEST_BUSINESS_IDENTIFIER)
                .displayName(TEST_BUSINESS_NAME)
                .type(GroupType.BUSINESS)
                .isActive(true)
                .build());

        abstractTestEntity = groupService.create(GroupSpec.builder()
                .parentGroupId(abstractTestBusiness.getId())
                .identifier(TEST_ENTITY_IDENTIFIER)
                .displayName(TEST_ENTITY_NAME)
                .type(GroupType.ENTITY)
                .isActive(true)
                .build());

        saveUserProfiles();
        saveGrantedAuthorities();
    }

    private void saveDefaultTemplates() throws IOException {
        String newMessageNotificationTemplate = IOUtils.toString(new ClassPathResource("templates/new-message-notification.html").getInputStream(), StandardCharsets.UTF_8);
        String messageStatusNotificationTemplate = IOUtils.toString(new ClassPathResource("templates/message-status-notification.html").getInputStream(), StandardCharsets.UTF_8);
        String userConfiguredNotificationTemplate = IOUtils.toString(new ClassPathResource("templates/user-configured-notification.html").getInputStream(), StandardCharsets.UTF_8);
        String businessDeletionTemplate = IOUtils.toString(new ClassPathResource("templates/business-deletion-notification.html").getInputStream(), StandardCharsets.UTF_8);

        List<Template> templates = new ArrayList<>();

        templates.add(templateRepository.save(new Template(1L, TemplateType.MESSAGE_SUMMARY_VIEW, "<span>Test Custom Summary View</span>", Collections.emptyList())));
        templates.add(templateRepository.save(new Template(2L, TemplateType.MESSAGE_STATUS_NOTIFICATION, messageStatusNotificationTemplate, Collections.emptyList())));
        templates.add(templateRepository.save(new Template(3L, TemplateType.NEW_MESSAGE_NOTIFICATION, newMessageNotificationTemplate, Collections.emptyList())));
        templates.add(templateRepository.save(new Template(4L, TemplateType.USER_CONFIGURED_NOTIFICATION, userConfiguredNotificationTemplate, Collections.emptyList())));
        templates.add(templateRepository.save(new Template(5L, TemplateType.BUSINESS_DELETION_NOTIFICATION, businessDeletionTemplate, Collections.emptyList())));
        templates.add(templateRepository.save(new Template(6L, TemplateType.PENDING_USER_REGISTRATION_REQUEST_NOTIFICATION, businessDeletionTemplate, Collections.emptyList())));
        templates.add(templateRepository.save(new Template(7L, TemplateType.UPDATE_RETENTION_POLICY_NOTIFICATION, businessDeletionTemplate, Collections.emptyList())));

        root.setTemplates(templates);
        groupRepository.save(root);
    }

    private void saveGrantedAuthorities() {
        grantedAuthorityRepository.save(new GrantedAuthority(abstractTestUser, abstractTestEntity, operatorRole));
        grantedAuthorityRepository.save(new GrantedAuthority(abstractTestUser, abstractTestEntity, groupAdminRole));
        grantedAuthorityRepository.save(new GrantedAuthority(abstractTestUser, abstractTestBusiness, groupAdminRole));
        grantedAuthorityRepository.save(new GrantedAuthority(abstractTestUser, root, sysAdminRole));
    }

    private void saveUserProfiles() {
        userProfileService.create(CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId())
                .name(otherUser.getEcasId() + NAME_SUFFIX)
                .groupId(abstractTestBusiness.getId())
                .alternativeEmail(otherUser.getEcasId() + EMAIL_SUFFIX)
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .build());


        userProfileService.create(CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId())
                .name(otherUser.getEcasId() + NAME_SUFFIX)
                .groupId(abstractTestEntity.getId())
                .alternativeEmail(otherUser.getEcasId() + EMAIL_SUFFIX)
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .build());
    }

}
