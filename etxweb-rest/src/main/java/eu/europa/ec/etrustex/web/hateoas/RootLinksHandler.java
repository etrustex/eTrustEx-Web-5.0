package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.rest.*;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static eu.europa.ec.etrustex.web.util.exchange.model.Rels.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class RootLinksHandler {
    private final LinksConverter linksConverter;

    @SuppressWarnings("ConstantConditions")
    public RootLinks addLinks(RootLinks rootLinks) {
        rootLinks.getLinks()
                .addAll(
                        Arrays.asList(
                                linksConverter.convert(linkTo(methodOn(MessageController.class).create(null, null)).withRel(MESSAGE_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).get(null, null, null, null, null, null, null, null, null)).withRel(MESSAGE_SUMMARIES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).get(null, null, null, null)).withRel(MESSAGE_SUMMARY_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).markRead(null, null, null)).withRel(MESSAGE_SUMMARIES_MARK_READ.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageController.class).markRead(null, null)).withRel(MESSAGES_MARK_READ.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageController.class).get(null, null, null, null, null, null, null, null, null)).withRel(MESSAGES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageController.class).countUnreadSent(null, null)).withRel(MESSAGES_COUNT_UNREAD_SENT.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryUserStatusController.class).get(null, null, null, null, null, null, null, null)).withRel(MESSAGES_MONITORING.toString())),
                                linksConverter.convert(linkTo(methodOn(UserController.class).getUserDetails(null)).withRel(USER_DETAILS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).get(null)).withRel(GROUP_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).get(null, null, null, null, null, null, null, null, null, null)).withRel(GROUPS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageController.class).get(null, null, null, null)).withRel(MESSAGE_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).getValidRecipients(null)).withRel(VALID_RECIPIENTS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(SettingsController.class).getEnvironment()).withRel(SETTINGS_ENVIRONMENT_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(SettingsController.class).getPublicKey()).withRel(SETTINGS_PUBLIC_KEY_GET.toString())),

                                linksConverter.convert(linkTo(methodOn(GroupController.class).create(null)).withRel(GROUP_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).countUnread(null, null, null)).withRel(MESSAGE_SUMMARIES_COUNT_UNREAD.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).create(null)).withRel(USER_PROFILE_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).fetchEuLoginInfo(null, null)).withRel(USER_PROFILE_EULOGIN.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).get(null, null, null, null, null, null)).withRel(CHANNELS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).get(null, null)).withRel(CHANNEL_GET_BY_GROUP.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).find(null, null)).withRel(CHANNEL_SEARCH.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).get(null)).withRel(CHANNEL_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).create(null)).withRel(CHANNEL_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).validate(null)).withRel(CHANNEL_VALIDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).update(null, null)).withRel(CHANNEL_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).delete(null)).withRel(CHANNEL_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(ChannelController.class).exportChannels(null)).withRel(EXPORT_CHANNELS.toString())),
                                linksConverter.convert(linkTo(methodOn(GrantedAuthorityController.class).create(null)).withRel(GRANTED_AUTHORITY_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(GrantedAuthorityController.class).updateGroup(null, null)).withRel(GRANTED_AUTHORITIES_UPDATE_GROUP.toString())),
                                linksConverter.convert(linkTo(methodOn(GrantedAuthorityController.class).updateGroupBulk(null, null)).withRel(GRANTED_AUTHORITIES_UPDATE_GROUP_BULK.toString())),
                                linksConverter.convert(linkTo(methodOn(GrantedAuthorityController.class).delete(null)).withRel(Rels.GRANTED_AUTHORITY_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).get(null, null, null, null, null, null, null)).withRel(EXCHANGE_RULES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).search(null, null)).withRel(EXCHANGE_RULES_SEARCH.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).find(null, null)).withRel(GROUPS_SEARCH.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).bulkCreate(null, null)).withRel(EXCHANGE_RULES_BULK_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).update(null)).withRel(EXCHANGE_RULES_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).delete(null)).withRel(EXCHANGE_RULES_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).uploadBulk(null, null)).withRel(EXCHANGE_RULES_UPLOAD_BULK.toString())),
                                linksConverter.convert(linkTo(methodOn(RecipientPreferencesController.class).get(null)).withRel(RECIPIENT_PREFERENCES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(RecipientPreferencesController.class).create(null)).withRel(RECIPIENT_PREFERENCES_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).getUserListItems(null, null, null, null, null, null, null, null, null)).withRel(USER_LIST_ITEMS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).search(null, null, null, null)).withRel(USER_PROFILES_SEARCH.toString())),

                                linksConverter.convert(linkTo(methodOn(UserExportController.class).exportUsers(null)).withRel(EXPORT_USERS.toString())),
                                linksConverter.convert(linkTo(methodOn(AlertController.class).get(null, null, null)).withRel(ALERT_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(AlertController.class).create(null)).withRel(ALERT_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).delete(null)).withRel(USER_PROFILE_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).retrieveEmailsForNotification(null)).withRel(USER_PROFILE_NOTIFICATION_EMAILS.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).sendNotificationEmails(null)).withRel(USER_PROFILE_SEND_NOTIFICATION_EMAILS.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupConfigurationController.class).getByGroup(null)).withRel(GROUP_CONFIGURATIONS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupConfigurationController.class).get(null, null)).withRel(GROUP_CONFIGURATION_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).isBusinessEmpty(null)).withRel(IS_BUSINESS_EMPTY.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).deleteGroup(null)).withRel(GROUP_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).deleteBusiness(null)).withRel(BUSINESS_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).cancelBusinessDeletion(null)).withRel(CANCEL_BUSINESS_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).confirmBusinessDeletion(null)).withRel(CONFIRM_BUSINESS_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).update(null)).withRel(USER_PROFILE_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).uploadBulk(null, null)).withRel(USER_PROFILE_UPLOAD_BULK.toString())),
                                linksConverter.convert(linkTo(methodOn(UserGuideController.class).get(null, null, null)).withRel(USER_GUIDE_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(UserGuideController.class).getByBusinessId(null)).withRel(USER_GUIDES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(UserGuideController.class).uploadUserGuide(null, null, null, null, null)).withRel(USER_GUIDE_FILE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserGuideController.class).delete(null, null, null)).withRel(USER_GUIDE_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).uploadBulk(null, null)).withRel(GROUP_UPLOAD_BULK.toString())),
                                linksConverter.convert(linkTo(methodOn(AlertUserStatusController.class).find(null, null)).withRel(ALERT_USER_STATUS_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(AlertUserStatusController.class).create(null, null)).withRel(ALERT_USER_STATUS_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(AlertUserStatusController.class).update(null)).withRel(ALERT_USER_STATUS_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserController.class).updateUserPreferences(null, null)).withRel(USER_PREFERENCES_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserRegistrationRequestController.class).create(null, null)).withRel(USER_REGISTRATION_REQUEST_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserRegistrationRequestController.class).delete(null)).withRel(USER_REGISTRATION_REQUEST_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(UserRegistrationRequestController.class).update(null)).withRel(USER_REGISTRATION_REQUEST_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(GrantedAuthorityController.class).createBulk(null)).withRel(GRANTED_AUTHORITIES_CREATE_GROUP_BULK.toString())),
                                linksConverter.convert(linkTo(methodOn(CertificateUpdateController.class).generateLinkToUpdateCertificate(null)).withRel(CERTIFICATE_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(CertificateUpdateController.class).updateCompromisedMessages(null, null)).withRel(CERTIFICATE_UPDATE_COMPROMISED_MESSAGES.toString())),
                                linksConverter.convert(linkTo(methodOn(UserProfileController.class).fetchUserProfileInfo(null, null)).withRel(USER_PROFILE_INFO.toString())),
                                linksConverter.convert(linkTo(methodOn(AttachmentController.class).bulkDelete(null, null, null)).withRel(ATTACHMENTS_DELETE.toString())),
                                linksConverter.convert(linkTo(methodOn(CertificateUpdateController.class).isValidRedirectLink(null, null, null)).withRel(CERTIFICATE_UPDATE_IS_VALID_REDIRECT_LINK.toString())),

                                linksConverter.convert(linkTo(methodOn(TemplateController.class).get(null, null)).withRel(TEMPLATES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(TemplateController.class).getDefault(null)).withRel(DEFAULT_TEMPLATES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(TemplateController.class).update(null)).withRel(TEMPLATES_UPDATE.toString())),

                                linksConverter.convert(linkTo(methodOn(QuickStartController.class).create(null)).withRel(QUICK_START.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).isValid(null)).withRel(IS_VALID_GROUP.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).getMessagesDisplay(null, null, null, null, null, null)).withRel(MESSAGES_DISPLAY.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).search(null, null)).withRel(MESSAGES_SEARCH.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).updateActiveStatus(null, null, null, null)).withRel(MESSAGE_SUMMARY_UPDATE_ACTIVE_STATUS.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).updateMultipleActiveStatus(null, null, null)).withRel(MESSAGE_SUMMARIES_UPDATE.toString())),
                                linksConverter.convert(linkTo(methodOn(MessageController.class).isReadyToSend(null)).withRel(MESSAGE_IS_READY_TO_SEND_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ExchangeRuleController.class).uploadParticipantsBulk(null, null, null)).withRel(EXCHANGE_RULES_PARTICIPANTS_UPLOAD_BULK.toString())),
                                linksConverter.convert(linkTo(methodOn(GroupController.class).isGroupEmpty(null)).withRel(IS_GROUP_EMPTY.toString()))

                    )
            );

        return rootLinks;
    }
}
