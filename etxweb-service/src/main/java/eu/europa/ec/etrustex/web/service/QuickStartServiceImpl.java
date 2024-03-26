package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.QuickStartSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
@Slf4j
public class QuickStartServiceImpl implements QuickStartService {
    private final GroupService groupService;
    private final ChannelService channelService;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final ExchangeRuleRepository exchangeRuleRepository;

    @Override
    @Transactional
    public void create(QuickStartSpec spec) {
        if ((spec.getNewUsers() == null || spec.getNewUsers().isEmpty()) && (spec.getExistingUsers() == null || spec.getExistingUsers().isEmpty())) {
            throw new EtxWebException("No users provided");
        }

        Group group = groupService.create(spec.getGroupSpec());

        if (spec.getChannelSpec() != null && spec.getChannelSpec().getBusinessId() != null) {
            createChannel(spec.getChannelSpec(), group);
        }

        List<User> addedUsers = (spec.getNewUsers() != null) ? createNewUsers(spec, group) : new ArrayList<>();
        addExistingUsers(spec, group, addedUsers);
    }

    private void createChannel(ChannelSpec channelSpec, Group group) {
        Channel channel = channelService.create(channelSpec);
        if (channelSpec.getDefaultExchangeMode() != null) {
            exchangeRuleRepository.save(
                    ExchangeRule.builder()
                            .exchangeMode(channelSpec.getDefaultExchangeMode())
                            .member(group)
                            .channel(channel)
                            .build()
            );
        }
    }

    private List<User> createNewUsers(QuickStartSpec quickStartSpec, Group group) {
        List<User> addedUsers = new ArrayList<>();
        for (UserProfileSpec u : quickStartSpec.getNewUsers()) {
            if (addedUsers.stream().anyMatch(user -> Objects.equals(user.getEcasId(), u.getEcasId()))) {
                continue;
            }
            UserProfile newUserProfile = userProfileService.create(CreateUserProfileSpec.builder()
                    .ecasId(u.getEcasId())
                    .name(u.getName())
                    .groupId(group.getId())
                    .roleNames(quickStartSpec.getRoleNames())
                    .newMessageNotification(quickStartSpec.isNewMessageNotification())
                    .retentionWarningNotification(quickStartSpec.isRetentionWarningNotification())
                    .statusNotification(quickStartSpec.isStatusNotification())
                    .build());
            addedUsers.add(newUserProfile.getUser());
        }
        return addedUsers;
    }

    private void addExistingUsers(QuickStartSpec quickStartSpec, Group group, List<User> addedUsers) {
        if (quickStartSpec.getExistingUsers() != null) {
            quickStartSpec.getExistingUsers().stream()
                    .map(u -> userService.findByEcasId(u.getEcasId()))
                    .filter(user -> !addedUsers.contains(user))
                    .forEach(u -> {
                        if (u == null) {
                            throw new EtxWebException("Some user was not found");
                        }
                        userProfileService.create(CreateUserProfileSpec.builder()
                                .ecasId(u.getEcasId())
                                .name(u.getName())
                                .groupId(group.getId())
                                .roleNames(quickStartSpec.getRoleNames())
                                .newMessageNotification(quickStartSpec.isNewMessageNotification())
                                .retentionWarningNotification(quickStartSpec.isRetentionWarningNotification())
                                .statusNotification(quickStartSpec.isStatusNotification())
                                .build());
                    });
        }
    }
}
