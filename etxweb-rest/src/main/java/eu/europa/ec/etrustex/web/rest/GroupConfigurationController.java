package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.*;
import eu.europa.ec.etrustex.web.hateoas.GroupConfigurationLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.*;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.GROUP_CONFIGURATION;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.GROUP_CONFIGURATIONS;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GroupConfigurationController {

    private final GroupConfigurationService groupConfigurationService;
    private final GroupConfigurationLinksHandler groupConfigurationLinksHandler;
    private final GroupService groupService;


    @GetMapping(value = GROUP_CONFIGURATION)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public GroupConfiguration<?> get(@PathVariable Long groupId, @RequestParam String dtype) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.findByGroupIdAndType(groupId, dtype));
    }   

    @GetMapping(value = GROUP_CONFIGURATIONS)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<GroupConfiguration<?>> getByGroup(@RequestParam Long groupId) {
        List<GroupConfiguration<?>> groupConfigurations = groupConfigurationService.findByGroupId(groupId);

        groupConfigurations.forEach(groupConfigurationLinksHandler::addLinks);
        return groupConfigurations;
    }

    @PutMapping(value = WindowsCompatibleFilenamesGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public WindowsCompatibleFilenamesGroupConfiguration updateWindowsCompatibleFilenamesGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody BooleanGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, WindowsCompatibleFilenamesGroupConfiguration.class, spec));
    }

    @PutMapping(value = ForbiddenExtensionsGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public ForbiddenExtensionsGroupConfiguration updateForbiddenExtensionsGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody ForbiddenExtensionsGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, ForbiddenExtensionsGroupConfiguration.class, spec));
    }

    @PutMapping(value = RetentionPolicyGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public RetentionPolicyGroupConfiguration updateRetentionPolicyGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody RetentionPolicyGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, RetentionPolicyGroupConfiguration.class, spec));
    }

    @PutMapping(value = RetentionPolicyEntityConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public RetentionPolicyEntityConfiguration updateRetentionPolicyEntityConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody RetentionPolicyGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, RetentionPolicyEntityConfiguration.class, spec));
    }

    @PutMapping(value = RetentionPolicyNotificationGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public RetentionPolicyNotificationGroupConfiguration updateRetentionPolicyNotificationGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody RetentionPolicyNotificationGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, RetentionPolicyNotificationGroupConfiguration.class, spec));
    }

    @PutMapping(value = SplashScreenGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public SplashScreenGroupConfiguration updateSplashScreenGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody SplashScreenGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, SplashScreenGroupConfiguration.class, spec));
    }

    @PutMapping(value = DisableEncryptionGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public DisableEncryptionGroupConfiguration updateDisableEncryptionGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody BooleanGroupConfigurationSpec spec) {
        if (spec.isActive()) {
            groupService.disableEncryption(groupId);
        }
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, DisableEncryptionGroupConfiguration.class, spec));
    }

    @PutMapping(value = SignatureGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public SignatureGroupConfiguration updateSignatureGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody BooleanGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, SignatureGroupConfiguration.class, spec));
    }

    @PutMapping(value = NotificationsEmailFromGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public NotificationsEmailFromGroupConfiguration updateNotificationsEmailFromGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody NotificationsEmailFromGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, NotificationsEmailFromGroupConfiguration.class, spec));
    }

    @PutMapping(value = UnreadMessageReminderConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public UnreadMessageReminderConfiguration updateUnreadMessageReminderConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody UnreadMessageReminderConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, UnreadMessageReminderConfiguration.class, spec));
    }

    @PutMapping(value = EnforceEncryptionGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public EnforceEncryptionGroupConfiguration updateEnforceEncryptionGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody BooleanGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, EnforceEncryptionGroupConfiguration.class, spec));
    }

    @PutMapping(value = FileSizeLimitationGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public FileSizeLimitationGroupConfiguration updateFileSizeLimitationGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody IntegerGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, FileSizeLimitationGroupConfiguration.class, spec));
    }

    @PutMapping(value = TotalFileSizeLimitationGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public TotalFileSizeLimitationGroupConfiguration updateTotalFileSizeLimitationGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody IntegerGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, TotalFileSizeLimitationGroupConfiguration.class, spec));
    }

    @PutMapping(value = NumberOfFilesLimitationGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public NumberOfFilesLimitationGroupConfiguration updateNumberOfFilesLimitationGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody IntegerGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, NumberOfFilesLimitationGroupConfiguration.class, spec));
    }

    @PutMapping(value = LogoGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public LogoGroupConfiguration updateLogoGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody StringGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, LogoGroupConfiguration.class, spec));
    }

    @PutMapping(value = SupportEmailGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public SupportEmailGroupConfiguration updateSupportEmailGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody StringGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, SupportEmailGroupConfiguration.class, spec));
    }

    @PutMapping(value = WelcomeEmailGroupConfiguration.URL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public WelcomeEmailGroupConfiguration updateWelcomeEmailGroupConfiguration(SecurityUserDetails principal, @PathVariable Long groupId, @Valid @RequestBody BooleanGroupConfigurationSpec spec) {
        return groupConfigurationLinksHandler.addLinks(groupConfigurationService.updateValue(principal.getUser(), groupId, WelcomeEmailGroupConfiguration.class, spec));
    }

}
