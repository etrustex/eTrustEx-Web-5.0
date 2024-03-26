package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.repository.UserRegistrationRequestRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.UserRegistrationRedirectRepository;
import eu.europa.ec.etrustex.web.service.notification.NotificationService;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.validation.model.BaseUserRegistrationRequestSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserRegistrationRequestSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class UserRegistrationRequestServiceTest {
    private static final String ECAS_ID = "ecasID";
    public static final String EMAIL = "email@gmail.com";
    public static final String TEST_USER = "testUser";

    @Mock
    private UserRegistrationRequestRepository userRegistrationRequestRepository;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private RedirectService redirectService;
    @Mock
    private MailService mailService;
    @Mock
    private UserRegistrationRedirectRepository userRegistrationRedirectRepository;
    @Mock
    private NotificationService notificationService;

    private UserRegistrationRequestService userRegistrationRequestService;

    @BeforeEach
    public void setUp() {
        this.userRegistrationRequestService = new UserRegistrationRequestServiceImpl(userRegistrationRequestRepository,
                userProfileService, redirectService, userRegistrationRedirectRepository, mailService, notificationService);
    }

    @Test
    void should_create_user_registration_request() {
        UserRegistrationRequestSpec userRegistrationRequestSpec = UserRegistrationRequestSpec.builder()
                .groupId(15L)
                .ecasId(ECAS_ID)
                .notificationEmail(EMAIL)
                .build();
        UserProfile userProfile = EntityTestUtils.mockUserProfile(EntityTestUtils.mockUser(userRegistrationRequestSpec.getEcasId(), TEST_USER), EntityTestUtils.mockGroup());

        UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
                .user(userProfile.getUser())
                .group(userProfile.getGroup())
                .build();

        when(userProfileService.create(any())).thenReturn(userProfile);
        when(userRegistrationRequestRepository.save(any())).thenReturn(userRegistrationRequest);

        UserRegistrationRequest savedUserRegistrationRequest = userRegistrationRequestService.create(userProfile.getUser(), userRegistrationRequestSpec);
        assertEquals(userRegistrationRequestSpec.getEcasId(), savedUserRegistrationRequest.getUser().getEcasId());
    }

    @Test
    void should_update() {
        UserRegistrationRequestSpec userRegistrationRequestSpec = UserRegistrationRequestSpec.builder()
                .groupId(15L)
                .ecasId(ECAS_ID)
                .notificationEmail(EMAIL)
                .build();
        UserProfile userProfile = EntityTestUtils.mockUserProfile(EntityTestUtils.mockUser(userRegistrationRequestSpec.getEcasId(), TEST_USER), EntityTestUtils.mockGroup());
        UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
                .user(userProfile.getUser())
                .group(userProfile.getGroup())
                .isAdmin(false)
                .isOperator(false)
                .build();

        when(userRegistrationRequestRepository.findByUserEcasIdAndGroupId(any(), any())).thenReturn(Optional.of(userRegistrationRequest));
        when(userRegistrationRequestRepository.save(any())).thenReturn(userRegistrationRequest);

        assertEquals(userRegistrationRequest, userRegistrationRequestService.update(userRegistrationRequestSpec));
    }

    @Test
    void should_delete_user_registration_request_and_cleanup() {
        UserRegistrationRequestSpec userRegistrationRequestSpec = UserRegistrationRequestSpec.builder()
                .groupId(15L)
                .ecasId(ECAS_ID)
                .notificationEmail(EMAIL)
                .build();
        UserProfile userProfile = EntityTestUtils.mockUserProfile(EntityTestUtils.mockUser(userRegistrationRequestSpec.getEcasId(), TEST_USER), EntityTestUtils.mockGroup());

        UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
                .user(userProfile.getUser())
                .group(userProfile.getGroup())
                .build();

        when(userRegistrationRequestRepository.findByUserEcasIdAndGroupId(any(), any())).thenReturn(Optional.of(userRegistrationRequest));
        when(userProfileService.findUserProfileByUserAndGroup(any(), any())).thenReturn(userProfile);

        BaseUserRegistrationRequestSpec baseUserRegistrationRequestSpec = BaseUserRegistrationRequestSpec.builder()
                .ecasId(userRegistrationRequest.getUser().getEcasId())
                .groupId(userRegistrationRequest.getGroup().getId())
                .build();
        userRegistrationRequestService.deleteUserRegistrationRequestAndCleanUp(Collections.singletonList(baseUserRegistrationRequestSpec));

        verify(userRegistrationRequestRepository, times(1)).deleteByUserIdAndGroupId(any(), any());
        verify(userRegistrationRedirectRepository, times(1)).deleteByGroupIdAndEmailAddress(any(), any());
        verify(userProfileService, times(1)).delete(any(), any());
    }
}
