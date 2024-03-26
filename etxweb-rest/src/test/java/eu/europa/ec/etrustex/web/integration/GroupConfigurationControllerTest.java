package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.*;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.GroupUtils;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.IntegerGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.StringGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.*;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class GroupConfigurationControllerTest extends AbstractControllerTest {

    @Autowired
    protected GroupUtils groupUtils;

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        validationTests();
    }


    private void happyFlows() throws Exception {
        should_retrieve_GroupConfiguration(abstractTestBusiness, sysAdminUserDetails);
        should_retrieve_GroupConfiguration(abstractTestBusiness, businessAdminUserDetails);
        should_retrieve_GroupConfiguration(abstractTestBusiness, entityAdminUserDetails);
        should_retrieve_GroupConfiguration(abstractTestBusiness, operatorUserDetails);
        should_retrieve_GroupConfigurationByGroup(abstractTestBusiness, sysAdminUserDetails);
        should_retrieve_GroupConfigurationByGroup(abstractTestBusiness, businessAdminUserDetails);
        should_retrieve_GroupConfigurationByGroup(abstractTestBusiness, entityAdminUserDetails);
        should_retrieve_GroupConfigurationByGroup(abstractTestBusiness, operatorUserDetails);

        should_update_GroupConfiguration(abstractTestBusiness, sysAdminUserDetails);
        should_update_GroupConfiguration(abstractTestBusiness, businessAdminUserDetails);
    }


    private void validationTests() throws Exception {
        validateUpdatedGroupConfiguration(ForbiddenExtensionsGroupConfiguration.URL, abstractTestBusiness, businessAdminUserDetails,
                ForbiddenExtensionsGroupConfigurationSpec.builder().active(true).value("bar,bar, asdf.-asdf-").build(),
                FORBIDDEN_EXTENSIONS_NO_REPETITIONS, FORBIDDEN_EXTENSIONS_FORMAT_ERROR_MSG);

        validateUpdatedGroupConfiguration(RetentionPolicyGroupConfiguration.URL, abstractTestBusiness, businessAdminUserDetails,
                RetentionPolicyGroupConfigurationSpec.builder().active(true).value(7301).build(),
                RETENTION_POLICY_MAX_LENGTH_ERROR_MSG);
    }

    private void should_retrieve_GroupConfiguration(Group group, SecurityUserDetails securityUserDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.GROUP_CONFIGURATION, group.getId())
                                .with(user(securityUserDetails))
                                .param("dtype", WindowsCompatibleFilenamesGroupConfiguration.class.getSimpleName())
                )
                .andExpect(status().isOk())
                .andReturn();

        WindowsCompatibleFilenamesGroupConfiguration windowsCompatibleFilenamesGroupConfiguration = objectMapper.readValue(result.getResponse().getContentAsString(), WindowsCompatibleFilenamesGroupConfiguration.class);

        assertNotNull(windowsCompatibleFilenamesGroupConfiguration);
    }

    @SuppressWarnings("SameParameterValue")
    private void should_retrieve_GroupConfigurationByGroup(Group group, SecurityUserDetails securityUserDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.GROUP_CONFIGURATIONS)
                                .with(user(securityUserDetails))
                                .param("groupId", "" + group.getId()))
                .andExpect(status().isOk())
                .andReturn();

        List<RetentionPolicyGroupConfiguration> groupConfigurations = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RetentionPolicyGroupConfiguration>>() {
        });

        Assertions.assertEquals(17, groupConfigurations.size());
    }


    private void should_update_GroupConfiguration(Group group, SecurityUserDetails securityUserDetails) throws Exception {
        updatedGroupConfiguration(WindowsCompatibleFilenamesGroupConfiguration.URL, group, securityUserDetails,
                BooleanGroupConfigurationSpec.builder().active(true).build(), WindowsCompatibleFilenamesGroupConfiguration.class);

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        updatedGroupConfiguration(ForbiddenExtensionsGroupConfiguration.URL, group, securityUserDetails,
                ForbiddenExtensionsGroupConfigurationSpec.builder().active(true).value("FOO,BAR").build(), ForbiddenExtensionsGroupConfiguration.class);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false);

        updatedGroupConfiguration(RetentionPolicyGroupConfiguration.URL, group, securityUserDetails,
                RetentionPolicyGroupConfigurationSpec.builder().active(true).value(123).build(), RetentionPolicyGroupConfiguration.class);

        updatedGroupConfiguration(RetentionPolicyNotificationGroupConfiguration.URL, group, securityUserDetails,
                RetentionPolicyNotificationGroupConfigurationSpec.builder().active(true).value(123).build(), RetentionPolicyNotificationGroupConfiguration.class);

        updatedGroupConfiguration(SplashScreenGroupConfiguration.URL, group, securityUserDetails,
                SplashScreenGroupConfigurationSpec.builder().active(true).value("Hi").build(), SplashScreenGroupConfiguration.class);

        updatedGroupConfiguration(DisableEncryptionGroupConfiguration.URL, group, securityUserDetails,
                BooleanGroupConfigurationSpec.builder().active(true).build(), DisableEncryptionGroupConfiguration.class);

        updatedGroupConfiguration(SignatureGroupConfiguration.URL, group, securityUserDetails,
                BooleanGroupConfigurationSpec.builder().active(true).build(), SignatureGroupConfiguration.class);

        updatedGroupConfiguration(NotificationsEmailFromGroupConfiguration.URL, group, securityUserDetails,
                NotificationsEmailFromGroupConfigurationSpec.builder().active(true).value("hi@ec.europa.eu").build(), NotificationsEmailFromGroupConfiguration.class);

        updatedGroupConfiguration(UnreadMessageReminderConfiguration.URL, group, securityUserDetails,
                UnreadMessageReminderConfigurationSpec.builder().active(true).value(123).build(), UnreadMessageReminderConfiguration.class);

        updatedGroupConfiguration(EnforceEncryptionGroupConfiguration.URL, group, securityUserDetails,
                BooleanGroupConfigurationSpec.builder().active(true).build(), EnforceEncryptionGroupConfiguration.class);

        updatedGroupConfiguration(FileSizeLimitationGroupConfiguration.URL, group, securityUserDetails,
                IntegerGroupConfigurationSpec.builder().active(true).value(123).build(), FileSizeLimitationGroupConfiguration.class);

        updatedGroupConfiguration(TotalFileSizeLimitationGroupConfiguration.URL, group, securityUserDetails,
                IntegerGroupConfigurationSpec.builder().active(true).value(123).build(), TotalFileSizeLimitationGroupConfiguration.class);

        updatedGroupConfiguration(NumberOfFilesLimitationGroupConfiguration.URL, group, securityUserDetails,
                IntegerGroupConfigurationSpec.builder().active(true).value(123).build(), NumberOfFilesLimitationGroupConfiguration.class);

        updatedGroupConfiguration(LogoGroupConfiguration.URL, group, securityUserDetails,
                StringGroupConfigurationSpec.builder().active(true).value("logo").build(), LogoGroupConfiguration.class);

        updatedGroupConfiguration(SupportEmailGroupConfiguration.URL, group, securityUserDetails,
                StringGroupConfigurationSpec.builder().active(true).value("email@test.com").build(), SupportEmailGroupConfiguration.class);

        updatedGroupConfiguration(WelcomeEmailGroupConfiguration.URL, group, securityUserDetails,
                BooleanGroupConfigurationSpec.builder().active(true).build(), WelcomeEmailGroupConfiguration.class);

    }

    private <Q, R extends GroupConfiguration<Q>, S extends GroupConfigurationSpec<Q>> void updatedGroupConfiguration(String url, Group group,
                                                                                                                     SecurityUserDetails securityUserDetails,
                                                                                                                     S spec,
                                                                                                                     Class<R> implementationClass) throws Exception {
        MvcResult result = mockMvc.perform(
                        put(url, group.getId())
                                .with(user(securityUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(spec)))
                .andExpect(status().isOk())
                .andReturn();

        R updatedGroupConfiguration = objectMapper.readValue(result.getResponse().getContentAsString(), implementationClass);

        Assertions.assertEquals(spec.isActive(), updatedGroupConfiguration.isActive());

        if (spec instanceof StringGroupConfigurationSpec) {
            Assertions.assertEquals(((StringGroupConfigurationSpec) spec).getValue(), ((StringGroupConfiguration) updatedGroupConfiguration).getStringValue());
        } else if (spec instanceof IntegerGroupConfigurationSpec) {
            Assertions.assertEquals(((IntegerGroupConfigurationSpec) spec).getValue(), ((IntegerGroupConfiguration) updatedGroupConfiguration).getIntegerValue());
        }
    }

    private <Q, S extends GroupConfigurationSpec<Q>> void validateUpdatedGroupConfiguration(String url, Group group,
                                                                                            SecurityUserDetails securityUserDetails,
                                                                                            S spec,
                                                                                            String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        put(url, group.getId())
                                .with(user(securityUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(spec)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }

}
