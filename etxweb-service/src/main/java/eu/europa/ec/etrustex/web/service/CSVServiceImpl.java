package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.service.validation.model.BulkExchangeRuleSpec;
import eu.europa.ec.etrustex.web.service.validation.model.BulkUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CSVServiceImpl implements CSVService {
    private static final String DELIMITERS = "[,;]";
    public static final String FILE_NOT_VALID = "The csv file is not valid. Please check if the content of the file is in the correct format.";
    public static final String ROLE_NOT_VALID = "Invalid role %s.";

    public static final String LINE = " - Line %s.";
    public static final String DUPLICATE_USERS = " Duplicate users";
    public static final String DUPLICATE_ENTITIES = " Duplicate entities";
    public static final String MISS_MATCH_DESCRIPTION_ENTITIES = " Channel description should match";

    private final GroupRepository groupRepository;
    private final UserService userService;

    @Override
    public List<BulkUserProfileSpec> parseCSVFileToUserProfile(byte[] fileContent, Long businessId) {
        List<BulkUserProfileSpec> userProfileSpecs = new ArrayList<>();

        Group parent = groupRepository.findById(businessId)
                .orElseThrow(() -> new EtxWebException("business not found"));
        try {
            Arrays.stream(contentToString(fileContent)).forEach(line -> userProfileSpecs.add(toUserProfileSpec(line, parent)));
            checkIfDuplicateUserExists(userProfileSpecs);
        } catch (EtxWebException e) {
            throw new EtxWebException(e.getMessage());
        }

        return userProfileSpecs;
    }

    @Override
    public List<GroupSpec> parseCSVFileToGroupSpecs(byte[] fileContent, Long businessId) {
        List<GroupSpec> groupSpecs = new ArrayList<>();
        try {
            Arrays.stream(contentToString(fileContent)).forEach(line -> groupSpecs.add(toGroupSpec(line, businessId)));
            checkIfDuplicateGroupExists(groupSpecs);
        } catch (EtxWebException e) {
            throw new EtxWebException(e.getMessage());
        }

        return groupSpecs;
    }

    @Override
    public List<BulkExchangeRuleSpec> parseCSVFileToBulkExchangeRuleSpecs(byte[] fileContent, Long businessId) {
        List<BulkExchangeRuleSpec> exchangeRuleSpecs = new ArrayList<>();
        try {
            Arrays.stream(contentToString(fileContent)).forEach(line -> exchangeRuleSpecs.add(toBulkExchangeRuleSpec(line, businessId)));
            checkIfDuplicateBulkExchangeRuleExists(exchangeRuleSpecs);
            checkIfChannelDescriptionsMatch(exchangeRuleSpecs);
        } catch (EtxWebException e) {
            throw new EtxWebException(e.getMessage());
        }

        return exchangeRuleSpecs;
    }

    @Override
    public List<ExchangeRule> parseCSVFileToExchangeRules(byte[] fileContent, Long businessId, Channel channel) {
        Group business = groupRepository.findById(businessId)
                .orElseThrow(() -> new EtxWebException(String.format("Business with id %s not found", businessId)));

        List<ExchangeRule> exchangeRules = new ArrayList<>();
        try {
            AtomicInteger i = new AtomicInteger(1);
            Arrays.stream(contentToString(fileContent)).forEach(line -> exchangeRules.add(toExchangeRuleSpec(i.getAndIncrement(), line, business, channel)));
            checkIfDuplicateExchangeRuleExists(exchangeRules);
        } catch (EtxWebException e) {
            throw new EtxWebException(e.getMessage());
        }

        return exchangeRules;
    }

    private String[] contentToString(byte[] fileContent) {
        return new String(fileContent).replace("\uFEFF", "").split("\n");
    }

    private BulkUserProfileSpec toUserProfileSpec(String line, Group parent) throws EtxWebException {
        String[] data = line.split(DELIMITERS);
        if (data.length != 8) {
            throw new EtxWebException(FILE_NOT_VALID);
        }

        Group group = groupRepository.findByIdentifierAndParentId(data[2].trim(), parent.getId())
                .orElseThrow(() -> new EtxWebException(String.format("Entity with identifier %s doesn't belong to business %s", data[2].trim(), parent.getIdentifier())));

        LdapUserDto ldapUserDto = userService.getEuLoginProfileFromLdap(data[0].trim());
        if (ldapUserDto == null)
            throw new EtxWebException(String.format("User with reference %s doesn't exist in EU Login LDAP", data[0].trim()));

        BulkUserProfileSpec userProfileSpec = BulkUserProfileSpec.builder()
                .ecasId(ldapUserDto.getEuLogin())
                .name(ldapUserDto.getFullName())
                .euLoginEmailAddress(ldapUserDto.getEuLoginEmail())
                .groupId(group.getId())
                .isOperator(isBoolean(data[3].trim()))
                .isAdmin(isBoolean(data[4].trim()))
                .statusNotification(isBoolean(data[5].trim()))
                .newMessageNotification(isBoolean(data[6].trim()))
                .retentionWarningNotification(isBoolean(data[7].trim()))
                .build();

        if (StringUtils.isNotBlank(data[1].trim())) {
            userProfileSpec.setAlternativeEmailUsed(true);
            userProfileSpec.setAlternativeEmail(data[1].trim());
        }

        checkIfNotificationIsConfiguredOnlyForOperator(userProfileSpec);

        return userProfileSpec;
    }

    private GroupSpec toGroupSpec(String line, Long businessId) throws EtxWebException {
        String[] data = line.split(DELIMITERS);
        if (data.length != 3) {
            throw new EtxWebException(FILE_NOT_VALID);
        }

        return GroupSpec.builder()
                .identifier(data[0].trim())
                .displayName(data[1].trim())
                .description(data[2].trim())
                .type(GroupType.ENTITY)
                .parentGroupId(businessId)
                .isActive(Boolean.TRUE)
                .build();

    }

    private BulkExchangeRuleSpec toBulkExchangeRuleSpec(String line, Long businessId) throws EtxWebException {
        String[] data = line.split(DELIMITERS);
        if (data.length != 4) {
            throw new EtxWebException(FILE_NOT_VALID);
        }

        ExchangeMode exchangeMode;
        try {
            exchangeMode = ExchangeMode.valueOf(data[3].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EtxWebException(String.format(ROLE_NOT_VALID, data[3]));
        }

        return BulkExchangeRuleSpec.builder()
                .channelName(data[0].trim())
                .channelDescription(data[1].trim())
                .memberIdentifier(data[2].trim())
                .exchangeMode(exchangeMode)
                .parentGroupId(businessId)
                .build();

    }

    private ExchangeRule toExchangeRuleSpec(int index, String line, Group business, Channel channel) throws EtxWebException {
        String[] data = line.split(DELIMITERS);
        if (data.length != 2) {
            throw new EtxWebException(FILE_NOT_VALID);
        }

        ExchangeMode exchangeMode;
        try {
            exchangeMode = ExchangeMode.valueOf(data[1].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EtxWebException(String.format(ROLE_NOT_VALID + LINE, data[1], index));
        }

        Group member = groupRepository.findByIdentifierAndParentId(data[0].trim(), business.getId())
                .orElseThrow(() -> new EtxWebException(String.format("Entity with identifier %s doesn't belong to business %s" + LINE, data[0].trim(), business.getIdentifier(), index)));
        return ExchangeRule.builder()
                .channel(channel)
                .member(member)
                .exchangeMode(exchangeMode)
                .build();

    }

    private boolean isBoolean(String value) throws EtxWebException {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        } else {
            throw new EtxWebException(FILE_NOT_VALID);
        }
    }

    private void checkIfNotificationIsConfiguredOnlyForOperator(BulkUserProfileSpec bulkUserProfileSpec) {
        if (Boolean.FALSE.equals(bulkUserProfileSpec.getIsOperator()) && Boolean.TRUE.equals(bulkUserProfileSpec.getIsAdmin())
                && (Boolean.TRUE.equals(bulkUserProfileSpec.isNewMessageNotification())
                || Boolean.TRUE.equals(bulkUserProfileSpec.isStatusNotification())))
            throw new EtxWebException(FILE_NOT_VALID + " Notifications should be configured only for operator role");
    }

    private void checkIfDuplicateUserExists(List<BulkUserProfileSpec> bulkUserProfileSpecs) {
        Set<BulkUserProfileSpec> withoutDuplicateUsers = bulkUserProfileSpecs.stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(BulkUserProfileSpec::getEcasId)
                                .thenComparing(BulkUserProfileSpec::getGroupId)))
                );

        if (withoutDuplicateUsers.size() != bulkUserProfileSpecs.size())
            throw new EtxWebException(FILE_NOT_VALID + DUPLICATE_USERS);
    }

    private void checkIfDuplicateGroupExists(List<GroupSpec> groupSpecs) {
        Set<GroupSpec> withoutDuplicateUsers = groupSpecs.stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(GroupSpec::getIdentifier)))
                );

        if (withoutDuplicateUsers.size() != groupSpecs.size())
            throw new EtxWebException(FILE_NOT_VALID + DUPLICATE_ENTITIES);
    }

    private void checkIfDuplicateBulkExchangeRuleExists(List<BulkExchangeRuleSpec> exchangeRuleSpecs) {
        Set<BulkExchangeRuleSpec> withoutDuplicateExchangeRules = exchangeRuleSpecs.stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(BulkExchangeRuleSpec::getChannelName)
                                .thenComparing(BulkExchangeRuleSpec::getMemberIdentifier)))
                );

        if (withoutDuplicateExchangeRules.size() != exchangeRuleSpecs.size())
            throw new EtxWebException(FILE_NOT_VALID + DUPLICATE_ENTITIES);
    }

    private void checkIfDuplicateExchangeRuleExists(List<ExchangeRule> exchangeRules) {
        Set<String> withoutDuplicateExchangeRules = new HashSet<>();

        for (ExchangeRule exchangeRule:exchangeRules) {
            if (withoutDuplicateExchangeRules.contains(exchangeRule.getMember().getIdentifier())) {
                throw new EtxWebException(FILE_NOT_VALID + DUPLICATE_ENTITIES);
            } else {
                withoutDuplicateExchangeRules.add(exchangeRule.getMember().getIdentifier());
            }
        }

    }

    private void checkIfChannelDescriptionsMatch(List<BulkExchangeRuleSpec> exchangeRuleSpecs) {
        exchangeRuleSpecs.forEach(er -> exchangeRuleSpecs.forEach(er1 -> {
            if (er.getChannelName().equals(er1.getChannelName()) && !er.getChannelDescription().equals(er1.getChannelDescription())) {
                throw new EtxWebException(FILE_NOT_VALID + MISS_MATCH_DESCRIPTION_ENTITIES);
            }
        }));
    }

}
