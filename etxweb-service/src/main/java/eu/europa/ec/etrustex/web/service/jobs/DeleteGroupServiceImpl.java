package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.*;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.*;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.SenderPreferencesRepository;
import eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration.GroupConfigurationRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DeleteGroupServiceImpl implements DeleteGroupService {

    private final AttachmentRepository attachmentRepository;
    private final GroupRepository groupRepository;
    private final AlertRepository alertRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final MessageSummaryRepository messageSummaryRepository;
    private final ExchangeRuleRepository exchangeRuleRepository;
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    private final GroupConfigurationRepository<GroupConfiguration<?>> groupConfigurationRepository;
    private final UserGuideRepository userGuideRepository;
    private final AlertUserStatusRepository alertUserStatusRepository;
    private final UserRegistrationRequestRepository userRegistrationRequestRepository;
    private final MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    private final MessageUserStatusRepository messageUserStatusRepository;
    private final SenderPreferencesRepository senderPreferencesRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;

    @Override
    @Transactional
    public void deleteBusiness(Group business) {
        if (business.getRemovedDate().before(deletionDate())) {
            List<Group> groups = groupRepository.findListByParentId(business.getId());
            groups.forEach(group -> {
                deleteMessages(group);
                emptyGroup(group);
            });

            log.info("remove business's groups");
            groups.forEach(groupRepository::delete);

            log.info("remove business's channels");
            channelRepository.deleteByBusiness(business);

            log.info("remove business's user guides");
            userGuideRepository.deleteByBusiness(business);

            emptyGroup(business);

            log.info("Remove business's templates");
            List<Template> templates = templateRepository.getByGroupId(business.getId()).stream()
                    .filter(t -> t.getGroups().size() == 1)
                    .collect(Collectors.toList());
            business.getTemplates().removeAll(templates);
            groupRepository.save(business);
            templateRepository.deleteAll(templates);

            log.info("remove the business");
            groupRepository.delete(business);
        }
    }

    @Override
    public void removeGroupConfigurations(Group group) {
        emptyGroup(group);
    }

    private Date deletionDate() {
        Date deleteBefore = Date.from(Instant.now().minus(11, ChronoUnit.DAYS));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deleteBefore);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private void deleteMessages(Group group) {
        deleteReceivedMessages(group);
        deleteSentMessages(group);
    }

    private void emptyGroup(Group group) {

        log.info("remove granted authorities for group: " + group.getIdentifier());
        grantedAuthorityRepository.deleteByGroup(group);

        log.info("remove exchange rules for group: " + group.getIdentifier());
        exchangeRuleRepository.deleteByMember(group);

        log.info("remove alert statuses for group: " + group.getIdentifier());
        alertUserStatusRepository.deleteByGroupId(group.getId());

        log.info("remove alerts for group: " + group.getIdentifier());
        alertRepository.deleteByGroup(group);

        log.info("remove user registration requests for group: " + group.getIdentifier());
        userRegistrationRequestRepository.deleteByGroup(group);

        log.info("remove group configurations for group: " + group.getIdentifier());
        groupConfigurationRepository.deleteByGroupId(group.getId());

        log.info("remove group preferences for group: " + group.getIdentifier());
        if (group.getSenderPreferences() != null && senderPreferencesRepository.countGroupsById(group.getSenderPreferences().getId()) == 1) {
            senderPreferencesRepository.delete(group.getSenderPreferences());
        }

        log.info("remove user profiles for group: " + group.getIdentifier());
        if (group.getType().equals(GroupType.ENTITY)) {
            userProfileRepository.deleteByGroupId(group.getId());
        } else {
            userProfileRepository.findByGroupId(group.getId()).forEach(userProfile -> deleteUser(userProfile.getUser(), group));
        }

    }

    private void deleteReceivedMessages(Group group) {
        log.info("remove received messages for group: " + group.getIdentifier());
        List<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientId(group.getId());
        List<MessageSummaryUserStatus> messageSummaryUserStatusesToRemove = new ArrayList<>();
        messageSummaries.forEach(messageSummary -> {
            messageSummaryUserStatusesToRemove.addAll(messageSummary.getMessageSummaryUserStatuses());
            messageSummary.getMessageSummaryUserStatuses().clear();
        });

        ListUtils.partition(messageSummaryUserStatusesToRemove, 500).forEach(messageSummaryUserStatusRepository::deleteAll);
        messageSummaryRepository.deleteAll(messageSummaries);
    }

    private void deleteSentMessages(Group group) {
        log.info("start: remove sent messages for group: " + group.getIdentifier());
        List<Message> messages = messageRepository.findBySenderGroupId(group.getId());

        List<MessageSummaryUserStatus> messageSummaryUserStatusesToRemove = new ArrayList<>();
        List<Attachment> attachmentsToRemove = new ArrayList<>();
        List<MessageSummary> messageSummariesToRemove = new ArrayList<>();
        List<MessageUserStatus> messageUserStatusesToRemove = new ArrayList<>();
        messages.forEach(message -> {
            message.getMessageSummaries().forEach(messageSummary -> {
                messageSummaryUserStatusesToRemove.addAll(messageSummary.getMessageSummaryUserStatuses());
                messageSummary.getMessageSummaryUserStatuses().clear();
            });
            messageSummariesToRemove.addAll(message.getMessageSummaries());
            messageUserStatusesToRemove.addAll(message.getMessageUserStatuses());
            attachmentsToRemove.addAll(attachmentRepository.findByMessage(message));

            message.getMessageSummaries().clear();
            message.getMessageUserStatuses().clear();
        });

        log.info("remove business's sent message attachments for group: " + group.getIdentifier());
        ListUtils.partition(attachmentsToRemove, 500).forEach(attachmentRepository::deleteAll);

        log.info("remove business's sent message summary user status for group: " + group.getIdentifier());
        ListUtils.partition(messageSummaryUserStatusesToRemove, 500).forEach(messageSummaryUserStatusRepository::deleteAll);

        log.info("remove business's sent message user status for group: " + group.getIdentifier());
        ListUtils.partition(messageUserStatusesToRemove, 500).forEach(messageUserStatusRepository::deleteAll);

        log.info("remove business's sent message summaries for group: " + group.getIdentifier());
        ListUtils.partition(messageSummariesToRemove, 500).forEach(messageSummaryRepository::deleteAll);

        log.info("remove business's sent messages for group: " + group.getIdentifier());
        ListUtils.partition(messages, 500).forEach(messageRepository::deleteAll);
        log.info("end: remove business's sent messages for group: " + group.getIdentifier());
    }

    private void deleteUser(User user, Group group) {
        userProfileRepository.deleteByUserEcasIdAndGroupId(user.getEcasId(), group.getId());
        if (!userProfileRepository.findByUserEcasId(user.getEcasId()).findAny().isPresent()) {
            Optional<User> maybeUser = userRepository.findByEcasIdIgnoreCase(user.getEcasId());
            if (maybeUser.isPresent()) {
                log.info("remove user: " + maybeUser.get().getId());
                alertUserStatusRepository.deleteByUser(maybeUser.get());
                userRepository.delete(maybeUser.get());
            }
        }
    }
}
