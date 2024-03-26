package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ExchangeRuleRepository exchangeRuleRepository;
    private final GroupRepository groupRepository;

    @Override
    public Page<Channel> findByBusinessIdAndName(Long businessId, String name, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by(Sort.Order.asc("id"))));
        return channelRepository.findByBusinessIdAndNameContainingIgnoreCase(businessId, name, pageRequest);
    }

    @Override
    public Channel findById(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new EtxWebException(String.format("Channel with id %s not found!", id)));
    }

    @Override
    public List<Channel> findByBusinessIdAndEntityId(Long businessId, Long entityId) {
        return channelRepository.findByBusinessIdAndEntityId(businessId, entityId);
    }

    @Override
    public void delete(Long id) {
        channelRepository.findById(id).ifPresent(
                channel -> {
                    exchangeRuleRepository.findByChannelId(channel.getId(), Pageable.unpaged()).stream().forEach(exchangeRuleRepository::delete);
                    channelRepository.delete(channel);
                }
        );
    }

    @Override
    public Channel create(@Valid ChannelSpec channelSpec) {

        Group business = groupRepository.findByIdAndType(channelSpec.getBusinessId(), GroupType.BUSINESS)
                .orElseThrow(() -> new EtxWebException(String.format("Business with id %s not found.", channelSpec.getBusinessId())));

        if(channelSpec.getDefaultChannel() != null && channelSpec.getDefaultChannel()){
            channelRepository.findByBusinessIdAndDefaultChannelIsTrue(business.getId()).forEach(channel -> {
                channel.setDefaultChannel(false);
                channel.setDefaultExchangeMode(null);
                channelRepository.save(channel);
            });
        }

        boolean isDefaultChannel = (channelSpec.getDefaultChannel() != null) && channelSpec.getDefaultChannel();
        return channelRepository.save(
                Channel.builder()
                        .business(business)
                        .description(channelSpec.getDescription())
                        .isActive(channelSpec.getIsActive())
                        .name(channelSpec.getName())
                        .defaultChannel(isDefaultChannel)
                        .defaultExchangeMode(isDefaultChannel ? channelSpec.getDefaultExchangeMode(): null)
                        .build()
        );
    }

    @Override
    public void update(Long channelId, ChannelSpec channelSpec) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new EtxWebException(String.format("Channel with id %s not found.", channelId)));

        if(!channel.isDefaultChannel() && channelSpec != null && channelSpec.getDefaultChannel()){
            channelRepository.findByBusinessIdAndDefaultChannelIsTrue(channel.getBusiness().getId()).forEach(channelS -> {
                channelS.setDefaultChannel(false);
                channelS.setDefaultExchangeMode(null);
                channelRepository.save(channelS);
            });
        }

        boolean isDefaultChannel = (channelSpec != null) && (channelSpec.getDefaultChannel() != null) && channelSpec.getDefaultChannel();

        channel.setDefaultChannel(isDefaultChannel);
        channel.setDefaultExchangeMode(isDefaultChannel ? channelSpec.getDefaultExchangeMode() : null);
        channel.setName(channelSpec != null ? channelSpec.getName():"");
        channel.setDescription(channelSpec != null ? channelSpec.getDescription():"");
        channelRepository.save(channel);
    }

    @Override
    public Channel findLastUpdatedChannel(String user) {
        return channelRepository.findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(user);
    }

    @Override
    public List<SearchItem> findByBusinessIdAndName(Long businessId, String name) {
        return channelRepository.findByBusinessIdAndNameContainingIgnoreCase(businessId, name)
                .stream()
                .map(channel ->
                        SearchItem.builder().searchValue(channel.getName()).value(channel.getName()).build())
                .collect(Collectors.toList());
    }
}
