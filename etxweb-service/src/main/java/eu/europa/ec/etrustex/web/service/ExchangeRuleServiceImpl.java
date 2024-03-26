package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.BulkExchangeRuleSpec;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.UNIQUE_GROUP_NAME_ERROR_MSG;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRuleServiceImpl implements ExchangeRuleService {
    private final ExchangeRuleRepository exchangeRuleRepository;
    private final GroupRepository groupRepository;
    private final ChannelRepository channelRepository;
    private final ChannelService channelService;
    private final CSVService csvService;

    private static final String LINE = " - line ";

    @Override
    public boolean canSend(Long senderId, Long... recipientIds) {
        return exchangeRuleRepository.countValidRecipientsForSender(senderId, recipientIds).equals(recipientIds.length);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> getValidRecipients(Long senderId) {
        return exchangeRuleRepository.getValidRecipients(senderId);
    }

    @Override
    public Page<ExchangeRule> findByChannelId(Long channelId, String filterValue, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by(Sort.Order.asc("member.id"))));
        return exchangeRuleRepository.findByChannelIdAndMemberIdentifierOrMemberNameContains(channelId, filterValue, pageRequest);
    }

    @Override
    public List<ExchangeRule> searchByChannelId(Long channelId, String filterValue) {
        return exchangeRuleRepository.findByChannelIdAndMemberIdentifierOrMemberNameContains(channelId, filterValue, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional
    public List<ExchangeRule> create(List<ExchangeRuleSpec> exchangeRuleSpecs, boolean forced) {
        if (!exchangeRuleSpecs.isEmpty()) {
            ExchangeMode exchangeMode = exchangeRuleSpecs.get(0).getExchangeMode();
            Channel channel = channelService.findById(exchangeRuleSpecs.get(0).getChannelId());
            List<Group> members = groupRepository.findByIdIn(exchangeRuleSpecs.stream().map(ExchangeRuleSpec::getMemberId).collect(Collectors.toList()));

            List<ExchangeRule> exchangeRuleOptional = exchangeRuleRepository
                    .findByChannelIdAndMemberIdIn(channel.getId(), members.stream().map(Group::getId).collect(Collectors.toList()));

            if (exchangeRuleOptional.isEmpty() || forced) {
                // filter invalid exchange rules
                exchangeRuleSpecs = exchangeRuleSpecs.stream()
                        .filter(exchangeRuleSpec -> exchangeRuleOptional.stream()
                                .noneMatch(n -> n.getMember().getId().equals(exchangeRuleSpec.getMemberId()) && n.getChannel().getId().equals(exchangeRuleSpec.getChannelId())))
                        .collect(Collectors.toList());
                return (List<ExchangeRule>) exchangeRuleRepository.saveAll(exchangeRuleSpecs.stream()
                        .map(exchangeRuleSpec -> ExchangeRule.builder()
                                .channel(channel)
                                .exchangeMode(exchangeMode)
                                .member(members.stream()
                                        .filter(member -> member.getId().equals(exchangeRuleSpec.getMemberId()))
                                        .findFirst()
                                        .orElseThrow(() -> new EtxWebException(String.format("Cannot find member with id: %s", exchangeRuleSpec.getMemberId())))
                                ).build()
                        ).collect(Collectors.toList())
                );
            } else {
                // return only the failed members
                return exchangeRuleOptional.stream()
                        .map(exchangeRuleSpec -> ExchangeRule.builder()
                                .member(exchangeRuleSpec.getMember())
                                .exchangeMode(exchangeRuleSpec.getExchangeMode())
                                .build())
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public ExchangeRule update(ExchangeRuleSpec exchangeRuleSpec) {
        Channel channel = channelService.findById(exchangeRuleSpec.getChannelId());
        Group member = groupRepository.findById(exchangeRuleSpec.getMemberId())
                .orElseThrow(() -> new EtxWebException("Cannot find group with id " + exchangeRuleSpec.getMemberId(), new IllegalArgumentException()));

        if (member == null) {
            throw new EtxWebException(String.format("Cannot find member with id: %s",
                    exchangeRuleSpec.getMemberId()));
        }
        ExchangeMode exchangeMode = exchangeRuleSpec.getExchangeMode();

        return exchangeRuleRepository.save(
                ExchangeRule.builder()
                        .channel(channel)
                        .member(member)
                        .exchangeMode(exchangeMode)
                        .build()
        );
    }

    @Override
    @Transactional
    public void delete(Long memberId, Long channelId) {
        exchangeRuleRepository.deleteExchangeRulesByMemberIdAndChannelId(memberId, channelId);
    }

    @Override
    public ExchangeRule findLastUpdatedExchangeRule(String user) {
        return exchangeRuleRepository.findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(user);
    }

    @Override
    public List<String> bulkAddExchangeRule(byte[] fileContent, Long businessId) {
        List<String> errors = new ArrayList<>();
        List<BulkExchangeRuleSpec> bulkExchangeRuleSpecs;
        try {
            bulkExchangeRuleSpecs = csvService.parseCSVFileToBulkExchangeRuleSpecs(fileContent, businessId);
            errors = validateBulkExchangeRuleSpec(bulkExchangeRuleSpecs);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return errors;
        }

        if (errors.isEmpty()) {
            List<ExchangeRule> exchangeRules = new ArrayList<>();
            bulkExchangeRuleSpecs.forEach(exchangeRuleSpec -> {
                Channel channel = channelRepository.findByBusinessIdAndName(exchangeRuleSpec.getParentGroupId(), exchangeRuleSpec.getChannelName());
                if (channel == null) {
                    channel = channelService.create(ChannelSpec.builder()
                            .name(exchangeRuleSpec.getChannelName())
                            .description(exchangeRuleSpec.getChannelDescription())
                            .businessId(businessId)
                            .isActive(true)
                            .build());
                }

                Group group = groupRepository.findByIdentifierAndParentId(exchangeRuleSpec.getMemberIdentifier(), businessId)
                        .orElseThrow(() -> new Error("Entity not found!"));

                exchangeRules.add(ExchangeRule.builder()
                        .channel(channel)
                        .member(group)
                        .exchangeMode(exchangeRuleSpec.getExchangeMode())
                        .build());
            });
            exchangeRuleRepository.saveAll(exchangeRules);
        }

        return errors;
    }

    @Override
    public List<String> bulkAddParticipants(byte[] fileContent, Long businessId, Long channelId) {
        List<String> errors = new ArrayList<>();
        List<ExchangeRule> exchangeRules;
        Channel channel = channelService.findById(channelId);

        try {
            exchangeRules = csvService.parseCSVFileToExchangeRules(fileContent, businessId, channel);
            errors = validateExchangeRule(exchangeRules);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return errors;
        }

        if (errors.isEmpty()) {
            exchangeRuleRepository.saveAll(exchangeRules);
        }

        return errors;
    }

    private List<String> validateBulkExchangeRuleSpec(List<BulkExchangeRuleSpec> exchangeRuleSpecs) {
        List<String> errors = new ArrayList<>();
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        IntStream.range(0, exchangeRuleSpecs.size())
                .forEach(i -> {
                    BulkExchangeRuleSpec exchangeRuleSpec = exchangeRuleSpecs.get(i);

                    // check no existing channel with given name exist in the group
                    String duplicateChannelName = checkDuplicateChannelName(exchangeRuleSpec, i);
                    if (StringUtils.isNotEmpty(duplicateChannelName)) {
                        errors.add(duplicateChannelName);
                    }

                    Set<ConstraintViolation<BulkExchangeRuleSpec>> constraintViolations = validator.validate(exchangeRuleSpec);
                    if (!constraintViolations.isEmpty()) {
                        constraintViolations.forEach(constraintViolation ->
                                errors.add(constraintViolation.getMessage() + LINE + (i + 1)));
                    }

                    // check participant exists
                    if (!groupRepository.existsByIdentifierAndParentId(exchangeRuleSpec.getMemberIdentifier(), exchangeRuleSpec.getParentGroupId())) {
                        errors.add(String.format("Entity with name %s does not exist", exchangeRuleSpec.getMemberIdentifier()) + LINE + (i + 1));
                    }
                });

        return errors;
    }

    private List<String> validateExchangeRule(List<ExchangeRule> exchangeRules) {
        List<String> errors = new ArrayList<>();

        IntStream.range(0, exchangeRules.size())
                .forEach(i -> {
                    ExchangeRule exchangeRule = exchangeRules.get(i);

                    if (exchangeRuleRepository.findByChannelIdAndMemberId(exchangeRule.getChannel().getId(), exchangeRule.getMember().getId()).isPresent()) {
                        errors.add(String.format("Participant %s already exists in channel %s with role %s", exchangeRule.getMember().getIdentifier(), exchangeRule.getChannel().getName(), exchangeRule.getExchangeMode().name()) + LINE + (i + 1));
                    }
                });

        return errors;
    }
    private String checkDuplicateChannelName(BulkExchangeRuleSpec exchangeRuleSpec, int lineNumber) {
        if (channelRepository.existsByBusinessIdAndName(exchangeRuleSpec.getParentGroupId(), exchangeRuleSpec.getChannelName())) {
            return UNIQUE_GROUP_NAME_ERROR_MSG + LINE + (lineNumber + 1);
        }

        return null;
    }

}
