package eu.europa.ec.etrustex.web.rest.logging;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.rest.*;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;

public enum LogRenderingEnum implements LogRenderingEnumConstants {

    DRAFT_SAVE(MessageController.class.getName(), "draft", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String messageId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the New Message section and has saved a Draft message " + WITH_ID + messageId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    DRAFT_REOPEN(MessageController.class.getName(), CLASS_METHOD_GET, HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            if (controllerLoggingItem.getParameters().size() == 1) {
                Long messageId = controllerLoggingItem.getLastURLElement();
                User user = controllerLoggingItem.getUserService().findByEcasId(controllerLoggingItem.getUserId());
                String clientPublicKey = controllerLoggingItem.getHeaders().get(CLIENT_PUBLIC_KEY_HEADER_NAME);
                Message dbMessage = controllerLoggingItem.getMessageService().getMessage(messageId, controllerLoggingItem.getParameters().get("senderEntityId") != null ? Long.valueOf(controllerLoggingItem.getParameters().get("senderEntityId")) : null, clientPublicKey, user);
                if (Status.DRAFT.equals(dbMessage.getStatus())) {
                    return controllerLoggingItem.getMethodStartTimestamp() + " " +
                            USER + "'" + controllerLoggingItem.getUserId() + "' " +
                            "is in the Draft folder and has selected or re-opened a Draft message" + WITH_ID + messageId + "' " +
                            (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
                }
            }
            return "";
        }
    },
    MESSAGE_DELETION(MessageController.class.getName(), CLASS_METHOD_DELETE, HttpMethod.DELETE.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long messageId = controllerLoggingItem.getLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "has deleted a message " + WITH_ID + messageId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    SEND_MESSAGE_FILE_UPLOAD(AttachmentController.class.getName(), "upload", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String attachmentId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the New Message section and has performed an upload of a file having an attachment id " + " '" + attachmentId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    SEND_MESSAGE_FILE_DELETE(AttachmentController.class.getName(), CLASS_METHOD_DELETE, HttpMethod.DELETE.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long attachmentId = controllerLoggingItem.getLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the New Message section and has performed the deletion of a file" + WITH_ID + attachmentId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    SEND_MESSAGE_SEND(MessageController.class.getName(), CLASS_METHOD_UPDATE, HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long messageId = controllerLoggingItem.getLastURLElement();

            String result = controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the New Message section and has sent a message " + WITH_ID + messageId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);

            if (StringUtils.isNotBlank(controllerLoggingItem.getErrorMessage())){
                result = result + "\n" + controllerLoggingItem.getErrorMessage();
            }

            return result;
        }
    },
    INBOX_ACCESS_INBOX_OR_READ_MESSAGE(MessageSummaryController.class.getName(), CLASS_METHOD_GET, HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            //INBOX ACCESS
            if (controllerLoggingItem.getParameters().size() > 1) {
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " +
                        "has accessed his Inbox folder " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            }
            //Reading a Message
            else if (controllerLoggingItem.getParameters().size() == 1) {
                String messageId = controllerLoggingItem.getBeforeLastURLElement();
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " +
                        "has read a message " + WITH_ID + messageId + "' " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            } else {
                return "";
            }
        }
    },
    INBOX_DOWNLOAD_FILES(AttachmentController.class.getName(), "download", HttpMethod.GET.name(), "is in the Inbox folder and downloaded one or more files "),
    SENT_DOWNLOAD_TRANSMISSION_PDF(PDFController.class.getName(), "getReceipt", HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String messageId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "in is the Sent folder and downloaded a transmission PDF for message " + WITH_ID + messageId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    GENERAL_CHANGE_ENTITIES(GroupController.class.getName(), CLASS_METHOD_GET, HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String url = controllerLoggingItem.getRequestURL().substring(controllerLoggingItem.getRequestURL().indexOf(LoggingInterceptor.API_PREFIX));
            if (StringUtils.isNumeric(url.substring(url.lastIndexOf('/') + 1))) {
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " +
                        "has changed entity to " + controllerLoggingItem.getLastURLElement() +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            }
            return "";
        }
    },
    ADMIN_BUSINESS_NEW_GROUP(GroupController.class.getName(), CLASS_METHOD_CREATE, HttpMethod.POST.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Group group = controllerLoggingItem.getGroupService().findLastUpdatedGroup(controllerLoggingItem.getUserId());
            if (group.getType().equals(GroupType.BUSINESS)) {
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " +
                        "is in the business Overview Page and created a new Business with id '" + group.getId() + "' " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            } else if (group.getType().equals(GroupType.ENTITY)) {
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " + "is in the entities Tab within the business '" +
                        group.getBusinessId() + "' and created an Entity with id '" + group.getId() + "' " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            }
            return "";
        }
    },
    ADMIN_BUSINESS_DELETE_GROUP(GroupController.class.getName(), CLASS_METHOD_DELETE, HttpMethod.DELETE.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long businessId = controllerLoggingItem.getLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "has deleted a Business or Entity with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_UPDATE_GROUP(GroupController.class.getName(), CLASS_METHOD_UPDATE, HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Group group = controllerLoggingItem.getGroupService().findById(controllerLoggingItem.getLastURLElement());
            if (group.getType().equals(GroupType.BUSINESS)) {
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " +
                        "is in the business Overview Tab and updated a Business with id '" + group.getId() + "'" + " " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            } else if (group.getType().equals(GroupType.ENTITY)) {
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " + "is in the entities Tab within the business '" +
                        group.getBusinessId() + "' and updated an Entity with id '" + group.getId() + "' " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            }
            return "";
        }
    },
    ADMIN_BUSINESS_EXPORT_USERS(UserExportController.class.getName(), "exportUsers", HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Overview Tab and exported the users and functional mailboxes for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_SEND_NOTIFICATION_EMAILS(UserProfileController.class.getName(), "sendNotificationEmails", HttpMethod.POST.name(), "is in the business Overview Tab and has sent a notification email to the users "),
    ADMIN_BUSINESS_CONFIG_SPLASH(GroupConfigurationController.class.getName(), "updateSplashScreenGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the Splash Screen for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_BANNER_CREATE(AlertController.class.getName(), CLASS_METHOD_CREATE, HttpMethod.POST.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long businessId = controllerLoggingItem.getAlertService().findLastUpdatedAlert(controllerLoggingItem.getUserId()).getGroup().getId();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and created a Banner for the business with id '" + businessId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_BANNER_UPDATE(AlertController.class.getName(), CLASS_METHOD_UPDATE, HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long businessId = controllerLoggingItem.getAlertService().findLastUpdatedAlert(controllerLoggingItem.getUserId()).getGroup().getId();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated a Banner for the business with id '" + businessId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_FILE_EXTENSIONS(GroupConfigurationController.class.getName(), "updateForbiddenExtensionsGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the Forbidden Extensions for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_WINDOWS_COMPATIBLE(GroupConfigurationController.class.getName(), "updateWindowsCompatibleFilenamesGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the Windows Compatible config for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_RETENTION_POLICY(GroupConfigurationController.class.getName(), "updateRetentionPolicyGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the Retention Policy config for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_RETENTION_POLICY_NOTIFICATION(GroupConfigurationController.class.getName(), "updateRetentionPolicyNotificationGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the Retention Policy Notification config for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_ENCRYPTION(GroupConfigurationController.class.getName(), "updateDisableEncryptionGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the End-to-End encryption config for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_WELCOME_EMAIL(GroupConfigurationController.class.getName(), "updateWelcomeEmailGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the Welcome email config for the Business with id '" + businessId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_CONFIG_SIGNATURE(GroupConfigurationController.class.getName(), "updateSignatureGroupConfiguration", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String businessId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the business Config Tab and updated the signature config for the Business with id '" + businessId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_NEW_USER(UserProfileController.class.getName(), CLASS_METHOD_CREATE, HttpMethod.POST.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String ecasId = controllerLoggingItem.getUserService().findLastUpdatedUser(controllerLoggingItem.getUserId()).getEcasId();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "created a new User with ecasId '" + ecasId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_UPDATE_USER(UserProfileController.class.getName(), CLASS_METHOD_UPDATE, HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String ecasId = controllerLoggingItem.getUserService().findLastUpdatedUser(controllerLoggingItem.getUserId()).getEcasId();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "created or updated a User with ecasId '" + ecasId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_DELETE_USER(UserProfileController.class.getName(), CLASS_METHOD_DELETE, HttpMethod.DELETE.name(), "has deleted a User from the Entity (in the log below) "),
    ADMIN_BUSINESS_NEW_EXCHANGE(ChannelController.class.getName(), CLASS_METHOD_CREATE, HttpMethod.POST.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long channelId = controllerLoggingItem.getChannelService().findLastUpdatedChannel(controllerLoggingItem.getUserId()).getId();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the exchange configurations Tab and created a new Channel with id '" + channelId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_DELETE_EXCHANGE(ChannelController.class.getName(), CLASS_METHOD_DELETE, HttpMethod.DELETE.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long channelId = controllerLoggingItem.getLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the exchange configurations Tab and deleted a Channel with id '" + channelId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_UPDATE_EXCHANGE(ChannelController.class.getName(), CLASS_METHOD_UPDATE, HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long channelId = controllerLoggingItem.getLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the exchange configurations Tab and updated a Channel with id '" + channelId + "'" + " " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_EXCHANGE_ADD_PARTICIPANT(ExchangeRuleController.class.getName(), CLASS_METHOD_CREATE, HttpMethod.POST.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            ExchangeRule exchangeRule = controllerLoggingItem.getExchangeRuleService().findLastUpdatedExchangeRule(controllerLoggingItem.getUserId());
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the exchange configurations Tab and added the participant with id '" + exchangeRule.getMember().getName() + "' " +
                    " with the role " + exchangeRule.getExchangeMode().name() + " to the channel with id '" + exchangeRule.getChannel().getId() + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_EXCHANGE_UPDATE_PARTICIPANT(ExchangeRuleController.class.getName(), CLASS_METHOD_UPDATE, HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            ExchangeRule exchangeRule = controllerLoggingItem.getExchangeRuleService().findLastUpdatedExchangeRule(controllerLoggingItem.getUserId());
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the exchange configurations Tab and updated the participant with id '" + exchangeRule.getMember().getName() + "' " +
                    "to the role " + exchangeRule.getExchangeMode().name() + " in the channel with id '" + exchangeRule.getChannel().getId() + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_EXCHANGE_DELETE_PARTICIPANT(ExchangeRuleController.class.getName(), CLASS_METHOD_DELETE, HttpMethod.DELETE.name(), "has deleted a participant from the current channel (present in the logs above) "),
    ADMIN_BUSINESS_EXCHANGE_LOAD_CHANNEL_BROWSE(ExchangeRuleController.class.getName(), CLASS_METHOD_GET, HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            if (controllerLoggingItem.getParameters().containsKey("channelId")) {
                String channelId = controllerLoggingItem.getParameters().get("channelId");
                return controllerLoggingItem.getMethodStartTimestamp() + " " +
                        USER + "'" + controllerLoggingItem.getUserId() + "' " +
                        "is loading the channel with id '" + channelId + "' " +
                        (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
            }
            return "";
        }
    },
    ADMIN_BUSINESS_CHANNEL_BROWSE(ChannelController.class.getName(), "getUserListItems", HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            String channelId = controllerLoggingItem.getBeforeLastURLElement();
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is loading the channel with id '" + channelId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_ENTITIES_BULK_CREATION(GroupController.class.getName(), "updateBulk", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is in the Entities Tab and launched an entities bulk creation " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_ENTITY_BROWSE(UserProfileController.class.getName(), "getUserListItems", HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            Long entityId = Long.valueOf(controllerLoggingItem.getParameters().get("groupId"));
            Group group = controllerLoggingItem.getGroupService().findById(entityId);
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is loading the " + group.getType().name() + WITH_ID + entityId + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_ENTITY_UPDATE_ALL_USERS(GrantedAuthorityController.class.getName(), "updateGroup", HttpMethod.PUT.name(), "is in the entity configurations Tab and either updated one user or enabled/disabled all the users for the entity (loaded in the log above) "),
    ADMIN_BUSINESS_ENTITY_ASSIGN_USER(GrantedAuthorityController.class.getName(), CLASS_METHOD_CREATE, HttpMethod.POST.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            GrantedAuthority grantedAuthority = controllerLoggingItem.getGrantedAuthorityService().findLastUpdatedGrantedAuthority(controllerLoggingItem.getUserId());
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "has assigned the user '" + grantedAuthority.getUser().getEcasId() + "' with role '" +
                    grantedAuthority.getRole().getName() +
                    "' to the entity with id '" +
                    grantedAuthority.getGroup().getId() + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_USER_BULK_CREATION(UserProfileController.class.getName(), "uploadBulk", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "has triggered a User bulk creation for the business with id '" + controllerLoggingItem.getParameters().get("businessId") + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },
    ADMIN_BUSINESS_ENTITY_BULK_CREATION(GroupController.class.getName(), "uploadBulk", HttpMethod.PUT.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "has triggered an Entities bulk creation for the business with id '" + controllerLoggingItem.getParameters().get("businessId") + "' " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    },

    SETTINGS_PUBLIC_KEY(SettingsController.class.getName(), "getPublicKey", HttpMethod.GET.name(), "") {
        @Override
        public String renderLog(ControllerLoggingItem controllerLoggingItem) {
            return controllerLoggingItem.getMethodStartTimestamp() + " " +
                    USER + "'" + controllerLoggingItem.getUserId() + "' " +
                    "is retrieving server public key " +
                    (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
        }
    };

    public final String className;
    public final String classMethod;
    public final String httpMethod;
    public final String message;

    LogRenderingEnum(String className, String classMethod, String httpMethod, String message) {
        this.className = className;
        this.classMethod = classMethod;
        this.httpMethod = httpMethod;
        this.message = message;
    }

    public String getMapKey() {
        return className + "." + classMethod + "." + httpMethod;
    }

    public String renderLog(ControllerLoggingItem controllerLoggingItem) {
        return controllerLoggingItem.getMethodStartTimestamp() + " " +
                USER + "'" + controllerLoggingItem.getUserId() + "' " +
                message +
                (controllerLoggingItem.isSuccessfulCall() ? SUCCESSFULLY : UNSUCCESSFULLY);
    }
}
